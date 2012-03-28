package ap.peppol.webgui.api;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ap.peppol.webgui.api.io.StorageIO;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.callback.INonThrowingRunnable;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.serialize.MicroReader;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.state.EChange;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

public abstract class AbstractManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractManager.class);

  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final File m_aFile;
  private boolean m_bPendingChanges = true;
  private boolean m_bAutoSaveEnabled = true;

  protected AbstractManager (@Nonnull @Nonempty final String sFilename) {
    m_aFile = StorageIO.getFile (sFilename);
    if (m_aFile.exists ()) {
      if (!m_aFile.isFile ())
        throw new IllegalArgumentException ("The passed filename '" + sFilename + "' is a directory and not a file!");
    }
    else {
      // Ensure the parent directory is present
      FileOperations.createDirRecursiveIfNotExisting (m_aFile.getParentFile ());
    }
  }

  @Nonnull
  @OverrideOnDemand
  protected EChange onInit () {
    return EChange.UNCHANGED;
  }

  @Nonnull
  protected abstract EChange onRead (@Nonnull IMicroDocument aDoc);

  @Nonnull
  protected abstract IMicroDocument createWriteData ();

  private void _writeToFile () {
    // Create XML document
    final IMicroDocument aDoc = createWriteData ();

    // Write to file
    if (MicroWriter.writeToStream (aDoc, FileUtils.getOutputStream (m_aFile), XMLWriterSettings.SUGGESTED_XML_SETTINGS)
                   .isFailure ())
      s_aLogger.error ("Failed to write data to " + m_aFile);
  }

  private void _writeToFileOnPendingChanges () {
    if (m_bPendingChanges) {
      _writeToFile ();
      m_bPendingChanges = false;
    }
  }

  protected final void initialRead () {
    if (!m_aFile.exists ()) {
      // initial setup
      if (onInit ().isChanged ())
        _writeToFile ();
    }
    else {
      // Read existing file
      final IMicroDocument aDoc = MicroReader.readMicroXML (new FileSystemResource (m_aFile));
      if (aDoc == null)
        s_aLogger.error ("Failed to read XML document from file " + m_aFile);
      else
        if (onRead (aDoc).isChanged ())
          _writeToFile ();
    }
    m_bPendingChanges = false;
  }

  public final boolean isAutoSaveEnabled () {
    m_aRWLock.readLock ().lock ();
    try {
      return m_bAutoSaveEnabled;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public final void setAutoSaveEnabled (final boolean bAutoSaveEnabled) {
    m_aRWLock.writeLock ().lock ();
    try {
      m_bAutoSaveEnabled = bAutoSaveEnabled;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  // Must be called in a writeLock!
  protected final void markAsChanged () {
    if (m_bAutoSaveEnabled) {
      // Auto save
      _writeToFile ();
      m_bPendingChanges = false;
    }
    else {
      // Just remember that something changed
      m_bPendingChanges = true;
    }
  }

  public final void runWithoutAutoSave (@Nonnull final INonThrowingRunnable aCallback) {
    if (aCallback == null)
      throw new NullPointerException ("callback");

    m_aRWLock.writeLock ().lock ();
    try {
      final boolean bOldAutoSave = m_bAutoSaveEnabled;
      m_bAutoSaveEnabled = false;
      try {
        aCallback.run ();
      }
      finally {
        m_bAutoSaveEnabled = bOldAutoSave;
        _writeToFileOnPendingChanges ();
      }
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}

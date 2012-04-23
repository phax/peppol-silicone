/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.webgui.io;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.callback.INonThrowingRunnable;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.impl.MicroComment;
import com.phloc.commons.microdom.serialize.MicroReader;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.state.EChange;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

public abstract class AbstractDAO {
  public static final boolean DEFAULT_AUTO_SAVE_ENABLED = true;
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractDAO.class);

  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final File m_aFile;
  private boolean m_bPendingChanges = false;
  private boolean m_bAutoSaveEnabled = DEFAULT_AUTO_SAVE_ENABLED;

  protected AbstractDAO (@Nonnull @Nonempty final String sFilename) {
    m_aFile = StorageIO.getFile (sFilename);
    if (m_aFile.exists ()) {
      // file exist -> must be a file!
      if (!m_aFile.isFile ())
        throw new IllegalArgumentException ("The passed filename '" + sFilename + "' is a directory and not a file!");
    }
    else {
      // Ensure the parent directory is present
      FileOperations.createDirRecursiveIfNotExisting (m_aFile.getParentFile ());
    }
  }

  /**
   * Custom initialization routine. Called only if the underlying file does not
   * exist yet.
   * 
   * @return {@link EChange#CHANGED} if something was modified inside this
   *         method
   */
  @Nonnull
  @OverrideOnDemand
  protected EChange onInit () {
    return EChange.UNCHANGED;
  }

  /**
   * Fill the internal structures with from the passed XML document.
   * 
   * @param aDoc
   *        The XML document to read from
   * @return {@link EChange#CHANGED} if reading the data changed something in
   *         the internal structures that requires a writing.
   */
  @Nonnull
  protected abstract EChange onRead (@Nonnull IMicroDocument aDoc);

  /**
   * Create the XML document that should be saved to the file.
   * 
   * @return The non-<code>null</code> document to write to the file.
   */
  @Nonnull
  protected abstract IMicroDocument createWriteData ();

  private void _writeToFile () {
    // Create XML document
    final IMicroDocument aDoc = createWriteData ();

    // Add a small comment
    aDoc.insertBefore (new MicroComment ("This file was generated automatically - do NOT modify!"),
                       aDoc.getDocumentElement ());

    // Write to file
    if (MicroWriter.writeToStream (aDoc, FileUtils.getOutputStream (m_aFile), XMLWriterSettings.SUGGESTED_XML_SETTINGS)
                   .isFailure ())
      s_aLogger.error ("Failed to write data to " + m_aFile);
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

  /**
   * @return <code>true</code> if auto save is enabled, <code>false</code>
   *         otherwise.
   */
  public final boolean isAutoSaveEnabled () {
    m_aRWLock.readLock ().lock ();
    try {
      return m_bAutoSaveEnabled;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Enable or disable auto save. Does not trigger any file writing operations.
   * 
   * @param bAutoSaveEnabled
   *        The new auto save state.
   */
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

  /**
   * @return <code>true</code> if unsaved changes are present
   */
  public boolean hasPendingChanges () {
    m_aRWLock.readLock ().lock ();
    try {
      return m_bPendingChanges;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * In case there are pending changes write them to the file.
   */
  public void writeToFileOnPendingChanges () {
    if (hasPendingChanges ()) {
      // Write changes
      m_aRWLock.writeLock ().lock ();
      try {
        _writeToFile ();
        m_bPendingChanges = false;
      }
      finally {
        m_aRWLock.writeLock ().unlock ();
      }
    }
  }

  /**
   * Run a set of bulk operations with disabled auto save. After the actions,
   * the original state of auto save is restored. After the actions, all pending
   * changes are written to disk.
   * 
   * @param aCallback
   *        The callback performing the operations. May not be <code>null</code>
   *        .
   */
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
      }
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }

    writeToFileOnPendingChanges ();
  }
}
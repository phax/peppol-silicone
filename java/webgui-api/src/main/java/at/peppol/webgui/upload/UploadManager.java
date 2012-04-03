package at.peppol.webgui.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import at.peppol.webgui.io.StorageIO;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.web.singleton.GlobalWebSingleton;

public final class UploadManager extends GlobalWebSingleton {
  private final Lock m_aLock = new ReentrantLock ();
  private final List <UploadedResource> m_aUploads = new ArrayList <UploadedResource> ();

  @Deprecated
  @UsedViaReflection
  public UploadManager () {}

  @Nonnull
  public static UploadManager getInstance () {
    return getGlobalSingleton (UploadManager.class);
  }

  @Nonnull
  public UploadedResource createManagedResource (@Nonnull @Nonempty final String sOriginalFilename) {
    m_aLock.lock ();
    try {
      final UploadedResource aRes = new UploadedResource (sOriginalFilename);
      m_aUploads.add (aRes);
      return aRes;
    }
    finally {
      m_aLock.unlock ();
    }
  }

  @Override
  protected void onDestroy () {
    m_aLock.lock ();
    try {
      // Delete all created temporary files
      for (final UploadedResource aRes : m_aUploads)
        StorageIO.getFileOpMgr ().deleteFile (aRes.getTemporaryFile ());
      m_aUploads.clear ();
    }
    finally {
      m_aLock.unlock ();
    }
  }
}

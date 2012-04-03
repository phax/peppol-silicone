package at.peppol.webgui.upload;

import java.io.File;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import at.peppol.webgui.security.login.LoggedInUserStorage;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.string.StringHelper;

@Immutable
public final class UploadedResource {
  private final String m_sOriginalFilename;
  private final File m_aTempFile;

  UploadedResource (@Nonnull @Nonempty final String sOriginalFilename) {
    if (StringHelper.hasNoText (sOriginalFilename))
      throw new IllegalArgumentException ("originalFilename");
    m_sOriginalFilename = FilenameHelper.getWithoutPath (sOriginalFilename);
    // Ensure a unique name
    m_aTempFile = new File (LoggedInUserStorage.getUploadDirectory (), "upload-" +
                                                                       GlobalIDFactory.getNewPersistentIntID ());
  }

  @Nonnull
  public String getOriginalFilename () {
    return m_sOriginalFilename;
  }

  @Nonnull
  public File getTemporaryFile () {
    return m_aTempFile;
  }

  @Nonnull
  public OutputStream createOutputStream () {
    return FileUtils.getOutputStream (m_aTempFile);
  }
}

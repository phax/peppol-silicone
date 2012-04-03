package at.peppol.webgui.security.login;

import java.io.File;

import javax.annotation.Nonnull;

import at.peppol.webgui.io.StorageIO;

import com.phloc.commons.string.StringHelper;

public final class LoggedInUserStorage {
  private LoggedInUserStorage () {}

  @Nonnull
  private static String _getLoggedInUserID () {
    final String ret = LoggedInUserManager.getInstance ().getCurrentUserID ();
    if (StringHelper.hasNoText (ret))
      throw new IllegalStateException ("No user is logged in!");
    return ret;
  }

  /**
   * @return The base directory for all user-related data
   */
  @Nonnull
  public static File getUserdataDirectory () {
    final File aDir = StorageIO.getFile ("userdata/" + _getLoggedInUserID ());
    StorageIO.getFileOpMgr ().createDirRecursiveIfNotExisting (aDir);
    return aDir;
  }

  /**
   * @return The upload directory of the current user.
   */
  @Nonnull
  public static File getUploadDirectory () {
    final File aDir = new File (getUserdataDirectory (), "upload");
    StorageIO.getFileOpMgr ().createDirIfNotExisting (aDir);
    return aDir;
  }
}

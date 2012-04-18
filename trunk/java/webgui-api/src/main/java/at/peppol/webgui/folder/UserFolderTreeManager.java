package at.peppol.webgui.folder;

import javax.annotation.Nonnull;

import com.phloc.scopes.nonweb.singleton.GlobalSingleton;

public final class UserFolderTreeManager extends GlobalSingleton {
  @Deprecated
  public UserFolderTreeManager () {}

  @Nonnull
  public static UserFolderTreeManager getInstance () {
    return getGlobalSingleton (UserFolderTreeManager.class);
  }
}

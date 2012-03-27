package ap.peppol.webgui.security;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ap.peppol.webgui.security.role.RoleManager;
import ap.peppol.webgui.security.user.IUser;
import ap.peppol.webgui.security.user.IUserManager;
import ap.peppol.webgui.security.user.UserManager;
import ap.peppol.webgui.security.usergroup.UserGroupManager;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.state.EChange;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;

/**
 * This is the central manager that encapsulates all security manages.
 * 
 * @author philip
 */
public final class AccessManager extends GlobalSingleton implements IUserManager {
  private final IUserManager m_aUserMgr;
  private final UserGroupManager m_aUserGroupMgr;
  private final RoleManager m_aRoleMgr;

  @Deprecated
  @UsedViaReflection
  public AccessManager () {
    m_aUserMgr = new UserManager ();
    m_aUserGroupMgr = new UserGroupManager ();
    m_aRoleMgr = new RoleManager ();
  }

  @Nonnull
  public static AccessManager getInstance () {
    return getGlobalSingleton (AccessManager.class);
  }

  // User API

  @Nonnull
  public IUser createNewUser (@Nonnull @Nonempty final String sEmailAddress,
                              @Nonnull @Nonempty final String sPlainTextPassword,
                              @Nullable final String sFirstName,
                              @Nullable final String sLastName,
                              @Nullable final Locale aDesiredLocale) {
    return m_aUserMgr.createNewUser (sEmailAddress, sPlainTextPassword, sFirstName, sLastName, aDesiredLocale);
  }

  @Nonnull
  public EChange deleteUser (@Nullable final String sUserID) {
    if (m_aUserMgr.deleteUser (sUserID).isUnchanged ()) {
      // No such user to delete
      return EChange.UNCHANGED;
    }

    // If something deleted, remove from all user groups
    m_aUserGroupMgr.unassignUserFromAllUserGroups (sUserID);
    return EChange.CHANGED;
  }

  @Nullable
  public IUser getUserOfID (@Nullable final String sUserID) {
    return m_aUserMgr.getUserOfID (sUserID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends IUser> getAllUsers () {
    return m_aUserMgr.getAllUsers ();
  }

  @Nonnull
  public EChange setUserData (@Nullable final String sUserID,
                              @Nullable final String sNewFirstName,
                              @Nullable final String sNewLastName,
                              @Nullable final Locale aNewDesiredLocale) {
    return m_aUserMgr.setUserData (sUserID, sNewFirstName, sNewLastName, aNewDesiredLocale);
  }

  public boolean isUsernamePasswordValid (@Nullable final String sUserID, @Nullable final String sPlainTextPassword) {
    return m_aUserMgr.isUsernamePasswordValid (sUserID, sPlainTextPassword);
  }
}

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
package at.peppol.webgui.security.login;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.security.AccessManager;
import at.peppol.webgui.security.user.IUser;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;
import com.phloc.scopes.web.singleton.SessionWebSingleton;

/**
 * This class manages all logged-in users.
 * 
 * @author philip
 */
@ThreadSafe
public final class LoggedInUserManager extends GlobalSingleton {
  /**
   * This class manages the user ID of the current session
   * 
   * @author philip
   */
  public static final class SessionUserHolder extends SessionWebSingleton {
    private String m_sUserID;

    @Deprecated
    @UsedViaReflection
    public SessionUserHolder () {}

    @Nonnull
    public static SessionUserHolder getInstance () {
      return getSessionSingleton (SessionUserHolder.class);
    }

    @Nonnull
    public EChange setUserID (@Nonnull @Nonempty final String sUserID) {
      if (StringHelper.hasNoText (sUserID))
        throw new IllegalArgumentException ("userID");
      if (m_sUserID != null)
        return EChange.UNCHANGED;
      m_sUserID = sUserID;
      return EChange.CHANGED;
    }

    public void resetUserID () {
      m_sUserID = null;
    }

    @Nullable
    public String getUserID () {
      return m_sUserID;
    }

    @Override
    protected void onDestroy () {
      // Called when the session is destroyed
      // -> Ensure the user is logged out!
      LoggedInUserManager.getInstance ().logoutUser (m_sUserID);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggedInUserManager.class);

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Set <String> m_aLoggedInUsers = new HashSet <String> ();

  @Deprecated
  @UsedViaReflection
  public LoggedInUserManager () {}

  @Nonnull
  public static LoggedInUserManager getInstance () {
    return getGlobalSingleton (LoggedInUserManager.class);
  }

  @Nonnull
  public ELoginResult loginUser (@Nullable final String sEmailAddress, @Nullable final String sPlainTextPassword) {
    // Try to resolve the user
    final IUser aUser = AccessManager.getInstance ().getUserOfEmailAddress (sEmailAddress);
    if (aUser == null)
      return ELoginResult.USER_NOT_EXISTING;
    final String sUserID = aUser.getID ();

    // Check the password
    if (!AccessManager.getInstance ().areUserIDAndPasswordValid (sUserID, sPlainTextPassword))
      return ELoginResult.INVALID_PASSWORD;

    // All checks done!
    m_aRWLock.writeLock ().lock ();
    try {
      if (!m_aLoggedInUsers.add (sUserID)) {
        // The user is already logged in
        return ELoginResult.USER_ALREADY_LOGGED_IN;
      }

      if (SessionUserHolder.getInstance ().setUserID (sUserID).isUnchanged ()) {
        // Another user is already in the current session
        m_aLoggedInUsers.remove (sUserID);
        return ELoginResult.SESSION_ALREADY_HAS_USER;
      }

      s_aLogger.info ("Logged in user '" + sUserID + "'");
      return ELoginResult.SUCCESS;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange logoutUser (@Nullable final String sUserID) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (!m_aLoggedInUsers.remove (sUserID))
        return EChange.UNCHANGED;
      SessionUserHolder.getInstance ().resetUserID ();
      s_aLogger.info ("Logged out user '" + sUserID + "'");
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Logout the currently logged in user.
   * 
   * @return {@link EChange#CHANGED} if the user was logged in.
   */
  @Nonnull
  public EChange logoutCurrentUser () {
    return logoutUser (getCurrentUserID ());
  }

  /**
   * Check if the specified user is logged in or not
   * 
   * @param sUserID
   *        The user ID to check. May be <code>null</code>.
   * @return <code>true</code> if the user is logged in, <code>false</code>
   *         otherwise.
   */
  @Nonnull
  public boolean isUserLoggedIn (@Nullable final String sUserID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aLoggedInUsers.contains (sUserID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return A non-<code>null</code> but maybe empty set with all currently
   *         logged in user IDs.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllLoggedInUserIDs () {
    m_aRWLock.readLock ().lock ();
    try {
      return ContainerHelper.newSet (m_aLoggedInUsers);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return The number of currently logged in users. Always &ge; 0.
   */
  @Nonnegative
  public int getLoggedInUserCount () {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aLoggedInUsers.size ();
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return The ID of the user logged in this session or <code>null</code> if
   *         no user is logged in.
   */
  @Nullable
  public String getCurrentUserID () {
    return SessionUserHolder.getInstance ().getUserID ();
  }

  /**
   * @return <code>true</code> if a user is currently logged into this session,
   *         <code>false</code> otherwise.
   */
  public boolean isUserLoggedInInCurrentSession () {
    return getCurrentUserID () != null;
  }

  /**
   * @return The user currently logged in this session or <code>null</code> if
   *         no user is logged in.
   */
  @Nullable
  public IUser getCurrentUser () {
    return AccessManager.getInstance ().getUserOfID (getCurrentUserID ());
  }
}

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
package at.peppol.webgui.security.user;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import at.peppol.webgui.api.CWebGUI;
import at.peppol.webgui.io.AbstractDAO;
import at.peppol.webgui.security.CSecurity;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.messagedigest.MessageDigestGeneratorHelper;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;

/**
 * This class manages the available users.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserManager extends AbstractDAO implements IUserManager {
  private final Map <String, User> m_aUsers = new HashMap <String, User> ();

  public UserManager () {
    super ("security/users.xml");
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onInit () {
    _addUser (new User (CSecurity.USER_ADMINISTRATOR_ID,
                        CSecurity.USER_ADMINISTRATOR_EMAIL,
                        UserManager.createUserPasswordHash (CSecurity.USER_ADMINISTRATOR_PASSWORD),
                        CSecurity.USER_ADMINISTRATOR_NAME,
                        null,
                        null));
    _addUser (new User (CSecurity.USER_USER_ID,
                        CSecurity.USER_USER_EMAIL,
                        UserManager.createUserPasswordHash (CSecurity.USER_USER_PASSWORD),
                        CSecurity.USER_USER_NAME,
                        null,
                        null));
    _addUser (new User (CSecurity.USER_GUEST_ID,
                        CSecurity.USER_GUEST_EMAIL,
                        UserManager.createUserPasswordHash (CSecurity.USER_GUEST_PASSWORD),
                        CSecurity.USER_GUEST_NAME,
                        null,
                        null));
    return EChange.CHANGED;
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc) {
    for (final IMicroElement eUser : aDoc.getDocumentElement ().getChildElements ())
      _addUser (MicroTypeConverter.convertToNative (eUser, User.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData () {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("users");
    for (final User aUser : ContainerHelper.getSortedByKey (m_aUsers).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aUser, "user"));
    return aDoc;
  }

  private void _addUser (@Nonnull final User aUser) {
    final String sUserID = aUser.getID ();
    if (m_aUsers.containsKey (sUserID))
      throw new IllegalArgumentException ("User ID " + sUserID + " is already in use!");
    m_aUsers.put (sUserID, aUser);
  }

  @Nullable
  public IUser createNewUser (@Nonnull @Nonempty final String sEmailAddress,
                              @Nonnull @Nonempty final String sPlainTextPassword,
                              @Nullable final String sFirstName,
                              @Nullable final String sLastName,
                              @Nullable final Locale aDesiredLocale) {
    if (StringHelper.hasNoText (sEmailAddress))
      throw new IllegalArgumentException ("emailAddress");
    if (StringHelper.hasNoText (sPlainTextPassword))
      throw new IllegalArgumentException ("plainTextPassword");

    if (getUserOfEmailAddress (sEmailAddress) != null) {
      // Another user with this email address already exists
      return null;
    }

    // Create user
    final User aUser = new User (sEmailAddress,
                                 createUserPasswordHash (sPlainTextPassword),
                                 sFirstName,
                                 sLastName,
                                 aDesiredLocale);

    m_aRWLock.writeLock ().lock ();
    try {
      _addUser (aUser);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aUser;
  }

  public boolean containsUserWithID (@Nullable final String sUserID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aUsers.containsKey (sUserID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Private locked version returning the implementation class
   * 
   * @param sUserID
   *        The ID to be resolved
   * @return May be <code>null</code>
   */
  @Nullable
  private User _internalGetUserOfID (@Nullable final String sUserID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aUsers.get (sUserID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public IUser getUserOfID (@Nullable final String sUserID) {
    return _internalGetUserOfID (sUserID);
  }

  @Nullable
  public IUser getUserOfEmailAddress (@Nullable final String sEmailAddress) {
    if (StringHelper.hasNoText (sEmailAddress))
      return null;

    m_aRWLock.readLock ().lock ();
    try {
      for (final User aUser : m_aUsers.values ())
        if (aUser.getEmailAddress ().equals (sEmailAddress))
          return aUser;
      return null;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends IUser> getAllUsers () {
    m_aRWLock.readLock ().lock ();
    try {
      return ContainerHelper.newList (m_aUsers.values ());
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange deleteUser (@Nullable final String sUserID) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aUsers.remove (sUserID) == null)
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setUserData (@Nullable final String sUserID,
                              @Nullable final String sNewFirstName,
                              @Nullable final String sNewLastName,
                              @Nullable final Locale aNewDesiredLocale) {
    // Resolve user
    final User aUser = _internalGetUserOfID (sUserID);
    if (aUser == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      EChange eChange = aUser.setFirstName (sNewFirstName);
      eChange = eChange.or (aUser.setLastName (sNewLastName));
      eChange = eChange.or (aUser.setDesiredLocale (aNewDesiredLocale));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public boolean areUserIDAndPasswordValid (@Nullable final String sUserID, @Nullable final String sPlainTextPassword) {
    // No password is not allowed
    if (sPlainTextPassword == null)
      return false;

    // Is there such a user?
    final IUser aUser = getUserOfID (sUserID);
    if (aUser == null)
      return false;

    // Now compare the hashes
    return aUser.getPasswordHash ().equals (createUserPasswordHash (sPlainTextPassword));
  }

  /**
   * The one and only method to create a message digest hash from a password.
   * 
   * @param sPlainTextPassword
   *        Plain text password
   * @return The String representation of the password hash
   */
  @Nonnull
  @Nonempty
  public static String createUserPasswordHash (@Nonnull final String sPlainTextPassword) {
    if (sPlainTextPassword == null)
      throw new NullPointerException ("plainTextPassword");

    final byte [] aDigest = MessageDigestGeneratorHelper.getDigest (CSecurity.USER_PASSWORD_ALGO,
                                                                    sPlainTextPassword,
                                                                    CWebGUI.CHARSET);
    return MessageDigestGeneratorHelper.getHexValueFromDigest (aDigest);
  }
}

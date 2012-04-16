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

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;

/**
 * Interface for a user manager
 * 
 * @author philip
 */
public interface IUserManager {
  /**
   * Create a new user.
   * 
   * @param sEmailAddress
   *        The email address, to be used as the login name. May neither be
   *        <code>null</code> nor empty. This email address must be unique over
   *        all existing users.
   * @param sPlainTextPassword
   *        The plain text password to be used. May neither be <code>null</code>
   *        nor empty.
   * @param sFirstName
   *        The users first name. May be <code>null</code>.
   * @param sLastName
   *        The users last name. May be <code>null</code>.
   * @param aDesiredLocale
   *        The users default locale. May be <code>null</code>.
   * @return The created user or <code>null</code> if another user with the same
   *         email address is already present.
   */
  @Nullable
  IUser createNewUser (@Nonnull @Nonempty String sEmailAddress,
                       @Nonnull @Nonempty String sPlainTextPassword,
                       @Nullable String sFirstName,
                       @Nullable String sLastName,
                       @Nullable Locale aDesiredLocale);

  /**
   * Delete the user with the specified ID.
   * 
   * @param sUserID
   *        The ID of the user to delete
   * @return {@link EChange#CHANGED} if the user was deleted,
   *         {@link EChange#UNCHANGED} if no such user ID exists.
   */
  @Nonnull
  EChange deleteUser (@Nullable String sUserID);

  /**
   * Change the modifiable data of a user
   * 
   * @param sUserID
   *        The ID of the user to be modified. May be <code>null</code>.
   * @param sNewFirstName
   *        The new first name. May be <code>null</code>.
   * @param sNewLastName
   *        The new last name. May be <code>null</code>.
   * @param aNewDesiredLocale
   *        The new desired locale. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange setUserData (@Nullable String sUserID,
                       @Nullable String sNewFirstName,
                       @Nullable String sNewLastName,
                       @Nullable Locale aNewDesiredLocale);

  /**
   * Check if the passed combination of user ID and password matches.
   * 
   * @param sUserID
   *        The ID of the user
   * @param sPlainTextPassword
   *        The plan text password to validate.
   * @return <code>true</code> if the password hash matches the stored hash for
   *         the specified user, <code>false</code> otherwise.
   */
  boolean areUserIDAndPasswordValid (@Nullable String sUserID, @Nullable String sPlainTextPassword);

  /**
   * Check if a user with the specified ID is present.
   * 
   * @param sUserID
   *        The user ID to resolve. May be <code>null</code>.
   * @return <code>true</code> if such user exists, <code>false</code>
   *         otherwise.
   */
  boolean containsUserWithID (@Nullable String sUserID);

  /**
   * Get the user with the specified ID.
   * 
   * @param sUserID
   *        The user ID to resolve. May be <code>null</code>.
   * @return <code>null</code> if no such user exists
   */
  @Nullable
  IUser getUserOfID (@Nullable String sUserID);

  /**
   * Get the user with the specified email address
   * 
   * @param sEmailAddress
   *        The email address to be checked. May be <code>null</code>.
   * @return <code>null</code> if no such user exists
   */
  @Nullable
  IUser getUserOfEmailAddress (@Nullable String sEmailAddress);

  /**
   * @return A non-<code>null</code> collection of all contained users
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <? extends IUser> getAllUsers ();
}

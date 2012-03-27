package ap.peppol.webgui.security.user;

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
  boolean isUsernamePasswordValid (@Nullable String sUserID, @Nullable String sPlainTextPassword);

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
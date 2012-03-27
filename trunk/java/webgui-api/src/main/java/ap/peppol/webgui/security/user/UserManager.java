package ap.peppol.webgui.security.user;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import ap.peppol.webgui.api.AbstractManager;
import ap.peppol.webgui.api.CWebGUI;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.messagedigest.MessageDigestGeneratorHelper;
import com.phloc.commons.state.EChange;

/**
 * This class manages the available users.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserManager extends AbstractManager implements IUserManager {
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, User> m_aUsers = new HashMap <String, User> ();

  public UserManager () {}

  /**
   * Create a new user.
   * 
   * @param sEmailAddress
   *        The email address, to be used as the login name. May neither be
   *        <code>null</code> nor empty.
   * @param sPlainTextPassword
   *        The plain text password to be used. May neither be <code>null</code>
   *        nor empty.
   * @param sFirstName
   *        The users first name. May be <code>null</code>.
   * @param sLastName
   *        The users last name. May be <code>null</code>.
   * @param aDesiredLocale
   *        The users default locale. May be <code>null</code>.
   * @return The created user and never <code>null</code>.
   */
  @Nonnull
  public IUser createNewUser (@Nonnull @Nonempty final String sEmailAddress,
                              @Nonnull @Nonempty final String sPlainTextPassword,
                              @Nullable final String sFirstName,
                              @Nullable final String sLastName,
                              @Nullable final Locale aDesiredLocale) {
    // Create user
    final User aUser = new User (sEmailAddress, createUserPasswordHash (sPlainTextPassword));
    aUser.setFirstName (sFirstName);
    aUser.setLastName (sLastName);
    aUser.setDesiredLocale (aDesiredLocale);

    m_aRWLock.writeLock ().lock ();
    try {
      // Store in memory
      m_aUsers.put (aUser.getID (), aUser);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aUser;
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

  public boolean isUsernamePasswordValid (@Nullable final String sUserID, @Nullable final String sPlainTextPassword) {
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

    final byte [] aDigest = MessageDigestGeneratorHelper.getDigest (CUser.USER_PASSWORD_ALGO,
                                                                    sPlainTextPassword,
                                                                    CWebGUI.CHARSET);
    return MessageDigestGeneratorHelper.getHexValueFromDigest (aDigest);
  }
}

package ap.peppol.webgui.api.user;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import ap.peppol.webgui.api.CWebGUI;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.messagedigest.MessageDigestGeneratorHelper;
import com.phloc.commons.string.StringHelper;

/**
 * This class manages the available users.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserManager {
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, User> m_aUsers = new HashMap <String, User> ();

  public UserManager () {}

  @Nonnull
  public IUser createNewUser (@Nonnull @Nonempty final String sEmailAddress,
                              @Nullable final String sFirstName,
                              @Nullable final String sLastName,
                              @Nonnull final String sPlainTextPassword,
                              @Nullable final Locale aDesiredLocale) {
    if (StringHelper.hasNoText (sEmailAddress))
      throw new IllegalArgumentException ("emailAddress");
    if (sPlainTextPassword == null)
      throw new NullPointerException ("plainTextPassword");

    // Create user
    final User aUser = new User ();
    aUser.setEmailAddress (sEmailAddress);
    aUser.setFirstName (sFirstName);
    aUser.setLastName (sLastName);
    aUser.setDesiredLocale (aDesiredLocale);
    aUser.setPasswordHash (createUserPasswordHash (sPlainTextPassword));

    m_aRWLock.writeLock ().lock ();
    try {
      // Store in memory
      m_aUsers.put (aUser.getID (), aUser);
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aUser;
  }

  @Nullable
  public IUser getUserOfID (@Nullable final String sUserID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aUsers.get (sUserID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
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
    final byte [] aDigest = MessageDigestGeneratorHelper.getDigest (CUser.USER_PASSWORD_ALGO,
                                                                    sPlainTextPassword,
                                                                    CWebGUI.CHARSET);
    return MessageDigestGeneratorHelper.getHexValueFromDigest (aDigest);
  }
}

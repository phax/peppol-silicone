package ap.peppol.webgui.security.user;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.string.StringHelper;

/**
 * This class represents a single user in the system.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class User implements IUser {
  private final String m_sID;
  private final String m_sEmailAddress;
  private final String m_sPasswordHash;
  private String m_sFirstName;
  private String m_sLastName;
  private Locale m_aDesiredLocale;

  /**
   * Create a new user
   * 
   * @param sEmailAddress
   *        Email address of the user. May neither be <code>null</code> nor
   *        empty.
   * @param sPasswordHash
   *        Password hash of the user. May neither be <code>null</code> nor
   *        empty.
   */
  public User (@Nonnull @Nonempty final String sEmailAddress, @Nonnull @Nonempty final String sPasswordHash) {
    this (GlobalIDFactory.getNewPersistentStringID (), sEmailAddress, sPasswordHash);
  }

  /**
   * Create an existing user
   * 
   * @param sID
   *        user ID
   * @param sEmailAddress
   *        Email address of the user. May neither be <code>null</code> nor
   *        empty.
   * @param sPasswordHash
   *        Password hash of the user. May neither be <code>null</code> nor
   *        empty.
   */
  public User (@Nonnull @Nonempty final String sID,
               @Nonnull @Nonempty final String sEmailAddress,
               @Nonnull @Nonempty final String sPasswordHash) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sEmailAddress))
      throw new IllegalArgumentException ("emailAddress");
    if (StringHelper.hasNoText (sPasswordHash))
      throw new IllegalArgumentException ("passwordHash");
    m_sID = sID;
    m_sEmailAddress = sEmailAddress;
    m_sPasswordHash = sPasswordHash;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getEmailAddress () {
    return m_sEmailAddress;
  }

  @Nonnull
  @Nonempty
  public String getPasswordHash () {
    return m_sPasswordHash;
  }

  @Nullable
  public String getFirstName () {
    return m_sFirstName;
  }

  public void setFirstName (@Nullable final String sFirstName) {
    m_sFirstName = sFirstName;
  }

  @Nullable
  public String getLastName () {
    return m_sLastName;
  }

  public void setLastName (@Nullable final String sLastName) {
    m_sLastName = sLastName;
  }

  @Nullable
  public Locale getDesiredLocale () {
    return m_aDesiredLocale;
  }

  public void setDesiredLocale (@Nullable final Locale aDesiredLocale) {
    m_aDesiredLocale = aDesiredLocale;
  }

  @Nonnull
  public String getDisplayName () {
    return StringHelper.concatenateOnDemand (m_sFirstName, " ", m_sLastName);
  }
}

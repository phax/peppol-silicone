package ap.peppol.webgui.security.user;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IUser} interface.
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
   * @param sFirstName
   *        The first name. May be <code>null</code>.
   * @param sLastName
   *        The last name. May be <code>null</code>.
   * @param aDesiredLocale
   *        The desired locale. May be <code>null</code>.
   */
  public User (@Nonnull @Nonempty final String sEmailAddress,
               @Nonnull @Nonempty final String sPasswordHash,
               @Nullable final String sFirstName,
               @Nullable final String sLastName,
               @Nullable final Locale aDesiredLocale) {
    this (GlobalIDFactory.getNewPersistentStringID (),
          sEmailAddress,
          sPasswordHash,
          sFirstName,
          sLastName,
          aDesiredLocale);
  }

  /**
   * For deserialization only.
   * 
   * @param sID
   *        user ID
   * @param sEmailAddress
   *        Email address of the user. May neither be <code>null</code> nor
   *        empty.
   * @param sPasswordHash
   *        Password hash of the user. May neither be <code>null</code> nor
   *        empty.
   * @param sFirstName
   *        The first name. May be <code>null</code>.
   * @param sLastName
   *        The last name. May be <code>null</code>.
   * @param aDesiredLocale
   *        The desired locale. May be <code>null</code>.
   */
  User (@Nonnull @Nonempty final String sID,
        @Nonnull @Nonempty final String sEmailAddress,
        @Nonnull @Nonempty final String sPasswordHash,
        @Nullable final String sFirstName,
        @Nullable final String sLastName,
        @Nullable final Locale aDesiredLocale) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sEmailAddress))
      throw new IllegalArgumentException ("emailAddress");
    if (StringHelper.hasNoText (sPasswordHash))
      throw new IllegalArgumentException ("passwordHash");
    m_sID = sID;
    m_sEmailAddress = sEmailAddress;
    m_sPasswordHash = sPasswordHash;
    m_sFirstName = sFirstName;
    m_sLastName = sLastName;
    m_aDesiredLocale = aDesiredLocale;
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

  @Nonnull
  EChange setFirstName (@Nullable final String sFirstName) {
    if (EqualsUtils.nullSafeEquals (sFirstName, m_sFirstName))
      return EChange.UNCHANGED;
    m_sFirstName = sFirstName;
    return EChange.CHANGED;
  }

  @Nullable
  public String getLastName () {
    return m_sLastName;
  }

  @Nonnull
  EChange setLastName (@Nullable final String sLastName) {
    if (EqualsUtils.nullSafeEquals (sLastName, m_sLastName))
      return EChange.UNCHANGED;
    m_sLastName = sLastName;
    return EChange.CHANGED;
  }

  @Nullable
  public Locale getDesiredLocale () {
    return m_aDesiredLocale;
  }

  @Nonnull
  EChange setDesiredLocale (@Nullable final Locale aDesiredLocale) {
    if (EqualsUtils.nullSafeEquals (aDesiredLocale, m_aDesiredLocale))
      return EChange.UNCHANGED;
    m_aDesiredLocale = aDesiredLocale;
    return EChange.CHANGED;
  }

  @Nonnull
  public String getDisplayName () {
    return StringHelper.concatenateOnDemand (m_sFirstName, " ", m_sLastName);
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof User))
      return false;
    final User rhs = (User) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("ID", m_sID)
                                       .append ("emailAddress", m_sEmailAddress)
                                       .append ("passwordHash", m_sPasswordHash)
                                       .append ("firstName", m_sFirstName)
                                       .append ("lastName", m_sLastName)
                                       .append ("desiredLocale", m_aDesiredLocale)
                                       .toString ();
  }
}

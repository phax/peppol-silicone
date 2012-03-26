package ap.peppol.webgui.api.user;

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
  private String m_sEmailAddress;
  private String m_sFirstName;
  private String m_sLastName;
  private Locale m_aDesiredLocale;
  private String m_sPasswordHash;

  /**
   * Create a new user
   */
  public User () {
    this (GlobalIDFactory.getNewPersistentStringID ());
  }

  /**
   * Create an existing user
   * 
   * @param sID
   *        user ID
   */
  public User (@Nonnull @Nonempty final String sID) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nullable
  public String getEmailAddress () {
    return m_sEmailAddress;
  }

  public void setEmailAddress (@Nullable final String sEmailAddress) {
    m_sEmailAddress = sEmailAddress;
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

  @Nullable
  public String getPasswordHash () {
    return m_sPasswordHash;
  }

  public void setPasswordHash (@Nullable final String sPasswordHash) {
    m_sPasswordHash = sPasswordHash;
  }
}

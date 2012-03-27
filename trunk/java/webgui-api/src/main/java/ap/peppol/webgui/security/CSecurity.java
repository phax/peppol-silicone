package ap.peppol.webgui.security;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.messagedigest.EMessageDigestAlgorithm;

/**
 * Constants for user handling
 * 
 * @author philip
 */
@Immutable
public final class CSecurity {
  /** Hashing algorithm to use */
  public static final EMessageDigestAlgorithm USER_PASSWORD_ALGO = EMessageDigestAlgorithm.SHA_512;

  // Default users

  // Default roles
  public static final String ROLE_NAME_ADMINISTRATOR = "Administrator";
  public static final String ROLE_NAME_USER = "User";

  // Default user groups
  public static final String USERGROUP_NAME_ADMINISTRATORS = "Administrators";
  public static final String USERGROUP_NAME_USERS = "Users";
  public static final String USERGROUP_NAME_GUESTS = "Guests";

  private CSecurity () {}
}

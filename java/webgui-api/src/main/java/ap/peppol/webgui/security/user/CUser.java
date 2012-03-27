package ap.peppol.webgui.security.user;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.messagedigest.EMessageDigestAlgorithm;

/**
 * Constants for user handling
 * 
 * @author philip
 */
@Immutable
public final class CUser {
  /** Hashing algorithm to use */
  public static final EMessageDigestAlgorithm USER_PASSWORD_ALGO = EMessageDigestAlgorithm.SHA_512;

  private CUser () {}
}
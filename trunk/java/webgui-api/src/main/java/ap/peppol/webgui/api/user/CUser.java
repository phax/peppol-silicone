package ap.peppol.webgui.api.user;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.messagedigest.EMessageDigestAlgorithm;

@Immutable
public final class CUser {
  /** Hashing algorithm to use */
  public static final EMessageDigestAlgorithm USER_PASSWORD_ALGO = EMessageDigestAlgorithm.SHA_512;

  private CUser () {}
}

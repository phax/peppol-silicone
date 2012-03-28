package ap.peppol.webgui.security.login;

import com.phloc.commons.state.ISuccessIndicator;

/**
 * Represents the different login results.
 * 
 * @author philip
 */
public enum ELoginResult implements ISuccessIndicator {
  SUCCESS,
  USER_NOT_EXISTING,
  INVALID_PASSWORD,
  USER_ALREADY_LOGGED_IN;

  public boolean isSuccess () {
    return this == SUCCESS;
  }

  public boolean isFailure () {
    return this != SUCCESS;
  }
}

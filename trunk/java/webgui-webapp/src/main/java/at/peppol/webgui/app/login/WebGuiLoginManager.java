package at.peppol.webgui.app.login;

import com.phloc.webbasics.app.html.IHTMLProvider;
import com.phloc.webbasics.app.security.LoginManager;

public class WebGuiLoginManager extends LoginManager {
  @Override
  protected IHTMLProvider createLoginScreen (final boolean bLoginError) {
    return new WebGuiLoginHTML (bLoginError);
  }
}

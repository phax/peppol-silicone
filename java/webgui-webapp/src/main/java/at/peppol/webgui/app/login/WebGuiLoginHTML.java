package at.peppol.webgui.app.login;

import java.util.Locale;

import com.phloc.commons.url.SimpleURL;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.api.EHCButtonType;
import com.phloc.html.hc.html.HCDiv;
import com.phloc.html.hc.html.HCEdit;
import com.phloc.html.hc.html.HCForm;
import com.phloc.html.hc.impl.HCEntityNode;
import com.phloc.html.hc.impl.HCNodeList;
import com.phloc.webbasics.EWebBasicsText;
import com.phloc.webbasics.app.security.BasicLoginHTML;
import com.phloc.webbasics.ui.bootstrap.BootstrapAlert;
import com.phloc.webbasics.ui.bootstrap.BootstrapButton;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentRow;
import com.phloc.webbasics.ui.bootstrap.CBootstrapCSS;
import com.phloc.webbasics.ui.bootstrap.EBootstrapAlertType;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonSize;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonType;
import com.phloc.webbasics.ui.bootstrap.EBootstrapSpan;

/**
 * Contains the HTML representation of the Login screen. Only to be called from
 * within {@link WebGuiLoginManager}.
 * 
 * @author philip
 */
final class WebGuiLoginHTML extends BasicLoginHTML {
  public WebGuiLoginHTML (final boolean bLoginError) {
    super (bLoginError);
  }

  @Override
  protected IHCNode getLoginScreen (final Locale aDisplayLocale) {
    final HCNodeList aContent = new HCNodeList ();
    aContent.addChild (new HCDiv ().addClass (CSS_CLASS_LOGIN_APPLOGO));
    if (showLoginError ())
      aContent.addChild (new BootstrapAlert (EWebBasicsText.LOGIN_ERROR_MSG.getDisplayText (aDisplayLocale)).setType (EBootstrapAlertType.ERROR));

    // Start the login form
    final HCForm aForm = aContent.addAndReturnChild (new HCForm (new SimpleURL ()));
    aForm.setSubmitPressingEnter (true);
    aForm.addClasses (CBootstrapCSS.FORM_HORIZONTAL, CBootstrapCSS.FORM_INLINE);
    aForm.addChild (new HCEdit (REQUEST_ATTR_USERID).setPlaceholder (EWebBasicsText.LOGIN_FIELD_USERNAME.getDisplayText (aDisplayLocale)));
    aForm.addChild (new HCEdit (REQUEST_ATTR_PASSWORD).setPlaceholder (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale)));

    // Submit button
    aForm.addChild (new BootstrapButton ().setType (EBootstrapButtonType.PRIMARY)
                                          .setSize (EBootstrapButtonSize.LARGE)
                                          .addChild (EWebBasicsText.LOGIN_BUTTON_SUBMIT.getDisplayText (aDisplayLocale))
                                          .setType (EHCButtonType.SUBMIT));

    // Centre content
    return new BootstrapContentRow ().addColumn (EBootstrapSpan.SPAN3, HCEntityNode.newNBSP ())
                                     .addColumn (EBootstrapSpan.SPAN6, aContent)
                                     .addColumn (EBootstrapSpan.SPAN3, HCEntityNode.newNBSP ());
  }
}

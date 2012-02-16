package at.peppol.webgui.app.login;

import java.util.Locale;

import com.phloc.commons.url.SimpleURL;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.api.EHCButtonType;
import com.phloc.html.hc.html.HCDiv;
import com.phloc.html.hc.html.HCEdit;
import com.phloc.html.hc.html.HCForm;
import com.phloc.html.hc.impl.HCEntityNode;
import com.phloc.webbasics.EWebBasicsText;
import com.phloc.webbasics.app.security.BasicLoginHTML;
import com.phloc.webbasics.ui.bootstrap.BootstrapButton;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentRow;
import com.phloc.webbasics.ui.bootstrap.CBootstrapCSS;
import com.phloc.webbasics.ui.bootstrap.EBootstrapSpan;

public class WebGuiLoginHTML extends BasicLoginHTML {
  public WebGuiLoginHTML (final boolean bLoginError) {
    super (bLoginError);
  }

  @Override
  protected IHCNode getLoginScreen (final Locale aDisplayLocale) {
    final HCForm aForm = new HCForm (new SimpleURL ());
    aForm.setSubmitPressingEnter (true);

    aForm.addChild (new HCDiv ().addClass (CSS_CLASS_LOGIN_APPLOGO));
    if (showLoginError ())
      aForm.addChild (new HCDiv (EWebBasicsText.LOGIN_ERROR_MSG.getDisplayText (aDisplayLocale)).addClass (CSS_CLASS_LOGIN_ERRORMSG));

    aForm.addClasses (CBootstrapCSS.FORM_HORIZONTAL, CBootstrapCSS.FORM_INLINE);
    aForm.addChild (new HCEdit (REQUEST_ATTR_USERID).setPlaceholder (EWebBasicsText.LOGIN_FIELD_USERNAME.getDisplayText (aDisplayLocale)));
    aForm.addChild (new HCEdit (REQUEST_ATTR_PASSWORD).setPlaceholder (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale)));

    // Submit button
    aForm.addChild (new BootstrapButton ().addChild (EWebBasicsText.LOGIN_BUTTON_SUBMIT.getDisplayText (aDisplayLocale))
                                          .setType (EHCButtonType.SUBMIT));

    return new BootstrapContentRow ().addColumn (EBootstrapSpan.SPAN3, HCEntityNode.newNBSP ())
                                     .addColumn (EBootstrapSpan.SPAN6, aForm)
                                     .addColumn (EBootstrapSpan.SPAN3, HCEntityNode.newNBSP ());
  }
}

/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.webgui.app.login;

import java.util.Locale;

import com.phloc.commons.url.SimpleURL;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.api.EHCButtonType;
import com.phloc.html.hc.html.HCDiv;
import com.phloc.html.hc.html.HCEdit;
import com.phloc.html.hc.html.HCEditPassword;
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
    aForm.addChild (new HCEditPassword (REQUEST_ATTR_PASSWORD).setPlaceholder (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale)));

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

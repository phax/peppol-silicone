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
package at.peppol.webgui.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.phloc.html.entities.EHTMLEntity;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCH1;
import com.phloc.html.hc.html.HCP;
import com.phloc.html.hc.impl.HCEntityNode;
import com.phloc.html.hc.impl.HCNodeList;
import com.phloc.html.hc.impl.HCTextNode;
import com.phloc.webbasics.app.LinkUtils;
import com.phloc.webbasics.app.page.AbstractPage;
import com.phloc.webbasics.ui.bootstrap.BootstrapHeroUnit;
import com.phloc.webbasics.ui.bootstrap.BootstrapLinkButton;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonSize;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonType;

public final class PageHome extends AbstractPage {
  public PageHome (final String sID) {
    super (sID, "Home");
  }

  public IHCNode getContent (@Nonnull final Locale aDisplayLocale) {
    final HCNodeList ret = new HCNodeList ();
    ret.addChild (new BootstrapHeroUnit (new HCH1 ("PEPPOL Post Award Web GUI"),
                                         new HCP ("This is a template for a simple marketing or informational website. It includes a large callout called the hero unit and three supporting pieces of content. Use it as a starting point to create something more unique."),
                                         new BootstrapLinkButton (LinkUtils.getHomeLink ()).setType (EBootstrapButtonType.PRIMARY)
                                                                                           .setSize (EBootstrapButtonSize.LARGE)
                                                                                           .addChildren (new HCTextNode ("Learn more "),
                                                                                                         new HCEntityNode (EHTMLEntity.raquo,
                                                                                                                           " "))));
    return ret;
  }
}

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
package at.peppol.webgui.app.layout;

import java.util.Locale;

import javax.annotation.Nonnull;

import at.peppol.webgui.app.CWebGui;

import com.phloc.css.CCSS;
import com.phloc.css.property.CCSSProperties;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCA;
import com.phloc.html.hc.html.HCA_Target;
import com.phloc.html.hc.html.HCImg;
import com.phloc.webbasics.app.LinkUtils;
import com.phloc.webbasics.app.RequestManager;
import com.phloc.webbasics.app.menu.IMenuItem;
import com.phloc.webbasics.app.page.IPage;
import com.phloc.webbasics.app.page.system.SystemPageNotFound;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentRow;
import com.phloc.webbasics.ui.bootstrap.BootstrapNav;
import com.phloc.webbasics.ui.bootstrap.BootstrapSidebarNav;
import com.phloc.webbasics.ui.bootstrap.EBootstrapSpan;

/**
 * Responsible class for creating the footer
 * 
 * @author philip
 */
final class Content {
  private Content () {}

  @Nonnull
  public static IHCNode createContent (final Locale aDisplayLocale) {
    final BootstrapNav aNav = new BootstrapNav ();
    aNav.addHeader ("Home", LinkUtils.getHomeLink (), true);
    aNav.addHeader ("Inbox");
    aNav.addItem ("Catalogues", LinkUtils.getHomeLink ());
    aNav.addItem ("Orders", LinkUtils.getHomeLink ());
    aNav.addItem ("Invoices (1)", LinkUtils.getHomeLink ());
    aNav.addHeader ("Drafts");
    aNav.addItem ("Catalogues", LinkUtils.getHomeLink ());
    aNav.addItem ("Orders", LinkUtils.getHomeLink ());
    aNav.addItem ("Invoices", LinkUtils.getHomeLink ());
    aNav.addHeader ("Outbox");
    aNav.addItem ("Catalogues", LinkUtils.getHomeLink ());
    aNav.addItem ("Orders", LinkUtils.getHomeLink ());
    aNav.addItem ("Invoices", LinkUtils.getHomeLink ());
    aNav.addHeader ("Settings");
    aNav.addItem ("My profile", LinkUtils.getHomeLink ());
    aNav.addItem ("Customers", LinkUtils.getHomeLink ());
    aNav.addItem ("Suppliers", LinkUtils.getHomeLink ());
    aNav.addItem (new HCA (CWebGui.URL_PEPPOL).setTarget (HCA_Target.BLANK)
                                              .addStyle (CCSSProperties.MARGIN_TOP.newValue (CCSS.em (3)))
                                              .addChild (new HCImg (LinkUtils.getContextAwareURI ("img/peppol_logo.png"))));
    final BootstrapSidebarNav aSidebar = new BootstrapSidebarNav ().setNav (aNav);

    // Get the requested menu item
    final IMenuItem aSelectedMenuItem = RequestManager.getRequestMenuItem ();

    // Resolve the page of the selected menu item (if found)
    final IPage aDisplayPage = aSelectedMenuItem != null ? aSelectedMenuItem.getPage ()
                                                        : SystemPageNotFound.getInstance ();

    // Build the main content row with sidebar and content area
    return new BootstrapContentRow ().addColumn (EBootstrapSpan.SPAN2, aSidebar)
                                     .addColumn (EBootstrapSpan.SPAN10, aDisplayPage.getContent (aDisplayLocale));
  }
}

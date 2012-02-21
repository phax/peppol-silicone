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

import javax.annotation.concurrent.Immutable;

import at.peppol.webgui.app.CWebGui;

import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCA;
import com.phloc.html.hc.html.HCP;
import com.phloc.webbasics.app.LinkUtils;
import com.phloc.webbasics.app.html.IAreaContentProvider;
import com.phloc.webbasics.app.html.LayoutManager;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentLayout;
import com.phloc.webbasics.ui.bootstrap.BootstrapNav;
import com.phloc.webbasics.ui.bootstrap.BootstrapNavbar;

@Immutable
public final class DefaultLayout {
  private DefaultLayout () {}

  public static void createDefaultLayout () {
    // 1. Navbar
    LayoutManager.registerAreaContentProvider ("navbar", new IAreaContentProvider () {
      public IHCNode getContent (final Locale aDisplayLocale) {
        final BootstrapNav aNav = new BootstrapNav ();
        aNav.addItem ("Home", LinkUtils.getHomeLink (), true);
        /* TODO start change */
        aNav.addItem ("About", LinkUtils.getHomeLink (), false);
        aNav.addItem ("Contact", LinkUtils.getHomeLink (), false);
        /* end change */
        aNav.addItem ("Logout", LinkUtils.getServletURL (CWebGui.SERVLET_LOGOUT), false);

        // Build navbar
        final BootstrapNavbar aNavBar = new BootstrapNavbar (true);
        aNavBar.setBrand (CWebGui.WEBGUI_PRODUCTNAME, LinkUtils.getHomeLink ());
        aNavBar.setNav (aNav);
        aNavBar.addTextContent (new HCP ("Logged in as ").addChild (new HCA (LinkUtils.getHomeLink ()).addChild ("Philip")));
        return aNavBar;
      }
    });

    // 2. Rest
    LayoutManager.registerAreaContentProvider ("rest", new IAreaContentProvider () {
      public IHCNode getContent (final Locale aDisplayLocale) {
        // Content and footer
        return new BootstrapContentLayout ().setContent (Content.createContent (aDisplayLocale))
                                            .setFooter (Footer.createFooter ());
      }
    });
  }
}
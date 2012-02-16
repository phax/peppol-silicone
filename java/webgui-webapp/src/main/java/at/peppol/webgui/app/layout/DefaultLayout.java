package at.peppol.webgui.app.layout;

import java.util.Locale;

import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCA;
import com.phloc.html.hc.html.HCP;
import com.phloc.webbasics.app.LinkUtils;
import com.phloc.webbasics.app.html.IAreaContentProvider;
import com.phloc.webbasics.app.html.LayoutManager;
import com.phloc.webbasics.ui.bootstrap.BootstrapNav;
import com.phloc.webbasics.ui.bootstrap.BootstrapNavbar;

public class DefaultLayout {
  public static void createDefaultLayout () {
    LayoutManager.registerAreaContentProvider ("navbar", new IAreaContentProvider () {
      public IHCNode getContent (final Locale aDisplayLocale) {
        final BootstrapNav aNav = new BootstrapNav ();
        aNav.addItem ("Home", LinkUtils.getHomeLink (), false);
        aNav.addItem ("About", LinkUtils.getHomeLink (), false);
        aNav.addItem ("Contact", LinkUtils.getHomeLink (), false);

        final BootstrapNavbar x = new BootstrapNavbar (true);
        x.setBrand ("PAWG", LinkUtils.getHomeLink ());
        x.setNav (aNav);
        x.addTextContent (new HCP ("Logged in as ").addChild (new HCA (LinkUtils.getHomeLink ()).addChild ("Philip")));
        return x;
      }
    });
    LayoutManager.registerAreaContentProvider ("all", new IAreaContentProvider () {
      public IHCNode getContent (final Locale aDisplayLocale) {
        return null;
      }
    });
  }
}

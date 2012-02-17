package at.peppol.webgui.app.layout;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import at.peppol.webgui.app.CWebGui;

import com.phloc.css.CCSS;
import com.phloc.css.property.CCSSProperties;
import com.phloc.html.entities.EHTMLEntity;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCA;
import com.phloc.html.hc.html.HCA_Target;
import com.phloc.html.hc.html.HCH1;
import com.phloc.html.hc.html.HCImg;
import com.phloc.html.hc.html.HCP;
import com.phloc.html.hc.impl.HCEntityNode;
import com.phloc.html.hc.impl.HCNodeList;
import com.phloc.html.hc.impl.HCTextNode;
import com.phloc.webbasics.app.LinkUtils;
import com.phloc.webbasics.app.html.IAreaContentProvider;
import com.phloc.webbasics.app.html.LayoutManager;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentLayout;
import com.phloc.webbasics.ui.bootstrap.BootstrapContentRow;
import com.phloc.webbasics.ui.bootstrap.BootstrapHeroUnit;
import com.phloc.webbasics.ui.bootstrap.BootstrapLinkButton;
import com.phloc.webbasics.ui.bootstrap.BootstrapNav;
import com.phloc.webbasics.ui.bootstrap.BootstrapNavbar;
import com.phloc.webbasics.ui.bootstrap.BootstrapSidebarNav;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonSize;
import com.phloc.webbasics.ui.bootstrap.EBootstrapButtonType;
import com.phloc.webbasics.ui.bootstrap.EBootstrapSpan;

@Immutable
public final class DefaultLayout {
  private DefaultLayout () {}

  public static void createDefaultLayout () {
    // 1. Navbar
    LayoutManager.registerAreaContentProvider ("navbar", new IAreaContentProvider () {
      public IHCNode getContent (final Locale aDisplayLocale) {
        final BootstrapNav aNav = new BootstrapNav ();
        aNav.addItem ("Home", LinkUtils.getHomeLink (), true);
        aNav.addItem ("About", LinkUtils.getHomeLink (), false);
        aNav.addItem ("Contact", LinkUtils.getHomeLink (), false);
        aNav.addItem ("Logout", LinkUtils.getServletURL ("/logout"), false);

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
        final HCNodeList aContent = new HCNodeList ();
        aContent.addChild (new BootstrapHeroUnit (new HCH1 ("PEPPOL Post Award Web GUI"),
                                                  new HCP ("This is a template for a simple marketing or informational website. It includes a large callout called the hero unit and three supporting pieces of content. Use it as a starting point to create something more unique."),
                                                  new BootstrapLinkButton (LinkUtils.getHomeLink ()).setType (EBootstrapButtonType.PRIMARY)
                                                                                                    .setSize (EBootstrapButtonSize.LARGE)
                                                                                                    .addChildren (new HCTextNode ("Learn more "),
                                                                                                                  new HCEntityNode (EHTMLEntity.raquo,
                                                                                                                                    " "))));

        // Build the main content row with sidebar and content area
        final BootstrapContentRow aContentRow = new BootstrapContentRow ().addColumn (EBootstrapSpan.SPAN2, aSidebar)
                                                                          .addColumn (EBootstrapSpan.SPAN10, aContent);
        return new BootstrapContentLayout ().setContent (aContentRow).setFooter (Footer.createFooter ());
      }
    });
  }
}

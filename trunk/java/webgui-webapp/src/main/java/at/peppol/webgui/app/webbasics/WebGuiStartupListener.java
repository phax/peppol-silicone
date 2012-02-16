package at.peppol.webgui.app.webbasics;

import at.peppol.webgui.app.layout.DefaultLayout;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.webbasics.app.WebLocaleManager;
import com.phloc.webbasics.spi.IApplicationStartupListenerSPI;

/**
 * SPI implementation of the startup listener.
 * 
 * @author philip
 */
@IsSPIImplementation
public final class WebGuiStartupListener implements IApplicationStartupListenerSPI {

  private static void _createMenu () {}

  /**
   * Called once when the web application starts up.
   */
  public void onStartup () {
    // Setup locales
    WebLocaleManager.registerLocale (LocaleCache.get ("en", "GB"));
    WebLocaleManager.setDefaultLocale (LocaleCache.get ("en", "GB"));

    // Create the application layouts
    DefaultLayout.createDefaultLayout ();

    // Create all menu items
    _createMenu ();
  }
}

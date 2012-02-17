package at.peppol.webgui.app;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.url.ISimpleURL;
import com.phloc.commons.url.ReadonlySimpleURL;

/**
 * Contains application constants.
 * 
 * @author philip
 */
@Immutable
public final class CWebGui {
  public static final String WEBGUI_PRODUCTNAME = "PAWG";

  /** Link to the PEPPOL web site */
  public static final ISimpleURL URL_PEPPOL = new ReadonlySimpleURL ("http://www.peppol.eu");

  private CWebGui () {}
}

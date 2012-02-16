package at.peppol.webgui.app.webbasics;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.webbasics.spi.IApplicationRequestListenerSPI;

/**
 * SPI implementation to handle begin and end of each request.
 * 
 * @author philip
 */
@IsSPIImplementation
public final class WebGuiRequestListener implements IApplicationRequestListenerSPI {
  @UsedViaReflection
  public WebGuiRequestListener () {}

  /**
   * Called before a request is executed.
   */
  public void onRequestBegin () {}

  /**
   * Called after a request has been finished, but before the response is sent.
   */
  public void onRequestEnd () {}
}

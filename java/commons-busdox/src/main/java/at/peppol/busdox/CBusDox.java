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
package at.peppol.busdox;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.ws.WebServiceClient;

import org.w3._2009._02.ws_tra.AccessPointService;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.exceptions.InitializationException;

/**
 * Constants for this BusDox implementation.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CBusDox {
  /**
   * The path to the START WSDL file within the class path. Must be a constant
   * String and cannot be determined from {@link AccessPointService} class
   * because only constant strings can be used as annotation parameter values.
   */
  @Nonnull
  @Nonempty
  public static final String START_WSDL_PATH = "WEB-INF/wsdl/peppol-start-2.0.wsdl";

  @Nonnull
  @Nonempty
  public static final String START_WSDL_RESOURCE = "/" + START_WSDL_PATH;

  /** The sample path contained in the START WSDL file */
  @Deprecated
  public static final String START_WSDL_SAMPLE_PORT_ADDRESS = "https://host:port/serviceName";

  static {
    // Sanity check
    final String sFoundPath = AccessPointService.class.getAnnotation (WebServiceClient.class).wsdlLocation ();
    if (!START_WSDL_RESOURCE.equals (sFoundPath))
      throw new InitializationException ("START WSDL path is incorrect! Expected " +
                                         START_WSDL_RESOURCE +
                                         " but found " +
                                         sFoundPath);
  }

  private CBusDox () {}

  public static void setMetroDebugSystemProperties (final boolean bDebug) {
    // Depending on the used JAX-WS version, the property names are
    // different....
    enableSoapLogging (bDebug);

    System.setProperty ("com.sun.xml.ws.transport.http.HttpAdapter.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", Boolean.toString (bDebug));

    if (bDebug)
      System.clearProperty ("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace");
    else
      System.setProperty ("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");

    System.setProperty ("com.sun.metro.soap.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.wss.jaxws.impl.SecurityServerTube.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.wss.jaxws.impl.SecurityClientTube.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", Boolean.toString (bDebug));
  }

  public static void enableSoapLogging (final boolean bDebug) {
    System.setProperty ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", Boolean.toString (bDebug));
    System.setProperty ("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
                        Boolean.toString (bDebug));
  }
}

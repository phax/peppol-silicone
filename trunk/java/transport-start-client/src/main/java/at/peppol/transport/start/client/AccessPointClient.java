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
package at.peppol.transport.start.client;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.AccessPointService;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.Document;

import at.peppol.commons.utils.HostnameVerifierAlwaysTrue;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadataHelper;
import at.peppol.transport.cert.AccessPointX509TrustManager;

import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.state.ESuccess;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.WSBindingProvider;


/**
 * The accesspointClient class aims to hold all the processes required for
 * consuming an AccessPoint.
 * 
 * @author Dante Malaga(dante@alfa1lab.com) Jose Gorvenia<br>
 *         Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class AccessPointClient {
  /** String that represents the SSL security provided. */
  public static final String SECURITY_PROVIDER = "SSL";

  /** Logger to follow this class behavior. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessPointClient.class);

  private AccessPointClient () {}

  /**
   * Sets up the certficate TrustManager.
   */
  @Nonnull
  private static ESuccess _setupCertificateTrustManager () {
    try {
      final TrustManager [] aTrustManagers = new TrustManager [] { new AccessPointX509TrustManager (null, null) };
      final SSLContext aSSLContext = SSLContext.getInstance (SECURITY_PROVIDER);
      aSSLContext.init (null, aTrustManagers, VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLContext.getSocketFactory ());
      return ESuccess.SUCCESS;
    }
    catch (final Exception e) {
      s_aLogger.error ("Error setting the Certificate Trust Manager.", e);
      return ESuccess.FAILURE;
    }
  }

  /**
   * Configures and returns a port that points to the a specific endpoint
   * address.
   * 
   * @param sAddress
   *        the address of the webservice.
   * @return the port.
   */
  @Nullable
  public static Resource createPort (final String sAddress) {
    try {
      HttpsURLConnection.setDefaultHostnameVerifier (new HostnameVerifierAlwaysTrue ());
      s_aLogger.debug (">> Set HostVerifier");
      _setupCertificateTrustManager ();
      s_aLogger.debug (">> Set CertificateTrustManager");

      final AccessPointService aService = new AccessPointService ();
      final Resource aPort = aService.getResourceBindingPort ();

      final Map <String, Object> aRequestContext = ((BindingProvider) aPort).getRequestContext ();
      aRequestContext.put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, sAddress);
      return aPort;
    }
    catch (final Exception e) {
      s_aLogger.error ("Error setting the Endpoint Address '" + sAddress + "'", e);
      return null;
    }
  }

  /**
   * Sends a Create object using a given port and attaching the given
   * SOAPHeaderObject data to the SOAP-envelope.
   * 
   * @param aPort
   *        the port which will be used to send the message.
   * @param aMetadata
   *        the SOAPHeaderObject holding the BUSDOX headers information that
   *        will be attached into the SOAP-envelope.
   * @param aBody
   *        Create object holding the SOAP-envelope payload.
   * @return {@link ESuccess}.
   */
  @Nonnull
  public static ESuccess send (final Resource aPort, final IMessageMetadata aMetadata, final Create aBody) {
    s_aLogger.info ("Ready for sending message\n" + MessageMetadataHelper.getDebugInfo (aMetadata));
    try {
      // Assign the headers
      final List <Header> aHeaders = MessageMetadataHelper.createHeadersFromMetadata (aMetadata);
      ((WSBindingProvider) aPort).setOutboundHeaders (aHeaders);

      // Main
      aPort.create (aBody);
      s_aLogger.info ("Message " + aMetadata.getMessageID () + " has been successfully delivered!");
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex) {
      // Usually a JAXB marshalling error
      s_aLogger.error ("An error occurred while marshalling headers.", ex);
    }
    catch (final FaultMessage ex) {
      // A wrapped error from the START server
      s_aLogger.error ("Error while sending the message.", ex);
    }
    catch (final WebServiceException ex) {
      // An error from the Metro framework
      s_aLogger.error ("Internal error while sending the message", ex);
    }
    finally {
      // Close the port directly after sending.
      // This is important for WSRM!
      ((com.sun.xml.ws.Closeable) aPort).close ();
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public static ESuccess send (@Nonnull final String sAddressURL,
                               @Nonnull final IMessageMetadata aMetadata,
                               @Nonnull final Create aBody) {
    final Resource aPort = createPort (sAddressURL);
    if (aPort == null) {
      // Warning was already emitted
      return ESuccess.FAILURE;
    }
    return send (aPort, aMetadata, aBody);
  }

  @Nonnull
  public static ESuccess send (@Nonnull final String sAddressURL,
                               @Nonnull final IMessageMetadata aMetadata,
                               @Nonnull final Document aXMLDocument) {
    final Create aCreateBody = new Create ();
    aCreateBody.getAny ().add (aXMLDocument.getDocumentElement ());
    return send (sAddressURL, aMetadata, aCreateBody);
  }
}

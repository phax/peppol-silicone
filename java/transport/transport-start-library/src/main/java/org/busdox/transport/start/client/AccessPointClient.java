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
package org.busdox.transport.start.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.busdox.transport.config.StartClientProperties;
import org.busdox.transport.soapheader.MessageMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.AccessPointService;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Resource;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.random.VerySecureRandom;
import com.sun.xml.ws.developer.WSBindingProvider;

import eu.peppol.common.ConfigFile;
import eu.peppol.common.HostnameVerifierAlwaysTrue;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger<br>
 *         PEPPOL.AT, BRZ, Andreas Haberl
 */
@SuppressWarnings ("rawtypes")
public class AccessPointClient {
  private static final Logger log = LoggerFactory.getLogger (AccessPointClient.class);

  private boolean m_bIsClosed = false;
  private final List <Handler> m_aHandlers;

  public AccessPointClient () {
    this (null);
  }

  public AccessPointClient (@Nullable final List <Handler> aHandlers) {
    m_aHandlers = aHandlers;
  }

  private static void _setupCertificateTrustManager () throws KeyManagementException, NoSuchAlgorithmException {
    final TrustManager [] trustManagers = new TrustManager [] { new AccessPointX509TrustManager (null, null) };
    final SSLContext sc = SSLContext.getInstance ("SSL");
    sc.init (null, trustManagers, VerySecureRandom.getInstance ());
    HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory ());
  }

  public Resource getPort (final String sEndpointAddress) throws Exception {
    _setupCertificateTrustManager ();

    final AccessPointService aAPService = new AccessPointService ();
    final Resource port = aAPService.getResourceBindingPort ();
    ((WSBindingProvider) port).setAddress (sEndpointAddress);
    return port;
  }

  private static void _setSamlProperties (final Resource port, final String sSenderId, final String sSamlTokenIssuerName) {
    final String sKeystoreName = ConfigFile.getInstance ().getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE);
    final String sKeystorePassword = ConfigFile.getInstance ()
                                               .getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE_PASSWORD);
    final Map <String, Object> aRequestContext = ((BindingProvider) port).getRequestContext ();
    aRequestContext.put (SamlCallbackHandler.SENDER_ID, sSenderId);
    aRequestContext.put (SamlCallbackHandler.SAML_TOKEN_ISSUER_NAME, sSamlTokenIssuerName);
    aRequestContext.put (SamlCallbackHandler.KEYSTORE_FILE, sKeystoreName);
    aRequestContext.put (SamlCallbackHandler.KEYSTORE_PASSWORD, sKeystorePassword);
  }

  private static void _send (final Resource port, final Create body) throws FaultMessage {
    log.info ("instantiating  called");
    HttpsURLConnection.setDefaultHostnameVerifier (new HostnameVerifierAlwaysTrue ());

    final String sEndpointAddress = (String) ((BindingProvider) port).getRequestContext ()
                                                                     .get (BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
    log.info ("Port endpoint: " + sEndpointAddress);
    if (false)
      log.info ("  Other: " + ContainerHelper.newMap (((BindingProvider) port).getRequestContext ()));

    port.create (body);
  }

  public void send (final Resource port,
                    final MessageMetaData soapHdr,
                    final String sAPEndpointAddress,
                    final Create body) throws Exception {
    if (false)
      ((WSBindingProvider) port).setAddress (sAPEndpointAddress);
    final BindingProvider binding = (BindingProvider) port;
    if (m_aHandlers != null)
      binding.getBinding ().setHandlerChain (m_aHandlers);

    _setSamlProperties (port, soapHdr.getSender ().getValue (), sAPEndpointAddress);
    _send (port, body);
  }

  public void close (final Resource port) throws Exception {
    if (!m_bIsClosed) {
      // Terminate WSRM Sequence
      ((com.sun.xml.ws.Closeable) port).close ();
      m_bIsClosed = true;
    }
    else {
      throw new Exception ("RM sequence is closed. Instantiate new " + AccessPointClient.class);
    }
  }
}

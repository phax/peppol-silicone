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
package org.busdox.transport.lime.api.impl;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;

import org.busdox.CBusDox;
import org.w3._2009._02.ws_tra.Resource;
import org.w3._2009._02.ws_tra.WstransferService;

import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.string.StringHelper;
import com.sun.xml.ws.developer.WSBindingProvider;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class WebservicePort {
  public static final String SERVICE_NAME = "wstransferService";

  public Resource getServicePort (final String urlStr, final String basicAuthUsername, final String basicAuthPassword) throws Exception {
    if (StringHelper.hasNoTextAfterTrim (urlStr))
      throw new Exception ("LIME access point url is empty");

    setupCertificateTrustManager ();

    // we need the wsdl for the lime version of accesspoint to get rid of ws-rm
    // policies etc.
    final URL wsdlURL = ClassPathResource.getAsURL (CBusDox.START_WSDL_PATH);
    // we need the right WebServiceClient annotation in order to retrieve the
    // correct dependencies e.g. servicename, wsdl-location
    final WebServiceClient ann = WstransferService.class.getAnnotation (WebServiceClient.class);
    if (ann == null)
      throw new IllegalStateException (WstransferService.class.getName () + " has no WebServiceClient annotation...");
    final WstransferService wstransferService = new WstransferService (wsdlURL, new QName (ann.targetNamespace (),
                                                                                           ann.name ()));
    final Resource port = wstransferService.getResourceBindingPort ();
    final WSBindingProvider bp = (WSBindingProvider) port;
    setupBasicAuthentication (bp, basicAuthUsername, basicAuthPassword);
    bp.setAddress (urlStr);
    verifyHostname ();

    return port;
  }

  private X509Certificate getRootCert () throws CertificateException {
    final X509Certificate x509 = (X509Certificate) CertificateFactory.getInstance ("X.509")
                                                                     .generateCertificate (this.getClass ()
                                                                                               .getResourceAsStream ("/peppol-root.crt"));
    return x509;
  }

  private static void setupBasicAuthentication (final WSBindingProvider bp,
                                                final String basicAuthUsername,
                                                final String basicAuthPassword) {
    bp.getRequestContext ().put (BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
    bp.getRequestContext ().put (BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
  }

  private void setupCertificateTrustManager () throws Exception, KeyManagementException, NoSuchAlgorithmException {
    final TrustManager [] trustManagers = new TrustManager [] { new AccessPointX509TrustManager (null, getRootCert ()) };
    final SSLContext sc = SSLContext.getInstance ("SSL");
    sc.init (null, trustManagers, new java.security.SecureRandom ());
    HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory ());
  }

  private static void verifyHostname () {
    final HostnameVerifier hv = new HostnameVerifier () {
      public boolean verify (final String urlHostName, final SSLSession session) {
        return urlHostName.equals (session.getPeerHost ());
      }
    };
    HttpsURLConnection.setDefaultHostnameVerifier (hv);
  }
}

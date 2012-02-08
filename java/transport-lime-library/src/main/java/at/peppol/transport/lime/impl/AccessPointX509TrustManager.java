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
package at.peppol.transport.lime.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.StringTokenizer;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class AccessPointX509TrustManager implements X509TrustManager {
  private static final Logger log = LoggerFactory.getLogger (AccessPointX509TrustManager.class);

  /* The permitted remote common names, or null if no restriction. */
  private final Set <String> commonNames;
  /* The accepted issuer */
  private final X509Certificate rootCertificate;

  AccessPointX509TrustManager (final Set <String> acceptedCommonNames, final X509Certificate acceptedRootCertificate) throws Exception {
    this.rootCertificate = acceptedRootCertificate;
    this.commonNames = acceptedCommonNames;
  }

  public void checkClientTrusted (final X509Certificate [] chain, final String authType) throws CertificateException {
    check (chain);
  }

  public void checkServerTrusted (final X509Certificate [] chain, final String authType) throws CertificateException {
    check (chain);
  }

  public X509Certificate [] getAcceptedIssuers () {
    final X509Certificate [] certs = new X509Certificate [1];
    certs[0] = rootCertificate;
    return certs;
  }

  private void check (final X509Certificate [] chain) throws CertificateException {
    checkPrincipal (chain);
    checkIssuer (chain);
  }

  private void checkIssuer (final X509Certificate [] chain) {
    // TODO MUST check that the certificate is signed by PEPPOL
    if (!chain[0].getIssuerX500Principal ().toString ().equals (rootCertificate.getIssuerX500Principal ().toString ())) {
      // throw new CertificateException("Issuer not trusted: " +
      // chain[0].getIssuerDN().getName());
    }
  }

  private void checkPrincipal (final X509Certificate [] chain) throws CertificateException {
    boolean commonNameOK = false;
    if (commonNames == null) {
      commonNameOK = true;
    }
    else {
      final StringTokenizer st = new StringTokenizer (chain[0].getSubjectX500Principal ().toString (), ",");
      while (st.hasMoreTokens ()) {
        final String tok = st.nextToken ();
        final int x = tok.indexOf ("CN=");
        if (x >= 0) {
          final String curCN = tok.substring (x + 3);
          if (commonNames.contains (curCN)) {
            commonNameOK = true;
            log.warn ("Accepted issuer: " + tok.substring (x + 3));
            break;
          }
        }
      }
    }
    if (!commonNameOK) {
      log.warn ("No accepted issuer: " + chain[0].getSubjectX500Principal ().toString ());
      throw new CertificateException ("Remote principal is not trusted");
    }
  }
}

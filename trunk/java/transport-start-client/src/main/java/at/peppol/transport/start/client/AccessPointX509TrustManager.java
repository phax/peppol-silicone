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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.regex.RegExHelper;

/**
 * The AccessPointX509TrustManager is pointed to authenticate the remote side
 * when using SSL.
 *
 * @author Alexander Aguirre Julcapoma(alex@alfa1lab.com) Jose Gorvenia<br>
 *         Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class AccessPointX509TrustManager implements X509TrustManager {

  /** Logger to follow this class behavior. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessPointX509TrustManager.class);

  /** The permitted remote common names, or null if no restriction. */
  private final Set <String> m_aCommonNames;

  /** The accepted issuer. */
  private final X509Certificate m_aRootCertificate;

  /**
   * Constructor with parameters.
   *
   * @param acceptedCommonNames
   *        A Collection(Set) of Names accepted.
   * @param acceptedRootCertificate
   *        Represents a Certificate.
   * @throws Exception
   *         Throws an Exception.
   */
  public AccessPointX509TrustManager (final Set <String> acceptedCommonNames,
                                      final X509Certificate acceptedRootCertificate) throws Exception {

    m_aRootCertificate = acceptedRootCertificate;
    m_aCommonNames = acceptedCommonNames;
  }

  /**
   * Check if client is trusted.
   *
   * @param chain
   *        an array of X509Certificate holding the certificates.
   * @param authType
   *        authentication type.
   * @throws CertificateException
   *         Throws a CertificateException.
   */
  @Override
  public final void checkClientTrusted (final X509Certificate [] chain, final String authType) throws CertificateException {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Checking client certificates.");
    _check (chain);
  }

  /**
   * Check if server is trusted.
   *
   * @param chain
   *        Array of Certificates.
   * @param authType
   *        is never used
   * @throws CertificateException
   *         Error with certificates.
   */
  @Override
  public final void checkServerTrusted (final X509Certificate [] chain, final String authType) throws CertificateException {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Checking server certificates.");
    _check (chain);
  }

  /**
   * Returns an array of X509Certificate objects which are trusted for
   * authenticating peers.
   *
   * @return X509Certificate array containing the accepted root certificates.
   */
  @Override
  public final X509Certificate [] getAcceptedIssuers () {
    return ArrayHelper.newArray (m_aRootCertificate);
  }

  /**
   * Checks chain.
   *
   * @param chain
   *        Array of certificates.
   * @throws CertificateException
   *         Exception for Certificates.
   */
  private void _check (final X509Certificate [] chain) throws CertificateException {
    _checkPrincipal (chain);
  }

  /**
   * Check Principal.
   *
   * @param chain
   *        Array of Certificates.
   * @throws CertificateException
   *         Exception for Certificates.
   */
  private void _checkPrincipal (final X509Certificate [] chain) throws CertificateException {
    if (m_aCommonNames != null) {
      boolean bCommonNameOK = false;
      final String [] aArray = RegExHelper.split (chain[0].getSubjectX500Principal ().toString (), ",");
      for (final String sToken : aArray) {
        final int nIndex = sToken.indexOf ("CN=");
        if (nIndex >= 0) {
          final String sCurCN = sToken.substring (nIndex + 3);
          if (m_aCommonNames.contains (sCurCN)) {
            bCommonNameOK = true;
            s_aLogger.info ("Accepted issuer: " + sCurCN);
            break;
          }
        }
      }

      if (!bCommonNameOK) {
        s_aLogger.error ("No accepted issuer: " + chain[0].getSubjectX500Principal ().toString ());
        throw new CertificateException ("Remote principal is not trusted");
      }
    }
  }
}

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
package at.peppol.sml.server.security;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract certificate principal from HTTP request.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ClientUniqueIDProvider {
  private static final Logger log = LoggerFactory.getLogger (ClientUniqueIDProvider.class);

  private ClientUniqueIDProvider () {}

  /**
   * Extract the client unique ID from the certificate.<br>
   * Note: this assumes that a single root certificate is present. Otherwise the
   * unique ID should also contain the subject DN and serial of the issuer!
   * 
   * @param aHttpRequest
   *        The HTTP request to use.
   * @return <code>null</code> if some error occurred.
   */
  @Nullable
  public static String getClientUniqueID (@Nonnull final HttpServletRequest aHttpRequest) {
    final Object aValue = aHttpRequest.getAttribute ("javax.servlet.request.X509Certificate");
    if (aValue == null) {
      log.warn ("No client certificates present in the request");
      return null;
    }
    if (!(aValue instanceof X509Certificate []))
      throw new IllegalStateException ("Request value is not of type X509Certificate[] but of " + aValue.getClass ());
    return getClientUniqueID ((X509Certificate []) aValue);
  }

  @Nullable
  public static String getClientUniqueID (final X509Certificate [] aRequestCerts) {
    if (aRequestCerts == null || aRequestCerts.length == 0) {
      // Empty array
      return null;
    }

    // Find all certificates that are not issuer to another certificate
    final List <X509Certificate> aNonIssuerCertList = new ArrayList <X509Certificate> ();
    for (int i = 0; i < aRequestCerts.length; ++i) {
      final X509Certificate aCert = aRequestCerts[i];
      final Principal aSubject = aCert.getSubjectX500Principal ();

      // Search for the issuer in the available certificate array
      boolean bFound = false;
      for (final X509Certificate aIssuerCert : aRequestCerts)
        if (aSubject.equals (aIssuerCert.getIssuerX500Principal ())) {
          bFound = true;
          break;
        }
      if (!bFound)
        aNonIssuerCertList.add (aCert);
    }

    // Do we have exactly 1 certificate to verify?
    if (aNonIssuerCertList.size () != 1)
      throw new IllegalStateException ("Found " +
                                       aNonIssuerCertList.size () +
                                       " certificates that are not issuer certificates!");
    final X509Certificate aNonIssuerCert = aNonIssuerCertList.get (0);
    return aNonIssuerCert.getSubjectX500Principal ().getName () + ':' + aNonIssuerCert.getSerialNumber ().toString (16);
  }
}

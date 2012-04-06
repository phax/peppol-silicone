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

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.exceptions.InitializationException;

/**
 * This class has the sole purpose of delivering the PEPPOL root certificate in
 * an efficient manner!
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class PeppolRootCertificateProvider {
  private static final String CONFIG_SML_TRUSTSTORE_PATH = "sml.truststore.path";
  private static final String CONFIG_SML_TRUSTSTORE_PASSWORD = "sml.truststore.password";
  private static final String CONFIG_SML_TRUSTSTORE_ALIAS = "sml.truststore.alias";
  private static final Logger s_aLogger = LoggerFactory.getLogger (PeppolRootCertificateProvider.class);

  private static X509Certificate s_aPeppolSMPRootCert;

  static {
    final ConfigFile aConfigFile = ConfigFile.getInstance ();
    final String sTrustStorePath = aConfigFile.getString (CONFIG_SML_TRUSTSTORE_PATH);
    final String sTrustStorePW = aConfigFile.getString (CONFIG_SML_TRUSTSTORE_PASSWORD);
    final String sTrustStoreAlias = aConfigFile.getString (CONFIG_SML_TRUSTSTORE_ALIAS);
    try {
      final KeyStore aKS = KeyStoreUtils.loadKeyStore (sTrustStorePath, sTrustStorePW);
      s_aPeppolSMPRootCert = (X509Certificate) aKS.getCertificate (sTrustStoreAlias);
    }
    catch (final Exception ex) {
      s_aLogger.error ("Failed to read SML trust store from '" + sTrustStorePath + "'", ex);
    }

    if (s_aPeppolSMPRootCert == null)
      throw new InitializationException ("Failed to resolve alias '" + sTrustStoreAlias + "' in trust store!");
  }

  private PeppolRootCertificateProvider () {}

  public static X509Certificate getPeppolSMPRootCertificate () {
    return s_aPeppolSMPRootCert;
  }
}

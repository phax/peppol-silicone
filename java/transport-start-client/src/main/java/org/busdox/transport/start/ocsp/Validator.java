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
package org.busdox.transport.start.ocsp;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EValidity;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback.CertificateValidator;

import eu.peppol.busdox.security.KeyStoreUtils;
import eu.peppol.common.ConfigFile;

/**
 * The main OCSP validator callback.<br>
 * Important: the name of this class is referenced from the WSDL file.
 * 
 * @author Alexander Aguirre Julcapoma(alex@alfa1lab.com) Jose Gorvenia<br>
 *         Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class Validator implements CertificateValidator {
  /**
   * Logger to follow this class behavior.
   */
  private static final Logger s_aLogger = LoggerFactory.getLogger (Validator.class);

  /**
   * URL from which data for validation is retrieved.
   */
  private static final String TEST_RESPONDER_URL = "http://pilot-ocsp.verisign.com:80";

  /**
   * Server truststore path.
   */
  private static final String TRUSTSTORE_PATH = "ocsp.truststore.path";

  /**
   * Server truststore password.
   */
  private static final String TRUSTSTORE_PASS = "ocsp.truststore.password";

  /**
   * Server truststore alias.
   */
  private static final String TRUSTORE_ALIAS = "ocsp.truststore.alias";

  /**
   * Configuration for certificates.
   */
  private static final ConfigFile s_aConf = new ConfigFile ("private-configOCSP.properties", "configOCSP.properties");

  /**
   * Validates a X.509 Certificate.
   * 
   * @param xc
   * @return true if the certificate passes all validations, otherwise returns
   *         false.
   */
  public final boolean validate (final X509Certificate xc) {
    final String sPath = s_aConf.getString (TRUSTSTORE_PATH);
    return certificateValidate (xc, sPath).isValid ();
  }

  /**
   * This method validate the X.509 Certificate.
   * 
   * @param xc
   * @param sFilePath
   * @return {@link EValidity}
   */
  @Nonnull
  public static final EValidity certificateValidate (final X509Certificate xc, @Nonnull final String sFilePath) {
    try {
      // Load keystore
      final String sTrustStorePassword = s_aConf.getString (TRUSTSTORE_PASS);
      final KeyStore aTrustStore = KeyStoreUtils.loadKeyStoreFromClassPath (sFilePath, sTrustStorePassword);

      // Get certificate from alias
      final String sTrustStoreAlias = s_aConf.getString (TRUSTORE_ALIAS);
      final X509Certificate rootcert = (X509Certificate) aTrustStore.getCertificate (sTrustStoreAlias);
      if (rootcert == null)
        s_aLogger.error ("Failed to resolve trust store alias '" + sTrustStoreAlias + "'");
      else {
        OCSP.check (ContainerHelper.newList (xc), rootcert, TEST_RESPONDER_URL);
        return EValidity.VALID;
      }
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error validating certificate in key store '" + sFilePath + "'", ex);
    }
    return EValidity.INVALID;
  }
}

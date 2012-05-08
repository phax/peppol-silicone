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

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EValidity;
import com.phloc.commons.string.StringHelper;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback.CertificateValidator;

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

  private static final String CONFIG_ENABLED = "ocsp.enabled";
  private static final String CONFIG_RESPONDER_URL = "ocsp.responderurl";
  private static final String CONFIG_TRUSTSTORE_PATH = "ocsp.truststore.path";
  private static final String CONFIG_TRUSTSTORE_PASSWORD = "ocsp.truststore.password";
  private static final String CONFIG_TRUSTORE_ALIAS = "ocsp.truststore.alias";

  private static final ConfigFile s_aConf = new ConfigFile ("private-configOCSP.properties", "configOCSP.properties");

  /**
   * Validates a X.509 Certificate.
   * 
   * @param aCert
   * @return true if the certificate passes all validations, otherwise returns
   *         false.
   */
  public final boolean validate (final X509Certificate aCert) {
    final String sTrustStorePath = s_aConf.getString (CONFIG_TRUSTSTORE_PATH);
    return certificateValidate (aCert, sTrustStorePath).isValid ();
  }

  /**
   * This method validate the X.509 Certificate.
   * 
   * @param aCert
   * @param sTrustStorePath
   * @return {@link EValidity}
   */
  @Nonnull
  public static final EValidity certificateValidate (final X509Certificate aCert, @Nonnull final String sTrustStorePath) {
    // Is the OCSP check enabled?
    // Note: use "enabled" as the default value, in case none is defined
    final boolean bEnabled = s_aConf.getBoolean (CONFIG_ENABLED, true);
    if (!bEnabled) {
      // OCSP is disabled - allow validity
      return EValidity.VALID;
    }

    try {
      // Load keystore
      final String sTrustStorePassword = s_aConf.getString (CONFIG_TRUSTSTORE_PASSWORD);
      final KeyStore aTrustStore = KeyStoreUtils.loadKeyStore (sTrustStorePath, sTrustStorePassword);

      // Get certificate from alias
      final String sTrustStoreAlias = s_aConf.getString (CONFIG_TRUSTORE_ALIAS);
      final X509Certificate aRootCert = (X509Certificate) aTrustStore.getCertificate (sTrustStoreAlias);
      if (aRootCert == null) {
        s_aLogger.error ("Failed to resolve trust store alias '" + sTrustStoreAlias + "'");
      }
      else {
        // Get the responder URL from the configuration
        // Note: use the old constant as the default value, in case none is
        // defined
        final String DEFAULT_RESPONDER_URL = "http://pilot-ocsp.verisign.com:80";
        final String sResponderURL = s_aConf.getString (CONFIG_RESPONDER_URL, DEFAULT_RESPONDER_URL);
        if (StringHelper.hasNoText (sResponderURL)) {
          // Error
          s_aLogger.error ("No OCSP responder URL configured (property '" +
                           CONFIG_RESPONDER_URL +
                           "'). The old default URL was: " +
                           DEFAULT_RESPONDER_URL);
        }
        else {
          // Start the main OCSP check
          OCSP.check (ContainerHelper.newList (aCert), aRootCert, sResponderURL);
          return EValidity.VALID;
        }
      }
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error validating certificate in trust store '" + sTrustStorePath + "'", ex);
    }
    return EValidity.INVALID;
  }
}

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
package eu.peppol.inbound.ocsp;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.collections.ContainerHelper;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback.CertificateValidator;

import eu.peppol.security.OcspValidatorCache;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * Call back handler for validation of certificates using OCSP
 * 
 * @author Nigel Parker
 */
public class OcspValidator implements CertificateValidator {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-inb");

  private static CertPathValidator s_aCertPathValidator;
  private static PKIXParameters s_aPkixParameters;
  private static final OcspValidatorCache s_aCache = new OcspValidatorCache ();

  public synchronized boolean validate (final X509Certificate certificate) {
    final BigInteger serialNumber = certificate.getSerialNumber ();
    final String certificateName = "Certificate " + serialNumber;
    log.debug ("Ocsp validation requested for " + certificateName);

    if (s_aCertPathValidator == null) {
      initialise ();
    }

    if (s_aCache.isKnownValidCertificate (serialNumber)) {
      log.debug (certificateName + " is OCSP valid (cached value)");
      return true;
    }

    try {

      final List <? extends Certificate> certificates = ContainerHelper.newList (certificate);
      final CertPath certPath = CertificateFactory.getInstance ("X.509").generateCertPath (certificates);
      s_aCertPathValidator.validate (certPath, s_aPkixParameters);
      s_aCache.setKnownValidCertificate (serialNumber);

      log.debug (certificateName + " is OCSP valid");
      return true;
    }
    catch (final Exception e) {
      log.error (certificateName + " failed OCSP validation", e);
      return false;
    }
  }

  public void initialise () {
    log.debug ("Initialising OCSP validator");
    try {
      final TrustAnchor trustAnchor = new KeystoreManager ().getTrustAnchor ();
      s_aCertPathValidator = CertPathValidator.getInstance ("PKIX");
      s_aPkixParameters = new PKIXParameters (Collections.singleton (trustAnchor));
      s_aPkixParameters.setRevocationEnabled (true);

      Security.setProperty ("ocsp.enable", "true");
      Security.setProperty ("ocsp.responderURL", "http://pilot-ocsp.verisign.com:80");
    }
    catch (final Exception e) {
      log.error ("Failed to init OCSP validator", e);
      throw new RuntimeException ("Failed to init OCSP validator", e);
    }
  }
}

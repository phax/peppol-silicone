/*
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
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.xml.wss.impl.callback.CertificateValidationCallback.CertificateValidator;

import eu.peppol.inbound.util.Log;
import eu.peppol.inbound.util.Util;
import eu.peppol.security.OcspValidatorCache;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * Call back handler for validation of certificates using OCSP
 * 
 * @author Nigel Parker
 */
public class OcspValidator implements CertificateValidator {

  private static CertPathValidator certPathValidator;
  private static PKIXParameters pkixParameters;
  private static OcspValidatorCache cache = new OcspValidatorCache ();

  public synchronized boolean validate (final X509Certificate certificate) {
    final BigInteger serialNumber = certificate.getSerialNumber ();
    final String certificateName = "Certificate " + serialNumber;
    Log.debug ("Ocsp validation requested for " + certificateName);

    if (certPathValidator == null) {
      initialise ();
    }

    if (cache.isKnownValidCertificate (serialNumber)) {
      Log.debug (certificateName + " is OCSP valid (cached value)");
      return true;
    }

    try {

      final List <Certificate> certificates = Arrays.asList (new Certificate [] { certificate });
      final CertPath certPath = CertificateFactory.getInstance ("X.509").generateCertPath (certificates);
      certPathValidator.validate (certPath, pkixParameters);
      cache.setKnownValidCertificate (serialNumber);

      Log.debug (certificateName + " is OCSP valid");
      return true;
    }
    catch (final Exception e) {
      Log.error (certificateName + " failed OCSP validation", e);
      return false;
    }
  }

  public void initialise () {
    Log.debug ("Initialising OCSP validator");
    try {
      final TrustAnchor trustAnchor = new KeystoreManager ().getTrustAnchor ();
      certPathValidator = CertPathValidator.getInstance ("PKIX");
      pkixParameters = new PKIXParameters (Collections.singleton (trustAnchor));
      pkixParameters.setRevocationEnabled (true);

      Security.setProperty ("ocsp.enable", "true");
      Security.setProperty ("ocsp.responderURL", "http://pilot-ocsp.verisign.com:80");
    }
    catch (final Exception e) {
      Util.logAndThrowRuntimeException ("Failed to get trust anchor", e);
    }
  }
}

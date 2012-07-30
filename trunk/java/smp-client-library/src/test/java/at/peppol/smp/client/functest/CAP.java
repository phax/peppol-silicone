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
package at.peppol.smp.client.functest;

import java.security.cert.CertificateException;

import javax.annotation.concurrent.Immutable;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.SMPServiceCaller;

import com.phloc.commons.exceptions.InitializationException;

@Immutable
public final class CAP {
  public static final String START_AP_ADDRESS = "https://peppol-ap.example.org/accessPointService/";
  public static final W3CEndpointReference START_AP_ENDPOINTREF = W3CEndpointReferenceUtils.createEndpointReference (START_AP_ADDRESS);

  /**
   * The Base64 encoded, DER encoded AP certificate. E.g. to be created by
   * extracting the head certificate from the ap_keystore.jks using Portecle.
   * This certificate only contains the public key!
   */
  public static final String AP_CERT_STRING = "TODO!!!!!";
  public static final String AP_SERVICE_DESCRIPTION = "My Accesspoint Service";
  public static final String AP_CONTACT_URL = "support@example.org";
  public static final String AP_INFO_URL = "http://www.example.org";

  // init
  static {
    try {
      if (SMPServiceCaller.convertStringToCertficate (AP_CERT_STRING) == null)
        throw new InitializationException ("Failed to convert certificate string to a certificate!");
    }
    catch (final CertificateException ex) {
      throw new InitializationException ("Failed to convert certificate string to a certificate!", ex);
    }
  }

  private CAP () {}
}

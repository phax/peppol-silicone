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
package at.peppol.smp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import org.junit.Test;

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;
import at.peppol.commons.sml.ESML;

import com.sun.jersey.api.client.ClientHandlerException;

/**
 * Test class for class {@link SMPServiceCaller}.
 * 
 * @author philip
 */
public final class SMPServiceCallerTest {
  private static IReadonlyDocumentTypeIdentifier DOCUMENT_INVOICE = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A;
  private static IReadonlyProcessIdentifier PROCESS_BII04 = EPredefinedProcessIdentifier.BIS4A;

  private static IReadonlyParticipantIdentifier PI_alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");
  private static IReadonlyParticipantIdentifier PI_helseVest = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:983974724");
  private static IReadonlyParticipantIdentifier PI_sendRegning = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897");

  @Test
  public void testGetEndpointAddress () throws Throwable {
    String sEndpointAddress;

    try {
      sEndpointAddress = new SMPServiceCaller (PI_alfa1lab, ESML.PRODUCTION).getEndpointAddress (PI_alfa1lab,
                                                                                                 DOCUMENT_INVOICE,
                                                                                                 PROCESS_BII04);
      assertEquals (sEndpointAddress, "https://start-ap.alfa1lab.com:443/accesspointService");

      // 2011-12-08: returns BadRequestException (HTTP status 400)
      if (false) {
        sEndpointAddress = new SMPServiceCaller (PI_helseVest, ESML.PRODUCTION).getEndpointAddress (PI_helseVest,
                                                                                                    DOCUMENT_INVOICE,
                                                                                                    PROCESS_BII04);
        assertEquals (sEndpointAddress, "https://peppolap.ibxplatform.net:8443/accesspointService");
      }

      if (false) {
        sEndpointAddress = new SMPServiceCaller (PI_sendRegning, ESML.PRODUCTION).getEndpointAddress (PI_sendRegning,
                                                                                                      DOCUMENT_INVOICE,
                                                                                                      PROCESS_BII04);
        assertEquals (sEndpointAddress, "https://aksesspunkt.sendregning.no:8443/oxalis/accessPointService");
      }
    }
    catch (final ClientHandlerException ex) {
      // Happens when being offline!
      assertTrue (ex.getCause () instanceof UnknownHostException);
    }
  }

  @Test
  public void testGetEndpointCertificate () throws Throwable {
    X509Certificate aEndpointCertificate;

    try {
      aEndpointCertificate = new SMPServiceCaller (PI_alfa1lab, ESML.PRODUCTION).getEndpointCertificate (PI_alfa1lab,
                                                                                                         DOCUMENT_INVOICE,
                                                                                                         PROCESS_BII04);
      assertNotNull (aEndpointCertificate);
      assertEquals (aEndpointCertificate.getSerialNumber ().toString (), "97394193891150626641360283873417712042");

      // 2011-12-08: returns BadRequestException (HTTP status 400)
      if (false) {
        aEndpointCertificate = new SMPServiceCaller (PI_helseVest, ESML.PRODUCTION).getEndpointCertificate (PI_helseVest,
                                                                                                            DOCUMENT_INVOICE,
                                                                                                            PROCESS_BII04);
        assertNotNull (aEndpointCertificate);
        assertEquals (aEndpointCertificate.getSerialNumber ().toString (), "37276025795984990954710880598937203007");
      }
    }
    catch (final ClientHandlerException ex) {
      // Happens when being offline!
      assertTrue (ex.getCause () instanceof UnknownHostException);
    }
  }
}

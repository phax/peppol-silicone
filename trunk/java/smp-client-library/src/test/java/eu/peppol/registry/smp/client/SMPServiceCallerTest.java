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
package eu.peppol.registry.smp.client;

import static org.junit.Assert.assertEquals;

import java.security.cert.X509Certificate;

import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;
import org.junit.Test;

import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.docid.EPredefinedDocumentIdentifier;
import eu.peppol.busdox.identifier.procid.EPredefinedProcessIdentifier;
import eu.peppol.busdox.sml.ESML;

/**
 * Test class for class {@link SMPServiceCaller}.
 * 
 * @author philip
 */
public final class SMPServiceCallerTest {
  private static IReadonlyDocumentIdentifier DOCUMENT_INVOICE = EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol4a_ver1_0__2_0;
  private static IReadonlyProcessIdentifier PROCESS_BII04 = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii04_ver1_0;

  private static IReadonlyParticipantIdentifier alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");
  private static IReadonlyParticipantIdentifier helseVest = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:983974724");
  private static IReadonlyParticipantIdentifier sendRegning = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897");

  @Test
  public void testGetEndpointAddress () throws Throwable {
    String endpointAddress;
    endpointAddress = new SMPServiceCaller (alfa1lab, ESML.PRODUCTION).getEndpointAddress (alfa1lab,
                                                                                           DOCUMENT_INVOICE,
                                                                                           PROCESS_BII04);
    assertEquals (endpointAddress, "https://start-ap.alfa1lab.com:443/accesspointService");

    // 2011-12-08: returns BadRequestException (HTTP status 400)
    if (false) {
      endpointAddress = new SMPServiceCaller (helseVest, ESML.PRODUCTION).getEndpointAddress (helseVest,
                                                                                              DOCUMENT_INVOICE,
                                                                                              PROCESS_BII04);
      assertEquals (endpointAddress, "https://peppolap.ibxplatform.net:8443/accesspointService");
    }

    endpointAddress = new SMPServiceCaller (sendRegning, ESML.PRODUCTION).getEndpointAddress (sendRegning,
                                                                                              DOCUMENT_INVOICE,
                                                                                              PROCESS_BII04);
    assertEquals (endpointAddress, "https://aksesspunkt.sendregning.no:8443/oxalis/accessPointService");
  }

  @Test
  public void testGetEndpointCertificate () throws Throwable {
    X509Certificate endpointCertificate;
    endpointCertificate = new SMPServiceCaller (alfa1lab, ESML.PRODUCTION).getEndpointCertificate (alfa1lab,
                                                                                                   DOCUMENT_INVOICE,
                                                                                                   PROCESS_BII04);
    assertEquals (endpointCertificate.getSerialNumber ().toString (), "97394193891150626641360283873417712042");

    // 2011-12-08: returns BadRequestException (HTTP status 400)
    if (false) {
      endpointCertificate = new SMPServiceCaller (helseVest, ESML.PRODUCTION).getEndpointCertificate (helseVest,
                                                                                                      DOCUMENT_INVOICE,
                                                                                                      PROCESS_BII04);
      assertEquals (endpointCertificate.getSerialNumber ().toString (), "37276025795984990954710880598937203007");
    }
  }
}

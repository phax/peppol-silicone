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
package eu.peppol.outbound.smp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.security.cert.X509Certificate;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Test;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IProcessIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;

/**
 * User: nigel Date: Oct 25, 2011 Time: 9:05:52 AM
 */
public class SmpLookupManagerTest {

  private static IDocumentTypeIdentifier invoice = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ();
  // private static ParticipantIdentifierType alfa1lab =
  // Identifiers.getParticipantIdentifier("9902:DK28158815");
  private static ParticipantIdentifierType alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");
  private static ParticipantIdentifierType helseVest = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:983974724");
  private static ParticipantIdentifierType sendRegning = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897");

  @Test
  public void test01 () throws Throwable {
    try {

      URL endpointAddress;
      endpointAddress = SmpLookupManager.getEndpointAddress (sendRegning, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://aksesspunkt.sendregning.no/oxalis/accessPointService");

      endpointAddress = SmpLookupManager.getEndpointAddress (alfa1lab, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://start-ap.alfa1lab.com:443/accessPointService");

      endpointAddress = SmpLookupManager.getEndpointAddress (helseVest, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://peppolap.ibxplatform.net:8443/accesspointService");

    }
    catch (final Throwable t) {
      t.printStackTrace (System.err);
    }
  }

  @Test
  public void test02 () throws Throwable {
    try {

      X509Certificate endpointCertificate;
      endpointCertificate = SmpLookupManager.getEndpointCertificate (alfa1lab, invoice);
      assertEquals (endpointCertificate.getSerialNumber ().toString (), "97394193891150626641360283873417712042");

      // endpointCertificate = new
      // SmpLookupManager().getEndpointCertificate(helseVest, invoice);
      // assertEquals(endpointCertificate.getSerialNumber().toString(),
      // "37276025795984990954710880598937203007");

    }
    catch (final Throwable t) {
      t.printStackTrace (System.err);
    }
  }

  /**
   * Tests what happens when the participant is not registered.
   * 
   * @throws Throwable
   */
  @Test
  public void test03 () throws Throwable {

    final ParticipantIdentifierType notRegisteredParticipant = SimpleParticipantIdentifier.createWithDefaultScheme ("1234:45678910");
    try {
      SmpLookupManager.getEndpointAddress (notRegisteredParticipant, invoice);
      fail (String.format ("Participant '%s' should not be registered", notRegisteredParticipant));
    }
    catch (final NumberFormatException e) {
      // expected
    }
  }

  /**
     *
     */
  @Test
  public void testGetFirstProcessIdentifier () throws SmpSignedServiceMetaDataException {
    final IProcessIdentifier processTypeIdentifier = SmpLookupManager.getProcessIdentifierForDocumentType (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:810017902"),
                                                                                                           EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ());

    assertEquals (processTypeIdentifier.getValue (), "urn:www.cenbii.eu:profile:bii04:ver1.0");
  }
}

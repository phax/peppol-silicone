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

import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.doctype.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.ReadonlyUsernamePWCredentials;

import com.phloc.commons.url.URLUtils;

/**
 * Expects a local SMP up and running with DNS enabled at port 80 at the ROOT
 * context. See {@link #SMP_URI} constant
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class ClientDNSTest {
  private static final ISMLInfo SML_INFO = ESML.DEVELOPMENT_LOCAL;

  private static final String TEST_BUSINESS_IDENTIFIER = "0088:5798000999988";

  private static final String SMP_USERNAME = "peppol_user";
  private static final String SMP_PASSWORD = "Test1234";
  private static final IReadonlyUsernamePWCredentials SMP_CREDENTIALS = new ReadonlyUsernamePWCredentials (SMP_USERNAME,
                                                                                                           SMP_PASSWORD);
  public static final URI SMP_URI = URLUtils.getAsURI ("http://localhost/");

  @BeforeClass
  public static void init () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);

    // Ensure to delete TEST_BUSINESS_IDENTIFIER
    try {
      final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER);
      aClient.deleteServiceGroup (aServiceGroupID, SMP_CREDENTIALS);
    }
    catch (final Exception e) {
      // This is ok
    }
  }

  @Test
  public void getByDNSTest () throws Exception {
    // Make sure that the dns exists.
    final String sParticipantID = "0088:5798000000001";
    final String sDocumentID = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::InvoiceDisputeDisputeInvoice##UBL-2.0";

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    final DocumentIdentifierType aDocumentTypeID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (sDocumentID);

    final ServiceGroupType aGroup = SMPServiceCaller.getServiceGroupByDNS (SML_INFO, aServiceGroupID);
    assertNotNull (aGroup);

    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (SML_INFO,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }

  @Test
  public void getByDNSTestForDocs () throws Exception {
    // ServiceGroup = participant identifier; GLN = 0088
    final ParticipantIdentifierType aServiceGroupID = EPredefinedIdentifierIssuingAgency.GLN.createParticipantIdentifier ("5798000000001");
    // Document type identifier from enumeration
    final DocumentIdentifierType aDocumentTypeID = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    // Main call to the SMP client with the correct SML to use
    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (ESML.DEVELOPMENT_LOCAL,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }

  @Test
  public void redirectTest () throws Exception {
    final String sParticipantID = "0088:5798000009997";
    final String sDocumentID = "urn:oasis:names:specification:ubl:schema:xsd:SubmitCatalogue-2::SubmitCatalogue##UBL-2.0";

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    final DocumentIdentifierType aDocumentTypeID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (sDocumentID);

    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (SML_INFO,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }
}

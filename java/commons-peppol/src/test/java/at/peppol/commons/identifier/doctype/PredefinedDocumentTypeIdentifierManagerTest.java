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
package at.peppol.commons.identifier.doctype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.doctype.PredefinedDocumentTypeIdentifierManager;

/**
 * Test class for class {@link PredefinedDocumentTypeIdentifierManager}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class PredefinedDocumentTypeIdentifierManagerTest {
  @Test
  public void testAll () {
    assertNotNull (PredefinedDocumentTypeIdentifierManager.getAllDocumentTypeIdentifiers ());

    assertNotNull (PredefinedDocumentTypeIdentifierManager.getDocumentTypeIdentifierOfID ("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0"));
    assertNull (PredefinedDocumentTypeIdentifierManager.getDocumentTypeIdentifierOfID ("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0a"));

    assertTrue (PredefinedDocumentTypeIdentifierManager.containsDocumentTypeIdentifierWithID ("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0"));
    assertFalse (PredefinedDocumentTypeIdentifierManager.containsDocumentTypeIdentifierWithID ("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0a"));

    // all enum ones must be contained
    for (final EPredefinedDocumentTypeIdentifier eDocID : EPredefinedDocumentTypeIdentifier.values ()) {
      assertSame (eDocID, PredefinedDocumentTypeIdentifierManager.getDocumentTypeIdentifierOfID (eDocID.getValue ()));
      assertTrue (PredefinedDocumentTypeIdentifierManager.containsDocumentTypeIdentifierWithID (eDocID.getValue ()));
    }
  }
}

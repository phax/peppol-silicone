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
package at.peppol.commons.identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import at.peppol.commons.identifier.ReadonlyDocumentIdentifier;

import com.phloc.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link ReadonlyDocumentIdentifier}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ReadonlyDocumentIdentifierTest {
  @Test
  public void testCtor () {
    ReadonlyDocumentIdentifier aID = new ReadonlyDocumentIdentifier ("scheme", "value");
    assertEquals ("scheme", aID.getScheme ());
    assertEquals ("value", aID.getValue ());
    aID = new ReadonlyDocumentIdentifier (null, "value");
    assertNull (aID.getScheme ());
    assertEquals ("value", aID.getValue ());
    aID = new ReadonlyDocumentIdentifier ("scheme", null);
    assertEquals ("scheme", aID.getScheme ());
    assertNull (aID.getValue ());
    aID = new ReadonlyDocumentIdentifier (null, null);
    assertNull (aID.getScheme ());
    assertNull (aID.getValue ());

    aID = new ReadonlyDocumentIdentifier ("scheme", "value");
    final ReadonlyDocumentIdentifier aID2 = new ReadonlyDocumentIdentifier (aID);
    assertEquals ("scheme", aID2.getScheme ());
    assertEquals ("value", aID2.getValue ());
  }

  @Test
  public void testBasicMethods () {
    final ReadonlyDocumentIdentifier aID1 = new ReadonlyDocumentIdentifier ("scheme", "value");
    final ReadonlyDocumentIdentifier aID2 = new ReadonlyDocumentIdentifier ("scheme", "value");
    final ReadonlyDocumentIdentifier aID3 = new ReadonlyDocumentIdentifier ("scheme2", "value");
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aID1, aID2);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID1, aID3);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID2, aID3);
  }

  @Test
  public void testURIStuff () {
    final ReadonlyDocumentIdentifier aID1 = new ReadonlyDocumentIdentifier ("scheme1", "value1");
    assertEquals ("scheme1::value1", aID1.getURIEncoded ());
    assertEquals ("scheme1%3A%3Avalue1", aID1.getURIPercentEncoded ());
  }
}

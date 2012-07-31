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
package at.peppol.commons.identifier.procid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.procid.ReadonlyProcessIdentifier;

import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.commons.string.StringHelper;

/**
 * Test class for class {@link ReadonlyProcessIdentifier}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ReadonlyProcessIdentifierTest {
  @Test
  public void testCtor () {
    final ReadonlyProcessIdentifier aID = new ReadonlyProcessIdentifier ("scheme", "value");
    assertEquals ("scheme", aID.getScheme ());
    assertEquals ("value", aID.getValue ());

    final ReadonlyProcessIdentifier aID2 = new ReadonlyProcessIdentifier (aID);
    assertEquals ("scheme", aID2.getScheme ());
    assertEquals ("value", aID2.getValue ());
  }

  @Test
  public void testBasicMethods () {
    final ReadonlyProcessIdentifier aID1 = new ReadonlyProcessIdentifier ("scheme", "value");
    final ReadonlyProcessIdentifier aID2 = new ReadonlyProcessIdentifier ("scheme", "value");
    final ReadonlyProcessIdentifier aID3 = new ReadonlyProcessIdentifier ("scheme2", "value");
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aID1, aID2);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID1, aID3);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID2, aID3);
  }

  @Test
  public void testURIStuff () {
    final ReadonlyProcessIdentifier aID1 = new ReadonlyProcessIdentifier ("scheme1", "value1");
    assertEquals ("scheme1::value1", aID1.getURIEncoded ());
    assertEquals ("scheme1%3A%3Avalue1", aID1.getURIPercentEncoded ());
  }

  @Test
  public void testConstraints () {
    try {
      // null key not allowed
      new ReadonlyProcessIdentifier (null, "value");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // null value not allowed
      new ReadonlyProcessIdentifier ("scheme", null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Both null not allowed
      new ReadonlyProcessIdentifier (null, null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Empty is not allowed
      new ReadonlyProcessIdentifier ("scheme", "");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Cannot be mapped to ISO-8859-1:
      new ReadonlyProcessIdentifier ("scheme", "Љ");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Scheme too long
      new ReadonlyProcessIdentifier (StringHelper.getRepeated ('a', CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH + 1),
                                     "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Value too long
      new ReadonlyProcessIdentifier ("scheme",
                                     StringHelper.getRepeated ('a', CIdentifier.MAX_PROCESS_IDENTIFIER_VALUE_LENGTH + 1));
      fail ();
    }
    catch (final IllegalArgumentException ex) {}
  }
}

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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.commons.string.StringHelper;

/**
 * Test class for class {@link ReadonlyParticipantIdentifier}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ReadonlyParticipantIdentifierTest {
  @Test
  public void testCtor () {
    final ReadonlyParticipantIdentifier aID = new ReadonlyParticipantIdentifier ("scheme-actorid-test", "value");
    assertEquals ("scheme-actorid-test", aID.getScheme ());
    assertEquals ("value", aID.getValue ());

    final ReadonlyParticipantIdentifier aID2 = new ReadonlyParticipantIdentifier (aID);
    assertEquals ("scheme-actorid-test", aID2.getScheme ());
    assertEquals ("value", aID2.getValue ());
  }

  @Test
  public void testBasicMethods () {
    final ReadonlyParticipantIdentifier aID1 = new ReadonlyParticipantIdentifier ("scheme-actorid-test", "value");
    final ReadonlyParticipantIdentifier aID2 = new ReadonlyParticipantIdentifier ("scheme-actorid-test", "value");
    final ReadonlyParticipantIdentifier aID3 = new ReadonlyParticipantIdentifier ("scheme2-actorid-test", "value");
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aID1, aID2);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID1, aID3);
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aID2, aID3);
  }

  @Test
  public void testURIStuff () {
    final ReadonlyParticipantIdentifier aID1 = new ReadonlyParticipantIdentifier ("scheme-actorid-test", "value1");
    assertEquals ("scheme-actorid-test::value1", aID1.getURIEncoded ());
    assertEquals ("scheme-actorid-test%3A%3Avalue1", aID1.getURIPercentEncoded ());
  }

  @Test
  public void testConstraints () {
    try {
      // null key not allowed
      new ReadonlyParticipantIdentifier (null, "value");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // null value not allowed
      new ReadonlyParticipantIdentifier ("scheme", null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Both null not allowed
      new ReadonlyParticipantIdentifier (null, null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Empty is not allowed
      new ReadonlyParticipantIdentifier ("scheme", "");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Cannot be mapped to ISO-8859-1:
      new ReadonlyParticipantIdentifier ("scheme", "Љ");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Scheme too long
      new ReadonlyParticipantIdentifier (StringHelper.getRepeated ('a', CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH + 1),
                                         "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Value too long
      new ReadonlyParticipantIdentifier ("scheme",
                                         StringHelper.getRepeated ('a',
                                                                   CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH + 1));
      fail ();
    }
    catch (final IllegalArgumentException ex) {}
  }
}

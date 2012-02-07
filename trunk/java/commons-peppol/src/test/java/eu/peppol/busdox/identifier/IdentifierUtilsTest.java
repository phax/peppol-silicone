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
package eu.peppol.busdox.identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import eu.peppol.busdox.identifier.actorid.EPredefinedIdentifierIssuingAgency;

/**
 * Test class for class {@link IdentifierUtils}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class IdentifierUtilsTest {
  private static final String [] PARTICIPANT_SCHEME_VALID = { "busdox-actorid-upis",
                                                             "BUSDOX-ACTORID-UPIS",
                                                             CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME,
                                                             "any-actorid-any",
                                                             "any-ACTORID-any" };
  private static final String [] PARTIFCIPANT_SCHEME_INVALID = { null,
                                                                "",
                                                                "busdox_actorid_upis",
                                                                "busdox-notactorid-upis",
                                                                "-actorid-upis",
                                                                "actorid-upis",
                                                                "busdox-actorid-",
                                                                "busdox-actorid",
                                                                "any-domain_actorid_any-type",
                                                                "any-nonactoid-anybutmuchtoooooooooooooooooooooooolong" };

  @Test
  public void testIsValidParticipantIdentifierScheme () {
    // valid
    for (final String scheme : PARTICIPANT_SCHEME_VALID)
      assertTrue (IdentifierUtils.isValidParticipantIdentifierScheme (scheme));

    // invalid
    for (final String scheme : PARTIFCIPANT_SCHEME_INVALID)
      assertFalse (IdentifierUtils.isValidParticipantIdentifierScheme (scheme));
  }

  @Test
  public void testAreIdentifiersEqualPariticpantIdentifier () {
    final SimpleParticipantIdentifier aPI1 = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    final SimpleParticipantIdentifier aPI2 = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123ABC");
    final SimpleParticipantIdentifier aPI3a = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123456");
    final SimpleParticipantIdentifier aPI3b = new SimpleParticipantIdentifier ("my-actorid-scheme", "0088:12345");
    assertTrue (IdentifierUtils.areIdentifiersEqual (aPI1, aPI1));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aPI1, aPI2));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aPI2, aPI1));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI1, aPI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI1, aPI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI2, aPI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI2, aPI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI3a, aPI3b));

    try {
      IdentifierUtils.areIdentifiersEqual (aPI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aPI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void testAreIdentifiersEqualDocumentIdentifier () {
    final SimpleDocumentIdentifier aDI1 = SimpleDocumentIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleDocumentIdentifier aDI2 = SimpleDocumentIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleDocumentIdentifier aDI3a = SimpleDocumentIdentifier.createWithDefaultScheme ("urn:doc:anyotherdoc");
    final SimpleDocumentIdentifier aDI3b = new SimpleDocumentIdentifier ("my-docid-test", "urn:doc:anyotherdoc");
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI1));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI2));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI2, aDI1));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI3a, aDI3b));

    try {
      IdentifierUtils.areIdentifiersEqual (aDI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void testAreIdentifiersEqualProcessIdentifier () {
    final SimpleProcessIdentifier aDI1 = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleProcessIdentifier aDI2 = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleProcessIdentifier aDI3a = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anyotherdoc");
    final SimpleProcessIdentifier aDI3b = new SimpleProcessIdentifier ("my-procid-test", "urn:doc:anyotherdoc");
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI1));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI2));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI2, aDI1));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI3a, aDI3b));

    try {
      IdentifierUtils.areIdentifiersEqual (aDI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void getIdentifierURIEncoded () {
    final SimpleParticipantIdentifier aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    assertEquals ("iso6523-actorid-upis::0088:123abc", IdentifierUtils.getIdentifierURIEncoded (aPI));

    final SimpleDocumentIdentifier aDI = SimpleDocumentIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    assertEquals ("busdox-docid-qns::urn:doc:anydoc", IdentifierUtils.getIdentifierURIEncoded (aDI));

    try {
      IdentifierUtils.getIdentifierURIEncoded (new SimpleParticipantIdentifier (null, "0088:12345"));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.getIdentifierURIEncoded (new SimpleParticipantIdentifier ("", "0088:12345"));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.getIdentifierURIEncoded (SimpleParticipantIdentifier.createWithDefaultScheme (null));
      fail ("null value should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }
  }

  @Test
  public void testGetIdentifierURIPercentEncoded () {
    SimpleParticipantIdentifier aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
    aPI = SimpleParticipantIdentifier.createWithDefaultScheme (EPredefinedIdentifierIssuingAgency.GLN.createIdentifierValue ("123abc"));
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
    aPI = EPredefinedIdentifierIssuingAgency.GLN.createParticipantIdentifier ("123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", IdentifierUtils.getIdentifierURIPercentEncoded (aPI));

    // Different value
    aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088/123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%2F123abc", IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
  }
}

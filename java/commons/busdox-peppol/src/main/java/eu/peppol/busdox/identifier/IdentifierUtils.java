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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;

import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;

import eu.peppol.busdox.uri.BusdoxURLUtils;

/**
 * This class contains several identifier related utility methods.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class IdentifierUtils {
  private IdentifierUtils () {}

  /**
   * Check if the given scheme is a valid participant identifier scheme. It is
   * valid if it has at last 25 characters and matches a certain regular
   * expression. Please note that the regular expression is applied case
   * insensitive!<br>
   * This limitation is important, because the participant identifier scheme is
   * directly encoded into the SML DNS name record.
   *
   * @param sScheme
   *        The scheme to check.
   * @return <code>true</code> if the passed scheme is a valid participant
   *         identifier scheme, <code>false</code> otherwise.
   * @see CIdentifier#PARTICIPANT_IDENTIFIER_SCHEME_REGEX
   */
  public static boolean isValidParticipantIdentifierScheme (@Nullable final String sScheme) {
    return sScheme != null &&
           sScheme.length () <= CIdentifier.MAX_PARTICIPANT_IDENTIFIER_SCHEME_LENGTH &&
           RegExHelper.stringMatchesPattern (CIdentifier.PARTICIPANT_IDENTIFIER_SCHEME_REGEX, sScheme.toLowerCase ());
  }

  /**
   * According to the specification, two participant identifiers are equal if
   * their parts are equal case insensitive.
   *
   * @param sIdentifierValue1
   *        First identifier value to compare. May be <code>null</code>.
   * @param sIdentifierValue2
   *        Second identifier value to compare. May be <code>null</code>.
   * @return <code>true</code> if the identifier values are equal,
   *         <code>false</code> otherwise.
   */
  public static boolean areParticipantIdentifierValuesEqual (@Nullable final String sIdentifierValue1,
                                                             @Nullable final String sIdentifierValue2) {
    // equal case insensitive!
    return EqualsUtils.nullSafeEqualsIgnoreCase (sIdentifierValue1, sIdentifierValue2);
  }

  /**
   * According to the specification, two document identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param sIdentifierValue1
   *        First identifier value to compare. May be <code>null</code>.
   * @param sIdentifierValue2
   *        Second identifier value to compare. May be <code>null</code>.
   * @return <code>true</code> if the identifier values are equal,
   *         <code>false</code> otherwise.
   */
  public static boolean areDocumentIdentifierValuesEqual (@Nullable final String sIdentifierValue1,
                                                          @Nullable final String sIdentifierValue2) {
    // Case sensitive!
    return EqualsUtils.nullSafeEquals (sIdentifierValue1, sIdentifierValue2);
  }

  /**
   * According to the specification, two process identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param sIdentifierValue1
   *        First identifier value to compare. May be <code>null</code>.
   * @param sIdentifierValue2
   *        Second identifier value to compare. May be <code>null</code>.
   * @return <code>true</code> if the identifier values are equal,
   *         <code>false</code> otherwise.
   */
  public static boolean areProcessIdentifierValuesEqual (@Nullable final String sIdentifierValue1,
                                                         @Nullable final String sIdentifierValue2) {
    // Case sensitive!
    return EqualsUtils.nullSafeEquals (sIdentifierValue1, sIdentifierValue2);
  }

  /**
   * According to the specification, two participant identifiers are equal if
   * their parts are equal case insensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final IReadonlyParticipantIdentifier aIdentifier1,
                                             @Nonnull final IReadonlyParticipantIdentifier aIdentifier2) {
    if (aIdentifier1 == null)
      throw new NullPointerException ("identifier1");
    if (aIdentifier2 == null)
      throw new NullPointerException ("identifier2");

    // Identifiers are equal, if both scheme and value match case insensitive!
    return EqualsUtils.nullSafeEqualsIgnoreCase (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.nullSafeEqualsIgnoreCase (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * According to the specification, two document identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final IReadonlyDocumentIdentifier aIdentifier1,
                                             @Nonnull final IReadonlyDocumentIdentifier aIdentifier2) {
    if (aIdentifier1 == null)
      throw new NullPointerException ("identifier1");
    if (aIdentifier2 == null)
      throw new NullPointerException ("identifier2");

    // Identifiers are equal, if both scheme and value match case sensitive!
    return EqualsUtils.nullSafeEquals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.nullSafeEquals (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * According to the specification, two process identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final IReadonlyProcessIdentifier aIdentifier1,
                                             @Nonnull final IReadonlyProcessIdentifier aIdentifier2) {
    if (aIdentifier1 == null)
      throw new NullPointerException ("identifier1");
    if (aIdentifier2 == null)
      throw new NullPointerException ("identifier2");

    // Identifiers are equal, if both scheme and value match case sensitive!
    return EqualsUtils.nullSafeEquals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.nullSafeEquals (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * Get the identifier suitable for an URI but NOT percent encoded.
   *
   * @param aIdentifier
   *        The identifier to be encoded. May not be <code>null</code>.
   * @return The URI encoded participant identifier (scheme::value). Never
   *         <code>null</code>.
   * @see #getIdentifierURIPercentEncoded(IReadonlyIdentifier)
   */
  @Nonnull
  public static String getIdentifierURIEncoded (@Nonnull final IReadonlyIdentifier aIdentifier) {
    final String sScheme = aIdentifier.getScheme ();
    if (StringHelper.hasNoText (sScheme))
      throw new IllegalArgumentException ("Passed identifier has an empty scheme: " + aIdentifier);

    final String sValue = aIdentifier.getValue ();
    if (sValue == null)
      throw new IllegalArgumentException ("Passed identifier has a null value: " + aIdentifier);

    // Combine scheme and value
    return sScheme + CIdentifier.URL_SCHEME_VALUE_SEPARATOR + sValue;
  }

  /**
   * Get the identifier suitable for an URI and percent encoded.
   *
   * @param aIdentifier
   *        The identifier to be encoded. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  public static String getIdentifierURIPercentEncoded (@Nonnull final IReadonlyIdentifier aIdentifier) {
    return BusdoxURLUtils.createPercentEncodedURL (getIdentifierURIEncoded (aIdentifier));
  }

  /**
   * Take the passed URI part and try to convert it back to a document
   * identifier. The URI part must have the layout <code>scheme::value</code>
   *
   * @param sURIPart
   *        The URI part to be scanned. May not be <code>null</code> if a
   *        correct result is expected.
   * @return The document identifier matching the passed URI part
   */
  @Nonnull
  public static SimpleDocumentIdentifier createDocumentIdentifierFromURIPart (@Nonnull final String sURIPart) {
    final String [] aSplitted = RegExHelper.split (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
    if (aSplitted.length != 2)
      throw new IllegalArgumentException ("Document identifier '" +
                                          sURIPart +
                                          "' did not include correct delimiter: " +
                                          CIdentifier.URL_SCHEME_VALUE_SEPARATOR);

    return new SimpleDocumentIdentifier (aSplitted[0], aSplitted[1]);
  }

  /**
   * Take the passed URI part and try to convert it back to a participant
   * identifier. The URI part must have the layout <code>scheme::value</code>
   *
   * @param sURIPart
   *        The URI part to be scanned. May not be <code>null</code> if a
   *        correct result is expected.
   * @return The participant identifier matching the passed URI part
   */
  @Nonnull
  public static SimpleParticipantIdentifier createParticipantIdentifierFromURIPart (@Nonnull final String sURIPart) {
    final String [] aSplitted = RegExHelper.split (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
    if (aSplitted.length != 2)
      throw new IllegalArgumentException ("Participant identifier '" +
                                          sURIPart +
                                          "' did not include correct delimiter: " +
                                          CIdentifier.URL_SCHEME_VALUE_SEPARATOR);

    return new SimpleParticipantIdentifier (aSplitted[0], aSplitted[1]);
  }

  /**
   * Take the passed URI part and try to convert it back to a process
   * identifier. The URI part must have the layout <code>scheme::value</code>
   *
   * @param sURIPart
   *        The URI part to be scanned. May not be <code>null</code> if a
   *        correct result is expected.
   * @return The process identifier matching the passed URI part
   */
  @Nonnull
  public static SimpleProcessIdentifier createProcessIdentifierFromURIPart (@Nonnull final String sURIPart) {
    final String [] aSplitted = RegExHelper.split (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
    if (aSplitted.length != 2)
      throw new IllegalArgumentException ("Process identifier '" +
                                          sURIPart +
                                          "' did not include correct delimiter: " +
                                          CIdentifier.URL_SCHEME_VALUE_SEPARATOR);

    return new SimpleProcessIdentifier (aSplitted[0], aSplitted[1]);
  }

  @Nullable
  public static String getUnifiedParticipantDBValue (@Nullable final String sValue) {
    return sValue == null ? null : sValue.toLowerCase (Locale.US);
  }
}

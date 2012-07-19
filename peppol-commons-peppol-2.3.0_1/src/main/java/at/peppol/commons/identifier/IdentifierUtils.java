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

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.uri.BusdoxURLUtils;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;

/**
 * This class contains several identifier related utility methods.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class IdentifierUtils {
  public static final boolean DEFAULT_CHARSET_CHECKS_DISABLED = false;

  // Grab the Charset objects, as they are thread-safe to use. The CharsetEncode
  // objects are not thread-safe and therefore queried each time
  private static final Charset CHARSET_ASCII = CCharset.CHARSET_US_ASCII_OBJ;
  private static final Charset CHARSET_ISO88591 = CCharset.CHARSET_ISO_8859_1_OBJ;

  private static final String PATTERN_PARTICIPANT_ID = "^([^:]*):(.*)$";

  private static final AtomicBoolean s_aCharsetChecksDisabled = new AtomicBoolean (DEFAULT_CHARSET_CHECKS_DISABLED);

  private IdentifierUtils () {}

  /**
   * @return <code>true</code> if the charset checks for identifier values are
   *         disabled, <code>false</code> if they are enabled
   */
  public static boolean areCharsetChecksDisabled () {
    return s_aCharsetChecksDisabled.get ();
  }

  /**
   * Enable or disable the charset checks. You may disable charset checks, if
   * you previously checked them for consistency.
   * 
   * @param bDisable
   *        if <code>true</code> all charset checks are disabled. If
   *        <code>false</code> charset checks are enabled
   */
  public static void disableCharsetChecks (final boolean bDisable) {
    s_aCharsetChecksDisabled.set (bDisable);
  }

  /**
   * Check if the given scheme is a valid participant identifier scheme. It is
   * valid if it has at least 1 character and at last 25 characters and matches
   * a certain regular expression. Please note that the regular expression is
   * applied case insensitive!<br>
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
    return isValidIdentifierScheme (sScheme) &&
           RegExHelper.stringMatchesPattern (CIdentifier.PARTICIPANT_IDENTIFIER_SCHEME_REGEX, sScheme.toLowerCase ());
  }

  /**
   * Check if the given identifier is valid. It is valid if it has at least 1
   * character and at last 25 characters. This method applies to all identifier
   * schemes, but there is a special version for participant identifier schemes,
   * as they are used in DNS names!
   * 
   * @param sScheme
   *        The scheme to check.
   * @return <code>true</code> if the passed scheme is a valid identifier
   *         scheme, <code>false</code> otherwise.
   * @see #isValidParticipantIdentifierScheme(String)
   */
  public static boolean isValidIdentifierScheme (@Nullable final String sScheme) {
    if (sScheme == null)
      return false;
    final int nLength = sScheme.length ();
    return nLength > 0 && nLength <= CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH;
  }

  /**
   * Check if the passed participant identifier value is valid. A valid
   * identifier must have at least 1 character and at last
   * {@link CIdentifier#MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH} characters.
   * Also it must be US ASCII encoded.
   * 
   * @param sValue
   *        The participant identifier value to be checked (without the scheme)
   * @return <code>true</code> if the participant identifier value is valid,
   *         <code>false</code> otherwise
   */
  public static boolean isValidParticipantIdentifierValue (@Nullable final String sValue) {
    final int nLength = StringHelper.getLength (sValue);
    if (nLength == 0 || nLength > CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
      return false;

    // Check if the value is US ASCII encoded
    return areCharsetChecksDisabled () || CHARSET_ASCII.newEncoder ().canEncode (sValue);
  }

  /**
   * Check if the passed document type identifier value is valid. A valid
   * identifier must have at least 1 character and at last
   * {@link CIdentifier#MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH} characters.
   * Also it must be ISO-8859-1 encoded.
   * 
   * @param sValue
   *        The document type identifier value to be checked (without the
   *        scheme)
   * @return <code>true</code> if the document type identifier value is valid,
   *         <code>false</code> otherwise
   */
  public static boolean isValidDocumentTypeIdentifierValue (@Nullable final String sValue) {
    final int nLength = StringHelper.getLength (sValue);
    if (nLength == 0 || nLength > CIdentifier.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH)
      return false;

    // Check if the value is ISO-8859-1 encoded
    return areCharsetChecksDisabled () || CHARSET_ISO88591.newEncoder ().canEncode (sValue);
  }

  /**
   * Check if the passed process identifier value is valid. A valid identifier
   * must have at least 1 character and at last
   * {@link CIdentifier#MAX_PROCESS_IDENTIFIER_VALUE_LENGTH} characters. Also it
   * must be ISO-8859-1 encoded.
   * 
   * @param sValue
   *        The process identifier value to be checked (without the scheme)
   * @return <code>true</code> if the process identifier value is valid,
   *         <code>false</code> otherwise
   */
  public static boolean isValidProcessIdentifierValue (@Nullable final String sValue) {
    final int nLength = StringHelper.getLength (sValue);
    if (nLength == 0 || nLength > CIdentifier.MAX_PROCESS_IDENTIFIER_VALUE_LENGTH)
      return false;

    // Check if the value is ISO-8859-1 encoded
    return areCharsetChecksDisabled () || CHARSET_ISO88591.newEncoder ().canEncode (sValue);
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
    return EqualsUtils.equals (sIdentifierValue1, sIdentifierValue2);
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
    return EqualsUtils.equals (sIdentifierValue1, sIdentifierValue2);
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
  public static boolean areIdentifiersEqual (@Nonnull final IReadonlyDocumentTypeIdentifier aIdentifier1,
                                             @Nonnull final IReadonlyDocumentTypeIdentifier aIdentifier2) {
    if (aIdentifier1 == null)
      throw new NullPointerException ("identifier1");
    if (aIdentifier2 == null)
      throw new NullPointerException ("identifier2");

    // Identifiers are equal, if both scheme and value match case sensitive!
    return EqualsUtils.equals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.equals (aIdentifier1.getValue (), aIdentifier2.getValue ());
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
    return EqualsUtils.equals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.equals (aIdentifier1.getValue (), aIdentifier2.getValue ());
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
    if (aIdentifier == null)
      throw new NullPointerException ("identifier");

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
  @Nonnull
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
   * @throws IllegalArgumentException
   *         If the passed identifier is not a valid URI encoded identifier
   */
  @Nonnull
  public static SimpleDocumentTypeIdentifier createDocumentTypeIdentifierFromURIPart (@Nonnull final String sURIPart) {
    if (sURIPart == null)
      throw new NullPointerException ("URIPart");

    final String [] aSplitted = RegExHelper.getSplitToArray (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
    if (aSplitted.length != 2)
      throw new IllegalArgumentException ("Document type identifier '" +
                                          sURIPart +
                                          "' did not include correct delimiter: " +
                                          CIdentifier.URL_SCHEME_VALUE_SEPARATOR);

    return new SimpleDocumentTypeIdentifier (aSplitted[0], aSplitted[1]);
  }

  /**
   * Take the passed URI part and try to convert it back to a participant
   * identifier. The URI part must have the layout <code>scheme::value</code>
   * 
   * @param sURIPart
   *        The URI part to be scanned. May not be <code>null</code> if a
   *        correct result is expected.
   * @return The participant identifier matching the passed URI part
   * @throws IllegalArgumentException
   *         If the passed identifier is not a valid URI encoded identifier
   */
  @Nonnull
  public static SimpleParticipantIdentifier createParticipantIdentifierFromURIPart (@Nonnull final String sURIPart) {
    if (sURIPart == null)
      throw new NullPointerException ("URIPart");

    final String [] aSplitted = RegExHelper.getSplitToArray (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
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
   * @throws IllegalArgumentException
   *         If the passed identifier is not a valid URI encoded identifier
   */
  @Nonnull
  public static SimpleProcessIdentifier createProcessIdentifierFromURIPart (@Nonnull final String sURIPart) {
    if (sURIPart == null)
      throw new NullPointerException ("URIPart");

    final String [] aSplitted = RegExHelper.getSplitToArray (sURIPart, CIdentifier.URL_SCHEME_VALUE_SEPARATOR, 2);
    if (aSplitted.length != 2)
      throw new IllegalArgumentException ("Process identifier '" +
                                          sURIPart +
                                          "' did not include correct delimiter: " +
                                          CIdentifier.URL_SCHEME_VALUE_SEPARATOR);

    return new SimpleProcessIdentifier (aSplitted[0], aSplitted[1]);
  }

  /**
   * Central method for unifying participant identifier values for storage in a
   * DB, as participant identifier values need to be handled case-insensitive.
   * This method can be applied both to participant identifier schemes and
   * business identifier values.
   * 
   * @param sValue
   *        The DB identifier value to unify. May be <code>null</code>.
   * @return <code>null</code> if the passed value is <code>null</code>
   */
  @Nullable
  public static String getUnifiedParticipantDBValue (@Nullable final String sValue) {
    return sValue == null ? null : sValue.toLowerCase (Locale.US);
  }

  @Nullable
  public static String getIssuingAgencyIDFromParticipantIDValue (@Nonnull final IExtendedParticipantIdentifier aIdentifier) {
    if (!aIdentifier.isDefaultScheme ())
      return null;
    return ArrayHelper.getSafeElement (RegExHelper.getAllMatchingGroupValues (PATTERN_PARTICIPANT_ID,
                                                                              aIdentifier.getValue ()),
                                       0);
  }

  @Nullable
  public static String getLocalParticipantIDFromParticipantIDValue (@Nonnull final IExtendedParticipantIdentifier aIdentifier) {
    if (!aIdentifier.isDefaultScheme ())
      return null;
    return ArrayHelper.getSafeElement (RegExHelper.getAllMatchingGroupValues (PATTERN_PARTICIPANT_ID,
                                                                              aIdentifier.getValue ()),
                                       1);
  }
}

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
package at.peppol.commons.uri;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.sml.ISMLInfo;

import com.google.gdata.util.common.base.CharEscapers;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.messagedigest.EMessageDigestAlgorithm;
import com.phloc.commons.messagedigest.MessageDigestGenerator;
import com.phloc.commons.string.StringHelper;


/**
 * Utility methods for assembling URLs and URL elements required for Busdox.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class BusdoxURLUtils {
  public static final Charset URL_CHARSET = CCharset.CHARSET_UTF_8_OBJ;
  public static final Locale URL_LOCALE = Locale.US;
  public static final String ENCODING_GZIP = "gzip";
  public static final String ENCODING_DEFLATE = "deflate";
  private static final Logger s_aLogger = LoggerFactory.getLogger (BusdoxURLUtils.class);

  private BusdoxURLUtils () {}

  /**
   * Escape the passed URL to use the percentage maskings.
   *
   * @param sURL
   *        The input URL or URL part. May be <code>null</code>.
   * @return <code>null</code> if the input stream was <code>null</code>.
   */
  @Nullable
  public static String createPercentEncodedURL (@Nullable final String sURL) {
    return sURL == null ? null : CharEscapers.uriEscaper (false).escape (sURL);
  }

  /**
   * Get the MD5-hash-string-representation of the passed value using the
   * {@link #URL_CHARSET} encoding. Each hash byte is represented as 2
   * characters in the range [0-9a-f]. Note: the hash value creation is done
   * case sensitive! The caller needs to ensure that the value to hash is lower
   * case!
   *
   * @param sValueToHash
   *        The value to be hashed. May not be <code>null</code>.
   * @return The non-<code>null</code> String containing the hash value.
   */
  @Nonnull
  public static String getHashValueStringRepresentation (@Nonnull final String sValueToHash) {
    // Create the MD5 hash
    final MessageDigestGenerator aMDGen = new MessageDigestGenerator (EMessageDigestAlgorithm.MD5);
    aMDGen.update (sValueToHash.getBytes (URL_CHARSET));
    return aMDGen.getDigestHexString ();
  }

  /**
   * Get DNS record from ParticipantIdentifier. "0010:1234 | scheme" ->
   * "B-&lt;hash over pi>.&lt;scheme>.&lt;sml-zone-name>".
   *
   * @param aParticipantIdentifier
   *        Participant identifier. May not be <code>null</code>.
   * @param aSML
   *        The SML information object to be used. May not be <code>null</code>.
   * @return DNS record
   */
  @Nonnull
  public static String getDNSNameOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                                @Nonnull final ISMLInfo aSML) {
    return getDNSNameOfParticipant (aParticipantIdentifier, aSML.getDNSZone ());
  }

  /**
   * Get DNS record from ParticipantIdentifier. "0010:1234 | scheme" ->
   * "B-&lt;hash over pi>.&lt;scheme>.&lt;sml-zone-name>". This method ensures
   * that the hash value is created from the lower case value of the identifier.
   * Lower casing is done with the {@link #URL_LOCALE} locale.
   *
   * @param aParticipantIdentifier
   *        Participant identifier. May not be <code>null</code>.
   * @param sSMLZoneName
   *        e.g. "sml.peppolcentral.org.". May be empty. If it is not empty, it
   *        must end with a dot!
   * @return DNS record. It does not contain any prefix like http:// or any path
   *         suffix. It is the plain DNS host name.
   */
  @Nonnull
  public static String getDNSNameOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                                @Nullable final String sSMLZoneName) {
    if (aParticipantIdentifier == null)
      throw new NullPointerException ("participantIdentifier");
    if (StringHelper.hasNoText (aParticipantIdentifier.getScheme ()))
      throw new IllegalArgumentException ("ParticipantIdentifier has no scheme: " + aParticipantIdentifier);
    if (StringHelper.hasNoText (aParticipantIdentifier.getValue ()))
      throw new IllegalArgumentException ("ParticipantIdentifier has no value: " + aParticipantIdentifier);
    if (StringHelper.hasText (sSMLZoneName) && !StringHelper.endsWith (sSMLZoneName, '.'))
      throw new IllegalArgumentException ("if an SML zone name is specified, it must end with a dot (.)");

    // Check identifier scheme (must be lowercase for the URL later on!)
    final String sScheme = aParticipantIdentifier.getScheme ().toLowerCase (URL_LOCALE);
    if (!IdentifierUtils.isValidParticipantIdentifierScheme (sScheme))
      throw new IllegalArgumentException ("Invalid participant identifier scheme '" + sScheme + "'");

    // Get the identifier value
    final String sValue = aParticipantIdentifier.getValue ();
    final StringBuilder ret = new StringBuilder ();
    if ("*".equals (sValue)) {
      // Wild card registration
      ret.append ("*.");
    }
    else {
      // Important: create hash from lowercase string!
      // Here the "B-0011223344..." string is assembled!
      ret.append (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX)
         .append (getHashValueStringRepresentation (sValue.toLowerCase (URL_LOCALE)))
         .append ('.');
    }

    // append the identifier scheme
    ret.append (sScheme).append ('.');

    // append the SML DNS zone name (if available)
    if (StringHelper.hasText (sSMLZoneName))
      ret.append (sSMLZoneName);

    // We're fine and done
    return ret.toString ();
  }

  @Nonnull
  public static URI getSMPURIOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                            @Nonnull final ISMLInfo aSMLInfo) {
    return getSMPURIOfParticipant (aParticipantIdentifier, aSMLInfo.getDNSZone ());
  }

  @Nonnull
  public static URI getSMPURIOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                            @Nullable final String sSMLZoneName) {
    try {
      return new URI ("http://" + getDNSNameOfParticipant (aParticipantIdentifier, sSMLZoneName));
    }
    catch (final URISyntaxException ex) {
      throw new IllegalArgumentException ("Error building SMP URI", ex);
    }
  }

  @Nonnull
  public static URL getSMPURLOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                            @Nonnull final ISMLInfo aSMLInfo) {
    return getSMPURLOfParticipant (aParticipantIdentifier, aSMLInfo.getDNSZone ());
  }

  @Nonnull
  public static URL getSMPURLOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                            @Nullable final String sSMLZoneName) {
    try {
      return getSMPURIOfParticipant (aParticipantIdentifier, sSMLZoneName).toURL ();
    }
    catch (final MalformedURLException ex) {
      throw new IllegalArgumentException ("Error building SMP URL", ex);
    }
  }

  /**
   * Gets the content of a given url.
   *
   * @param sURL
   *        URL where the content is allocated.
   * @return URL content or <code>null</code> when fetching the content of the
   *         URL fails.
   */
  @Nullable
  public static String getURLContent (@Nonnull final String sURL) {
    InputStream aIS = null;
    BufferedReader aBR = null;

    try {
      final URL aUrl = new URL (sURL);

      final HttpURLConnection aHttpConn = (HttpURLConnection) aUrl.openConnection ();
      aHttpConn.setReadTimeout (2000);
      aHttpConn.connect ();
      final String sEncoding = aHttpConn.getContentEncoding ();

      aIS = aHttpConn.getInputStream ();
      if (sEncoding != null) {
        if (sEncoding.equalsIgnoreCase (ENCODING_GZIP))
          aIS = new GZIPInputStream (aIS);
        else
          if (sEncoding.equalsIgnoreCase (ENCODING_DEFLATE))
            aIS = new InflaterInputStream (aIS);
      }

      aBR = new BufferedReader (new InputStreamReader (aIS));

      final StringBuilder aSB = new StringBuilder ();
      String sLine = null;
      while ((sLine = aBR.readLine ()) != null)
        aSB.append (sLine).append ("\n");
      return aSB.toString ();
    }
    catch (final FileNotFoundException ex) {
      // No need for exception stack trace
      s_aLogger.error ("URL does not exist: '" + sURL + "'");
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error reading URL '" + sURL + "'", ex);
    }
    finally {
      StreamUtils.close (aBR);
      StreamUtils.close (aIS);
    }
    return null;
  }
}

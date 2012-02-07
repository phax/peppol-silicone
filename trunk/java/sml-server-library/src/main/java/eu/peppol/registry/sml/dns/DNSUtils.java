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
package eu.peppol.registry.sml.dns;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;

import com.phloc.commons.regex.RegExHelper;

import eu.peppol.registry.sml.exceptions.IllegalHostnameException;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DNSUtils {
  private static final Logger log = LoggerFactory.getLogger (DNSUtils.class);

  private DNSUtils () {}

  /**
   * Utility methods which extracts Identifier Hash Value from DNS record.
   * Additionally it is checked whether the passed DNS name belongs to the given
   * SML zone.
   *
   * @param sDNSName
   *        The DNS service name.
   * @param smlZoneName
   *        The SML zone name to which the DNS name must belong
   * @return the hash value from the DNS name or <code>null</code> if parsing
   *         failed
   */
  @Nullable
  public static String getIdentifierHashValueFromDnsName (final String sDNSName, final String smlZoneName) {
    final ParticipantIdentifierType aPI = getIdentiferFromDnsName (sDNSName, smlZoneName);
    return aPI == null ? null : aPI.getValue ();
  }

  /**
   * Utility methods which extracts Identifier Hash Value from DNS record.
   *
   * @param sDNSName
   * @return the hash value from the DNS name or <code>null</code> if parsing
   *         failed
   */
  @Nullable
  public static String getIdentifierHashValueFromDnsName (final String sDNSName) {
    final ParticipantIdentifierType aPI = getIdentiferFromDnsName (sDNSName);
    return aPI == null ? null : aPI.getValue ();
  }

  /**
   * Utility methods which extracts ParticipantIdentifier from DNS record.
   *
   * @param sName
   * @param smlZoneName
   * @return ParticipantIdentifier or <code>null</code> if parsing failed
   */
  @Nullable
  public static ParticipantIdentifierType getIdentiferFromDnsName (@Nonnull final String sName,
                                                                   @Nonnull final String smlZoneName) {
    if (!sName.endsWith ("." + smlZoneName)) {
      log.warn ("wrong DNS zone : " + sName + " not in : " + smlZoneName);
      return null;
    }

    // Remove trailing SML zone name
    final String sIdentifierPart = sName.substring (0, sName.length () - (1 + smlZoneName.length ()));
    return getIdentiferFromDnsName (sIdentifierPart);
  }

  /**
   * Utility methods which extracts ParticipantIdentifier from DNS record.
   *
   * @param sDNSName
   * @return ParticipantIdentifier or <code>null</code> if parsing failed
   */
  @Nullable
  public static ParticipantIdentifierType getIdentiferFromDnsName (@Nullable final String sDNSName) {
    // Split in hash, scheme and rest
    final String [] parts = RegExHelper.split (sDNSName, "\\.", 3);
    if (parts.length < 2) {
      log.warn ("wrong syntax of identifier - must contain at least on separator : " + sDNSName);
      return null;
    }

    // Check scheme
    final String sSchemeID = parts[1];
    if (!IdentifierUtils.isValidParticipantIdentifierScheme (sSchemeID)) {
      log.warn ("wrong syntax of identifier - scheme is invalid : " + sSchemeID);
      return null;
    }

    // check hash
    String sHash = parts[0];
    if (!sHash.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX)) {
      // must start with "B-"
      log.warn ("wrong syntax of identifier - must start with : " + CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
      return null;
    }

    sHash = sHash.substring (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX.length ());

    // is it a valid MD5 hash?
    if (sHash.length () != 32) {
      log.warn ("id part : " + sHash + " is not hashed; length=" + sHash.length ());
      return null;
    }

    if (!RegExHelper.stringMatchesPattern ("[0-9a-f]{32}", sHash)) {
      log.warn ("id part : " + sHash + " contains illegal characters");
      return null;
    }

    // OK, all checks done!
    return new SimpleParticipantIdentifier (parts[1], sHash);
  }

  /**
   * Get the SMP ID from the passed DNS name. SMP DNS names are identified by
   * the ".publisher." identifier in the name.
   *
   * @param sDNSName
   * @param sSMLZoneName
   * @return <code>null</code> if the passed DNS name is not an SMP DNS name
   */
  @Nullable
  public static String getPublisherAnchorFromDnsName (@Nonnull final String sDNSName, @Nonnull final String sSMLZoneName) {
    if (log.isDebugEnabled ())
      log.debug ("Get PublisherAnchroFromDnsName : " + sDNSName);
    if (!isHandledZone (sDNSName, sSMLZoneName)) {
      log.error ("This is not correct zone : " + sDNSName + " not in : " + sSMLZoneName);
      return null;
    }

    final String sDNSNameLC = sDNSName.toLowerCase ();
    final String sSuffix = ".publisher." + sSMLZoneName;
    if (sDNSNameLC.endsWith (sSuffix))
      return sDNSName.substring (0, sDNSName.length () - sSuffix.length ());

    if (log.isDebugEnabled ())
      log.debug ("This is not Publisher Anchor " + sDNSName);
    return null;
  }

  public static boolean isHandledZone (@Nonnull final String sDNSName, @Nonnull final String sSMLZoneName) {
    if (log.isDebugEnabled ())
      log.debug ("isHandledZone : " + sDNSName + " : " + sSMLZoneName);

    return sDNSName.toLowerCase ().endsWith ("." + sSMLZoneName);
  }

  private static final String DOMAIN_IDENTIFIER = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
  private static final String DOMAIN_NAME_RULE = "(" + DOMAIN_IDENTIFIER + ")((\\.)(" + DOMAIN_IDENTIFIER + "))*";

  public static void verifyHostname (final String host) throws IllegalHostnameException {
    if (log.isDebugEnabled ())
      log.debug ("verifyHostname : " + host);

    if (host == null)
      throw new IllegalHostnameException ("Hostname cannot be 'null'");

    if (host.length () > 253)
      throw new IllegalHostnameException ("Hostname total length > 253 : " + host + " : " + host.length ());

    if (!host.matches (DOMAIN_NAME_RULE))
      throw new IllegalHostnameException ("Hostname not legal : " + host);

    final String [] parts = RegExHelper.split (host, "\\.");
    for (final String p : parts)
      if (p.length () > 63)
        throw new IllegalHostnameException ("Hostname part length > 63 : " + host);
  }

  public static boolean isValidHostname (final String host) {
    try {
      verifyHostname (host);
      return true;
    }
    catch (final IllegalHostnameException ex) {
      return false;
    }
  }
}

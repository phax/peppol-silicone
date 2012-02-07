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
package eu.peppol.registry.sml.management;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.concurrent.Immutable;

import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;

import eu.peppol.busdox.identifier.CIdentifier;
import eu.peppol.busdox.identifier.IdentifierUtils;
import eu.peppol.busdox.identifier.actorid.IdentifierIssuingAgencyManager;
import eu.peppol.busdox.sml.CSMLDefault;
import eu.peppol.registry.sml.exceptions.BadRequestException;

/**
 * Validates Input values. Is used in the two web service implementations.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DataValidator {
  private static final String DOMAIN_IDENTIFIER = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
  private static final String DOMAIN_NAME_RULE = "(" + DOMAIN_IDENTIFIER + ")((\\.)(" + DOMAIN_IDENTIFIER + "))*";
  private static final String IP_RULE = "(\\p{Digit})+(\\.)(\\p{Digit})+(\\.)(\\p{Digit})+(\\.)(\\p{Digit})+";

  private DataValidator () {}

  public static void validate (final IReadonlyParticipantIdentifier pi) throws BadRequestException {
    final String sScheme = pi.getScheme ();
    if (StringHelper.hasNoText (sScheme))
      throw new BadRequestException ("Participant Identifier Scheme cannot be 'null' or empty");

    // Check if the scheme matches the rules
    if (!IdentifierUtils.isValidParticipantIdentifierScheme (sScheme))
      throw new BadRequestException ("Illegal format of Participant Identifier Scheme: " + sScheme);

    // If it is the default identifier scheme, check if the agency is valid
    if (sScheme.equals (CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME)) {
      // Check if the value matches the format "agency:id" and whether the
      // agency code is known
      final String sValue = pi.getValue ();
      if (StringHelper.hasNoText (sValue))
        throw new BadRequestException ("Participant Identifier Value cannot be 'null' or empty");

      final String [] aParts = RegExHelper.split (sValue, ":", 2);
      if (aParts.length != 2)
        throw new BadRequestException ("Participant Identifier Value is not valid for the default scheme");

      final String sIssuingAgency = aParts[0];
      if (!IdentifierIssuingAgencyManager.containsAgencyWithISO6523Code (sIssuingAgency))
        throw new BadRequestException ("Participant Identifier Value contains the illegal issuing agency '" +
                                       sIssuingAgency +
                                       "'");
    }
  }

  public static void validateSMPID (final String sSMPID) throws BadRequestException {
    if (StringHelper.hasNoText (sSMPID))
      throw new BadRequestException ("Publisher ID cannot be 'null' or empty");

    if (!sSMPID.matches (DOMAIN_NAME_RULE))
      throw new BadRequestException ("Publisher ID cannot contain _");
  }

  public static void validate (final ParticipantIdentifierPageType list) throws BadRequestException {
    validateSMPID (list.getServiceMetadataPublisherID ());
    for (final ParticipantIdentifierType pi : list.getParticipantIdentifier ())
      validate (pi);
  }

  public static void validate (final MigrationRecordType aMigrationRecord) throws BadRequestException {
    if (StringHelper.hasNoText (aMigrationRecord.getMigrationKey ()))
      throw new BadRequestException ("Migration key must not be null or empty.");
    if (aMigrationRecord.getMigrationKey ().length () > CSMLDefault.MAX_MIGRATION_CODE_LENGTH)
      throw new BadRequestException ("The migration key exceeds the maximum allowed length of " +
                                     CSMLDefault.MAX_MIGRATION_CODE_LENGTH);

    validate (aMigrationRecord.getParticipantIdentifier ());
    validateSMPID (aMigrationRecord.getServiceMetadataPublisherID ());
  }

  public static void validate (final ServiceMetadataPublisherServiceForParticipantType pi) throws BadRequestException {
    validate (pi.getParticipantIdentifier ());
    validateSMPID (pi.getServiceMetadataPublisherID ());
  }

  public static void validate (final PageRequestType messagePart) throws BadRequestException {
    validateSMPID (messagePart.getServiceMetadataPublisherID ());
  }

  public static void validate (final ServiceMetadataPublisherServiceType messagePart) throws BadRequestException {
    validateSMPID (messagePart.getServiceMetadataPublisherID ());
    validate (messagePart.getPublisherEndpoint ());
  }

  public static void validate (final PublisherEndpointType publisherEndpoint) throws BadRequestException {
    if (StringHelper.hasNoText (publisherEndpoint.getLogicalAddress ()))
      throw new BadRequestException ("Logical address must not be null or empty.");

    if (StringHelper.hasNoText (publisherEndpoint.getPhysicalAddress ()))
      throw new BadRequestException ("Physical address must not be null or empty.");

    try {
      new URL (publisherEndpoint.getLogicalAddress ());
    }
    catch (final MalformedURLException e) {
      throw new BadRequestException ("Logical address is malformed: " + e.getMessage ());
    }

    if (!RegExHelper.stringMatchesPattern (IP_RULE, publisherEndpoint.getPhysicalAddress ()))
      throw new BadRequestException ("Physical address is malformed: " + publisherEndpoint.getPhysicalAddress ());
  }
}

package at.peppol.commons.identifier.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.peppol.commons.identifier.actorid.EPredefinedIdentifierIssuingAgency;

/**
 * Test class for class {@link IdentifierValidator}.
 * 
 * @author philip
 */
public final class IdentifierValidatorTest {
  @Test
  public void testNorwayOrgNumber () {
    assertFalse (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_ORGNR.createParticipantIdentifier ("")));
    assertFalse (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_ORGNR.createParticipantIdentifier ("123456789")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_ORGNR.createParticipantIdentifier ("123456785")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_ORGNR.createParticipantIdentifier ("968218743")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_ORGNR.createParticipantIdentifier ("961329310")));

    assertFalse (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_VAT.createParticipantIdentifier ("")));
    assertFalse (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_VAT.createParticipantIdentifier ("123456789")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_VAT.createParticipantIdentifier ("123456785")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_VAT.createParticipantIdentifier ("968218743")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.NO_VAT.createParticipantIdentifier ("961329310")));

    // No special rules available -> all valid!
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.AD_VAT.createParticipantIdentifier ("123456789")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.AD_VAT.createParticipantIdentifier ("123456785")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.AD_VAT.createParticipantIdentifier ("968218743")));
    assertTrue (IdentifierValidator.isValidParticipantIdentifier (EPredefinedIdentifierIssuingAgency.AD_VAT.createParticipantIdentifier ("961329310")));
  }
}

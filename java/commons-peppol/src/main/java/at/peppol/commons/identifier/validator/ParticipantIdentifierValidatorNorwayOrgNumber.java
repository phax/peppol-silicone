package at.peppol.commons.identifier.validator;

import at.peppol.commons.identifier.actorid.EPredefinedIdentifierIssuingAgency;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.string.StringHelper;

@IsSPIImplementation
public final class ParticipantIdentifierValidatorNorwayOrgNumber implements IParticipantIdentifierValidatorSPI {
  private static final int [] WEIGHTS = new int [] { 3, 2, 7, 6, 5, 4, 3, 2 };

  public boolean isSupportedIssuingAgency (final String sScheme) {
    return EPredefinedIdentifierIssuingAgency.NO_ORGNR.getISO6523Code ().equals (sScheme) ||
           EPredefinedIdentifierIssuingAgency.NO_VAT.getISO6523Code ().equals (sScheme);
  }

  public boolean isValueValid (final String sValue) {
    if (StringHelper.getLength (sValue) != 9)
      return false;

    final char [] aData = sValue.toCharArray ();
    if (!Character.isDigit (aData[8]))
      return false;

    final int nActualCheckDigit = aData[8] - 48;
    int nSum = 0;

    for (int i = 0; i < 8; i++) {
      final char cNext = aData[i];
      if (!Character.isDigit (cNext))
        return false;

      final int nDigit = cNext - 48;
      nSum += nDigit * WEIGHTS[i];
    }

    final int nModulus = nSum % 11;

    /** don't subtract from length if the modulus is 0 */
    if (nModulus == 0 && nActualCheckDigit == 0)
      return true;

    final int nCalculatedCheckDigit = 11 - nModulus;
    return nActualCheckDigit == nCalculatedCheckDigit;
  }
}

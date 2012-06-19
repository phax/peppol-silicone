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

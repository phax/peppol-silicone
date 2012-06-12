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
package at.peppol.validation.pyramid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import at.peppol.commons.cenbii.profiles.ETransaction;
import at.peppol.test.ETestFileType;
import at.peppol.test.TestFiles;
import at.peppol.validation.rules.EValidationDocumentType;
import at.peppol.validation.rules.EValidationLevel;
import at.peppol.validation.rules.ValidationTransaction;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.locale.country.CountryCache;

/**
 * Test class for class {@link ValidationPyramid}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ValidationPyramidTest {
  @Test
  public void testInvoice () {
    final ValidationPyramid vp = new ValidationPyramid (EValidationDocumentType.INVOICE,
                                                        ValidationTransaction.createUBLTransaction (ETransaction.T10));
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.INVOICE)) {
      for (final ValidationPyramidResultLayer aResultLayer : vp.applyValidation (aTestFile)
                                                               .getAllValidationResultLayers ())
        for (final IResourceError aError : aResultLayer.getValidationErrors ())
          System.out.println (aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
    }
  }

  @Test
  public void testInvoiceAT () {
    final Locale aCountry = CountryCache.getCountry ("AT");
    final ValidationPyramid vp = new ValidationPyramid (EValidationDocumentType.INVOICE,
                                                        ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                        aCountry);
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.INVOICE, aCountry)) {
      // Do validation
      final ValidationPyramidResult aResult = vp.applyValidation (aTestFile);
      assertNotNull (aResult);

      // Check that we have results for all levels except entity specific (even
      // of they may be empty)
      for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
        if (!eValidationLevel.equals (EValidationLevel.ENTITY_SPECIFC))
          assertTrue (eValidationLevel.getID () + " is not contained",
                      aResult.containsValidationResultLayerForLevel (eValidationLevel));

      // List all elements of all layers
      int nItems = 0;
      for (final ValidationPyramidResultLayer aResultLayer : aResult.getAllValidationResultLayers ()) {
        assertNotNull (aResultLayer);
        assertNotNull (aResultLayer.getValidationLevel ());
        assertNotNull (aResultLayer.getXMLValidationType ());
        assertNotNull (aResultLayer.getValidationErrors ());

        for (final IResourceError aError : aResultLayer.getValidationErrors ()) {
          System.out.println (aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
          nItems++;
        }
      }

      // Check that the number matches the aggregated number
      assertEquals (nItems, aResult.getAggregatedResults ().size ());
    }
  }
}

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
package eu.peppol.validation.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;

import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceError;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.error.ResourceLocation;
import com.phloc.commons.io.IReadableResource;

import eu.peppol.validation.engine.config.ValidationLayerManager;
import eu.peppol.validation.engine.jaxb.layers.LayerType;
import eu.peppol.validation.engine.jaxb.result.ValidationResultType;
import eu.peppol.validation.engine.result.ValidationResult;
import eu.peppol.validation.engine.result.ValidationResultConverter;
import eu.peppol.validation.engine.validator.SchemaValidator;
import eu.peppol.validation.engine.validator.SchematronValidator;

/**
 * Class to validate a XML document through layers.
 *
 * @version 3.0 10/05/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com) <br>
 *         Jorge Reategui Ravina (jorge@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class Validator {
  private Validator () {}

  /**
   * Set the values for the result when there are not rules applied to a layer.
   *
   * @return The no-validator error
   */
  @Nonnull
  private static IResourceErrorGroup _createNoValidator () {
    return new ResourceErrorGroup (new ResourceError (new ResourceLocation (null),
                                                      EErrorLevel.WARN,
                                                      ValidationMessages.NO_VALIDATOR));
  }

  /**
   * Method to execute the Schema validation.
   *
   * @param aResult
   *        ResultBean to store the results of the validation.
   * @param aXSDPaths
   *        Validators to be used during the validation.
   * @param aXMLRes
   *        File to validate.
   * @return ResultBean.
   */
  @Nullable
  private static IResourceErrorGroup _schemaValidation (final ValidationResult aResult,
                                                        final List <String> aXSDPaths,
                                                        final IReadableResource aXMLRes) {

    if (aXSDPaths.isEmpty ())
      return _createNoValidator ();

    try {
      final ResourceErrorGroup validationResults = new ResourceErrorGroup ();
      for (final String sXSDPath : aXSDPaths)
        validationResults.addResourceErrorGroup (SchemaValidator.validate (aXMLRes, sXSDPath));
      return _resultsFilter (validationResults);
    }
    catch (final IOException ex) {
      aResult.setError (ex);
    }
    return null;
  }

  /**
   * Method to execute the Schematron validation.
   *
   * @param aResult
   *        ResultBean to store the results of the validation.
   * @param aSchematronPaths
   *        Validators to be used during the validation.
   * @param aXMLRes
   *        File to validate.
   * @return ResultBean.
   */
  @Nullable
  private static IResourceErrorGroup _schematronValidation (final ValidationResult aResult,
                                                            final List <String> aSchematronPaths,
                                                            final IReadableResource aXMLRes) {
    if (aSchematronPaths.isEmpty ())
      return _createNoValidator ();

    try {
      final ResourceErrorGroup aOverallResults = new ResourceErrorGroup ();
      for (final String sSchematronFile : aSchematronPaths) {
        final IResourceErrorGroup result = SchematronValidator.validate (aXMLRes, sSchematronFile);
        aOverallResults.addResourceErrorGroup (result);
      }
      return _resultsFilter (aOverallResults);
    }
    catch (final RuntimeException ex) {
      aResult.setError (ex);
      return null;
    }
  }

  /**
   * Method to filter the result received from a validation. If only success
   * messages are contained, return a list with 1 success message. If at least
   * one error message is contained, remove all success messages
   *
   * @param aResourceErrorGroup
   *        List of results.
   * @return List of results filtered.
   */
  @Nonnull
  private static IResourceErrorGroup _resultsFilter (final ResourceErrorGroup aResourceErrorGroup) {
    if (aResourceErrorGroup.isEmpty ())
      return new ResourceErrorGroup (new ResourceError (new ResourceLocation (null),
                                                        EErrorLevel.SUCCESS,
                                                        ValidationMessages.VALIDATION_SUCCESSFULLY));

    if (aResourceErrorGroup.containsOnlySuccess ())
      return new ResourceErrorGroup (aResourceErrorGroup.getAllResourceErrors ().get (0));

    // Use only the failures
    return aResourceErrorGroup.getAllFailures ();
  }

  /**
   * Execute the validation through layers.
   *
   * @param sProfile
   *        CENBII Profile code.
   * @param sDocument
   *        Core Data Model code.
   * @param aXMLRes
   *        The XML resource to be validated.
   * @return The validation result
   * @throws JAXBException
   *         If loading the layers failed
   */
  @Nonnull
  public static ValidationResultType executeValidationLayers (final String sProfile,
                                                              final String sDocument,
                                                              final IReadableResource aXMLRes) throws JAXBException {

    final List <ValidationResult> aLayerResults = new ArrayList <ValidationResult> ();

    // 1. Load all the Validation layers to set up the Validation Process.
    // 2. go through each validation Layer and set up a validation process.
    for (final LayerType aLayer : ValidationLayerManager.getAllLayers (sProfile, sDocument)) {
      final ValidationResult aLayerResult = new ValidationResult (aLayer);
      switch (aLayer.getValidationType ()) {
        case SCHEMA:
          // Schema Validation
          aLayerResult.setResults (_schemaValidation (aLayerResult, aLayer.getValidationDoc (), aXMLRes));
          break;
        case SCHEMATRON:
          // Schematron Validation
          aLayerResult.setResults (_schematronValidation (aLayerResult, aLayer.getValidationDoc (), aXMLRes));
          break;
        default:
          throw new IllegalStateException ("Unknown validator type " + aLayer.getValidationType ());
      }

      // Add to the total result list
      aLayerResults.add (aLayerResult);
    }

    // 3. Create the XML result.
    return ValidationResultConverter.createResult (sProfile, sDocument, aLayerResults);
  }
}

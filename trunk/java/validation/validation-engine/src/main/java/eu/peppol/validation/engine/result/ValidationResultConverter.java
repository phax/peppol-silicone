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
package eu.peppol.validation.engine.result;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.IResourceLocation;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.LoggingValidationEventHandler;
import com.phloc.commons.state.ETriState;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.schema.XMLSchemaCache;

import eu.peppol.validation.engine.jaxb.result.ObjectFactory;
import eu.peppol.validation.engine.jaxb.result.ResultErrorType;
import eu.peppol.validation.engine.jaxb.result.ResultLayerType;
import eu.peppol.validation.engine.jaxb.result.ResultLayersType;
import eu.peppol.validation.engine.jaxb.result.ResultMessageType;
import eu.peppol.validation.engine.jaxb.result.ResultType;
import eu.peppol.validation.engine.jaxb.result.ValidationResultType;

/**
 * Class for create the result of the validation.
 *
 * @version 2.0 27/09/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ValidationResultConverter {
  private ValidationResultConverter () {}

  /**
   * Method to generate an output xml with the result of the all validation
   * layers.
   *
   * @param validationResult
   *        The validation result
   * @return The created DOM document
   * @throws JAXBException
   *         Throws the exception to be handler by other method.
   */
  @Nonnull
  public static Document convertToXML (final ValidationResultType validationResult) throws JAXBException {

    final JAXBContext jaxbContext = JAXBContextCache.getInstance ().getFromCache (ValidationResultType.class);
    final Marshaller marshaller = jaxbContext.createMarshaller ();
    marshaller.setEventHandler (new LoggingValidationEventHandler (marshaller.getEventHandler ()));
    marshaller.setSchema (XMLSchemaCache.getInstance ()
                                        .getSchema (new ClassPathResource ("schemas/Validation_result.xsd")));

    final ObjectFactory factory = new ObjectFactory ();
    final JAXBElement <ValidationResultType> validationResultElement = factory.createValidationResult (validationResult);
    marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    final Document aDoc = XMLFactory.newDocument ();
    marshaller.marshal (validationResultElement, new DOMResult (aDoc));
    return aDoc;
  }

  /**
   * Determine the overall success. The first error element results in a
   * failure! Warnings are interpreted as success!
   *
   * @param aValidationResultListItems
   *        The result items
   * @return {@link ETriState#TRUE} for success, {@link ETriState#FALSE} if at
   *         least one error is contained and {@link ETriState#UNDEFINED} if no
   *         results were found.
   */
  @Nonnull
  private static ETriState _getOverallSuccess (@Nullable final List <ValidationResult> aValidationResultListItems) {
    boolean bFoundAtLeastOneResult = false;
    if (aValidationResultListItems != null)
      // For all validation results
      for (final ValidationResult aResult : aValidationResultListItems) {
        final IResourceErrorGroup aREG = aResult.getResults ();
        if (!aREG.isEmpty ())
          bFoundAtLeastOneResult = true;
        if (aREG.containsAtLeastOneError ()) {
          // We found an error, so it's not an overall success!
          return ETriState.FALSE;
        }
      }

    // If we found no failures, it means we have either only successes or no
    // results at all.
    // If we have only successes -> over all success
    // If we have no items -> the result is undefined!
    return bFoundAtLeastOneResult ? ETriState.TRUE : ETriState.UNDEFINED;
  }

  /**
   * Method to generate an output xml with the result of the all validation
   * layers.
   *
   * @param profile
   *        CENBII Profile code.
   * @param document
   *        Core Data Model code.
   * @param aValidationResultListItems
   *        ArrayList filled with ResultBeans.
   * @return The validation result object
   */
  @Nonnull
  public static ValidationResultType createResult (final String profile,
                                                   final String document,
                                                   final List <ValidationResult> aValidationResultListItems) {
    final ObjectFactory aFactory = new ObjectFactory ();

    final ValidationResultType aJAXBValidationResult = aFactory.createValidationResultType ();
    aJAXBValidationResult.setProfile (profile);
    aJAXBValidationResult.setDocumentCore (document);
    aJAXBValidationResult.setOverallSuccess (_getOverallSuccess (aValidationResultListItems).getAsBooleanObj (null));

    final List <ResultLayersType> aJAXBLayersList = aJAXBValidationResult.getResultLayers ();
    final ResultLayersType aJAXBLayers = aFactory.createResultLayersType ();
    aJAXBLayersList.add (aJAXBLayers);

    final List <ResultLayerType> aJAXBLayerList = aJAXBLayers.getResultLayer ();

    for (final ValidationResult aValidationResultItem : aValidationResultListItems) {
      final ResultLayerType aJAXBLayer = aFactory.createResultLayerType ();
      aJAXBLayer.setCode (aValidationResultItem.getCode ());
      aJAXBLayer.setName (aValidationResultItem.getName ());
      aJAXBLayer.setValidationGroup (aValidationResultItem.getValidationGroup ());
      aJAXBLayer.setValidationType (aValidationResultItem.getValidationType ());
      aJAXBLayer.getValidationDoc ().addAll (aValidationResultItem.getValidationDocs ());

      // Add all result resource errors
      final ResultType aJAXBResult = aFactory.createResultType ();
      final List <ResultMessageType> aMessagesList = aJAXBResult.getMessage ();
      if (aValidationResultItem.getResults () != null) {
        // For all recorded errors
        for (final IResourceError aResError : aValidationResultItem.getResults ()) {
          final ResultMessageType aJAXBMessage = aFactory.createResultMessageType ();
          aJAXBMessage.setResource (aResError.getLocation ().getResourceID ());
          aJAXBMessage.setFlag (aResError.getErrorLevel ().getID ());
          if (aResError.getLocation ().getLineNumber () != IResourceLocation.ILLEGAL_NUMBER)
            aJAXBMessage.setLine (Integer.toString (aResError.getLocation ().getLineNumber ()));
          if (aResError.getLocation ().getColumnNumber () != IResourceLocation.ILLEGAL_NUMBER)
            aJAXBMessage.setCol (Integer.toString (aResError.getLocation ().getColumnNumber ()));
          aJAXBMessage.setField (aResError.getLocation ().getField ());
          aJAXBMessage.setValue (aResError.getErrorText (Locale.getDefault ()));

          aMessagesList.add (aJAXBMessage);
        }
      }
      aJAXBLayer.setResult (aJAXBResult);

      // Set error item on demand
      if (aValidationResultItem.getErrorType () != null) {
        final ResultErrorType aJAXBError = aFactory.createResultErrorType ();
        aJAXBError.setType (aValidationResultItem.getErrorType ());
        aJAXBError.setMessage (aValidationResultItem.getErrorMessage ());
        aJAXBError.setStacktrace (aValidationResultItem.getErrorStackTrace ());
        aJAXBLayer.setError (aJAXBError);
      }

      aJAXBLayerList.add (aJAXBLayer);
    }

    return aJAXBValidationResult;
  }
}

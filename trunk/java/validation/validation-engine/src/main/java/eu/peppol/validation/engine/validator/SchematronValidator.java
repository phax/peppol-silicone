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
package eu.peppol.validation.engine.validator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.ETriState;
import com.phloc.commons.xml.transform.CollectingTransformErrorListener;

import eu.peppol.validation.commons.schematron.ISchematronResource;
import eu.peppol.validation.commons.schematron.SchematronHelper;
import eu.peppol.validation.commons.schematron.xslt.SchematronResourceSCH;
import eu.peppol.validation.engine.extensions.ISchematronValidationDocumentProvider;
import eu.peppol.validation.engine.extensions.ValidationDocumentProviderRegistry;

/**
 * Schematron validation implementation
 *
 * @version 1.5 27/09/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SchematronValidator {
  private static final ValidationDocumentProviderRegistry <ISchematronValidationDocumentProvider> s_aValidationDocProviderRegistry = new ValidationDocumentProviderRegistry <ISchematronValidationDocumentProvider> ();

  private SchematronValidator () {}

  /**
   * Check if the given schematron path can be resolved to a document.
   *
   * @param sSchematronPath
   *        The schematron path to be validated.
   * @return {@link ESuccess#FAILURE} if the document cannot be resolved
   */
  @Nonnull
  public static ESuccess isValidSchematronPath (final String sSchematronPath) {
    // Check all external document provider
    final ETriState eResult = s_aValidationDocProviderRegistry.isSupportedDocument (sSchematronPath);
    if (eResult.isFalse ())
      return ESuccess.FAILURE;
    if (eResult.isTrue ()) {
      // Yes, the document is handled by a document provider
      return ESuccess.SUCCESS;
    }

    // Simple check for file existence in classpath
    return ESuccess.valueOf (new ClassPathResource (sSchematronPath).exists ());
  }

  @Nonnull
  private static ISchematronResource _resolveSchematronResource (final String sSchematronPath,
                                                                 @Nullable final ErrorListener aCustomErrorListener,
                                                                 @Nullable final URIResolver aCustomURIResolver) {
    // Retrieve the compiled XML Schema
    IReadableResource aSchematronRes;
    final ISchematronValidationDocumentProvider aValDocProvider = s_aValidationDocProviderRegistry.getProvider (sSchematronPath);
    if (aValDocProvider != null) {
      // Remove the prefix
      final String sDocumentName = sSchematronPath.substring (aValDocProvider.getHandledPrefix ().length ());
      aSchematronRes = aValDocProvider.getSchematronResource (sDocumentName);
    }
    else {
      // Read the Schematron from classpath and compile it!
      aSchematronRes = new ClassPathResource (sSchematronPath);
    }
    if (aSchematronRes == null)
      throw new IllegalArgumentException ("Failed to resolve Schematron for " + sSchematronPath);

    // Use Schematron file directly
    return new SchematronResourceSCH (aSchematronRes, aCustomErrorListener, aCustomURIResolver);
  }

  /**
   * Validate an XML document against a SCH Schematron.
   *
   * @param aXMLRes
   *        The XML path.
   * @param sSchematronPath
   *        The Schematron path.
   * @return ArrayList object containing the validation result.
   */
  @Nonnull
  public static IResourceErrorGroup validate (@Nonnull final IReadableResource aXMLRes,
                                              @Nonnull final String sSchematronPath) {
    final CollectingTransformErrorListener aErrorListener = new CollectingTransformErrorListener ();
    final ISchematronResource aSchematronRes = _resolveSchematronResource (sSchematronPath, aErrorListener, null);
    final SchematronOutputType schOutput = SchematronHelper.applySchematron (aSchematronRes, aXMLRes);

    // Get all errors of XSLT transformation
    final IResourceErrorGroup aTransformErrors = aErrorListener.getResourceErrors ();

    // Append Schematron resource errors as well
    if (schOutput == null)
      return aTransformErrors;

    // Build combined set
    final ResourceErrorGroup aCombinedErrors = new ResourceErrorGroup (aTransformErrors);
    aCombinedErrors.addResourceErrorGroup (SchematronHelper.convertToResourceErrorGroup (schOutput,
                                                                                         aXMLRes.getResourceID ()));
    return aCombinedErrors;
  }
}

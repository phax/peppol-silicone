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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Schema;

import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.ETriState;
import com.phloc.commons.xml.ls.SimpleLSResourceResolver;
import com.phloc.commons.xml.schema.XMLSchemaCache;
import com.phloc.commons.xml.schema.XMLSchemaValidationHelper;

import eu.peppol.validation.engine.extensions.ISchemaValidationDocumentProvider;
import eu.peppol.validation.engine.extensions.ValidationDocumentProviderRegistry;
import eu.peppol.validation.engine.extensions.impl.UBL20SchemaValidationDocumentProvider;

/**
 * Schema validation implementation.
 *
 * @version 2.5 27/09/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class SchemaValidator {
  private static final ValidationDocumentProviderRegistry <ISchemaValidationDocumentProvider> s_aValidationDocProviderRegistry = new ValidationDocumentProviderRegistry <ISchemaValidationDocumentProvider> ();

  // A special XML schema cache with a specific resource resolver for the
  // PreAward stuff, referencing UBL documents
  private static final XMLSchemaCache s_aSchemaCache = new XMLSchemaCache (new SimpleLSResourceResolver () {
    @Override
    @Nullable
    protected IReadableResource resolveResource (final String sSystemId, final String sBaseURI) throws Exception {
      // Call default resolver
      final IReadableResource aRes = super.resolveResource (sSystemId, sBaseURI);
      if (aRes != null && aRes.exists ())
        return aRes;

      // Try all document providers whether they can resolve the resource
      for (final ISchemaValidationDocumentProvider aProvider : s_aValidationDocProviderRegistry.getAllRegisteredProviders ()) {
        final IReadableResource aProviderRes = aProvider.resolveResource (sSystemId);
        if (aProviderRes != null)
          return aProviderRes;
      }

      // Not found at all
      return null;
    }
  });

  static {
    // Default: UBL 2.0 XML Schema
    s_aValidationDocProviderRegistry.registerValidationDocumentProvider (new UBL20SchemaValidationDocumentProvider ());
  }

  private SchemaValidator () {}

  @Nonnull
  public static ESuccess isValidXSDPath (final String sXSDPath) {
    // Check all external document provider
    final ETriState eResult = s_aValidationDocProviderRegistry.isSupportedDocument (sXSDPath);
    if (eResult.isFalse ())
      return ESuccess.FAILURE;
    if (eResult.isTrue ())
      return ESuccess.SUCCESS;

    // Check for file existence in library
    return ESuccess.valueOf (new ClassPathResource (sXSDPath).exists ());
  }

  @Nonnull
  private static Schema _resolveSchema (final String sXSDPath) {
    // Retrieve the compiled XML Schema
    Schema aSchema;
    final ISchemaValidationDocumentProvider aValDocProvider = s_aValidationDocProviderRegistry.getProvider (sXSDPath);
    if (aValDocProvider != null) {
      // Remove the handler specific prefix
      final String sDocumentName = sXSDPath.substring (aValDocProvider.getHandledPrefix ().length ());
      aSchema = aValDocProvider.getSchema (sDocumentName);
    }
    else {
      // Read the XSD from classpath and compile it!
      aSchema = s_aSchemaCache.getSchema (new ClassPathResource (sXSDPath));
    }
    if (aSchema == null)
      throw new IllegalStateException ("Failed to resolve XML schema for " + sXSDPath);
    return aSchema;
  }

  /**
   * Validate an XML document against a XML Schema.
   *
   * @param aXMLRes
   *        The XML path.
   * @param sXSDPath
   *        The XSD schema path.
   * @return The errors of validation
   * @throws IOException
   *         Throws the exception to be handler by other method.
   */
  @Nonnull
  public static IResourceErrorGroup validate (final IReadableResource aXMLRes, final String sXSDPath) throws IOException {
    // Get the internal Schema object of the passed XSD path
    final Schema aSchema = _resolveSchema (sXSDPath);
    return XMLSchemaValidationHelper.validate (aSchema, aXMLRes);
  }
}

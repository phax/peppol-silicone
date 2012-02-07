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
package eu.peppol.validation.engine.extensions.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Schema;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.ubl.EUBL20DocumentType;
import com.phloc.ubl.UBL20DocumentTypes;

import eu.peppol.validation.engine.extensions.ISchemaValidationDocumentProvider;

/**
 * Provide UBL 2.0 XML schemas via the phloc-ubl library.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class UBL20SchemaValidationDocumentProvider implements ISchemaValidationDocumentProvider {
  @Nonnull
  public String getHandledPrefix () {
    return "ubl20:";
  }

  public boolean isSupportedDocument (@Nullable final String sDocumentName) {
    return UBL20DocumentTypes.getDocumentTypeOfLocalName (sDocumentName) != null;
  }

  @Nonnull
  public Schema getSchema (@Nullable final String sDocumentName) {
    final EUBL20DocumentType eDocType = UBL20DocumentTypes.getDocumentTypeOfLocalName (sDocumentName);
    if (eDocType == null)
      throw new IllegalArgumentException ("No UBL 2.0 document type has the local name " + sDocumentName);
    return eDocType.getSchema ();
  }

  @Nullable
  public IReadableResource resolveResource (final String sPath) {
    // Build the path, relative to the UBL main documents
    final String sAbsolutePath = FilenameHelper.getCleanConcatenatedUrlPath ("schemas/ubl20/maindoc", sPath);
    final IReadableResource aRes = new ClassPathResource (sAbsolutePath);

    // If it exists, return it - else return null
    return aRes.exists () ? aRes : null;
  }
}

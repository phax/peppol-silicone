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
package at.peppol.commons.identifier.docid;

import java.util.List;

import javax.annotation.Nonnull;

import at.peppol.busdox.identifier.IBusdoxDocumentTypeIdentifierParts;

import com.phloc.commons.annotations.Nonempty;

/**
 * Contains all the different fields of a document identifier for PEPPOL. Note:
 * the sub type identifier is specified in more detail than in BusDox:
 * <code>&lt;customization id>::&lt;version></code> even more detailed the
 * customization ID can be split further:
 * <code>&lt;transactionId>:#&lt;extensionId>[#&lt;extensionId>]::&lt;version></code>
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IPEPPOLDocumentTypeIdentifierParts extends IBusdoxDocumentTypeIdentifierParts {
  /**
   * Separates the transaction ID from the extensions
   */
  String TRANSACTIONID_SEPARATOR = ":#";

  /**
   * Separates the different extensions from each other
   */
  String EXTENSION_SEPARATOR = "#";

  /**
   * Separates the customization ID from the version
   */
  String VERSION_SEPARATOR = "::";

  /**
   * @return The transaction ID
   */
  @Nonnull
  @Nonempty
  String getTransactionID ();

  /**
   * @return The contained extension IDs
   */
  @Nonnull
  @Nonempty
  List <String> getExtensionIDs ();

  /**
   * @return The version number
   */
  @Nonnull
  @Nonempty
  String getVersion ();

  /**
   * @return transaction ID + extension IDs (no version number)
   */
  @Nonnull
  @Nonempty
  String getAsUBLCustomizationID ();
}

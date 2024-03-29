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
package at.peppol.busdox.identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;

/**
 * Contains all the different fields of a document type identifier for BusDox.
 * Note: it is important to note, that the PEPPOL specification separates the
 * sub type identifier more clearly!
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IBusdoxDocumentTypeIdentifierParts {
  /**
   * Separator between namespace and local name
   */
  String NAMESPACE_SEPARATOR = "::";

  /**
   * Separator between namespace elements and the optional subtype
   */
  String SUBTYPE_SEPARATOR = "##";

  /**
   * @return The root namespace. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getRootNS ();

  /**
   * @return The document element local name. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getLocalName ();

  /**
   * @return The optional sub type identifier. May be <code>null</code>.
   */
  @Nullable
  String getSubTypeIdentifier ();

  /**
   * @return The parts assembled into a complete document identifier value.
   *         Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getAsDocumentTypeIdentifierValue ();
}

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
package at.peppol.validation.rules;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.peppol.commons.cenbii.profiles.ETransaction;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.io.IReadableResource;


/**
 * Interface for a single validation artefact.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IValidationArtefact {
  /**
   * @return The validation level. May not be <code>null</code>.
   */
  @Nonnull
  EValidationLevel getValidationLevel ();

  /**
   * @return The document type. May not be <code>null</code>.
   */
  @Nonnull
  IValidationDocumentType getValidationDocumentType ();

  /**
   * @return The country for which this artefact is relevant. May be
   *         <code>null</code> for country independent artefacts.
   */
  @Nullable
  Locale getValidationCountry ();

  /**
   * @return <code>true</code> if this artefact is country independent,
   *         <code>false</code> otherwise. Is a shortcut for
   *         <code>getCountry () == null</code>.
   */
  boolean isValidationCountryIndependent ();

  /**
   * Check if the passed transaction is supported by this validation artefact.
   *
   * @param eTransaction
   *        The transaction to be searched. May be <code>null</code>.
   * @return <code>true</code> if the passed transaction is not
   *         <code>null</code> and is contained in this artefact
   */
  boolean containsTransaction (@Nullable ETransaction eTransaction);

  /**
   * Get the Schematron resource (.SCH) of this artefact for the specified
   * transaction.
   *
   * @param aTransaction
   *        The transaction to be searched. May not be <code>null</code>.
   * @return <code>null</code> if no such transaction is present.
   */
  @Nullable
  IReadableResource getValidationSchematronResource (@Nonnull IValidationTransaction aTransaction);

  /**
   * @return A list of all Schematron resources (.SCH) for the current artefact.
   *         Never <code>null</code>. The returned list always contains at least
   *         one entry.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  List <IReadableResource> getAllValidationSchematronResources ();

  /**
   * Get the XSLT resource of this artefact for the specified transaction.
   *
   * @param aRuleSet
   *        The transaction to be searched. May not be <code>null</code>.
   * @return <code>null</code> if no such transaction is present.
   */
  @Nullable
  IReadableResource getValidationXSLTResource (@Nonnull IValidationTransaction aRuleSet);

  /**
   * @return A list of all XSLT resources for the current artefact. Never
   *         <code>null</code>. The returned list always contains at least one
   *         entry.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  List <IReadableResource> getAllValidationXSLTResources ();
}

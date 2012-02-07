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
package eu.peppol.validation.rules;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

import eu.cen.bii.profiles.ETransaction;

/**
 * This is the default implementation of the {@link IValidationTransaction}
 * interface. It represents a single "validation transaction" consisting of a
 * syntax binding (see {@link EValidationSyntaxBinding}) and a CEN BII
 * transaction.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ValidationTransaction implements IValidationTransaction {
  private final EValidationSyntaxBinding m_eSyntaxBinding;
  private final ETransaction m_eTransaction;

  public ValidationTransaction (@Nonnull final EValidationSyntaxBinding eSyntaxBinding,
                                @Nonnull final ETransaction eTransaction) {
    if (eSyntaxBinding == null)
      throw new NullPointerException ("syntaxBinding");
    if (eTransaction == null)
      throw new NullPointerException ("transactionID");

    m_eSyntaxBinding = eSyntaxBinding;
    m_eTransaction = eTransaction;
  }

  @Nonnull
  public EValidationSyntaxBinding getSyntaxBinding () {
    return m_eSyntaxBinding;
  }

  @Nonnull
  public ETransaction getTransaction () {
    return m_eTransaction;
  }

  @Nonnull
  public String getAsString () {
    return m_eSyntaxBinding.getFileNamePart () + "-" + m_eTransaction.name ();
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ValidationTransaction))
      return false;
    final ValidationTransaction rhs = (ValidationTransaction) o;
    return m_eSyntaxBinding.equals (rhs.m_eSyntaxBinding) && m_eTransaction.equals (rhs.m_eTransaction);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_eSyntaxBinding).append (m_eTransaction).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("syntaxBinding", m_eSyntaxBinding)
                                       .append ("transaction", m_eTransaction)
                                       .toString ();
  }

  /**
   * This is a shortcut method for creating a transaction using the UBL syntax
   * binding.
   *
   * @param eTransaction
   *        The transaction to use. May not be <code>null</code>.
   * @return The non-<code>null</code> object.
   */
  @Nonnull
  public static IValidationTransaction createUBLTransaction (@Nonnull final ETransaction eTransaction) {
    return new ValidationTransaction (EValidationSyntaxBinding.UBL, eTransaction);
  }
}

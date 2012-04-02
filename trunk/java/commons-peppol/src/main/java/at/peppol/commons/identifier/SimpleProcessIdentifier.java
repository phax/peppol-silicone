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
package at.peppol.commons.identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.ProcessIdentifierType;

import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;

import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * This is a sanity class around the {@link ProcessIdentifierType} class with
 * easier construction and some sanity access methods. It may be used in all
 * places where {@link ProcessIdentifierType} objects are required.<br>
 * Important note: this class implements {@link #equals(Object)} and
 * {@link #hashCode()} where its base class does not. So be careful when mixing
 * this class and its base class!
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class SimpleProcessIdentifier extends ProcessIdentifierType {
  public SimpleProcessIdentifier (@Nonnull final IReadonlyProcessIdentifier aIdentifier) {
    this (aIdentifier.getScheme (), aIdentifier.getValue ());
  }

  public SimpleProcessIdentifier (@Nonnull final String sScheme, @Nonnull final String sValue) {
    if (!IdentifierUtils.isValidIdentifierScheme (sScheme))
      throw new IllegalArgumentException ("Process identifier scheme '" + sScheme + "' is invalid!");
    if (!IdentifierUtils.isValidProcessIdentifierValue (sValue))
      throw new IllegalArgumentException ("Process identifier value '" + sValue + "' is invalid!");
    setScheme (sScheme);
    setValue (sValue);
  }

  @Nonnull
  public String getURIEncoded () {
    return IdentifierUtils.getIdentifierURIEncoded (this);
  }

  @Nonnull
  public String getURIPercentEncoded () {
    return IdentifierUtils.getIdentifierURIPercentEncoded (this);
  }

  /*
   * Note: this method does compare case sensitive!!!! Otherwise the required
   * semantics of #equals would not be fulfilled!
   * @see IdentifierUtils#areIdentifiersEqual(IIdentifier,IIdentifier)
   */
  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SimpleProcessIdentifier rhs = (SimpleProcessIdentifier) o;
    return EqualsUtils.nullSafeEquals (scheme, rhs.scheme) && EqualsUtils.nullSafeEquals (value, rhs.value);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (scheme).append (value).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("scheme", scheme).append ("value", value).toString ();
  }

  @Nonnull
  public static SimpleProcessIdentifier createWithDefaultScheme (@Nullable final String sValue) {
    return new SimpleProcessIdentifier (CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME, sValue);
  }

  @Nonnull
  public static SimpleProcessIdentifier createFromURIPart (@Nonnull final String sURIPart) {
    return IdentifierUtils.createProcessIdentifierFromURIPart (sURIPart);
  }
}

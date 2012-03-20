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
package at.peppol.smp.server.data.dbms.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;

import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * ServiceGroupId == participant ID
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBServiceGroupID implements Serializable {
  private String m_sBusinessIdentifierScheme;
  private String m_sBusinessIdentifier;

  public DBServiceGroupID () {}

  public DBServiceGroupID (@Nonnull final IReadonlyParticipantIdentifier aIdentifier) {
    setBusinessIdentifierScheme (aIdentifier.getScheme ());
    setBusinessIdentifier (aIdentifier.getValue ());
  }

  @Column (name = "businessIdentifierScheme", nullable = false, length = 256)
  public String getBusinessIdentifierScheme () {
    return m_sBusinessIdentifierScheme;
  }

  public void setBusinessIdentifierScheme (final String sBusinessIdentifierScheme) {
    m_sBusinessIdentifierScheme = IdentifierUtils.getUnifiedParticipantDBValue (sBusinessIdentifierScheme);
  }

  @Column (name = "businessIdentifier", nullable = false, length = 256)
  public String getBusinessIdentifier () {
    return m_sBusinessIdentifier;
  }

  public void setBusinessIdentifier (final String sBusinessIdentifier) {
    m_sBusinessIdentifier = IdentifierUtils.getUnifiedParticipantDBValue (sBusinessIdentifier);
  }

  @Transient
  @Nonnull
  public SimpleParticipantIdentifier asParticipantIdentifier () {
    return new SimpleParticipantIdentifier (m_sBusinessIdentifierScheme, m_sBusinessIdentifier);
  }

  @Override
  public boolean equals (final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof DBServiceGroupID))
      return false;
    final DBServiceGroupID rhs = (DBServiceGroupID) other;
    return EqualsUtils.nullSafeEquals (m_sBusinessIdentifierScheme, rhs.m_sBusinessIdentifierScheme) &&
           EqualsUtils.nullSafeEquals (m_sBusinessIdentifier, rhs.m_sBusinessIdentifier);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sBusinessIdentifierScheme)
                                       .append (m_sBusinessIdentifier)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("scheme", m_sBusinessIdentifierScheme)
                                       .append ("value", m_sBusinessIdentifier)
                                       .toString ();
  }
}

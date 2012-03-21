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

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;

import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;

/**
 * EndpointId generated by hbm2java
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBEndpointID implements Serializable {
  private String m_sBusinessIdentifierScheme;
  private String m_sBusinessIdentifier;
  private String m_sProcessIdentifierScheme;
  private String m_sProcessIdentifier;
  private String m_sDocumentIdentifierScheme;
  private String m_sDocumentIdentifier;
  private String m_sEndpointReference;
  private String m_sTransportProfile;

  public DBEndpointID () {}

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
  public void setBusinessIdentifier (@Nonnull final IReadonlyParticipantIdentifier aBusinessIdentifier) {
    setBusinessIdentifierScheme (aBusinessIdentifier.getScheme ());
    setBusinessIdentifier (aBusinessIdentifier.getValue ());
  }

  @Column (name = "processIdentifierType", nullable = false, length = 256)
  public String getProcessIdentifierType () {
    return m_sProcessIdentifierScheme;
  }

  public void setProcessIdentifierType (final String sProcessIdentifierScheme) {
    m_sProcessIdentifierScheme = sProcessIdentifierScheme;
  }

  @Column (name = "processIdentifier", nullable = false, length = 256)
  public String getProcessIdentifier () {
    return m_sProcessIdentifier;
  }

  public void setProcessIdentifier (final String sProcessIdentifier) {
    m_sProcessIdentifier = sProcessIdentifier;
  }

  @Transient
  public void setProcessIdentifier (@Nonnull final IReadonlyProcessIdentifier aProcessID) {
    setProcessIdentifierType (aProcessID.getScheme ());
    setProcessIdentifier (aProcessID.getValue ());
  }

  @Column (name = "documentIdentifierScheme", nullable = false, length = 256)
  public String getDocumentIdentifierScheme () {
    return m_sDocumentIdentifierScheme;
  }

  public void setDocumentIdentifierScheme (final String sDocumentIdentifierScheme) {
    m_sDocumentIdentifierScheme = sDocumentIdentifierScheme;
  }

  @Column (name = "documentIdentifier", nullable = false, length = 256)
  public String getDocumentIdentifier () {
    return m_sDocumentIdentifier;
  }

  public void setDocumentIdentifier (final String sDocumentIdentifier) {
    m_sDocumentIdentifier = sDocumentIdentifier;
  }

  @Transient
  public void setDocumentIdentifier (@Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) {
    setDocumentIdentifierScheme (aDocumentTypeID.getScheme ());
    setDocumentIdentifier (aDocumentTypeID.getValue ());
  }

  @Column (name = "endpointReference", nullable = false, length = 256)
  public String getEndpointReference () {
    return m_sEndpointReference;
  }

  public void setEndpointReference (final String sEndpointReference) {
    m_sEndpointReference = sEndpointReference;
  }

  @Column (name = "transportProfile", nullable = false, length = 256)
  public String getTransportProfile () {
    return m_sTransportProfile;
  }

  public void setTransportProfile (final String sTransportProfile) {
    m_sTransportProfile = sTransportProfile;
  }

  @Override
  public boolean equals (final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof DBEndpointID))
      return false;
    final DBEndpointID rhs = (DBEndpointID) other;
    return EqualsUtils.nullSafeEquals (m_sBusinessIdentifierScheme, rhs.m_sBusinessIdentifierScheme) &&
           EqualsUtils.nullSafeEquals (m_sBusinessIdentifier, rhs.m_sBusinessIdentifier) &&
           EqualsUtils.nullSafeEquals (m_sDocumentIdentifierScheme, rhs.m_sDocumentIdentifierScheme) &&
           EqualsUtils.nullSafeEquals (m_sDocumentIdentifier, rhs.m_sDocumentIdentifier) &&
           EqualsUtils.nullSafeEquals (m_sProcessIdentifierScheme, rhs.m_sProcessIdentifierScheme) &&
           EqualsUtils.nullSafeEquals (m_sProcessIdentifier, rhs.m_sProcessIdentifier) &&
           EqualsUtils.nullSafeEquals (m_sEndpointReference, rhs.m_sEndpointReference) &&
           EqualsUtils.nullSafeEquals (m_sTransportProfile, rhs.m_sTransportProfile);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sBusinessIdentifierScheme)
                                       .append (m_sBusinessIdentifier)
                                       .append (m_sDocumentIdentifierScheme)
                                       .append (m_sDocumentIdentifier)
                                       .append (m_sProcessIdentifierScheme)
                                       .append (m_sProcessIdentifier)
                                       .append (m_sEndpointReference)
                                       .append (m_sTransportProfile)
                                       .getHashCode ();
  }
}

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
package eu.peppol.registry.sml.datamodel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.phloc.commons.annotations.DevelopersNote;

/**
 * ServiceMetadataPublisher generated by hbm2java
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Entity
@Table (name = "sml_service_metadata_publisher")
public class DBServiceMetadataPublisher implements Serializable {
  private String m_sSMPID;
  private DBUser m_aUser;
  private String m_sPhysicalAddress;
  private String m_sLogicalAddress;
  private Set <DBParticipantIdentifier> m_aRecipientParticipantIdentifiers = new HashSet <DBParticipantIdentifier> (0);

  @Deprecated
  @DevelopersNote ("Used only by JPA")
  public DBServiceMetadataPublisher () {}

  public DBServiceMetadataPublisher (final String sSMPID,
                                     final DBUser aDBUser,
                                     final String sPhysicalAddress,
                                     final String sLogicalAddress) {
    setSmpId (sSMPID);
    setUser (aDBUser);
    setPhysicalAddress (sPhysicalAddress);
    setLogicalAddress (sLogicalAddress);
  }

  public DBServiceMetadataPublisher (final String sSMPID,
                                     final DBUser aDBUser,
                                     final String sPhysicalAddress,
                                     final String sLogicalAddress,
                                     final Set <DBParticipantIdentifier> recipientParticipantIdentifiers) {
    this (sSMPID, aDBUser, sPhysicalAddress, sLogicalAddress);
    setRecipientParticipantIdentifiers (recipientParticipantIdentifiers);
  }

  @Id
  @Column (name = "smp_id", unique = true, nullable = false, length = 200)
  public String getSmpId () {
    return m_sSMPID;
  }

  public void setSmpId (final String smpId) {
    m_sSMPID = smpId;
  }

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumn (name = "username", nullable = false)
  public DBUser getUser () {
    return m_aUser;
  }

  public void setUser (final DBUser user) {
    m_aUser = user;
  }

  @Column (name = "physical_address", nullable = false, length = 65535)
  public String getPhysicalAddress () {
    return m_sPhysicalAddress;
  }

  public void setPhysicalAddress (final String physicalAddress) {
    m_sPhysicalAddress = physicalAddress;
  }

  @Column (name = "logical_address", nullable = false, length = 65535)
  public String getLogicalAddress () {
    return m_sLogicalAddress;
  }

  public void setLogicalAddress (final String logicalAddress) {
    m_sLogicalAddress = logicalAddress;
  }

  @OneToMany (fetch = FetchType.LAZY, mappedBy = "serviceMetadataPublisher", cascade = CascadeType.ALL)
  public Set <DBParticipantIdentifier> getRecipientParticipantIdentifiers () {
    return m_aRecipientParticipantIdentifiers;
  }

  public void setRecipientParticipantIdentifiers (final Set <DBParticipantIdentifier> recipientParticipantIdentifiers) {
    m_aRecipientParticipantIdentifiers = recipientParticipantIdentifiers;
  }
}

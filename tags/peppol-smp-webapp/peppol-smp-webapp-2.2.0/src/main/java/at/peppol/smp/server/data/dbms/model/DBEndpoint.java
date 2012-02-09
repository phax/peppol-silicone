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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Endpoint generated by hbm2java
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Entity
@Table (name = "smp_endpoint")
public class DBEndpoint implements Serializable {
  private DBEndpointID m_aID;
  private DBProcess m_aProcess;
  private String m_sExtension;
  private boolean m_bRequireBusinessLevelSignature;
  private String m_sMinimumAuthenticationLevel;
  private Date m_aServiceActivationDate;
  private Date m_aServiceExpirationDate;
  private String m_sCertificate;
  private String m_sServiceDescription;
  private String m_sTechnicalContactUrl;
  private String m_sTechnicalInformationUrl;

  public DBEndpoint () {}

  public DBEndpoint (final DBEndpointID id,
                     final DBProcess process,
                     final boolean requireBusinessLevelSignature,
                     final Date serviceExpirationDate,
                     final String certificate,
                     final String serviceDescription,
                     final String technicalContactUrl) {
    m_aID = id;
    m_aProcess = process;
    m_bRequireBusinessLevelSignature = requireBusinessLevelSignature;
    m_aServiceExpirationDate = serviceExpirationDate;
    m_sCertificate = certificate;
    m_sServiceDescription = serviceDescription;
    m_sTechnicalContactUrl = technicalContactUrl;
  }

  public DBEndpoint (final DBEndpointID id,
                     final DBProcess process,
                     final String extension,
                     final boolean requireBusinessLevelSignature,
                     final String minimumAuthenticationLevel,
                     final Date serviceActivationDate,
                     final Date serviceExpirationDate,
                     final String certificate,
                     final String serviceDescription,
                     final String technicalContactUrl,
                     final String technicalInformationUrl) {
    m_aID = id;
    m_aProcess = process;
    m_sExtension = extension;
    m_bRequireBusinessLevelSignature = requireBusinessLevelSignature;
    m_sMinimumAuthenticationLevel = minimumAuthenticationLevel;
    m_aServiceActivationDate = serviceActivationDate;
    m_aServiceExpirationDate = serviceExpirationDate;
    m_sCertificate = certificate;
    m_sServiceDescription = serviceDescription;
    m_sTechnicalContactUrl = technicalContactUrl;
    m_sTechnicalInformationUrl = technicalInformationUrl;
  }

  @EmbeddedId
  public DBEndpointID getId () {
    return m_aID;
  }

  public void setId (final DBEndpointID id) {
    m_aID = id;
  }

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumns ({ @JoinColumn (name = "processIdentifier",
                               referencedColumnName = "processIdentifier",
                               nullable = false,
                               insertable = false,
                               updatable = false),
                 @JoinColumn (name = "processIdentifierType",
                              referencedColumnName = "processIdentifierType",
                              nullable = false,
                              insertable = false,
                              updatable = false),
                 @JoinColumn (name = "businessIdentifier",
                              referencedColumnName = "businessIdentifier",
                              nullable = false,
                              insertable = false,
                              updatable = false),
                 @JoinColumn (name = "businessIdentifierScheme",
                              referencedColumnName = "businessIdentifierScheme",
                              nullable = false,
                              insertable = false,
                              updatable = false),
                 @JoinColumn (name = "documentIdentifier",
                              referencedColumnName = "documentIdentifier",
                              nullable = false,
                              insertable = false,
                              updatable = false),
                 @JoinColumn (name = "documentIdentifierScheme",
                              referencedColumnName = "documentIdentifierScheme",
                              nullable = false,
                              insertable = false,
                              updatable = false) })
  public DBProcess getProcess () {
    return m_aProcess;
  }

  public void setProcess (final DBProcess process) {
    m_aProcess = process;
  }

  @Lob
  @Column (name = "extension", length = 65535)
  public String getExtension () {
    return m_sExtension;
  }

  public void setExtension (final String extension) {
    m_sExtension = extension;
  }

  @Column (name = "requireBusinessLevelSignature", nullable = false)
  public boolean isRequireBusinessLevelSignature () {
    return m_bRequireBusinessLevelSignature;
  }

  public void setRequireBusinessLevelSignature (final boolean requireBusinessLevelSignature) {
    m_bRequireBusinessLevelSignature = requireBusinessLevelSignature;
  }

  @Column (name = "minimumAuthenticationLevel", length = 256)
  public String getMinimumAuthenticationLevel () {
    return m_sMinimumAuthenticationLevel;
  }

  public void setMinimumAuthenticationLevel (final String minimumAuthenticationLevel) {
    m_sMinimumAuthenticationLevel = minimumAuthenticationLevel;
  }

  @Temporal (TemporalType.TIMESTAMP)
  @Column (name = "serviceActivationDate", length = 19)
  public Date getServiceActivationDate () {
    return m_aServiceActivationDate;
  }

  public void setServiceActivationDate (final Date serviceActivationDate) {
    m_aServiceActivationDate = serviceActivationDate;
  }

  @Temporal (TemporalType.TIMESTAMP)
  @Column (name = "serviceExpirationDate", nullable = false, length = 19)
  public Date getServiceExpirationDate () {
    return m_aServiceExpirationDate;
  }

  public void setServiceExpirationDate (final Date serviceExpirationDate) {
    m_aServiceExpirationDate = serviceExpirationDate;
  }

  @Lob
  @Column (name = "certificate", nullable = false, length = 65535)
  public String getCertificate () {
    return m_sCertificate;
  }

  public void setCertificate (final String certificate) {
    m_sCertificate = certificate;
  }

  @Lob
  @Column (name = "serviceDescription", nullable = false, length = 65535)
  public String getServiceDescription () {
    return m_sServiceDescription;
  }

  public void setServiceDescription (final String serviceDescription) {
    m_sServiceDescription = serviceDescription;
  }

  @Column (name = "technicalContactUrl", nullable = false, length = 256)
  public String getTechnicalContactUrl () {
    return m_sTechnicalContactUrl;
  }

  public void setTechnicalContactUrl (final String technicalContactUrl) {
    m_sTechnicalContactUrl = technicalContactUrl;
  }

  @Column (name = "technicalInformationUrl", length = 256)
  public String getTechnicalInformationUrl () {
    return m_sTechnicalInformationUrl;
  }

  public void setTechnicalInformationUrl (final String technicalInformationUrl) {
    m_sTechnicalInformationUrl = technicalInformationUrl;
  }
}

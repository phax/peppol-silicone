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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents a single user within the SMP database.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Entity
@Table (name = "smp_user")
public class DBUser implements Serializable {
  private String m_sUserName;
  private String m_sPassword;
  private Set <DBOwnership> m_aOwnerships = new HashSet <DBOwnership> ();

  public DBUser () {}

  @Id
  @Column (name = "username", unique = true, nullable = false, length = 256)
  public String getUsername () {
    return m_sUserName;
  }

  public void setUsername (final String sUserName) {
    m_sUserName = sUserName;
  }

  @Column (name = "password", nullable = false, length = 256)
  public String getPassword () {
    return m_sPassword;
  }

  public void setPassword (final String sPassword) {
    m_sPassword = sPassword;
  }

  @OneToMany (fetch = FetchType.LAZY, mappedBy = "user")
  public Set <DBOwnership> getOwnerships () {
    return m_aOwnerships;
  }

  public void setOwnerships (final Set <DBOwnership> aOwnerships) {
    m_aOwnerships = aOwnerships;
  }
}

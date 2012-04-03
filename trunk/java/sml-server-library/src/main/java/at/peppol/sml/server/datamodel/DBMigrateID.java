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
package at.peppol.sml.server.datamodel;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;

/**
 * MigrateId generated by hbm2java
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBMigrateID implements Serializable {
  private String m_sScheme;
  private String m_sValue;
  private String m_sMigrationCode;

  @Deprecated
  @DevelopersNote ("Used only by Hibernate")
  public DBMigrateID () {}

  public DBMigrateID (@Nonnull final MigrationRecordType aMigrationRecord) {
    setRecipientParticipantIdentifierScheme (aMigrationRecord.getParticipantIdentifier ().getScheme ());
    setRecipientParticipantIdentifierValue (aMigrationRecord.getParticipantIdentifier ().getValue ());
    setMigrationCode (aMigrationRecord.getMigrationKey ());
  }

  @Column (name = "recipient_participant_identifier_scheme",
           nullable = false,
           length = CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getRecipientParticipantIdentifierScheme () {
    return m_sScheme;
  }

  public void setRecipientParticipantIdentifierScheme (final String sScheme) {
    m_sScheme = IdentifierUtils.getUnifiedParticipantDBValue (sScheme);
  }

  @Column (name = "recipient_participant_identifier_value",
           nullable = false,
           length = CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
  public String getRecipientParticipantIdentifierValue () {
    return m_sValue;
  }

  public void setRecipientParticipantIdentifierValue (final String sValue) {
    m_sValue = IdentifierUtils.getUnifiedParticipantDBValue (sValue);
  }

  @Column (name = "migration_code", nullable = false, length = CDB.MAX_MIGRATION_CODE_LENGTH)
  public String getMigrationCode () {
    return m_sMigrationCode;
  }

  public void setMigrationCode (final String sMigrationCode) {
    m_sMigrationCode = sMigrationCode;
  }

  @Nonnull
  public SimpleParticipantIdentifier asParticipantIdentifier () {
    return new SimpleParticipantIdentifier (m_sScheme, m_sValue);
  }

  @Override
  public boolean equals (final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof DBMigrateID))
      return false;
    final DBMigrateID rhs = (DBMigrateID) other;
    return EqualsUtils.nullSafeEquals (m_sScheme, rhs.m_sScheme) &&
           EqualsUtils.nullSafeEquals (m_sValue, rhs.m_sValue) &&
           EqualsUtils.nullSafeEquals (m_sMigrationCode, rhs.m_sMigrationCode);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sScheme).append (m_sValue).append (m_sMigrationCode).getHashCode ();
  }
}

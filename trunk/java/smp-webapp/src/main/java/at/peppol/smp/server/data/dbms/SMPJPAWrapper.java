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
package at.peppol.smp.server.data.dbms;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import at.peppol.commons.jpa.AbstractJPAWrapper;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.annotations.ReturnsMutableCopy;

/**
 * Specific SMP JPA entity manager
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
final class SMPJPAWrapper extends AbstractJPAWrapper {
  private static final String CONFIG_JDBC_DRIVER = "jdbc.driver";
  private static final String CONFIG_JDBC_URL = "jdbc.url";
  private static final String CONFIG_JDBC_USER = "jdbc.user";
  private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";
  private static final String CONFIG_TARGET_DATABASE = "target-database";
  private static final String CONFIG_JDBC_READ_CONNECTIONS_MAX = "jdbc.read-connections.max";

  private static final SMPJPAWrapper s_aInstance = new SMPJPAWrapper ();

  @Nonnull
  @ReturnsMutableCopy
  private static Map <String, Object> _createSettingsMap () {
    // Standard configuration file
    final ConfigFile aCF = ConfigFile.getInstance ();

    final Map <String, Object> ret = new HashMap <String, Object> ();
    // Read all properties from the standard configuration file
    ret.put ("javax.persistence.jdbc.driver", aCF.getString (CONFIG_JDBC_DRIVER));
    ret.put ("javax.persistence.jdbc.url", aCF.getString (CONFIG_JDBC_URL));
    ret.put ("javax.persistence.jdbc.user", aCF.getString (CONFIG_JDBC_USER));
    ret.put ("javax.persistence.jdbc.password", aCF.getString (CONFIG_JDBC_PASSWORD));
    ret.put ("eclipselink.target-database", aCF.getString (CONFIG_TARGET_DATABASE));
    // Connection pooling
    ret.put ("eclipselink.jdbc.read-connections.max", aCF.getString (CONFIG_JDBC_READ_CONNECTIONS_MAX));

    // EclipseLink should create the database schema automatically
    // Values: Values: none/create-tables/drop-and-create-tables
    ret.put ("eclipselink.ddl-generation", "drop-and-create-tables");
    ret.put ("eclipselink.ddl-generation.output-mode", "sql-script");
    ret.put ("eclipselink.create-ddl-jdbc-file-name", "db-create-smp.sql");
    ret.put ("eclipselink.drop-ddl-jdbc-file-name", "db-drop-smp.sql");

    return ret;
  }

  private SMPJPAWrapper () {
    super ("peppol-smp", _createSettingsMap ());
  }

  @Nonnull
  public static SMPJPAWrapper getInstance () {
    return s_aInstance;
  }
}

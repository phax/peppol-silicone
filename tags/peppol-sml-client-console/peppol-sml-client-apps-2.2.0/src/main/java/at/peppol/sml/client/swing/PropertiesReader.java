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
package at.peppol.sml.client.swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class PropertiesReader {
  private static final String KEY_HOSTNNAME = "hostname";
  private static final String KEY_SMP_ID = "smp_id";
  private static final String KEY_KEYSTORE_PATH = "keystore_path";
  private static final String KEY_KEYSTORE_PASSWORD = "keystore_password";

  private File propertiesPath;
  private Properties properties;

  public boolean readProperties (final File path) {
    this.propertiesPath = path;

    return readProperties ();
  }

  public boolean writeProperties (final File path) {
    this.propertiesPath = path;

    return writeProperties ();
  }

  private boolean readProperties () {
    try {
      properties = new Properties ();
      if (propertiesPath != null && propertiesPath.exists ())
        properties.load (new FileInputStream (propertiesPath));

      return true;
    }
    catch (final IOException e) {
      e.printStackTrace ();
    }

    return false;
  }

  private boolean writeProperties () {
    if (properties != null) {
      try {
        properties.store (new FileOutputStream (propertiesPath), "PEPPOL SML Client properties file\n");

        return true;
      }
      catch (final IOException e) {
        e.printStackTrace ();
      }
    }

    return false;
  }

  public String getHostname () {
    return properties.getProperty (KEY_HOSTNNAME);
  }

  public String getSmpId () {
    return properties.getProperty (KEY_SMP_ID);
  }

  public String getKeyStorePath () {
    return properties.getProperty (KEY_KEYSTORE_PATH);
  }

  public String getKeyStorePwd () {
    return properties.getProperty (KEY_KEYSTORE_PASSWORD);
  }

  public void setHostname (final String hostname) {
    properties.setProperty (KEY_HOSTNNAME, hostname);
  }

  public void setSmpId (final String smpID) {
    properties.setProperty (KEY_SMP_ID, smpID);
  }

  public void setKeyStorePath (final String keyStorePath) {
    properties.setProperty (KEY_KEYSTORE_PATH, keyStorePath);
  }

  public void setKeyStorePwd (final String keyStorePwd) {
    properties.setProperty (KEY_KEYSTORE_PASSWORD, keyStorePwd);
  }
}
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
package eu.peppol.inbound.server;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.peppol.start.identifier.Configuration;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * User: nigel Date: Oct 24, 2011 Time: 3:08:30 PM
 */
public class ContextListener implements ServletContextListener {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-inb");

  private static File _locateKeystore (final Configuration configuration) {
    final String keystoreFileName = configuration.getKeyStoreFilename ();
    final File keystoreFile = new File (keystoreFileName);
    if (!keystoreFile.exists ()) {
      throw new IllegalStateException ("Keystore file does not exist:" + keystoreFileName);
    }
    if (!keystoreFile.canRead ()) {
      throw new IllegalStateException ("Unable to read keystore in:" + keystoreFileName + ", check permissions");
    }
    return keystoreFile;
  }

  public void contextInitialized (final ServletContextEvent event) {
    event.getServletContext ().log ("Oxalis messages are emitted using SLF4J, search for oxalis.log");

    log.info ("Starting Oxalis Access Point");
    log.debug ("Initialising keystore");

    try {
      final KeystoreManager keystoreManager = new KeystoreManager ();
      final Configuration configuration = Configuration.getInstance ();

      final File keystore = _locateKeystore (configuration);

      final String keystorePassword = configuration.getKeyStorePassword ();
      keystoreManager.initialiseKeystore (keystore, keystorePassword);
      log.debug ("Keystore initialised from " + keystore);
    }
    catch (final RuntimeException e) {
      log.error ("Unable to initialize: " + e, e);

      // Shoves a decent error message into the Tomcat log
      event.getServletContext ().log ("ERROR: Unable to initialize: " + e, e);
      throw e;
    }
  }

  public void contextDestroyed (final ServletContextEvent event) {
    log.info ("Stopping Oxalis Access Point");
  }
}

package eu.peppol.inbound.server;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eu.peppol.inbound.util.Log;
import eu.peppol.start.identifier.Configuration;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * User: nigel Date: Oct 24, 2011 Time: 3:08:30 PM
 */
public class ContextListener implements ServletContextListener {
  public void contextInitialized (final ServletContextEvent event) {
    event.getServletContext ().log ("Oxalis messages are emitted using SLF4J, search for oxalis.log");

    Log.info ("Starting Oxalis Access Point");
    Log.debug ("Initialising keystore");

    try {
      final KeystoreManager keystoreManager = new KeystoreManager ();
      final Configuration configuration = Configuration.getInstance ();

      final File keystore = locateKeystore (configuration);

      final String keystorePassword = configuration.getKeyStorePassword ();
      keystoreManager.initialiseKeystore (keystore, keystorePassword);
      Log.debug ("Keystore initialised from " + keystore);
    }
    catch (final RuntimeException e) {
      Log.error ("Unable to initialize: " + e, e);

      // Shoves a decent error message into the Tomcat log
      event.getServletContext ().log ("ERROR: Unable to initialize: " + e, e);
      throw e;
    }
  }

  File locateKeystore (final Configuration configuration) {
    final String keystoreFileName = configuration.getKeyStoreFileName ();
    final File keystoreFile = new File (keystoreFileName);
    if (!keystoreFile.exists ()) {
      throw new IllegalStateException ("Keystore file does not exist:" + keystoreFileName);
    }
    if (!keystoreFile.canRead ()) {
      throw new IllegalStateException ("Unable to read keystore in:" + keystoreFileName + ", check permissions");
    }
    return keystoreFile;
  }

  public void contextDestroyed (final ServletContextEvent event) {
    Log.info ("Stopping Oxalis Access Point");
  }
}

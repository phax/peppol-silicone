package eu.peppol.start.identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 09.11.11
 *         Time: 10:05
 */
public class ConfigurationTest {
  @Test
  public void testGetProperty () throws Exception {

    final Properties fallback = new Properties ();
    final Properties p = new Properties (fallback);

    fallback.setProperty ("p1", "fallback");

    assertEquals ("fallback", p.getProperty ("p1"));

    p.setProperty ("p1", "overridden");
    assertEquals ("overridden", p.getProperty ("p1"));
    assertEquals ("fallback", fallback.getProperty ("p1"));

    assertNull (System.getProperty ("p1"));

  }

  @Test
  public void getKeyStorePath () {
    final Configuration configuration = Configuration.getInstance ();

    // This requires the presence of the resource named by
    // Configuration#CUSTOM_PROPERTIES_PATH,
    // which needs to hold oxalis.keystore property
    assertNotNull (configuration.getKeyStoreFileName ());

    assertEquals ("TEST", configuration.getInboundMessageStore ());
  }

}

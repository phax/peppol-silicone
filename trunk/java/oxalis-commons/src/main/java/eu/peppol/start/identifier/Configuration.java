package eu.peppol.start.identifier;

import static eu.peppol.start.identifier.Configuration.PropertyDef.INBOUND_MESSAGE_STORE;
import static eu.peppol.start.identifier.Configuration.PropertyDef.KEYSTORE_PASSWORD;
import static eu.peppol.start.identifier.Configuration.PropertyDef.KEYSTORE_PATH;
import static eu.peppol.start.identifier.Configuration.PropertyDef.OUTBOUND_MESSAGE_STORE;
import static eu.peppol.start.identifier.Configuration.PropertyDef.PEPPOL_SENDER_ID;
import static eu.peppol.start.identifier.Configuration.PropertyDef.PEPPOL_SERVICE_NAME;
import static eu.peppol.start.identifier.Configuration.PropertyDef.WSDL_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import com.phloc.commons.string.StringHelper;

/**
 * User: nigel Date: Oct 8, 2011 Time: 5:05:52 PM
 */
public final class Configuration {

  private static String CUSTOM_PROPERTIES_PATH = "/oxalis-web.properties";
  private static String FALLBACK_PROPERTIES_PATH = "/oxalis.properties";

  /**
   * Property definitions, which are declared separately from the actual
   * instances of the properties.
   */
  static enum PropertyDef {
    KEYSTORE_PATH ("oxalis.keystore", true),
    KEYSTORE_PASSWORD ("oxalis.keystore.password", true),
    INBOUND_MESSAGE_STORE ("oxalis.inbound.message.store", false),
    OUTBOUND_MESSAGE_STORE ("oxalis.outbound.message.store", false),
    WSDL_FILE_NAME ("oxalis.wsdl", true),
    PEPPOL_SENDER_ID ("peppol.senderid", true),
    PEPPOL_SERVICE_NAME ("peppol.servicename", true);

    /**
     * External name of property as it appears in your .properties file, i.e.
     * with the dot notation, like for instance "x.y.z = value"
     */
    private final String m_sPropertyName;
    private final boolean m_bRequired;

    /**
     * Enum constructor
     * 
     * @param propertyName
     *        name of property as it appears in your .properties file
     */
    PropertyDef (final String propertyName, final boolean required) {
      if (propertyName == null || propertyName.trim ().length () == 0) {
        throw new IllegalArgumentException ("Property name is required");
      }
      this.m_sPropertyName = propertyName;
      this.m_bRequired = required;
    }

    /**
     * Locates the value of this named property in the supplied collection of
     * properties.
     * 
     * @param properties
     *        collection of properties to search
     * @return value of property
     */
    public String getValue (final Properties properties) {
      if (m_bRequired)
        return required (properties.getProperty (m_sPropertyName));
      return properties.getProperty (m_sPropertyName);
    }

    String required (final String value) {
      if (StringHelper.hasNoTextAfterTrim (value)) {
        throw new IllegalStateException ("Property '" +
                                         m_sPropertyName +
                                         "' does not exist or is empty, check " +
                                         FALLBACK_PROPERTIES_PATH);
      }
      return value;
    }

  }

  // Holds our singleton configuration instance
  private static Configuration s_aInstance;

  // Holds the properties, which we loaded upon instantiation
  private final Properties m_aProperties;

  /**
   * This is the factory method, which gives access to the singleton instance
   * 
   * @return a reference to our singleton configuration
   */
  public static synchronized Configuration getInstance () {
    if (s_aInstance == null) {
      s_aInstance = new Configuration ();
    }

    return s_aInstance;
  }

  @SuppressWarnings ("unchecked")
  private Configuration () {

    final Properties fallBackProps = new Properties ();
    loadPropertiesFile (fallBackProps, FALLBACK_PROPERTIES_PATH);

    Log.info ("Loading properties from " +
              CUSTOM_PROPERTIES_PATH +
              ", which will fallback to " +
              FALLBACK_PROPERTIES_PATH +
              " if properties do not exist.");
    final Properties customProps = new Properties (fallBackProps);
    loadPropertiesFile (customProps, CUSTOM_PROPERTIES_PATH);

    m_aProperties = customProps;

    Log.info ("======= Properties in effect: =======");
    for (final Enumeration <String> e = (Enumeration <String>) m_aProperties.propertyNames (); e.hasMoreElements ();) {
      final String propName = e.nextElement ();
      Log.info (propName + " = " + m_aProperties.getProperty (propName));
    }
    Log.info ("======================================");
    Log.info ("Configuration loaded.");
  }

  /**
   * Loads properties from the given properties resource into the given
   * properties object.
   * 
   * @param properties
   *        references the object into which properties will be loaded
   * @param propertiesResourceName
   *        the name of the properties resource
   */
  void loadPropertiesFile (final Properties properties, final String propertiesResourceName) {
    final URL url = getClass ().getResource (propertiesResourceName);
    if (url == null) {
      Log.info ("No properties loaded from " + propertiesResourceName + ", file was not found in classpath");
      return;
    }

    Log.info (propertiesResourceName + " --> " + url.toString ());

    InputStream inputStream = null;
    try {
      inputStream = url.openStream ();
      properties.load (inputStream);
    }
    catch (final IOException e) {
      throw new RuntimeException ("No configuration file found at " + FALLBACK_PROPERTIES_PATH, e);
    }
    finally {
      try {
        if (inputStream != null)
          inputStream.close ();
      }
      catch (final Exception e) {
        Log.warn ("Unable to close input stream");
      }
    }
  }

  public String getKeyStoreFileName () {
    final String s = KEYSTORE_PATH.getValue (m_aProperties);
    return s == null ? null : s.trim ();
  }

  public String getKeyStorePassword () {
    return KEYSTORE_PASSWORD.getValue (m_aProperties);
  }

  public String getInboundMessageStore () {

    String msgStore = INBOUND_MESSAGE_STORE.getValue (m_aProperties);
    if (msgStore == null) {
      msgStore = System.getProperty ("java.io.tmpdir") + File.separator + "inbound";
    }
    return msgStore;
  }

  public String getOutboundMessageStore () {
    String msgStore = OUTBOUND_MESSAGE_STORE.getValue (m_aProperties);
    if (msgStore == null) {
      msgStore = System.getProperty ("java.io.tmpdir") + File.separator + "outbound";
    }
    return msgStore;
  }

  public String getWsdlFileName () {
    return WSDL_FILE_NAME.getValue (m_aProperties);
  }

  public String getPeppolSenderId () {
    return PEPPOL_SENDER_ID.getValue (m_aProperties);
  }

  public String getPeppolServiceName () {
    return PEPPOL_SERVICE_NAME.getValue (m_aProperties);
  }
}

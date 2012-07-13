package eu.peppol.start.identifier;

import java.util.Properties;

import com.phloc.commons.string.StringHelper;

/**
 * Property definitions, which are declared separately from the actual instances
 * of the properties.
 */
enum PropertyDef {
  KEYSTORE_PATH ("oxalis.keystore", true),
  KEYSTORE_PASSWORD ("oxalis.keystore.password", true),
  INBOUND_MESSAGE_STORE ("oxalis.inbound.message.store", false),
  OUTBOUND_MESSAGE_STORE ("oxalis.outbound.message.store", false),
  PEPPOL_SENDER_ID ("peppol.senderid", true),
  PEPPOL_SERVICE_NAME ("peppol.servicename", true);

  /**
   * External name of property as it appears in your .properties file, i.e. with
   * the dot notation, like for instance "x.y.z = value"
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
    if (StringHelper.hasNoTextAfterTrim (value))
      throw new IllegalStateException ("Property '" + m_sPropertyName + "' does not exist or is empty, check the file");
    return value;
  }
}

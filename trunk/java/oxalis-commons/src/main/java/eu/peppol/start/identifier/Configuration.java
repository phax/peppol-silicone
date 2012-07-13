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
package eu.peppol.start.identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.streams.StreamUtils;

/**
 * User: nigel Date: Oct 8, 2011 Time: 5:05:52 PM
 */
public final class Configuration {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-com");
  private static final String CUSTOM_PROPERTIES_PATH = "/oxalis-web.properties";
  static final String FALLBACK_PROPERTIES_PATH = "/oxalis.properties";

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

    log.info ("Loading properties from " +
              CUSTOM_PROPERTIES_PATH +
              ", which will fallback to " +
              FALLBACK_PROPERTIES_PATH +
              " if properties do not exist.");
    final Properties customProps = new Properties (fallBackProps);
    loadPropertiesFile (customProps, CUSTOM_PROPERTIES_PATH);

    m_aProperties = customProps;

    log.info ("======= Properties in effect: =======");
    for (final Enumeration <String> e = (Enumeration <String>) m_aProperties.propertyNames (); e.hasMoreElements ();) {
      final String propName = e.nextElement ();
      log.info (propName + " = " + m_aProperties.getProperty (propName));
    }
    log.info ("======================================");
    log.info ("Configuration loaded.");
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
    final InputStream aIS = ClassPathResource.getInputStream (propertiesResourceName);
    if (aIS == null) {
      log.info ("No properties loaded from " + propertiesResourceName + ", file was not found in classpath");
      return;
    }

    try {
      properties.load (aIS);
    }
    catch (final IOException e) {
      throw new RuntimeException ("No configuration file found at " + propertiesResourceName, e);
    }
    finally {
      StreamUtils.close (aIS);
    }
  }

  @Nullable
  public String getKeyStoreFileName () {
    final String s = PropertyDef.KEYSTORE_PATH.getValue (m_aProperties);
    return s == null ? null : s.trim ();
  }

  @Nullable
  public String getKeyStorePassword () {
    return PropertyDef.KEYSTORE_PASSWORD.getValue (m_aProperties);
  }

  @Nonnull
  public String getInboundMessageStore () {
    String msgStore = PropertyDef.INBOUND_MESSAGE_STORE.getValue (m_aProperties);
    if (msgStore == null)
      msgStore = SystemProperties.getTmpDir () + File.separator + "inbound";
    return msgStore;
  }

  @Nonnull
  public String getOutboundMessageStore () {
    String msgStore = PropertyDef.OUTBOUND_MESSAGE_STORE.getValue (m_aProperties);
    if (msgStore == null)
      msgStore = SystemProperties.getTmpDir () + File.separator + "outbound";
    return msgStore;
  }

  @Nullable
  public String getWsdlFileName () {
    return PropertyDef.WSDL_FILE_NAME.getValue (m_aProperties);
  }

  @Nullable
  public String getPeppolSenderId () {
    return PropertyDef.PEPPOL_SENDER_ID.getValue (m_aProperties);
  }

  @Nullable
  public String getPeppolServiceName () {
    return PropertyDef.PEPPOL_SERVICE_NAME.getValue (m_aProperties);
  }
}

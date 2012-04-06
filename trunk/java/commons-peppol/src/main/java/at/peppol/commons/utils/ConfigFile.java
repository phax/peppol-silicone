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
package at.peppol.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.string.StringHelper;

/**
 * Used for accessing configuration files based on properties. By default first
 * the private (not checked-in) version of the config file called
 * <code>private-config.properties</code> is accessed. If no such file is
 * present, the default config file (which is also in the SCM) is accessed by
 * the name<code>config.properties</code>.<br>
 * Additionally you can create a new instance with a custom file path.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ConfigFile {
  private static final class SingletonHolder {
    static final ConfigFile s_aInstance = new ConfigFile ();
  }

  /** Default file name for the private config file */
  public static final String DEFAULT_PRIVATE_CONFIG_PROPERTIES = "private-config.properties";

  /** Default file name for the regular config file */
  public static final String DEFAULT_CONFIG_PROPERTIES = "config.properties";

  private static final Logger s_aLogger = LoggerFactory.getLogger (ConfigFile.class);

  private final Properties m_aProps = new Properties ();

  /**
   * Default constructor for the default file paths (private-config.properties
   * and afterwards config.properties)
   */
  private ConfigFile () {
    this (DEFAULT_PRIVATE_CONFIG_PROPERTIES, DEFAULT_CONFIG_PROPERTIES);
  }

  /**
   * Constructor for explicitly specifying a file path to read.
   * 
   * @param aConfigPaths
   *        The array of paths to the config files to be read. Must be
   *        classpath-relative. The first file that could be read will be taken
   */
  public ConfigFile (@Nonnull @Nonempty final String... aConfigPaths) {
    if (ArrayHelper.isEmpty (aConfigPaths))
      throw new IllegalArgumentException ("No config file paths have been specified!");

    boolean bRead = false;
    for (final String sConfigPath : aConfigPaths)
      if (_readConfigFile (sConfigPath).isSuccess ()) {
        bRead = true;
        break;
      }

    if (!bRead) {
      // No config file found at all
      s_aLogger.warn ("Failed to resolve config file paths: " + ContainerHelper.newList (aConfigPaths).toString ());
    }
  }

  @Nonnull
  private ESuccess _readConfigFile (@Nonnull final String sPath) {
    // Try to get the input stream for the passed property file name
    InputStream aIS = ClassPathResource.getInputStream (sPath);
    if (aIS == null) {
      // Fallback to filesystem - maybe this helps...
      aIS = new FileSystemResource (sPath).getInputStream ();
    }
    if (aIS != null) {
      try {
        // Does not close the input stream!
        m_aProps.load (aIS);
        s_aLogger.info ("Loaded configuration from '" + sPath + "': " + Collections.list (m_aProps.keys ()));
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex) {
        s_aLogger.error ("Failed to read config file '" + sPath + "'", ex);
      }
      finally {
        // Manually close the input stream!
        StreamUtils.close (aIS);
      }
    }
    return ESuccess.FAILURE;
  }

  /**
   * @return The default configuration file denoted by the file names
   *         {@value #DEFAULT_PRIVATE_CONFIG_PROPERTIES} and
   *         {@value #DEFAULT_CONFIG_PROPERTIES}.
   */
  @Nonnull
  public static ConfigFile getInstance () {
    return SingletonHolder.s_aInstance;
  }

  /**
   * Get the string from the configuration files
   * 
   * @param sKey
   *        The key to search
   * @return <code>null</code> if no such value is in the configuration file.
   */
  @Nullable
  public final String getString (@Nonnull final String sKey) {
    return getString (sKey, null);
  }

  /**
   * Get the string from the configuration files
   * 
   * @param sKey
   *        The key to search
   * @param sDefault
   *        The default value to be returned if the value was not found. May be
   *        <code>null</code>.
   * @return the passed default value if no such value is in the configuration
   *         file.
   */
  @Nullable
  public final String getString (@Nonnull final String sKey, @Nullable final String sDefault) {
    final String sValue = m_aProps.getProperty (sKey);
    return sValue != null ? StringHelper.trim (sValue) : sDefault;
  }

  @Nullable
  public final char [] getCharArray (@Nonnull final String sKey) {
    final String ret = getString (sKey);
    return ret == null ? null : ret.toCharArray ();
  }

  public final boolean getBoolean (@Nonnull final String sKey, final boolean bDefault) {
    return StringHelper.parseBool (getString (sKey), bDefault);
  }

  public final int getInt (@Nonnull final String sKey, final int nDefault) {
    return StringHelper.parseInt (getString (sKey), nDefault);
  }

  /**
   * @return A {@link Set} with all keys contained in the configuration file
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Set <String> getAllKeys () {
    // Convert from Set<Object> to Set<String>
    final Set <String> ret = new HashSet <String> ();
    for (final Object o : m_aProps.keySet ())
      ret.add ((String) o);
    return ret;
  }
}

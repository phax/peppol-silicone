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

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
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
   * @param sPropertyName
   *        name of property as it appears in your .properties file
   */
  private PropertyDef (@Nonnull @Nonempty final String sPropertyName, final boolean required) {
    m_sPropertyName = sPropertyName;
    m_bRequired = required;
  }

  /**
   * Locates the value of this named property in the supplied collection of
   * properties.
   * 
   * @param aProperties
   *        collection of properties to search
   * @return value of property
   */
  @Nullable
  public String getValue (final Properties aProperties) {
    final String sValue = aProperties.getProperty (m_sPropertyName);
    if (m_bRequired && StringHelper.hasNoTextAfterTrim (sValue))
      throw new IllegalStateException ("Property '" + m_sPropertyName + "' does not exist or is empty, check the file");
    return sValue;
  }
}

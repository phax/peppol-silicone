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
package at.peppol.sml.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public enum ESMLAction {
  A1 (ESMLObjectType.PARTICIPANT, ESMLCommand.CREATE, new SMLActionParameter ("identifier value"), new SMLActionParameter ("identifier scheme")),
  A2 (ESMLObjectType.PARTICIPANT, ESMLCommand.DELETE, new SMLActionParameter ("identifier value"), new SMLActionParameter ("identifier scheme")),
  A3 (ESMLObjectType.PARTICIPANT, ESMLCommand.LIST),
  A4 (ESMLObjectType.PARTICIPANT, ESMLCommand.PREPARETOMIGRATE, new SMLActionParameter ("identifier value"), new SMLActionParameter ("identifier scheme")),
  A5 (ESMLObjectType.PARTICIPANT, ESMLCommand.MIGRATE, new SMLActionParameter ("identifier value"), new SMLActionParameter ("identifier scheme"), new SMLActionParameter ("migration code")),
  A6 (ESMLObjectType.METADATA, ESMLCommand.CREATE, new SMLActionParameter ("physical address"), new SMLActionParameter ("logical address")),
  A7 (ESMLObjectType.METADATA, ESMLCommand.UPDATE, new SMLActionParameter ("physical address"), new SMLActionParameter ("logical address")),
  A8 (ESMLObjectType.METADATA, ESMLCommand.DELETE),
  A9 (ESMLObjectType.METADATA, ESMLCommand.READ);

  private ESMLObjectType m_eObjectType;
  private ESMLCommand m_eCommand;
  private SMLActionParameter [] m_aParams;

  private ESMLAction (@Nonnull final ESMLObjectType eObjectType,
                      @Nonnull final ESMLCommand eCommand,
                      @Nonnegative final SMLActionParameter... aParams) {
    m_eObjectType = eObjectType;
    m_eCommand = eCommand;
    m_aParams = aParams;
  }

  @Nonnull
  public ESMLObjectType getObjectType () {
    return m_eObjectType;
  }

  @Nonnull
  public ESMLCommand getCommand () {
    return m_eCommand;
  }

  @Nonnegative
  public int getRequiredParameters () {
    return m_aParams.length;
  }

  @Nonnegative
  public List <String> getRequiredParameterDescriptions () {
    final List <String> ret = new ArrayList <String> ();
    for (final SMLActionParameter x : m_aParams)
      ret.add (x.getDescription ());
    return ret;
  }

  @Override
  public String toString () {
    return m_eObjectType.getName () + " " + m_eCommand.getName ();
  }
}

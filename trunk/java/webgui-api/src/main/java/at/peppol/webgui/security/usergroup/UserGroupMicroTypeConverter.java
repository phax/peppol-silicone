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
package at.peppol.webgui.security.usergroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroElement;

public final class UserGroupMicroTypeConverter implements IMicroTypeConverter {
  private static final String ATTR_ID = "id";
  private static final String ATTR_NAME = "name";
  private static final String ELEMENT_USER = "user";
  private static final String ELEMENT_ROLE = "role";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName) {
    final IUserGroup aUserGroup = (IUserGroup) aObject;
    final IMicroElement eUserGroup = new MicroElement (sNamespaceURI, sTagName);
    eUserGroup.setAttribute (ATTR_ID, aUserGroup.getID ());
    eUserGroup.setAttribute (ATTR_NAME, aUserGroup.getName ());
    for (final String sUserID : ContainerHelper.getSorted (aUserGroup.getAllContainedUserIDs ()))
      eUserGroup.appendElement (ELEMENT_USER).setAttribute (ATTR_ID, sUserID);
    for (final String sRoleID : ContainerHelper.getSorted (aUserGroup.getAllContainedRoleIDs ()))
      eUserGroup.appendElement (ELEMENT_ROLE).setAttribute (ATTR_ID, sRoleID);
    return eUserGroup;
  }

  @Nonnull
  public IUserGroup convertToNative (@Nonnull final IMicroElement eUserGroup) {
    final String sID = eUserGroup.getAttribute (ATTR_ID);
    final String sName = eUserGroup.getAttribute (ATTR_NAME);
    final UserGroup aUserGroup = new UserGroup (sID, sName);
    for (final IMicroElement eUser : eUserGroup.getChildElements (ELEMENT_USER))
      aUserGroup.assignUser (eUser.getAttribute (ATTR_ID));
    for (final IMicroElement eRole : eUserGroup.getChildElements (ELEMENT_ROLE))
      aUserGroup.assignRole (eRole.getAttribute (ATTR_ID));
    return aUserGroup;
  }
}

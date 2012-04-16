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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.peppol.webgui.security.usergroup.UserGroup;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;

/**
 * Test class for class {@link UserGroup}.
 * 
 * @author philip
 */
public final class UserGroupTest {
  @Test
  public void testBasic () {
    final UserGroup aUserGroup = new UserGroup ("id1", "User group 5");
    assertEquals ("id1", aUserGroup.getID ());
    assertEquals ("User group 5", aUserGroup.getName ());
  }

  @Test
  public void testMicroConversion () {
    final UserGroup aUserGroup = new UserGroup ("id1", "User group 5");
    aUserGroup.assignUser ("user1");
    aUserGroup.assignUser ("user2");
    aUserGroup.assignRole ("role1");
    aUserGroup.assignRole ("role2");

    // To XML
    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aUserGroup, "usergroup");
    assertNotNull (aElement);

    // From XML
    final UserGroup aUserGroup2 = MicroTypeConverter.convertToNative (aElement, UserGroup.class);
    assertNotNull (aUserGroup2);
    assertEquals ("id1", aUserGroup2.getID ());
    assertEquals ("User group 5", aUserGroup2.getName ());
    assertEquals (2, aUserGroup.getAllContainedUserIDs ().size ());
    assertTrue (aUserGroup.getAllContainedUserIDs ().contains ("user1"));
    assertTrue (aUserGroup.getAllContainedUserIDs ().contains ("user2"));
    assertEquals (2, aUserGroup.getAllContainedRoleIDs ().size ());
    assertTrue (aUserGroup.getAllContainedRoleIDs ().contains ("role1"));
    assertTrue (aUserGroup.getAllContainedRoleIDs ().contains ("role2"));
  }
}

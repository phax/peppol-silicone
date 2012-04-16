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
package at.peppol.webgui.security.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.peppol.webgui.AbstractStorageAwareTestCase;
import at.peppol.webgui.security.CSecurity;
import at.peppol.webgui.security.login.ELoginResult;
import at.peppol.webgui.security.login.LoggedInUserManager;

/**
 * Test class for class {@link LoggedInUserManager}.
 * 
 * @author philip
 */
public final class LoggedInUserManagerTest extends AbstractStorageAwareTestCase {
  @Test
  public void testInit () {
    LoggedInUserManager.getInstance ();
  }

  @Test
  public void testLoginLogout () {
    final LoggedInUserManager aUM = LoggedInUserManager.getInstance ();
    // Check any non-present user
    assertFalse (aUM.isUserLoggedIn ("any"));
    assertEquals (ELoginResult.USER_NOT_EXISTING, aUM.loginUser ("bla@foo.com", "mypw"));
    assertNull (aUM.getCurrentUserID ());

    // Login user
    assertEquals (ELoginResult.SUCCESS,
                  aUM.loginUser (CSecurity.USER_ADMINISTRATOR_EMAIL, CSecurity.USER_ADMINISTRATOR_PASSWORD));
    assertTrue (aUM.isUserLoggedIn (CSecurity.USER_ADMINISTRATOR_ID));
    assertEquals (1, aUM.getLoggedInUserCount ());

    // Try to login another user in the same session
    assertEquals (ELoginResult.SESSION_ALREADY_HAS_USER,
                  aUM.loginUser (CSecurity.USER_USER_EMAIL, CSecurity.USER_USER_PASSWORD));
    assertTrue (aUM.isUserLoggedIn (CSecurity.USER_ADMINISTRATOR_ID));
    assertFalse (aUM.isUserLoggedIn (CSecurity.USER_USER_ID));
    assertEquals (1, aUM.getLoggedInUserCount ());

    // Check current user ID
    assertEquals (CSecurity.USER_ADMINISTRATOR_ID, aUM.getCurrentUserID ());

    // Logout non-logged in user
    assertTrue (aUM.logoutUser (CSecurity.USER_USER_ID).isUnchanged ());
    assertEquals (1, aUM.getLoggedInUserCount ());

    // Logout correct user
    assertTrue (aUM.logoutCurrentUser ().isChanged ());
    assertEquals (0, aUM.getLoggedInUserCount ());
    assertNull (aUM.getCurrentUserID ());
  }
}

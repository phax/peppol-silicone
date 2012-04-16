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
package at.peppol.webgui.security.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import at.peppol.webgui.security.user.User;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;

/**
 * Test class for class {@link User}.
 * 
 * @author philip
 */
public final class UserTest {
  @Test
  public void testBasic () {
    final User aUser = new User ("id1", "me@example.org", "ABCDEF", "Philip", "Helger", Locale.GERMANY);
    assertEquals ("id1", aUser.getID ());
    assertEquals ("me@example.org", aUser.getEmailAddress ());
    assertEquals ("ABCDEF", aUser.getPasswordHash ());
    assertEquals ("Philip", aUser.getFirstName ());
    assertEquals ("Helger", aUser.getLastName ());
    assertEquals (Locale.GERMANY, aUser.getDesiredLocale ());
  }

  @Test
  public void testMicroConversion () {
    final User aUser = new User ("id1", "me@example.org", "ABCDEF", "Philip", "Helger", Locale.GERMANY);

    // To XML
    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aUser, "user");
    assertNotNull (aElement);

    // From XML
    final User aUser2 = MicroTypeConverter.convertToNative (aElement, User.class);
    assertNotNull (aUser2);
    assertEquals ("id1", aUser2.getID ());
    assertEquals ("me@example.org", aUser2.getEmailAddress ());
    assertEquals ("ABCDEF", aUser2.getPasswordHash ());
    assertEquals ("Philip", aUser2.getFirstName ());
    assertEquals ("Helger", aUser2.getLastName ());
    assertEquals (Locale.GERMANY, aUser2.getDesiredLocale ());
  }
}

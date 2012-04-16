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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.microdom.utils.MicroUtils;

public final class UserMicroTypeConverter implements IMicroTypeConverter {
  private static final String ATTR_ID = "id";
  private static final String ATTR_DESIREDLOCALE = "desiredlocale";
  private static final String ELEMENT_EMAILADDRESS = "emailaddress";
  private static final String ELEMENT_PASSWORDHASH = "passwordhash";
  private static final String ELEMENT_FIRSTNAME = "firstname";
  private static final String ELEMENT_LASTNAME = "lastname";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName) {
    final IUser aUser = (IUser) aObject;
    final IMicroElement eUser = new MicroElement (sNamespaceURI, sTagName);
    eUser.setAttribute (ATTR_ID, aUser.getID ());
    eUser.appendElement (ELEMENT_EMAILADDRESS).appendText (aUser.getEmailAddress ());
    eUser.appendElement (ELEMENT_PASSWORDHASH).appendText (aUser.getPasswordHash ());
    if (aUser.getFirstName () != null)
      eUser.appendElement (ELEMENT_FIRSTNAME).appendText (aUser.getFirstName ());
    if (aUser.getLastName () != null)
      eUser.appendElement (ELEMENT_LASTNAME).appendText (aUser.getLastName ());
    if (aUser.getDesiredLocale () != null)
      eUser.setAttribute (ATTR_DESIREDLOCALE, aUser.getDesiredLocale ().toString ());
    return eUser;
  }

  @Nonnull
  public IUser convertToNative (@Nonnull final IMicroElement eUser) {
    final String sID = eUser.getAttribute (ATTR_ID);
    final String sEmailAddress = MicroUtils.getChildTextContent (eUser, ELEMENT_EMAILADDRESS);
    final String sPasswordHash = MicroUtils.getChildTextContent (eUser, ELEMENT_PASSWORDHASH);
    final String sFirstName = MicroUtils.getChildTextContent (eUser, ELEMENT_FIRSTNAME);
    final String sLastName = MicroUtils.getChildTextContent (eUser, ELEMENT_LASTNAME);
    final String sDesiredLocale = eUser.getAttribute (ATTR_DESIREDLOCALE);
    final Locale aDesiredLocale = sDesiredLocale == null ? null : LocaleCache.get (sDesiredLocale);
    return new User (sID, sEmailAddress, sPasswordHash, sFirstName, sLastName, aDesiredLocale);
  }
}

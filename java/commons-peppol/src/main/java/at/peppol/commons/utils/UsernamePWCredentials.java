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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.base64.Base64Helper;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IUsernamePWCredentials}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class UsernamePWCredentials implements IUsernamePWCredentials {
  private static final Logger s_aLogger = LoggerFactory.getLogger (UsernamePWCredentials.class);
  private static final String BASIC_AUTH_PREFIX = "Basic ";
  private String m_sUsername;
  private String m_sPassword;

  public UsernamePWCredentials () {}

  public UsernamePWCredentials (@Nonnull @Nonempty final String sUsername, @Nullable final String sPassword) {
    setUsername (sUsername);
    setPassword (sPassword);
  }

  public void setUsername (@Nonnull @Nonempty final String sUsername) {
    if (StringHelper.hasNoText (sUsername))
      throw new IllegalArgumentException ("No user name given!");
    if (sUsername.indexOf (':') >= 0)
      s_aLogger.error ("The user name '" +
                       sUsername +
                       "' contains a ':' character which prevents it from being correctly converted to an HTTP basic authentication value!!!");
    m_sUsername = sUsername;
  }

  @Nullable
  public String getUsername () {
    return m_sUsername;
  }

  public void setPassword (@Nullable final String sPassword) {
    m_sPassword = sPassword;
  }

  @Nullable
  public String getPassword () {
    return m_sPassword;
  }

  @Nonnull
  public String getAsHTTPHeaderValue () {
    final String sCombined = m_sPassword == null ? m_sUsername : m_sUsername + ':' + m_sPassword;
    return BASIC_AUTH_PREFIX + Base64Helper.safeEncode (sCombined, CCharset.CHARSET_UTF_8);
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UsernamePWCredentials))
      return false;
    final UsernamePWCredentials rhs = (UsernamePWCredentials) o;
    return EqualsUtils.equals (m_sUsername, rhs.m_sUsername) && EqualsUtils.equals (m_sPassword, rhs.m_sPassword);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sUsername).append (m_sPassword).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("username", m_sUsername).appendPassword ("password").toString ();
  }

  @Nullable
  public static IUsernamePWCredentials createFromBasicAuth (@Nonnull final String sAuthHeader) {
    if (!sAuthHeader.startsWith (BASIC_AUTH_PREFIX))
      return null;

    // Remove the auth prefix
    final String sEncodedCredentials = sAuthHeader.substring (BASIC_AUTH_PREFIX.length ());

    // Apply Base64 decoding
    final String sUsernamePassword = Base64Helper.safeDecodeAsString (sEncodedCredentials, CCharset.CHARSET_UTF_8);
    if (sUsernamePassword == null) {
      // Illegal base64 encoded value
      return null;
    }

    // Do we have a username/password separator?
    final int nIndex = sUsernamePassword.indexOf (':');
    if (nIndex >= 0)
      return new UsernamePWCredentials (sUsernamePassword.substring (0, nIndex),
                                        sUsernamePassword.substring (nIndex + 1));
    return new UsernamePWCredentials (sUsernamePassword, null);
  }
}

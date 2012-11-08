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
package at.peppol.smp.server.util;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.exceptions.InitializationException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * This class adds a XML DSIG to successful GET's for SignedServiceMetadata
 * objects.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SignatureFilter implements ContainerResponseFilter {
  private static final String CONFIG_XMLDSIG_KEYSTORE_CLASSPATH = "xmldsig.keystore.classpath";
  private static final String CONFIG_XMLDSIG_KEYSTORE_PASSWORD = "xmldsig.keystore.password";
  private static final String CONFIG_XMLDSIG_KEYSTORE_KEY_ALIAS = "xmldsig.keystore.key.alias";
  private static final String CONFIG_XMLDSIG_KEYSTORE_KEY_PASSWORD = "xmldsig.keystore.key.password";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SignatureFilter.class);

  private KeyStore.PrivateKeyEntry m_aKeyEntry;
  private X509Certificate m_aCert;

  public SignatureFilter () {
    // Load the KeyStore and get the signing key and certificate.
    try {
      final ConfigFile aConfigFile = ConfigFile.getInstance ();
      final String sKeyStoreClassPath = aConfigFile.getString (CONFIG_XMLDSIG_KEYSTORE_CLASSPATH);
      final String sKeyStorePassword = aConfigFile.getString (CONFIG_XMLDSIG_KEYSTORE_PASSWORD);
      final String sKeyStoreKeyAlias = aConfigFile.getString (CONFIG_XMLDSIG_KEYSTORE_KEY_ALIAS);
      final char [] aKeyStoreKeyPassword = aConfigFile.getCharArray (CONFIG_XMLDSIG_KEYSTORE_KEY_PASSWORD);

      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (sKeyStoreClassPath, sKeyStorePassword);
      final KeyStore.Entry aEntry = aKeyStore.getEntry (sKeyStoreKeyAlias,
                                                        new KeyStore.PasswordProtection (aKeyStoreKeyPassword));
      if (aEntry == null) {
        // Alias not found
        throw new IllegalStateException ("Failed to find key store alias '" +
                                         sKeyStoreKeyAlias +
                                         "' in keystore '" +
                                         sKeyStorePassword +
                                         "'. Does the alias exist? Is the password correct?");
      }
      if (!(aEntry instanceof KeyStore.PrivateKeyEntry)) {
        // Not a private key
        throw new IllegalStateException ("The keystore alias '" +
                                         sKeyStoreKeyAlias +
                                         "' was found in keystore '" +
                                         sKeyStorePassword +
                                         "' but it is not a private key! The internal type is " +
                                         aEntry.getClass ().getName ());
      }
      m_aKeyEntry = (KeyStore.PrivateKeyEntry) aEntry;
      m_aCert = (X509Certificate) m_aKeyEntry.getCertificate ();
    }
    catch (final Throwable t) {
      s_aLogger.error ("Error in constructor of SignatureFilter", t);
      throw new InitializationException ("Error in constructor of SignatureFilter", t);
    }
  }

  @Nonnull
  public ContainerResponse filter (@Nonnull final ContainerRequest aRequest, @Nonnull final ContainerResponse aResponse) {
    // Make sure that the signature is only added to GET/OK on service metadata.
    if (aRequest.getMethod ().equals ("GET") && aResponse.getResponse ().getStatus () == Status.OK.getStatusCode ()) {
      final String sRequestPath = aRequest.getPath (false);
      if (sRequestPath.contains ("/services/") && !sRequestPath.endsWith ("/services/")) {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Will sign response to " + sRequestPath);
        aResponse.setContainerResponseWriter (new SigningContainerResponseWriter (aResponse.getContainerResponseWriter (),
                                                                                  m_aKeyEntry,
                                                                                  m_aCert));
      }
    }

    return aResponse;
  }
}

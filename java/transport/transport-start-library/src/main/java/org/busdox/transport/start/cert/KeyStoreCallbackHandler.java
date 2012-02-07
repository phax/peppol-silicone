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
package org.busdox.transport.start.cert;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.wss.impl.callback.KeyStoreCallback;
import com.sun.xml.wss.impl.callback.PrivateKeyCallback;

import eu.peppol.busdox.security.KeyStoreUtils;
import eu.peppol.common.ConfigFile;

/**
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
public final class KeyStoreCallbackHandler implements CallbackHandler {
  private static final Logger log = LoggerFactory.getLogger (KeyStoreCallbackHandler.class);

  public KeyStoreCallbackHandler () {}

  @Override
  public void handle (final Callback [] aCallbacks) throws IOException, UnsupportedCallbackException {
    final ConfigFile aConfigFile = ConfigFile.getInstance ();
    final String sKeyStorePath = aConfigFile.getString ("org.busdox.transport.start.server.keystore.path");
    final String sKeyStorePassword = aConfigFile.getString ("org.busdox.transport.start.server.keystore.password");
    final String sKeyStoreAlias = aConfigFile.getString ("org.busdox.transport.start.server.keystore.alias");
    final char [] aKeyStoreAliasPassword = aConfigFile.getCharArray ("org.busdox.transport.start.server.keystore.aliaspassword");

    log.info ("Loading keystore from" + sKeyStorePath);
    KeyStore aKeyStore;
    try {
      aKeyStore = KeyStoreUtils.loadKeyStoreFromClassPath (sKeyStorePath, sKeyStorePassword);
    }
    catch (final Exception ex) {
      throw new IllegalStateException ("Error in KeyStore loading!", ex);
    }

    for (final Callback aCallback : aCallbacks) {
      if (aCallback instanceof KeyStoreCallback) {
        try {
          // The returned keystore may not contain other entries than the
          // requested one!
          ((KeyStoreCallback) aCallback).setKeystore (KeyStoreUtils.createKeyStoreWithOnlyOneItem (aKeyStore,
                                                                                                   sKeyStoreAlias,
                                                                                                   aKeyStoreAliasPassword));
        }
        catch (final Exception ex) {
          log.error ("Failed to set key store", ex);
        }
      }
      else
        if (aCallback instanceof PrivateKeyCallback) {
          try {
            final PrivateKey aPrivateKey = (PrivateKey) aKeyStore.getKey (sKeyStoreAlias, aKeyStoreAliasPassword);
            ((PrivateKeyCallback) aCallback).setKey (aPrivateKey);
          }
          catch (final Exception ex) {
            log.warn ("Failed to set private key", ex);
          }
        }
    }
  }
}

package eu.peppol.outbound.callbacks;

import java.security.KeyStore;
import java.security.PrivateKey;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import com.sun.xml.wss.impl.callback.KeyStoreCallback;
import com.sun.xml.wss.impl.callback.PrivateKeyCallback;

import eu.peppol.outbound.util.Log;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * User: nigel
 * Date: Oct 18, 2011
 * Time: 4:59:37 PM
 */
public final class KeyStoreCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) {
        KeystoreManager keystoreManager = new KeystoreManager();

        for (Callback callback : callbacks) {

            if (callback instanceof KeyStoreCallback) {

                Log.debug("Keystore callback handler: returning keystore");
                KeyStore keystore = keystoreManager.getKeystore();
                ((KeyStoreCallback) callback).setKeystore(keystore);

            } else if (callback instanceof PrivateKeyCallback) {

                Log.debug("Keystore callback handler: returning private key");
                PrivateKey privateKey = keystoreManager.getOurPrivateKey();
                ((PrivateKeyCallback) callback).setKey(privateKey);
            }
        }
    }
}

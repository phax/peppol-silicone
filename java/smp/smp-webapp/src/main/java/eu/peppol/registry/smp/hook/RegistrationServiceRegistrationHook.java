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
package eu.peppol.registry.smp.hook;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.busdox.identifier.IParticipantIdentifier;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.state.ESuccess;

import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.security.DoNothingTrustManager;
import eu.peppol.busdox.security.KeyStoreUtils;
import eu.peppol.common.ConfigFile;
import eu.peppol.registry.sml.management.client.ManageParticipantIdentifierServiceCaller;

/**
 * An implementation of the RegistrationHook that informs the SML of updates to
 * this SMP's identifiers.<br>
 * The design of this hook is very bogus! It relies on the postUpdate always
 * being called in order in this Thread.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class RegistrationServiceRegistrationHook extends AbstractRegistrationHook {
  private static final Logger s_aLogger = LoggerFactory.getLogger (RegistrationServiceRegistrationHook.class);
  private static final URL s_aSMLEndpointURL;
  private static final String s_sSMPID;

  static {
    final ConfigFile aConfigFile = ConfigFile.getInstance ();

    // SML endpoint (incl. the service name)
    final String sURL = aConfigFile.getString ("regServiceRegistrationHook.regLocatorUrl");
    try {
      s_aSMLEndpointURL = new URL (sURL);
    }
    catch (final MalformedURLException ex) {
      throw new IllegalStateException ("Failed to init hook", ex);
    }

    // SMP ID
    s_sSMPID = aConfigFile.getString ("regServiceRegistrationHook.id");

    // Keystore for SML access:
    try {
      final String sKeystorePath = aConfigFile.getString ("regServiceRegistrationHook.keystore.classpath");
      final String sKeystorePassword = aConfigFile.getString ("regServiceRegistrationHook.keystore.password");

      // Main key storage
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStoreFromClassPath (sKeystorePath, sKeystorePassword);

      // Key manager
      final KeyManagerFactory aKeyManagerFactory = KeyManagerFactory.getInstance ("SunX509");
      aKeyManagerFactory.init (aKeyStore, sKeystorePassword.toCharArray ());

      // Trust manager

      // Assign key manager and empty trust manager to SSL context
      final SSLContext aSSLCtx = SSLContext.getInstance ("TLS");
      aSSLCtx.init (aKeyManagerFactory.getKeyManagers (),
                    new TrustManager [] { new DoNothingTrustManager () },
                    VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLCtx.getSocketFactory ());
    }
    catch (final Exception ex) {
      throw new IllegalStateException ("Failed to init keyStore for SML access");
    }
  }

  private static enum MethodType {
    CREATE,
    DELETE
  }

  private SimpleParticipantIdentifier m_aBusinessIdentifier;
  private MethodType m_eMethodType;

  public RegistrationServiceRegistrationHook () {
    resetQueue ();
  }

  public void create (final IParticipantIdentifier aPI) throws HookException {
    s_aLogger.debug ("Trying to create business in Business Identifier Manager Service");
    m_aBusinessIdentifier = new SimpleParticipantIdentifier (aPI);
    m_eMethodType = MethodType.CREATE;

    try {
      final ManageParticipantIdentifierServiceCaller caller = new ManageParticipantIdentifierServiceCaller (s_aSMLEndpointURL);
      caller.create (s_sSMPID, m_aBusinessIdentifier);
      s_aLogger.debug ("Succeded in creating business using Business Identifier Manager Service");
      getQueueInstance ().set (this);
    }
    catch (final Throwable t) {
      s_aLogger.warn ("Could not create business in Business Identifier Manager Service", t);
      throw new HookException ("Unable to register business", t);
    }
  }

  public void delete (final IParticipantIdentifier aPI) throws HookException {
    s_aLogger.debug ("Trying to delete business in Business Identifier Manager Service");
    m_aBusinessIdentifier = new SimpleParticipantIdentifier (aPI);
    m_eMethodType = MethodType.DELETE;

    try {
      final ManageParticipantIdentifierServiceCaller caller = new ManageParticipantIdentifierServiceCaller (s_aSMLEndpointURL);
      caller.delete (m_aBusinessIdentifier);
      s_aLogger.debug ("Succeded in deleting business using Business Identifier Manager Service");
      getQueueInstance ().set (this);
    }
    catch (final NotFoundFault e) {
      s_aLogger.debug ("The business was not present. Just ignore.");
      // getQueueInstance().set(this); // Don't create it if DB fails, since it
      // was not the in the first place.
    }
    catch (final Throwable t) {
      s_aLogger.warn ("Could not delete business in Business Identifier Manager Service", t);
      throw new HookException ("Unable to delete business", t);
    }
  }

  public void postUpdate (final ESuccess eSuccess) throws HookException {
    if (eSuccess.isFailure ())
      try {
        final ManageParticipantIdentifierServiceCaller caller = new ManageParticipantIdentifierServiceCaller (s_aSMLEndpointURL);

        switch (m_eMethodType) {
          case CREATE:
            s_aLogger.warn ("CREATE failed in database, so deleting " + m_aBusinessIdentifier.getURIEncoded () + " from SML.");
            caller.delete (m_aBusinessIdentifier);
            break;
          case DELETE:
            s_aLogger.warn ("DELETE failed in database, so creating " + m_aBusinessIdentifier.getURIEncoded () + " in SML.");
            caller.create (s_sSMPID, m_aBusinessIdentifier);
            break;
        }
      }
      catch (final Throwable t) {
        throw new HookException ("Unable to rollback update business.", t);
      }
  }
}

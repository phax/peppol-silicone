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
package eu.peppol.start.identifier;

import java.io.File;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.security.KeyStoreUtils;

import com.phloc.commons.collections.ContainerHelper;

import eu.peppol.security.OcspValidatorCache;

/**
 * Main manager for handling operations related to our keystore and truststore.
 * <p/>
 * User: nigel Date: Oct 9, 2011 Time: 4:01:31 PM
 * 
 * @author steinar@sendregning.no
 */
public final class KeystoreManager {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-com");

  private static String s_sKeystoreLocation;
  private static String s_sKeystorePassword;
  private static KeyStore s_aKeyStore;
  private static KeyStore s_aTrustStore;
  private static PrivateKey s_aPrivateKey;

  private CertPathValidator m_aCertPathValidator;
  private final OcspValidatorCache m_aOcspValidatorCache = new OcspValidatorCache ();
  private PKIXParameters m_aPkixParameters;

  public KeystoreManager () {
    initCertPathValidator ();
  }

  @Nonnull
  public synchronized KeyStore getKeystore () {
    if (s_aKeyStore == null)
      s_aKeyStore = getKeystore (s_sKeystoreLocation, s_sKeystorePassword);
    return s_aKeyStore;
  }

  @Nonnull
  private KeyStore getKeystore (final String location, final String password) {
    try {
      return KeyStoreUtils.loadKeyStore (location, password);
    }
    catch (final Exception e) {
      throw new RuntimeException ("Failed to open keystore " + location, e);
    }
  }

  @Nullable
  public X509Certificate getOurCertificate () {
    try {
      final KeyStore keystore = getKeystore ();
      final String alias = keystore.aliases ().nextElement ();
      return (X509Certificate) keystore.getCertificate (alias);
    }
    catch (final KeyStoreException e) {
      throw new RuntimeException ("Failed to get our certificate from keystore", e);
    }
  }

  @Nonnull
  public synchronized PrivateKey getOurPrivateKey () {
    if (s_aPrivateKey == null) {
      try {
        final KeyStore keystore = getKeystore ();
        final String alias = keystore.aliases ().nextElement ();
        final Key key = keystore.getKey (alias, s_sKeystorePassword.toCharArray ());

        if (key instanceof PrivateKey) {
          s_aPrivateKey = (PrivateKey) key;
        }
        else {
          throw new RuntimeException ("Private key is not first element in keystore at " + s_sKeystoreLocation);
        }

      }
      catch (final Exception e) {
        throw new RuntimeException ("Failed to get our private key", e);
      }
    }

    return s_aPrivateKey;
  }

  public TrustAnchor getTrustAnchor () {
    try {
      final KeyStore truststore = getTruststore ();
      final String alias = "peppol access point test ca (peppol root test ca)";
      return new TrustAnchor ((X509Certificate) truststore.getCertificate (alias), null);
    }
    catch (final Exception e) {
      throw new RuntimeException ("Failed to get the PEPPOL access point certificate", e);
    }
  }

  public synchronized KeyStore getTruststore () {
    if (s_aTrustStore == null)
      s_aTrustStore = getKeystore ("/truststore/global-truststore.jks", "peppol");
    return s_aTrustStore;
  }

  public void initialiseKeystore (final File keystoreFile, final String keystorePassword) {
    if (keystoreFile == null)
      throw new IllegalStateException ("Keystore file not specified");
    if (keystorePassword == null)
      throw new IllegalStateException ("Keystore password not specified");
    if (!keystoreFile.exists ())
      throw new IllegalStateException ("Keystore file " + keystoreFile + " does not exist");

    try {
      setKeystoreLocation (keystoreFile.getCanonicalPath ());
      setKeystorePassword (keystorePassword);
      getKeystore ();
    }
    catch (final Exception e) {
      throw new IllegalArgumentException ("Problem accessing keystore file", e);
    }
  }

  void initCertPathValidator () {
    log.debug ("Initialising OCSP validator");

    try {
      final TrustAnchor trustAnchor = getTrustAnchor ();
      m_aCertPathValidator = CertPathValidator.getInstance ("PKIX");
      m_aPkixParameters = new PKIXParameters (Collections.singleton (trustAnchor));
      m_aPkixParameters.setRevocationEnabled (true);

      Security.setProperty ("ocsp.enable", "true");
      Security.setProperty ("ocsp.responderURL", "http://pilot-ocsp.verisign.com:80");
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to construct Certificate Path Validator; " + e, e);
    }
  }

  /**
   * Validates a X509 certificate using the OCSP services.
   * 
   * @param certificate
   *        the certificate to be checked for validity
   * @return <code>true</code> if certificate is valid, <code>false</code>
   *         otherwise
   */
  public synchronized boolean validate (@Nonnull final X509Certificate certificate) {
    final BigInteger serialNumber = certificate.getSerialNumber ();
    final String certificateName = "Certificate " + serialNumber;
    log.debug ("Ocsp validation requested for " +
               certificateName +
               "\n\tSubject:" +
               certificate.getSubjectDN () +
               "\n\tIssued by:" +
               certificate.getIssuerDN ());

    if (m_aCertPathValidator == null)
      throw new IllegalStateException ("Certificate Path validator not initialized");

    if (m_aOcspValidatorCache.isKnownValidCertificate (serialNumber)) {
      log.debug (certificateName + " is OCSP valid (cached value)");
      return true;
    }

    try {
      final List <? extends Certificate> certificates = ContainerHelper.newList (certificate);
      final CertPath certPath = CertificateFactory.getInstance ("X.509").generateCertPath (certificates);
      m_aCertPathValidator.validate (certPath, m_aPkixParameters);
      m_aOcspValidatorCache.setKnownValidCertificate (serialNumber);

      log.debug (certificateName + " is OCSP valid");
      return true;
    }
    catch (final Exception e) {
      log.error (certificateName + " failed OCSP validation", e);
      return false;
    }
  }

  public boolean isOurCertificate (final X509Certificate candidate) {
    final X509Certificate ourCertificate = getOurCertificate ();
    return ourCertificate.getSerialNumber ().equals (candidate.getSerialNumber ());
  }

  public static void setKeystoreLocation (final String keystoreLocation) {
    s_sKeystoreLocation = keystoreLocation;
  }

  public static void setKeystorePassword (final String keystorePassword) {
    s_sKeystorePassword = keystorePassword;
  }
}

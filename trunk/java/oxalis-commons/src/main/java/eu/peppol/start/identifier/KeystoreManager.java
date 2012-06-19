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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.peppol.commons.security.KeyStoreUtils;
import eu.peppol.security.OcspValidatorCache;

/**
 * Main manager for handling operations related to our keystore and truststore.
 * <p/>
 * User: nigel Date: Oct 9, 2011 Time: 4:01:31 PM
 * 
 * @author steinar@sendregning.no
 */
public class KeystoreManager {

  private static String keystoreLocation;
  private static String keystorePassword;
  private static KeyStore keyStore;
  private static KeyStore trustStore;
  private static PrivateKey privateKey;
  private CertPathValidator certPathValidator;
  private final OcspValidatorCache ocspValidatorCache = new OcspValidatorCache ();
  private PKIXParameters pkixParameters;

  public KeystoreManager () {
    initCertPathValidator ();
  }

  public synchronized KeyStore getKeystore () {
    if (keyStore == null)
      keyStore = getKeystore (keystoreLocation, keystorePassword);
    return keyStore;
  }

  private KeyStore getKeystore (final String location, final String password) {
    try {
      return KeyStoreUtils.loadKeyStore (location, password);
    }
    catch (final Exception e) {
      throw new RuntimeException ("Failed to open keystore " + location, e);
    }
  }

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

  public synchronized PrivateKey getOurPrivateKey () {
    if (privateKey == null) {
      try {

        final KeyStore keystore = getKeystore ();
        final String alias = keystore.aliases ().nextElement ();
        final Key key = keystore.getKey (alias, keystorePassword.toCharArray ());

        if (key instanceof PrivateKey) {
          privateKey = (PrivateKey) key;
        }
        else {
          throw new RuntimeException ("Private key is not first element in keystore at " + keystoreLocation);
        }

      }
      catch (final Exception e) {
        throw new RuntimeException ("Failed to get our private key", e);
      }
    }

    return privateKey;
  }

  public TrustAnchor getTrustAnchor () {

    try {

      final KeyStore truststore = getTruststore ();
      final String alias = "ap";
      return new TrustAnchor ((X509Certificate) truststore.getCertificate (alias), null);

    }
    catch (final Exception e) {
      throw new RuntimeException ("Failed to get the PEPPOL access point certificate", e);
    }
  }

  public synchronized KeyStore getTruststore () {
    if (trustStore == null)
      trustStore = getKeystore ("/truststore/global-truststore.jks", "peppol");
    return trustStore;
  }

  public void initialiseKeystore (final File keystoreFile, final String keystorePassword) {
    if (keystoreFile == null) {
      throw new IllegalStateException ("Keystore file not specified");
    }

    if (keystorePassword == null) {
      throw new IllegalStateException ("Keystore password not specified");
    }

    if (!keystoreFile.exists ()) {
      throw new IllegalStateException ("Keystore file " + keystoreFile + " does not exist");
    }

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
    Log.debug ("Initialising OCSP validator");

    try {
      final TrustAnchor trustAnchor = getTrustAnchor ();
      certPathValidator = CertPathValidator.getInstance ("PKIX");
      pkixParameters = new PKIXParameters (Collections.singleton (trustAnchor));
      pkixParameters.setRevocationEnabled (true);

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
  public synchronized boolean validate (final X509Certificate certificate) {

    final BigInteger serialNumber = certificate.getSerialNumber ();
    final String certificateName = "Certificate " + serialNumber;
    Log.debug ("Ocsp validation requested for " +
               certificateName +
               "\n\tSubject:" +
               certificate.getSubjectDN () +
               "\n\tIssued by:" +
               certificate.getIssuerDN ());

    if (certPathValidator == null) {
      throw new IllegalStateException ("Certificate Path validator not initialized");
    }

    if (ocspValidatorCache.isKnownValidCertificate (serialNumber)) {
      Log.debug (certificateName + " is OCSP valid (cached value)");
      return true;
    }

    try {

      final List <Certificate> certificates = Arrays.asList (new Certificate [] { certificate });
      final CertPath certPath = CertificateFactory.getInstance ("X.509").generateCertPath (certificates);
      certPathValidator.validate (certPath, pkixParameters);
      ocspValidatorCache.setKnownValidCertificate (serialNumber);

      Log.debug (certificateName + " is OCSP valid");
      return true;

    }
    catch (final Exception e) {
      Log.error (certificateName + " failed OCSP validation", e);
      return false;
    }
  }

  public boolean isOurCertificate (final X509Certificate candidate) {
    final X509Certificate ourCertificate = getOurCertificate ();
    return ourCertificate.getSerialNumber ().equals (candidate.getSerialNumber ());
  }

  public static void setKeystoreLocation (final String keystoreLocation) {
    KeystoreManager.keystoreLocation = keystoreLocation;
  }

  public static void setKeystorePassword (final String keystorePassword) {
    KeystoreManager.keystorePassword = keystorePassword;
  }
}

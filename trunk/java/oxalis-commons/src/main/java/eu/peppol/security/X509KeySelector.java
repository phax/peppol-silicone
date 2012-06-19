/* Created by steinar on 14.05.12 at 00:38 */
package eu.peppol.security;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

/**
 * Finds and returns a key using the data contained in a {@link KeyInfo} object
 * 
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @see <a
 *      href="http://java.sun.com/developer/technicalArticles/xml/dig_signature_api/">Programming
 *      with the Java XML Digital Signature API</a>
 */
public class X509KeySelector extends KeySelector {

  /**
   * Invoked whenever the caller needs to retrieve the key. Note to self; how
   * can they make it so damned complicated?
   */
  @Override
  public KeySelectorResult select (final KeyInfo keyInfo,
                                   final KeySelector.Purpose purpose,
                                   final AlgorithmMethod method,
                                   final XMLCryptoContext context) throws KeySelectorException {

    final Iterator <?> ki = keyInfo.getContent ().iterator ();
    while (ki.hasNext ()) {
      final XMLStructure info = (XMLStructure) ki.next ();
      if (!(info instanceof X509Data))
        continue;
      final X509Data x509Data = (X509Data) info;
      final Iterator <?> xi = x509Data.getContent ().iterator ();
      while (xi.hasNext ()) {
        final Object o = xi.next ();
        if (!(o instanceof X509Certificate))
          continue;

        final PublicKey key = ((X509Certificate) o).getPublicKey ();
        // Make sure the algorithm is compatible
        // with the method.
        if (algEquals (method.getAlgorithm (), key.getAlgorithm ())) {
          final X509Certificate x509Certificate = (X509Certificate) o;
          try {
            // Ensures the certificate is valid for current date
            x509Certificate.checkValidity ();
          }
          catch (final CertificateExpiredException e) {
            throw new KeySelectorException ("Certificate of SMP has expired ", e);
          }
          catch (final CertificateNotYetValidException e) {
            throw new KeySelectorException ("Certificate of SMP not yet valid", e);
          }
          return new KeySelectorResult () {
            public Key getKey () {

              return key;
            }
          };
        }
      }
    }
    throw new KeySelectorException ("No key found!");
  }

  static boolean algEquals (final String algURI, final String algName) {
    return (algName.equalsIgnoreCase ("DSA") && algURI.equalsIgnoreCase (SignatureMethod.DSA_SHA1)) ||
           (algName.equalsIgnoreCase ("RSA") && algURI.equalsIgnoreCase (SignatureMethod.RSA_SHA1));
  }
}

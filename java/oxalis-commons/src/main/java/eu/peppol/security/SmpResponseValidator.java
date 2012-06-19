/* Created by steinar on 14.05.12 at 00:21 */
package eu.peppol.security;

import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Validation methods for the supplied SMP response document. To verify the
 * certificate supplied with the signature, use the
 * {@link eu.peppol.start.identifier.KeystoreManager#validate(java.security.cert.X509Certificate)}
 * 
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @see <a
 *      href="http://java.sun.com/developer/technicalArticles/xml/dig_signature_api/">Programming
 *      with the Java XML Digital Signature API</a>
 */
public class SmpResponseValidator {

  private XMLSignature signature;
  private final DOMValidateContext domValidateContext;

  /**
   * Uses the XML Document to create an XMLSignature object, which we may
   * inspect later. Holding the XMLSignature as an instance variable, enables us
   * to inspect and retrieve other parts of the signature, like for instance the
   * certificate supplied together with the signature.
   * 
   * @param smpResponse
   *        the W3C DOM object representing the response from a PEPPOL SMP.
   */
  public SmpResponseValidator (final Document smpResponse) {
    final NodeList nl = smpResponse.getElementsByTagNameNS (XMLSignature.XMLNS, "Signature");
    if (nl.getLength () < 1) {
      throw new SmpSecurityException ("Element <Signature> not found in SMP XML response");
    }

    final Node signatureNode = nl.item (0); // Retrieves the <Signature> W3C
                                            // Node

    // The X509KeySelector is our own helper class, assisting us when retrieving
    // the public key used for
    // subsequent signature validation operations.
    domValidateContext = new DOMValidateContext (new X509KeySelector (), signatureNode);

    // Unmarshals the XMLSignature, i.e. gets rid of white space etc.
    final XMLSignatureFactory fac = XMLSignatureFactory.getInstance ("DOM");
    try {
      signature = fac.unmarshalXMLSignature (domValidateContext);
    }
    catch (final MarshalException e) {
      throw new IllegalStateException ("Unable to unmarshal the XML signature", e);
    }
  }

  /**
   * Determines if the signature is valid, i.e. nobody has tampered with the
   * contents. The certificate chain is not inspected to determine whether the
   * certificate holding the public key, is valid and legal. This is a separate
   * operation, which requires access to a truststore.
   * 
   * @return true if the signature is valid, false otherwise.
   */
  public boolean isSmpSignatureValid () {
    try {
      return signature.validate (domValidateContext);
    }
    catch (final XMLSignatureException e) {
      throw new IllegalStateException ("Unable to validate signature: " + e, e);
    }
  }

  /**
   * Retrieves the certificate of the SMP, which signed the response.
   * 
   * @return the certificate used to sign the request or null if not found.
   */
  public X509Certificate getCertificate () {

    // This is horrible, read the JavaDoc to understand :-)
    final Iterator <?> ki = signature.getKeyInfo ().getContent ().iterator ();
    while (ki.hasNext ()) {
      final XMLStructure info = (XMLStructure) ki.next ();
      if (!(info instanceof X509Data))
        continue;
      final X509Data x509Data = (X509Data) info;
      final Iterator <?> xi = x509Data.getContent ().iterator ();
      while (xi.hasNext ()) {
        final Object o = xi.next ();
        if (o instanceof X509Certificate)
          return (X509Certificate) o;
      }
    }
    return null; // Did not find an X509Certificate together with the signature
  }
}

/* Created by steinar on 14.05.12 at 00:21 */
package eu.peppol.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.peppol.start.identifier.KeystoreManager;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpResponseValidatorTest {

  private static Document document;

  @BeforeClass
  public static void loadSampleSmpResponse () throws IOException, SAXException, ParserConfigurationException {
    final InputStream is = SmpResponseValidator.class.getClassLoader ().getResourceAsStream ("sr-smp-result.xml");
    assertNotNull (is);

    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    documentBuilderFactory.setNamespaceAware (true);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();

    document = documentBuilder.parse (is);
  }

  @Test
  public void testVerificationOfSmpResponseSignature () throws ParserConfigurationException, IOException, SAXException {

    final SmpResponseValidator smpResponseValidator = new SmpResponseValidator (document);
    final boolean isValid = smpResponseValidator.isSmpSignatureValid ();

    assertTrue ("Sample SMP response contained invalid signature", isValid);
  }

  @Test
  public void testRetrievalOfCertificateFromSmpResponse () {
    final SmpResponseValidator smpResponseValidator = new SmpResponseValidator (document);
    final X509Certificate x509Certificate = smpResponseValidator.getCertificate ();
    assertNotNull (x509Certificate);
  }

  @Test
  public void testValidityOfSmpCertificate () {
    final SmpResponseValidator smpResponseValidator = new SmpResponseValidator (document);
    final X509Certificate smpX509Certificate = smpResponseValidator.getCertificate ();

    final KeystoreManager keystoreManager = new KeystoreManager ();
    final boolean isValid = keystoreManager.validate (smpX509Certificate);

  }

}

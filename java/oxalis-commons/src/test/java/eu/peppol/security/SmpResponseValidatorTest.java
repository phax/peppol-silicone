/* Created by steinar on 14.05.12 at 00:21 */
package eu.peppol.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.cert.X509Certificate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.start.identifier.KeystoreManager;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpResponseValidatorTest {

  private static Document document;

  @BeforeClass
  public static void loadSampleSmpResponse () throws SAXException {
    document = XMLReader.readXMLDOM (new ClassPathResource ("sr-smp-result.xml"));
  }

  @Test
  public void testVerificationOfSmpResponseSignature () {

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
    assertNotNull (smpX509Certificate);

    final KeystoreManager keystoreManager = new KeystoreManager ();
    keystoreManager.validate (smpX509Certificate);
  }
}

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
/* Created by steinar on 14.05.12 at 00:21 */
package eu.peppol.outbound.smp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.cert.X509Certificate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.outbound.smp.SmpResponseValidator;
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

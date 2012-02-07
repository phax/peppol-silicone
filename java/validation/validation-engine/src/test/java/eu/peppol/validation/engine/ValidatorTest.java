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
package eu.peppol.validation.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLWriter;

import eu.peppol.validation.engine.jaxb.result.ValidationResultType;
import eu.peppol.validation.engine.result.ValidationResultConverter;

/**
 * Class to test the Engine Validator.
 * 
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ValidatorTest {
  @Test
  public void testCatalogue () throws JAXBException {
    // Catalogue Test
    final String profileBII = "BII01";
    final String coreDataModel = "BiiCoreTrdm019";
    final IReadableResource xml = new ClassPathResource ("Catalogue.305190c2-3dc3-490b-a138-8b999c9e87f9.xml");
    final ValidationResultType aResult = Validator.executeValidationLayers (profileBII, coreDataModel, xml);
    assertNotNull (aResult);
    assertEquals (profileBII, aResult.getProfile ());
    assertEquals (coreDataModel, aResult.getDocumentCore ());
    assertEquals (1, aResult.getResultLayers ().size ());
    final Document aDoc = ValidationResultConverter.convertToXML (aResult);
    assertNotNull (aDoc);
    System.out.println (XMLWriter.getXMLString (aDoc));
  }

  @Test
  public void testInvoice () throws JAXBException {
    // Invoice Test
    final String profileBII = "BII06";
    final String coreDataModel = "BiiCoreTrdm010";
    final IReadableResource xml = new ClassPathResource ("SubmitInvoice.14d51dd3-3954-45b9-8750-f14b051bd46e.xml");
    final ValidationResultType aResult = Validator.executeValidationLayers (profileBII, coreDataModel, xml);
    assertNotNull (aResult);
    assertEquals (profileBII, aResult.getProfile ());
    assertEquals (coreDataModel, aResult.getDocumentCore ());
    assertEquals (1, aResult.getResultLayers ().size ());
    final Document aDoc = ValidationResultConverter.convertToXML (aResult);
    assertNotNull (aDoc);
    System.out.println (XMLWriter.getXMLString (aDoc));
  }

  @Test
  public void testPreAward () throws JAXBException {
    // Invoice Test
    final String profileBII = "PreAward";
    final String coreDataModel = "PA001";
    final IReadableResource xml = new ClassPathResource ("SubmitInvoice.14d51dd3-3954-45b9-8750-f14b051bd46e.xml");
    final ValidationResultType aResult = Validator.executeValidationLayers (profileBII, coreDataModel, xml);
    assertNotNull (aResult);
    assertEquals (profileBII, aResult.getProfile ());
    assertEquals (coreDataModel, aResult.getDocumentCore ());
    assertEquals (1, aResult.getResultLayers ().size ());
    final Document aDoc = ValidationResultConverter.convertToXML (aResult);
    assertNotNull (aDoc);
    if (false)
      System.out.println (XMLWriter.getXMLString (aDoc));
  }
}

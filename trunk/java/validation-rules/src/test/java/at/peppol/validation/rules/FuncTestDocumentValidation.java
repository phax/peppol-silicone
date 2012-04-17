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
package at.peppol.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_2.OrderType;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.xml.sax.SAXException;

import at.peppol.commons.cenbii.profiles.ETransaction;
import at.peppol.validation.CValidattionTestFiles;
import at.peppol.validation.schematron.SchematronHelper;
import at.peppol.validation.schematron.svrl.SVRLFailedAssert;
import at.peppol.validation.schematron.svrl.SVRLUtils;
import at.peppol.validation.schematron.svrl.SVRLWriter;
import at.peppol.validation.schematron.xslt.SchematronResourceXSLT;

import com.phloc.commons.CGlobal;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.locale.country.CountryCache;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.ubl.UBL20DocumentMarshaller;

/**
 * Validate documents using the supplied functionality of
 * {@link EValidationArtefact}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class FuncTestDocumentValidation {
  @Test
  public void testReadCataloguesSuccess () throws SAXException {
    // For all available catalogues
    for (final String sCatalogueFile : CValidattionTestFiles.TEST_CATALOGUES_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aCatalogueRes = new ClassPathResource (CValidattionTestFiles.PATH_CATALOGUE_TESTFILES +
                                                                     CValidattionTestFiles.PATH_SUCCESS +
                                                                     sCatalogueFile);

      // Ensure the UBL file validates against the scheme
      final CatalogueType aUBLCatalogue = UBL20DocumentMarshaller.readCatalogue (XMLReader.readXMLDOM (aCatalogueRes));
      assertNotNull (aUBLCatalogue);

      // Test the country-independent catalogue layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.CATALOGUE,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T19
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T19));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aCatalogueRes);
        assertNotNull (aSVRL);

        if (false) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aCatalogueRes.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadOrdersSuccess () throws SAXException {
    // For all available orders
    for (final String sOrderFile : CValidattionTestFiles.TEST_ORDERS_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aOrderRes = new ClassPathResource (CValidattionTestFiles.PATH_ORDER_TESTFILES +
                                                                 CValidattionTestFiles.PATH_SUCCESS +
                                                                 sOrderFile);

      // Ensure the UBL file validates against the scheme
      final OrderType aUBLOrder = UBL20DocumentMarshaller.readOrder (XMLReader.readXMLDOM (aOrderRes));
      assertNotNull (aUBLOrder);

      // Test the country-independent orders layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.ORDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T01
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T01));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aOrderRes);
        assertNotNull (aSVRL);

        if (false) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aOrderRes.toString () + "\n" + aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadInvoicesSuccess () throws SAXException {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available invoices
    for (final String sInvoiceFile : CValidattionTestFiles.TEST_INVOICES_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aInvoiceRes = new ClassPathResource (CValidattionTestFiles.PATH_INVOICE_TESTFILES +
                                                                   CValidattionTestFiles.PATH_SUCCESS +
                                                                   sInvoiceFile);

      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20DocumentMarshaller.readInvoice (XMLReader.readXMLDOM (aInvoiceRes));
      assertNotNull (aUBLInvoice);

      // Test the country-independent invoice layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.INVOICE,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T10
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (aVT);

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aInvoiceRes);
        assertNotNull (aSVRL);

        if (false) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aInvoiceRes.toString () + "\n" + aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadInvoicesATSuccess () throws SAXException {
    // For all available invoices
    for (final String sInvoiceFile : CValidattionTestFiles.TEST_INVOICES_AT_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aInvoiceRes = new ClassPathResource (CValidattionTestFiles.PATH_INVOICE_TESTFILES +
                                                                   CValidattionTestFiles.PATH_SUCCESS +
                                                                   sInvoiceFile);

      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20DocumentMarshaller.readInvoice (XMLReader.readXMLDOM (aInvoiceRes));
      assertNotNull (aUBLInvoice);

      // Test the country-independent invoice layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.INVOICE,
                                                                                              CountryCache.getCountry ("AT"))) {
        // Get the XSLT for transaction T10
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T10));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aInvoiceRes);
        assertNotNull (aSVRL);

        if (false) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aInvoiceRes.toString () + " " + aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }
}

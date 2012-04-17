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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_2.OrderType;

import org.junit.Ignore;
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
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.ubl.UBL20DocumentMarshaller;

/**
 * Validate documents using the supplied functionality of
 * {@link EValidationArtefact}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class FuncTestDocumentValidationError {
  public static final List <String> TEST_ORDERS_ERROR = ContainerHelper.newUnmodifiableList ("TC01.1.TS1.xml",
                                                                                             "TC01.10.TS1.xml",
                                                                                             "TC01.11.TS1.xml",
                                                                                             "TC01.12.TS1.xml",
                                                                                             "TC01.13.TS1.xml",
                                                                                             "TC01.14.TS1.xml",
                                                                                             "TC01.15.TS1.XML",
                                                                                             "TC01.16.TS1.xml",
                                                                                             "TC01.17.TS1.xml",
                                                                                             "TC01.18.TS1.xml",
                                                                                             "TC01.19.TS1.xml",
                                                                                             "TC01.2.TS1.xml",
                                                                                             "TC01.20.TS1.xml",
                                                                                             "TC01.21.TS1.xml",
                                                                                             "TC01.22.TS1.xml",
                                                                                             "TC01.23.TS1.xml",
                                                                                             "TC01.24.TS1.xml",
                                                                                             "TC01.25.TS1.xml",
                                                                                             "TC01.26.TS1.xml",
                                                                                             "TC01.27.TS1.xml",
                                                                                             "TC01.28.TS1.xml",
                                                                                             "TC01.29.TS1.xml",
                                                                                             "TC01.3.TS1.xml",
                                                                                             "TC01.30.TS1.xml",
                                                                                             "TC01.31.TS1.xml",
                                                                                             "TC01.32.TS1.xml",
                                                                                             "TC01.33.TS1.xml",
                                                                                             "TC01.34.TS1.xml",
                                                                                             "TC01.35.TS1.xml",
                                                                                             "TC01.36.TS1.xml",
                                                                                             "TC01.37.TS1.xml",
                                                                                             "TC01.38.TS1.xml",
                                                                                             "TC01.39.TS1.xml",
                                                                                             "TC01.4.TS1.xml",
                                                                                             "TC01.40.TS1.xml",
                                                                                             "TC01.41.TS1.xml",
                                                                                             "TC01.42.TS1.xml",
                                                                                             "TC01.43.TS1.xml",
                                                                                             "TC01.44.TS1.xml",
                                                                                             "TC01.45.TS1.xml",
                                                                                             "TC01.46.TS1.xml",
                                                                                             "TC01.47.TS1.xml",
                                                                                             "TC01.48.TS1.xml",
                                                                                             "TC01.49.TS1.xml",
                                                                                             "TC01.5.TS1.xml",
                                                                                             "TC01.6.TS1.xml",
                                                                                             "TC01.7.TS1.xml",
                                                                                             "TC01.8.TS1.xml",
                                                                                             "TC01.9.TS1.xml");
  public static final List <String> TEST_INVOICES_ERROR = ContainerHelper.newUnmodifiableList ("ERR-10 BII04 minimal VAT invoice example 02.xml",
                                                                                               "ERR-11 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-13 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-18 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-19 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-2 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-20 BII04 reverse charge invoice example 01.xml",
                                                                                               "ERR-3 BII04 minimal VAT invoice example 02.xml",
                                                                                               "ERR-4 BII04 minimal invoice example 02.xml",
                                                                                               "ERR-5 BII04 minimal VAT invoice example 02.xml",
                                                                                               "ERR-9 BII04 minimal VAT invoice example 02.xml",
                                                                                               "TC10.1.TS1.xml",
                                                                                               "TC10.10.TS1.xml",
                                                                                               "TC10.11.TS1.xml",
                                                                                               "TC10.12.TS1.xml",
                                                                                               "TC10.13.TS1.xml",
                                                                                               "TC10.14.TS1.xml",
                                                                                               "TC10.15.TS1.xml",
                                                                                               "TC10.16.TS1.xml",
                                                                                               "TC10.17.TS1.xml",
                                                                                               "TC10.18.TS1.xml",
                                                                                               "TC10.2.TS1.xml",
                                                                                               "TC10.3.TS1.xml",
                                                                                               "TC10.4.TS1.xml",
                                                                                               "TC10.5.TS1.xml",
                                                                                               "TC10.6.TS1.xml",
                                                                                               "TC10.7.TS1.xml",
                                                                                               "TC10.8.TS1.xml",
                                                                                               "TC10.9.TS1.xml");

  @Test
  @Ignore
  public void testReadOrdersError () throws SAXException {
    // For all available orders
    for (final String sOrderFile : TEST_ORDERS_ERROR) {
      System.out.println (sOrderFile);
      // Get the UBL XML file
      final IReadableResource aOrderRes = new ClassPathResource (CValidattionTestFiles.PATH_ORDER_TESTFILES +
                                                                 CValidattionTestFiles.PATH_ERROR +
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
        final List <SVRLFailedAssert> aFailedAsserts = SVRLUtils.getAllFailedAssertionsMoreOrEqualSevereThan (aSVRL,
                                                                                                              EErrorLevel.ERROR);
        assertNotNull (aFailedAsserts);

        for (final SVRLFailedAssert aFailedAssert : aFailedAsserts) {
          assertTrue (aOrderRes.toString () + "\n" + aFailedAssert.toString (),
                      aFailedAssert.getFlag ().equals (EErrorLevel.FATAL_ERROR));
        }
      }
    }
  }

  @Test
  public void testReadInvoicesError () throws SAXException {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available invoices
    for (final String sInvoiceFile : TEST_INVOICES_ERROR) {
      // Get the UBL XML file
      final IReadableResource aInvoiceRes = new ClassPathResource (CValidattionTestFiles.PATH_INVOICE_TESTFILES +
                                                                   CValidattionTestFiles.PATH_ERROR +
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

        // Check that there is at least 1 failed assertions with type error
        final List <SVRLFailedAssert> aFailedAsserts = SVRLUtils.getAllFailedAssertionsMoreOrEqualSevereThan (aSVRL,
                                                                                                              EErrorLevel.ERROR);
        assertNotNull (aFailedAsserts);

        for (final SVRLFailedAssert aFailedAssert : aFailedAsserts) {
          assertTrue (aInvoiceRes.toString () + "\n" + aFailedAssert.toString (),
                      aFailedAssert.getFlag ().equals (EErrorLevel.FATAL_ERROR));
        }
      }
    }
  }
}

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_2.OrderType;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.xml.sax.SAXException;

import at.peppol.commons.cenbii.profiles.ETransaction;
import at.peppol.test.CTestFiles;
import at.peppol.validation.rules.mock.AbstractErrorDefinition;
import at.peppol.validation.rules.mock.FatalError;
import at.peppol.validation.rules.mock.TestDocument;
import at.peppol.validation.rules.mock.Warning;
import at.peppol.validation.schematron.SchematronHelper;
import at.peppol.validation.schematron.svrl.SVRLFailedAssert;
import at.peppol.validation.schematron.svrl.SVRLUtils;
import at.peppol.validation.schematron.svrl.SVRLWriter;
import at.peppol.validation.schematron.xslt.SchematronResourceXSLT;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.regex.RegExHelper;
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
  public static final List <TestDocument> TEST_ORDERS_ERROR;
  static {
    TEST_ORDERS_ERROR = ContainerHelper.newUnmodifiableList (new TestDocument ("TC01.1.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R001")),
                                                             new TestDocument ("TC01.2.TS1.xml",
                                                                               new Warning ("BIICORE-T01-R000"),
                                                                               new FatalError ("BIIRULE-T01-R002")),
                                                             new TestDocument ("TC01.3.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R003")),
                                                             /**
                                                              * TC01.4.TS1.xml,
                                                              * TC01.5.TS1.xml
                                                              * not XSD
                                                              * compliant
                                                              */
                                                             new TestDocument ("TC01.6.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R027")),
                                                             new TestDocument ("TC01.7.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R027")),
                                                             new TestDocument ("TC01.8.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R030")),
                                                             new TestDocument ("TC01.9.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R004")),
                                                             /**
                                                              * TC01.10.TS1.xml
                                                              * not XSD
                                                              * compliant
                                                              */
                                                             new TestDocument ("TC01.11.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R008")),
                                                             new TestDocument ("TC01.12.TS1.xml",
                                                                               new Warning ("BIICORE-T01-R436"),
                                                                               new FatalError ("BIIRULE-T01-R009")),
                                                             new TestDocument ("TC01.13.TS1.xml",
                                                                               new Warning ("BIICORE-T01-R439"),
                                                                               new FatalError ("BIIRULE-T01-R010")),
                                                             new TestDocument ("TC01.14.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R015")),
                                                             new TestDocument ("TC01.15.TS1.XML",
                                                                               new Warning ("BIICORE-T01-R080"),
                                                                               new Warning ("EUGEN-T01-R002"),
                                                                               new FatalError ("BIIRULE-T01-R028")),
                                                             new TestDocument ("TC01.16.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R001")),
                                                             new TestDocument ("TC01.17.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R001")),
                                                             new TestDocument ("TC01.18.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R001")),
                                                             new TestDocument ("TC01.19.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R001")),
                                                             new TestDocument ("TC01.20.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R001")),
                                                             new TestDocument ("TC01.21.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R002")),
                                                             new TestDocument ("TC01.22.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R002")),
                                                             new TestDocument ("TC01.23.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R002")),
                                                             new TestDocument ("TC01.24.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R002")),
                                                             new TestDocument ("TC01.25.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R018")),
                                                             new TestDocument ("TC01.26.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R019")),
                                                             new TestDocument ("TC01.27.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R020")),
                                                             new TestDocument ("TC01.28.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R021")),
                                                             new TestDocument ("TC01.29.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R021")),
                                                             new TestDocument ("TC01.30.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R021")),
                                                             new TestDocument ("TC01.31.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R021")),
                                                             new TestDocument ("TC01.32.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R021")),
                                                             new TestDocument ("TC01.33.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R029")),
                                                             new TestDocument ("TC01.34.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R018"),
                                                                               new Warning ("BIIRULE-T01-R021"),
                                                                               new FatalError ("EUGEN-T01-R008")),
                                                             new TestDocument ("TC01.35.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R018"),
                                                                               new Warning ("BIIRULE-T01-R021"),
                                                                               new FatalError ("EUGEN-T01-R009")),
                                                             new TestDocument ("TC01.36.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R019"),
                                                                               new FatalError ("EUGEN-T01-R006")),
                                                             new TestDocument ("TC01.37.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R018"),
                                                                               new FatalError ("BIIRULE-T01-R026"),
                                                                               new FatalError ("EUGEN-T01-R009")),
                                                             new TestDocument ("TC01.38.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R013")),
                                                             /*
                                                              * TC01.39.TS1.xml,
                                                              * TC01.40.TS1.xml
                                                              * not XSD
                                                              * compliant!
                                                              */
                                                             new TestDocument ("TC01.41.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R005"),
                                                                               new FatalError ("EUGEN-T01-R010")),
                                                             new TestDocument ("TC01.42.TS1.xml",
                                                                               new FatalError ("EUGEN-T01-R010")),
                                                             new TestDocument ("TC01.43.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R024")),
                                                             /*
                                                              * TC01.44.TS1.xml
                                                              * not XSD
                                                              * compliant!
                                                              */
                                                             new TestDocument ("TC01.45.TS1.xml",
                                                                               new FatalError ("BIIRULE-T01-R011")),
                                                             new TestDocument ("TC01.46.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R003")),
                                                             new TestDocument ("TC01.47.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R003")),
                                                             new TestDocument ("TC01.48.TS1.xml",
                                                                               new Warning ("EUGEN-T01-R003")),
                                                             new TestDocument ("TC01.49.TS1.xml",
                                                                               new Warning ("BIIRULE-T01-R020"),
                                                                               new Warning ("BIIRULE-T01-R021")));
  }

  public static final List <TestDocument> TEST_INVOICES_ERROR;
  static {
    TEST_INVOICES_ERROR = ContainerHelper.newUnmodifiableList (new TestDocument ("ERR-2 BII04 minimal invoice example 02.xml",
                                                                                 new Warning ("BIIRULE-T10-R002"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003"),
                                                                                 new Warning ("BIICORE-T10-R392")),
                                                               new TestDocument ("ERR-3 BII04 minimal VAT invoice example 02.xml",
                                                                                 new Warning ("BIIRULE-T10-R003"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R009"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-4 BII04 minimal invoice example 02.xml",
                                                                                 new Warning ("BIIRULE-T10-R004"),
                                                                                 new Warning ("EUGEN-T10-R002"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-5 BII04 minimal VAT invoice example 02.xml",
                                                                                 new Warning ("BIIRULE-T10-R005"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003"),
                                                                                 new Warning ("EUGEN-T10-R009")),
                                                               new TestDocument ("ERR-9 BII04 minimal VAT invoice example 02.xml",
                                                                                 new FatalError ("BIIRULE-T10-R009"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003"),
                                                                                 new Warning ("EUGEN-T10-R009")),
                                                               new TestDocument ("ERR-10 BII04 minimal VAT invoice example 02.xml",
                                                                                 new FatalError ("BIIRULE-T10-R010"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R009"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-11 BII04 minimal invoice example 02.xml",
                                                                                 new FatalError ("BIIRULE-T10-R011"),
                                                                                 new FatalError ("BIIRULE-T10-R012"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-13 BII04 minimal invoice example 02.xml",
                                                                                 new FatalError ("BIIRULE-T10-R013"),
                                                                                 new FatalError ("BIIRULE-T10-R017"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-18 BII04 minimal invoice example 02.xml",
                                                                                 new FatalError ("BIIRULE-T10-R018"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-19 BII04 minimal invoice example 02.xml",
                                                                                 new Warning ("BIIRULE-T10-R019"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("ERR-20 BII04 reverse charge invoice example 01.xml",
                                                                                 new FatalError ("EUGEN-T10-R015"),
                                                                                 new FatalError ("EUGEN-T10-R016"),
                                                                                 new FatalError ("EUGEN-T10-R017"),
                                                                                 new Warning ("BIIRULE-T10-R003"),
                                                                                 new Warning ("EUGEN-T10-R009")),
                                                               new TestDocument ("TC10.1.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R052"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.2.TS1.xml",
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.5.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R011"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.6.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R013"),
                                                                                 new FatalError ("BIIRULE-T10-R017"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.7.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R009"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.8.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R010"),
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.9.TS1.xml",
                                                                                 new Warning ("BIIRULE-T10-R004"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R002"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.10.TS1.xml",
                                                                                 new Warning ("BIICORE-T10-R392"),
                                                                                 new Warning ("BIIRULE-T10-R002"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.11.TS1.xml",
                                                                                 new FatalError ("BIIRULE-T10-R018"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.12.TS1.xml",
                                                                                 new Warning ("BIIRULE-T10-R019"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.13.TS1.xml",
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("BIIRULE-T10-R005"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.14.TS1.xml",
                                                                                 new FatalError ("EUGEN-T10-R008"),
                                                                                 new Warning ("BIIRULE-T10-R003"),
                                                                                 new Warning ("EUGEN-T10-R001"),
                                                                                 new Warning ("EUGEN-T10-R003")),
                                                               new TestDocument ("TC10.16.TS1.xml"),
                                                               new TestDocument ("TC10.17.TS1.xml",
                                                                                 new FatalError ("EUGEN-T10-R015"),
                                                                                 new FatalError ("EUGEN-T10-R016"),
                                                                                 new FatalError ("EUGEN-T10-R017"),
                                                                                 new Warning ("BIIRULE-T10-R003"),
                                                                                 new Warning ("EUGEN-T10-R009")),
                                                               new TestDocument ("TC10.18.TS1.xml",
                                                                                 new FatalError ("PCL-010-008"),
                                                                                 new Warning ("BIICORE-T10-R114"),
                                                                                 new Warning ("BIICORE-T10-R185"),
                                                                                 new Warning ("EUGEN-T10-R023")));
  }

  @Nonnull
  @ReturnsMutableCopy
  private static Set <AbstractErrorDefinition> _getAllFailedAssertionErrorCode (@Nonnull final SchematronOutputType aSVRL) {
    final Set <AbstractErrorDefinition> ret = new HashSet <AbstractErrorDefinition> ();
    final List <SVRLFailedAssert> aFAs = SVRLUtils.getAllFailedAssertions (aSVRL);
    for (final SVRLFailedAssert aFA : aFAs) {
      final String sText = aFA.getText ();
      final Matcher m = RegExHelper.getMatcher ("^\\[(.+)\\].+", sText);
      if (m.find ()) {
        final String sErrorCode = m.group (1);
        if (aFA.getFlag ().equals (EErrorLevel.WARN))
          ret.add (new Warning (sErrorCode));
        else
          ret.add (new FatalError (sErrorCode));
      }
    }
    return ret;
  }

  @Test
  public void testReadOrdersError () throws SAXException {
    // For all available orders
    for (final TestDocument aTestDoc : TEST_ORDERS_ERROR) {
      // Get the UBL XML file
      final IReadableResource aOrderRes = new ClassPathResource (CTestFiles.PATH_ORDER_TESTFILES +
                                                                 CTestFiles.PATH_ERROR +
                                                                 aTestDoc.getFilename ());

      // Ensure the UBL file validates against the scheme
      final OrderType aUBLOrder = UBL20DocumentMarshaller.readOrder (XMLReader.readXMLDOM (aOrderRes));
      assertNotNull (aUBLOrder);

      final Set <AbstractErrorDefinition> aErrCodes = new HashSet <AbstractErrorDefinition> ();

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

        aErrCodes.addAll (_getAllFailedAssertionErrorCode (aSVRL));
      }

      final Set <AbstractErrorDefinition> aCopy = new TreeSet <AbstractErrorDefinition> (aErrCodes);
      for (final AbstractErrorDefinition aExpectedErrCode : aTestDoc.getAllExpectedErrors ())
        assertTrue (aTestDoc.getFilename () +
                        " expected " +
                        aExpectedErrCode.toString () +
                        " but having " +
                        aCopy.toString (),
                    aCopy.remove (aExpectedErrCode));
      if (!aCopy.isEmpty ())
        System.out.println (aCopy);
      assertTrue (aTestDoc.getFilename () + " also indicated: " + aCopy, aCopy.isEmpty ());
    }
  }

  @Test
  public void testReadInvoicesError () throws SAXException {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available invoices
    for (final TestDocument aErrDoc : TEST_INVOICES_ERROR) {
      // Get the UBL XML file
      final IReadableResource aInvoiceRes = new ClassPathResource (CTestFiles.PATH_INVOICE_TESTFILES +
                                                                   CTestFiles.PATH_ERROR +
                                                                   aErrDoc.getFilename ());

      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20DocumentMarshaller.readInvoice (XMLReader.readXMLDOM (aInvoiceRes));
      assertNotNull (aUBLInvoice);

      final Set <AbstractErrorDefinition> aErrCodes = new HashSet <AbstractErrorDefinition> ();

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

        aErrCodes.addAll (_getAllFailedAssertionErrorCode (aSVRL));
      }
      final Set <AbstractErrorDefinition> aCopy = new TreeSet <AbstractErrorDefinition> (aErrCodes);
      for (final AbstractErrorDefinition aExpectedErrCode : aErrDoc.getAllExpectedErrors ())
        assertTrue (aErrDoc.getFilename () +
                        " expected " +
                        aExpectedErrCode.toString () +
                        " but having " +
                        aCopy.toString (),
                    aCopy.remove (aExpectedErrCode));
      assertTrue (aErrDoc.getFilename () + " also indicated: " + aCopy, aCopy.isEmpty ());
    }
  }
}

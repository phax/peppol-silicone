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
import oasis.names.specification.ubl.schema.xsd.callfortenders_21.CallForTendersType;
import oasis.names.specification.ubl.schema.xsd.tender_21.TenderType;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.xml.sax.SAXException;

import at.peppol.commons.cenbii.profiles.ETransaction;
import at.peppol.test.ETestFileType;
import at.peppol.test.TestFiles;
import at.peppol.validation.schematron.SchematronHelper;
import at.peppol.validation.schematron.svrl.SVRLFailedAssert;
import at.peppol.validation.schematron.svrl.SVRLUtils;
import at.peppol.validation.schematron.svrl.SVRLWriter;
import at.peppol.validation.schematron.xslt.SchematronResourceXSLT;

import com.phloc.commons.CGlobal;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.ubl.UBL21DocumentMarshaller;

public class FuncTestTenderValidation {

  private static final boolean DEBUG = false;

  @Test
  public void testReadTender () throws SAXException {
    // For all available tenders
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.TENDER)) {
      // Ensure the UBL file validates against the scheme
      final TenderType aUBLTender = UBL21DocumentMarshaller.readTender (XMLReader.readXMLDOM (aTestFile));
      assertNotNull (aUBLTender);

      // Test the country-independent catalogue layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.TENDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T44
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T44));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (DEBUG) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aTestFile.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }

  public void testReadCallForTenders () throws SAXException {
    // For all available call for tenders
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.CALLFORTENDERS)) {
      // Ensure the UBL file validates against the scheme
      final CallForTendersType aUBLCallForTenders = UBL21DocumentMarshaller.readCallForTenders (XMLReader.readXMLDOM (aTestFile));
      assertNotNull (aUBLCallForTenders);

      // Test the country-independent catalogue layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.TENDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T44
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T44));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (DEBUG) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aTestFile.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }
}

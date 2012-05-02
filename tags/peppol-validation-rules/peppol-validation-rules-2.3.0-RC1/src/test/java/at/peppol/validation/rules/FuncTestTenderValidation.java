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

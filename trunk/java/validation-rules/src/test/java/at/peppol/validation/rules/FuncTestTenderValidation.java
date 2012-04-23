package at.peppol.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import oasis.names.specification.ubl.schema.xsd.callfortenders_21.CallForTendersType;
import oasis.names.specification.ubl.schema.xsd.tender_21.TenderType;

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
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.ubl.UBL21DocumentMarshaller;

public class FuncTestTenderValidation {

  private static final boolean DEBUG = false;

  @Test
  public void testReadTender () throws SAXException {
    // For all available catalogues
    for (final String sTenderFile : CValidattionTestFiles.TEST_TENDER_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aTenderRes = new ClassPathResource ("/test-tender/" + sTenderFile);

      // Ensure the UBL file validates against the scheme
      final TenderType aUBLTender = UBL21DocumentMarshaller.readTender (XMLReader.readXMLDOM (aTenderRes));
      assertNotNull (aUBLTender);

      // Test the country-independent catalogue layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.TENDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T44
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T44));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTenderRes);
        assertNotNull (aSVRL);

        if (DEBUG) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aTenderRes.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }

  public void testReadVallForTender () throws SAXException {
    // For all available catalogues
    for (final String sCallForTendersFile : CValidattionTestFiles.TEST_CALLFORTENDERS_SUCCESS) {
      // Get the UBL XML file
      final IReadableResource aCallForTendersRes = new ClassPathResource ("/test-callfortender/" + sCallForTendersFile);

      // Ensure the UBL file validates against the scheme
      final CallForTendersType aUBLCallForTenders = UBL21DocumentMarshaller.readCallForTenders (XMLReader.readXMLDOM (aCallForTendersRes));
      assertNotNull (aUBLCallForTenders);

      // Test the country-independent catalogue layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.TENDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT)) {
        // Get the XSLT for transaction T44
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T44));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aCallForTendersRes);
        assertNotNull (aSVRL);

        if (DEBUG) {
          // For debugging purposes: print the SVRL
          System.out.println (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSVRL)) {
          assertEquals (aCallForTendersRes.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }
}

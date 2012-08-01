package at.peppol.validation.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import com.phloc.commons.collections.multimap.IMultiMapListBased;
import com.phloc.commons.collections.multimap.MultiHashMapArrayListBased;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

public final class MainCreateValidationRules {
  private static final String NS_SCHEMATRON = "http://purl.oclc.org/dsdl/schematron";

  private static void _log (final String s) {
    System.out.println (s);
  }

  private static String _makeID (final String s) {
    return s.replaceAll ("\\b[ \\t]+\\b", "_");
  }

  private static void _brExtractAbstractRules (final RuleSourceBusinessRule aBusinessRuleFile,
                                               final SpreadSheet aSpreadSheet) {
    final Sheet aFirstSheet = aSpreadSheet.getSheet (0);
    int nRow = 1;
    final Map <String, IMultiMapListBased <String, RuleAssertion>> aAbstractRules = new HashMap <String, IMultiMapListBased <String, RuleAssertion>> ();
    while (!aFirstSheet.getCellAt (0, nRow).isEmpty ()) {
      final String sRuleID = aFirstSheet.getCellAt (0, nRow).getTextValue ();
      final String sMessage = aFirstSheet.getCellAt (1, nRow).getTextValue ();
      final String sContext = aFirstSheet.getCellAt (2, nRow).getTextValue ();
      final String sSeverity = aFirstSheet.getCellAt (3, nRow).getTextValue ();
      final String sTransaction = aFirstSheet.getCellAt (4, nRow).getTextValue ();

      // Save in nested maps
      IMultiMapListBased <String, RuleAssertion> aTransactionRules = aAbstractRules.get (sTransaction);
      if (aTransactionRules == null) {
        aTransactionRules = new MultiHashMapArrayListBased <String, RuleAssertion> ();
        aAbstractRules.put (sTransaction, aTransactionRules);
      }
      aTransactionRules.putSingle (sContext, new RuleAssertion (sRuleID, sMessage, sSeverity));

      // Next row
      ++nRow;
    }

    // Now iterate and assemble Schematron
    for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aRuleEntry : aAbstractRules.entrySet ()) {
      final String sRuleID = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRuleFile.getSchematronAbstractFile (sRuleID);
      _log ("    Writing abstract Schematron file " +
            aSCHFile +
            " with " +
            aRuleEntry.getValue ().getTotalValueCount () +
            " rules");

      // Create the XML content
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Abstract Schematron rules for " + sRuleID);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      ePattern.setAttribute ("abstract", "true");
      ePattern.setAttribute ("id", sRuleID);
      for (final Map.Entry <String, List <RuleAssertion>> aPatternEntry : aRuleEntry.getValue ().entrySet ()) {
        final String sContext = '$' + _makeID (aPatternEntry.getKey ());
        final IMicroElement eRule = ePattern.appendElement (NS_SCHEMATRON, "rule");
        eRule.setAttribute ("context", sContext);

        for (final RuleAssertion aRuleAssertion : aPatternEntry.getValue ()) {
          final String sTestID = _makeID (aRuleAssertion.getRuleID ());
          final IMicroElement eAssert = eRule.appendElement (NS_SCHEMATRON, "assert");
          eAssert.setAttribute ("flag", aRuleAssertion.getSeverity ());
          eAssert.setAttribute ("test", "$" + sTestID);
          eAssert.appendText ("[" + sTestID + "]-" + aRuleAssertion.getMessage ());
        }
      }
      SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
    }
  }

  public static void main (final String [] args) throws IOException {
    // Base directory for source rules
    final File aRuleSource = new File ("src/test/resources/rule-source");

    // Add all base directories
    final List <RuleSourceItem> aRuleSourceItems = new ArrayList <RuleSourceItem> ();
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atgov")).addBussinessRule ("businessrules/atgov-T10-BusinessRules-v01.ods",
                                                                                                 "schematron",
                                                                                                 "ATGOV"));

    // Start execution
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      _log ("Start processing " + aRuleSourceItem.getID ());

      // Process all business rule files
      for (final RuleSourceBusinessRule aBusinessRuleFile : aRuleSourceItem.getAllBusinessRuleFiles ()) {
        // Read ODS file
        _log ("  Reading business rule source file " + aBusinessRuleFile.getSourceFile ());
        final SpreadSheet aSpreadSheet = SpreadSheet.createFromFile (aBusinessRuleFile.getSourceFile ());
        _log ("    Identified " + (aSpreadSheet.getSheetCount () - 2) + " syntax binding(s)");

        // Read abstract rules
        _brExtractAbstractRules (aBusinessRuleFile, aSpreadSheet);

        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Sheet aSheet = aSpreadSheet.getSheet (1);
          final String sBindingName = aSheet.getName ();
          _log ("    Handling sheet for binding '" + sBindingName + "'");
          int nRow = 1;
          final IMultiMapListBased <String, RuleParam> aRules = new MultiHashMapArrayListBased <String, RuleParam> ();
          while (!aSheet.getCellAt (0, nRow).isEmpty ()) {
            final String sTransaction = aSheet.getCellAt (0, nRow).getTextValue ();
            final String sRuleID = aSheet.getCellAt (1, nRow).getTextValue ();
            String sTest = aSheet.getCellAt (2, nRow).getTextValue ();
            final String sPrerequisite = aSheet.getCellAt (3, nRow).getTextValue ();
            if (StringHelper.hasText (sPrerequisite))
              sTest += " and " + sPrerequisite + " or not (" + sPrerequisite + ")";
            aRules.putSingle (sTransaction, new RuleParam (sRuleID, sTest));
            nRow++;
          }

          // Now iterate and assemble Schematron
          for (final Map.Entry <String, List <RuleParam>> aRuleEntry : aRules.entrySet ()) {
            final String sRuleID = aRuleEntry.getKey ();
            final File aSCHFile = aBusinessRuleFile.getSchematronBindingFile (sBindingName, sRuleID);
            _log ("    Writing " +
                  sBindingName +
                  " Schematron file " +
                  aSCHFile +
                  " with " +
                  aRuleEntry.getValue ().size () +
                  " test(s)");

            final IMicroDocument aDoc = new MicroDocument ();
            aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
            aDoc.appendComment ("Schematron tests for binding " + sBindingName + " for " + sRuleID);
            final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
            ePattern.setAttribute ("is-a", sRuleID);
            ePattern.setAttribute ("id", sBindingName + "-" + sRuleID);
            for (final RuleParam aRuleParam : aRuleEntry.getValue ()) {
              final IMicroElement eParam = ePattern.appendElement (NS_SCHEMATRON, "param");
              eParam.setAttribute ("name", _makeID (aRuleParam.getRuleID ()));
              eParam.setAttribute ("value", aRuleParam.getTest ());
            }
            SimpleFileIO.writeFile (aSCHFile,
                                    MicroWriter.getXMLString (aDoc),
                                    XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
          }
        }
      }
    }

    _log ("Finished build validation rules");
  }
}

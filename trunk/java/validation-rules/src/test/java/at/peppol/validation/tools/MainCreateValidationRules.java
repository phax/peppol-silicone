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
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;

public final class MainCreateValidationRules {
  private static final String NS_SCHEMATRON = "http://purl.oclc.org/dsdl/schematron";

  private static void _log (final String s) {
    System.out.println (s);
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
        {
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

          for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aRuleEntry : aAbstractRules.entrySet ()) {
            final String sRuleID = aRuleEntry.getKey ();
            final File aFile = new File (aBusinessRuleFile.getOutputDirectory (), aBusinessRuleFile.getFilePrefix () +
                                                                                  "-" +
                                                                                  sRuleID +
                                                                                  "-abstract.sch");
            _log ("    Writing abstract Schematron file " +
                  aFile +
                  " with " +
                  aRuleEntry.getValue ().getTotalValueCount () +
                  " rules");

            // Create the eain file
            final IMicroDocument aDoc = new MicroDocument ();
            aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
            aDoc.appendComment ("Abstract Schematron rules for " + sRuleID);
            final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
            ePattern.setAttribute ("abstract", "true");
            ePattern.setAttribute ("id", sRuleID);
            for (final Map.Entry <String, List <RuleAssertion>> aPatternEntry : aRuleEntry.getValue ().entrySet ()) {
              final String sContext = '$' + aPatternEntry.getKey ().replaceAll ("\\b[ \\t]+\\b", "_");
              final IMicroElement eRule = ePattern.appendElement (NS_SCHEMATRON, "rule");
              eRule.setAttribute ("context", sContext);

              for (final RuleAssertion aRuleAssertion : aPatternEntry.getValue ()) {
                final String sTest = aRuleAssertion.getRuleID ().replaceAll ("\\b[ \\t]+\\b", "_");
                final IMicroElement eAssert = eRule.appendElement (NS_SCHEMATRON, "assert");
                eAssert.setAttribute ("flag", aRuleAssertion.getSeverity ());
                eAssert.setAttribute ("test", "$" + sTest);
                eAssert.appendText ("[" + sTest + "]-" + aRuleAssertion.getMessage ());
              }
            }
          }
        }

        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Sheet aSheet = aSpreadSheet.getSheet (1);
          final String sSheetName = aSheet.getName ();
          _log ("    Handling sheet '" + sSheetName + "'");
          int nRow = 1;
          while (!aSheet.getCellAt (0, nRow).isEmpty ()) {
            final String sTransaction = aSheet.getCellAt (0, nRow).getTextValue ();
            final String sRuleID = aSheet.getCellAt (1, nRow).getTextValue ();
            final String sPredicate = aSheet.getCellAt (2, nRow).getTextValue ();
            final String sPrerequisite = aSheet.getCellAt (3, nRow).getTextValue ();
            nRow++;
          }
        }
      }
    }

    _log ("Finished build validation rules");
  }
}

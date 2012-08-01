package at.peppol.validation.tools.sch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import at.peppol.validation.tools.Utils;

import com.phloc.commons.collections.multimap.IMultiMapListBased;
import com.phloc.commons.collections.multimap.MultiHashMapArrayListBased;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

public final class SchematronCreator {
  private static final String NS_SCHEMATRON = "http://purl.oclc.org/dsdl/schematron";

  private static String _makeID (final String s) {
    return s.replaceAll ("\\b[ \\t]+\\b", "_");
  }

  private static void _brExtractAbstractRules (final RuleSourceBusinessRule aBusinessRule,
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
      final String sTransaction = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRule.getSchematronAbstractFile (sTransaction);
      Utils.log ("    Writing abstract Schematron file " +
      aSCHFile.getName () +
      " with " +
      aRuleEntry.getValue ().getTotalValueCount () +
      " rule(s)");

      // Create the XML content
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Abstract Schematron rules for " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      ePattern.setAttribute ("abstract", "true");
      ePattern.setAttribute ("id", sTransaction);
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

  private static void _brExtractBindingTests (final RuleSourceBusinessRule aBusinessRule, final Sheet aSheet) {
    final String sBindingName = aSheet.getName ();
    Utils.log ("    Handling sheet for binding '" + sBindingName + "'");
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

    // Now iterate rules and assemble Schematron
    for (final Map.Entry <String, List <RuleParam>> aRuleEntry : aRules.entrySet ()) {
      final String sTransaction = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction);
      Utils.log ("      Writing " +
      sBindingName +
      " Schematron file " +
      aSCHFile.getName () +
      " with " +
      aRuleEntry.getValue ().size () +
      " test(s)");

      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Schematron tests for binding " + sBindingName + " and transaction " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      ePattern.setAttribute ("is-a", sTransaction);
      ePattern.setAttribute ("id", sBindingName + "-" + sTransaction);
      for (final RuleParam aRuleParam : aRuleEntry.getValue ()) {
        final IMicroElement eParam = ePattern.appendElement (NS_SCHEMATRON, "param");
        eParam.setAttribute ("name", _makeID (aRuleParam.getRuleID ()));
        eParam.setAttribute ("value", aRuleParam.getTest ());
      }
      SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
    }
  }

  private static void _brCreateAssemblyFiles (final RuleSourceBusinessRule aBusinessRule, final SpreadSheet aSpreadSheet) {
    // Create assembled Schematron
    Utils.log ("    Creating assembly Schematron file(s)");
    final Sheet aLastSheet = aSpreadSheet.getSheet (aSpreadSheet.getSheetCount () - 1);
    int nRow = 1;
    // cell 0 (profile) is optional!
    while (nRow < aLastSheet.getRowCount () && !aLastSheet.getCellAt (1, nRow).isEmpty ()) {
      final String sProfile = aLastSheet.getCellAt (0, nRow).getTextValue ();
      final String sTransaction = aLastSheet.getCellAt (1, nRow).getTextValue ();
      final String sBindingName = aLastSheet.getCellAt (2, nRow).getTextValue ();
      final String sNamespace = aLastSheet.getCellAt (3, nRow).getTextValue ();

      final File aSCHFile = aBusinessRule.getSchematronAssemblyFile (sBindingName, sTransaction);
      Utils.log ("      Writing " + sBindingName + " Schematron assembly file " + aSCHFile.getName ());

      final String sBindingPrefix = sBindingName.toLowerCase (Locale.US);
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Schematron assembly for binding " + sBindingName + " and transaction " + sTransaction);
      final IMicroElement eSchema = aDoc.appendElement (NS_SCHEMATRON, "schema");
      eSchema.setAttribute ("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
      eSchema.setAttribute ("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
      eSchema.setAttribute ("xmlns:" + sBindingPrefix, sNamespace);
      eSchema.setAttribute ("queryBinding", "xslt2");
      eSchema.appendElement (NS_SCHEMATRON, "title").appendText (aBusinessRule.getID () +
                                                                 " " +
                                                                 sTransaction +
                                                                 " bound to " +
                                                                 sBindingName);
      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", "cbc")
             .setAttribute ("uri", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", "cac")
             .setAttribute ("uri", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
      eSchema.appendElement (NS_SCHEMATRON, "ns")
             .setAttribute ("prefix", sBindingPrefix)
             .setAttribute ("uri", sNamespace);

      IMicroElement ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
      ePhase.setAttribute ("id", aBusinessRule.getID () + "_" + sTransaction + "_phase");
      ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", sBindingName + "-" + sTransaction);

      if (StringHelper.hasText (sProfile)) {
        ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
        ePhase.setAttribute ("id", aBusinessRule.getID () + "_" + sProfile + "_phase");
        ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", sBindingName + "-" + sProfile);
      }

      if (aBusinessRule.hasCodeList ()) {
        ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
        ePhase.setAttribute ("id", "codelist_phase");
        ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", "Codes" + sProfile);
      }

      IMicroElement eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", aBusinessRule.getSchematronAbstractFile (sTransaction).getName ());
      if (StringHelper.hasText (sProfile)) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronAbstractFile (sProfile).getName ());
      }
      eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction).getName ());
      if (StringHelper.hasText (sProfile)) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronBindingFile (sBindingName, sProfile).getName ());
      }
      if (aBusinessRule.hasCodeList ()) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronCodeListFile ().getName ());
      }
      SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);

      ++nRow;
    }
  }

  public static void createSchematrons (final List <RuleSourceItem> aRuleSourceItems) throws IOException {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Start processing " + aRuleSourceItem.getID ());

      // Process all business rule files
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRuleFiles ()) {
        // Read ODS file
        Utils.log ("  Reading business rule source file " + aBusinessRule.getSourceFile ());
        final SpreadSheet aSpreadSheet = SpreadSheet.createFromFile (aBusinessRule.getSourceFile ());
        Utils.log ("    Identified " + (aSpreadSheet.getSheetCount () - 2) + " syntax binding(s)");

        // Read abstract rules
        _brExtractAbstractRules (aBusinessRule, aSpreadSheet);

        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Sheet aSheet = aSpreadSheet.getSheet (nSheetIndex);
          _brExtractBindingTests (aBusinessRule, aSheet);
        }

        _brCreateAssemblyFiles (aBusinessRule, aSpreadSheet);
      }
    }
  }
}

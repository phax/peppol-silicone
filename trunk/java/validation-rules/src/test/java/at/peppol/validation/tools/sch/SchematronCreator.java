package at.peppol.validation.tools.sch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import at.peppol.validation.tools.RuleSourceBusinessRule;
import at.peppol.validation.tools.RuleSourceItem;
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

  private static boolean _isEmpty (final Cell c) {
    return c.getValueType () == null;
  }

  private static void _brExtractAbstractRules (final RuleSourceBusinessRule aBusinessRule,
                                               final SpreadsheetDocument aSpreadSheet) {
    final Table aFirstSheet = aSpreadSheet.getSheetByIndex (0);
    int nRow = 1;
    final Map <String, IMultiMapListBased <String, RuleAssertion>> aAbstractRules = new HashMap <String, IMultiMapListBased <String, RuleAssertion>> ();
    while (!_isEmpty (aFirstSheet.getCellByPosition (0, nRow))) {
      final String sRuleID = aFirstSheet.getCellByPosition (0, nRow).getStringValue ();
      final String sMessage = aFirstSheet.getCellByPosition (1, nRow).getStringValue ();
      final String sContext = aFirstSheet.getCellByPosition (2, nRow).getStringValue ();
      final String sSeverity = aFirstSheet.getCellByPosition (3, nRow).getStringValue ();
      final String sTransaction = aFirstSheet.getCellByPosition (4, nRow).getStringValue ();

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

  private static void _brExtractBindingTests (final RuleSourceBusinessRule aBusinessRule, final Table aSheet) {
    final String sBindingName = aSheet.getTableName ().toUpperCase (Locale.US);
    Utils.log ("    Handling sheet for binding '" + sBindingName + "'");
    int nRow = 1;
    final IMultiMapListBased <String, RuleParam> aRules = new MultiHashMapArrayListBased <String, RuleParam> ();
    while (!_isEmpty (aSheet.getCellByPosition (0, nRow))) {
      final String sTransaction = aSheet.getCellByPosition (0, nRow).getStringValue ();
      final String sRuleID = aSheet.getCellByPosition (1, nRow).getStringValue ();
      String sTest = aSheet.getCellByPosition (2, nRow).getStringValue ();
      final String sPrerequisite = aSheet.getCellByPosition (3, nRow).getStringValue ();

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

  private static void _brCreateAssemblyFiles (final RuleSourceBusinessRule aBusinessRule,
                                              final SpreadsheetDocument aSpreadSheet) {
    // Create assembled Schematron
    Utils.log ("    Creating assembly Schematron file(s)");
    final Table aLastSheet = aSpreadSheet.getSheetByIndex (aSpreadSheet.getSheetCount () - 1);
    int nRow = 1;
    // cell 0 (profile) is optional!
    while (nRow < aLastSheet.getRowCount () && !_isEmpty (aLastSheet.getCellByPosition (1, nRow))) {
      final String sProfile = aLastSheet.getCellByPosition (0, nRow).getStringValue ();
      final String sTransaction = aLastSheet.getCellByPosition (1, nRow).getStringValue ();
      final String sBindingName = aLastSheet.getCellByPosition (2, nRow).getStringValue ();
      final String sNamespace = aLastSheet.getCellByPosition (3, nRow).getStringValue ();

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

      // Remember file for XSLT creation
      aBusinessRule.addResultSchematronFile (aSCHFile);

      ++nRow;
    }
  }

  public static void createSchematrons (final List <RuleSourceItem> aRuleSourceItems) throws Exception {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Creating Schematron files for " + aRuleSourceItem.getID ());

      // Process all business rule files
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRules ()) {
        // Read ODS file
        Utils.log ("  Reading business rule source file " + aBusinessRule.getSourceFile ());
        final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aBusinessRule.getSourceFile ());
        Utils.log ("    Identified " + (aSpreadSheet.getSheetCount () - 2) + " syntax binding(s)");

        // Read abstract rules
        _brExtractAbstractRules (aBusinessRule, aSpreadSheet);

        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          _brExtractBindingTests (aBusinessRule, aSheet);
        }

        _brCreateAssemblyFiles (aBusinessRule, aSpreadSheet);
      }
    }
  }
}

package at.peppol.validation.tools.sch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import at.peppol.validation.schematron.CSchematron;
import at.peppol.validation.tools.RuleSourceItem;
import at.peppol.validation.tools.utils.ODFUtils;
import at.peppol.validation.tools.utils.Utils;

import com.phloc.commons.collections.multimap.IMultiMapListBased;
import com.phloc.commons.collections.multimap.MultiHashMapArrayListBased;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

@Immutable
public final class SchematronCreator {
  private static final String NS_SCHEMATRON = CSchematron.NAMESPACE_SCHEMATRON;

  // Map from transaction to Map from context to list of assertions
  final Map <String, IMultiMapListBased <String, RuleAssertion>> m_aAbstractRules = new HashMap <String, IMultiMapListBased <String, RuleAssertion>> ();

  private SchematronCreator () {}

  private void _extractAbstractRules (final RuleSourceBusinessRule aBusinessRule, final SpreadsheetDocument aSpreadSheet) {
    final Table aFirstSheet = aSpreadSheet.getSheetByIndex (0);
    int nRow = 1;
    while (!ODFUtils.isEmpty (aFirstSheet, 0, nRow)) {
      final String sRuleID = ODFUtils.getText (aFirstSheet, 0, nRow);
      final String sMessage = ODFUtils.getText (aFirstSheet, 1, nRow);
      final String sContext = ODFUtils.getText (aFirstSheet, 2, nRow);
      final String sSeverity = ODFUtils.getText (aFirstSheet, 3, nRow);
      final String sTransaction = ODFUtils.getText (aFirstSheet, 4, nRow);

      // Save in nested maps
      IMultiMapListBased <String, RuleAssertion> aTransactionRules = m_aAbstractRules.get (sTransaction);
      if (aTransactionRules == null) {
        aTransactionRules = new MultiHashMapArrayListBased <String, RuleAssertion> ();
        m_aAbstractRules.put (sTransaction, aTransactionRules);
      }
      aTransactionRules.putSingle (sContext, new RuleAssertion (sRuleID, sMessage, sSeverity));

      // Next row
      ++nRow;
    }

    if (m_aAbstractRules.isEmpty ())
      throw new IllegalStateException ("No abstract rules found!");

    // Now iterate and assemble Schematron
    for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aRuleEntry : m_aAbstractRules.entrySet ()) {
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
        final String sContext = '$' + Utils.makeID (aPatternEntry.getKey ());
        final IMicroElement eRule = ePattern.appendElement (NS_SCHEMATRON, "rule");
        eRule.setAttribute ("context", sContext);

        for (final RuleAssertion aRuleAssertion : aPatternEntry.getValue ()) {
          final String sTestID = aRuleAssertion.getRuleID ();
          final IMicroElement eAssert = eRule.appendElement (NS_SCHEMATRON, "assert");
          eAssert.setAttribute ("flag", aRuleAssertion.getSeverity ());
          eAssert.setAttribute ("test", "$" + sTestID);
          eAssert.appendText ("[" + sTestID + "]-" + aRuleAssertion.getMessage ());
        }
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);
    }
  }

  private static boolean _containsRuleID (@Nonnull final List <RuleParam> aRuleParams, final String sRuleID) {
    for (final RuleParam aRuleParam : aRuleParams)
      if (aRuleParam.getRuleID ().equals (sRuleID))
        return true;
    return false;
  }

  private void _extractBindingTests (final RuleSourceBusinessRule aBusinessRule, final Table aSheet) {
    final String sBindingName = aSheet.getTableName ();
    Utils.log ("    Handling sheet for binding '" + sBindingName + "'");
    int nRow = 1;
    final IMultiMapListBased <String, RuleParam> aRules = new MultiHashMapArrayListBased <String, RuleParam> ();
    while (!ODFUtils.isEmpty (aSheet, 0, nRow)) {
      final String sTransaction = ODFUtils.getText (aSheet, 0, nRow);
      final String sRuleID = ODFUtils.getText (aSheet, 1, nRow);
      String sTest = ODFUtils.getText (aSheet, 2, nRow);
      final String sPrerequisite = ODFUtils.getText (aSheet, 3, nRow);

      if (StringHelper.hasText (sPrerequisite))
        sTest += " and " + sPrerequisite + " or not (" + sPrerequisite + ")";
      aRules.putSingle (sTransaction, new RuleParam (sRuleID, sTest));
      nRow++;
    }

    // Check if all required rules derived from the abstract rules are present
    for (final Map.Entry <String, IMultiMapListBased <String, RuleAssertion>> aEntryTransaction : m_aAbstractRules.entrySet ()) {
      final String sTransaction = aEntryTransaction.getKey ();
      final List <RuleParam> aFoundRules = aRules.get (sTransaction);
      if (aFoundRules == null)
        throw new IllegalStateException ("Found no rules for transaction " +
                                         sTransaction +
                                         " and binding " +
                                         sBindingName);
      for (final Map.Entry <String, List <RuleAssertion>> aEntryContext : aEntryTransaction.getValue ().entrySet ()) {
        final String sContext = aEntryContext.getKey ();
        if (!_containsRuleID (aFoundRules, Utils.makeID (sContext))) {
          // Create an invalid context
          Utils.warn ("      Missing parameter for context '" + sContext + "'");
          aRules.putSingle (sTransaction, new RuleParam (sContext, "//NonExistingDummyNode"));
        }
        for (final RuleAssertion aRuleAssertion : aEntryContext.getValue ()) {
          final String sRuleID = aRuleAssertion.getRuleID ();
          if (!_containsRuleID (aFoundRules, sRuleID)) {
            // No test needed
            Utils.warn ("      Missing parameter for rule '" + sRuleID + "'");
            aRules.putSingle (sTransaction, new RuleParam (sRuleID, "./false"));
          }
        }
      }
    }

    // Now iterate rules and assemble Schematron
    for (final Map.Entry <String, List <RuleParam>> aRuleEntry : aRules.entrySet ()) {
      final String sTransaction = aRuleEntry.getKey ();
      final File aSCHFile = aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction);
      Utils.log ("      Writing " +
                 sBindingName +
                 " Schematron file " +
                 aSCHFile.getName () +
                 " for transaction " +
                 sTransaction +
                 " with " +
                 aRuleEntry.getValue ().size () +
                 " test(s)");

      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
      aDoc.appendComment ("Schematron tests for binding " + sBindingName + " and transaction " + sTransaction);
      final IMicroElement ePattern = aDoc.appendElement (NS_SCHEMATRON, "pattern");
      // Assign to the global pattern
      ePattern.setAttribute ("is-a", sTransaction);
      ePattern.setAttribute ("id", sBindingName.toUpperCase (Locale.US) + "-" + sTransaction);
      for (final RuleParam aRuleParam : aRuleEntry.getValue ()) {
        final IMicroElement eParam = ePattern.appendElement (NS_SCHEMATRON, "param");
        eParam.setAttribute ("name", aRuleParam.getRuleID ());
        eParam.setAttribute ("value", aRuleParam.getTest ());
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);
    }
  }

  private static void _createAssemblyFiles (final RuleSourceBusinessRule aBusinessRule,
                                            final SpreadsheetDocument aSpreadSheet) {
    // Create assembled Schematron
    Utils.log ("    Creating assembly Schematron file(s)");
    final Table aLastSheet = aSpreadSheet.getSheetByIndex (aSpreadSheet.getSheetCount () - 1);
    int nRow = 1;
    // cell 0 (profile) is optional!
    while (!ODFUtils.isEmpty (aLastSheet, 1, nRow)) {
      final String sProfile = ODFUtils.getText (aLastSheet, 0, nRow);
      final String sTransaction = ODFUtils.getText (aLastSheet, 1, nRow);
      final String sBindingName = ODFUtils.getText (aLastSheet, 2, nRow);
      final String sNamespace = ODFUtils.getText (aLastSheet, 3, nRow);

      final File aSCHFile = aBusinessRule.getSchematronAssemblyFile (sBindingName, sTransaction);
      Utils.log ("      Writing " + sBindingName + " Schematron assembly file " + aSCHFile.getName ());

      if (StringHelper.hasText (sProfile))
        throw new IllegalStateException ("Profile currently not supported! Found '" + sProfile + "'");

      final String sBindingPrefix = sBindingName.toLowerCase (Locale.US);
      final String sBindingUC = sBindingName.toUpperCase (Locale.US);

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

      // Phases
      IMicroElement ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
      ePhase.setAttribute ("id", aBusinessRule.getID () + "_" + sTransaction + "_phase");
      ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", sBindingUC + "-" + sTransaction);
      if (StringHelper.hasText (sProfile)) {
        ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
        ePhase.setAttribute ("id", aBusinessRule.getID () + "_" + sProfile + "_phase");
        ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", sBindingUC + "-" + sProfile);
      }
      if (aBusinessRule.hasCodeList ()) {
        ePhase = eSchema.appendElement (NS_SCHEMATRON, "phase");
        ePhase.setAttribute ("id", "codelist_phase");
        ePhase.appendElement (NS_SCHEMATRON, "active").setAttribute ("pattern", "Codes-" + sTransaction);
      }

      // Includes
      IMicroElement eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", aBusinessRule.getSchematronAbstractFile (sTransaction).getName ());
      if (StringHelper.hasText (sProfile)) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronAbstractFile (sProfile).getName ());
      }
      if (aBusinessRule.hasCodeList ()) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronCodeListFile ().getName ());
      }
      eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
      eInclude.setAttribute ("href", aBusinessRule.getSchematronBindingFile (sBindingName, sTransaction).getName ());
      if (StringHelper.hasText (sProfile)) {
        eInclude = eSchema.appendElement (NS_SCHEMATRON, "include");
        eInclude.setAttribute ("href", aBusinessRule.getSchematronBindingFile (sBindingName, sProfile).getName ());
      }
      if (SimpleFileIO.writeFile (aSCHFile, MicroWriter.getXMLString (aDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ)
                      .isFailure ())
        throw new IllegalStateException ("Failed to write " + aSCHFile);

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

        final SchematronCreator aSC = new SchematronCreator ();

        // Read abstract rules
        aSC._extractAbstractRules (aBusinessRule, aSpreadSheet);

        // Skip the first sheet (abstract rules) and skip the last sheet
        // (transaction information)
        for (int nSheetIndex = 1; nSheetIndex < aSpreadSheet.getSheetCount () - 1; ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          aSC._extractBindingTests (aBusinessRule, aSheet);
        }

        _createAssemblyFiles (aBusinessRule, aSpreadSheet);
      }
    }
  }
}

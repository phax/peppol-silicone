package at.peppol.validation.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import at.peppol.validation.tools.sch.SchematronCreator;
import at.peppol.validation.tools.sch.XSLTCreator;

public final class MainCreateValidationRules {
  public static void main (final String [] args) throws Exception {
    // Base directory for source rules
    final File aRuleSource = new File ("src/test/resources/rule-source");

    // Add all base directories
    final List <RuleSourceItem> aRuleSourceItems = new ArrayList <RuleSourceItem> ();
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atgov")).addBussinessRule ("businessrules/atgov-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atnat")).addBussinessRule ("businessrules/atnat-T10-BusinessRules-v01.ods"));
    // XSLT creation takes forever
    if (false)
      aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biicore")).addBussinessRule ("businessrules/biicore-T01-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T10-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T14-BusinessRules-v01.ods")
                                                                                  .addBussinessRule ("businessrules/biicore-T15-BusinessRules-v01.ods"));
    // businessrules/biiprofiles-T01-BusinessRules-v01.ods is corrupted
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biiprofiles")).addBussinessRule ("businessrules/biiprofiles-T10-BusinessRules-v01.ods")
                                                                                    .addBussinessRule ("businessrules/biiprofiles-T14-BusinessRules-v01.ods")
                                                                                    .addBussinessRule ("businessrules/biiprofiles-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biirules")).addCodeList ("businessrules/biirules-CodeLists-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "dknat")).addBussinessRule ("businessrules/dknat-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "itnat")).addBussinessRule ("businessrules/itnat-T10-BusinessRules-v03.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nogov")).addBussinessRule ("businessrules/nogov-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nonat")).addBussinessRule ("businessrules/nonat-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T15-BusinessRules-v01.ods"));

    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      // Process all code lists
      for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ()) {
        Utils.log ("Reading code list file " + aCodeList.getSourceFile ());
        final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aCodeList.getSourceFile ());
        for (int nSheetIndex = 0; nSheetIndex < aSpreadSheet.getSheetCount (); ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          final String sSheetName = aSheet.getTableName ();
          if (!sSheetName.equals ("CVA")) {
            final String sShortname = aSheet.getCellByPosition (0, 1).getStringValue ();
            final String sVersion = aSheet.getCellByPosition (1, 1).getStringValue ();
            final String sAgency = aSheet.getCellByPosition (2, 1).getStringValue ();
            final String sLocationURI = aSheet.getCellByPosition (3, 1).getStringValue ();
            final String sLocale = aSheet.getCellByPosition (4, 1).getStringValue ();
          }
        }
      }
    }

    if (true) {
      // Create Schematron
      SchematronCreator.createSchematrons (aRuleSourceItems);

      // Now create the validation XSLTs
      XSLTCreator.createXSLTs (aRuleSourceItems);
    }

    Utils.log ("Finished building validation rules");
  }
}

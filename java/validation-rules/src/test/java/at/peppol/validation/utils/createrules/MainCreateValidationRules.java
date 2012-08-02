package at.peppol.validation.utils.createrules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.peppol.validation.utils.createrules.codelist.CodeListCreator;
import at.peppol.validation.utils.createrules.codelist.RuleSourceCodeList;
import at.peppol.validation.utils.createrules.sch.SchematronCreator;
import at.peppol.validation.utils.createrules.sch.XSLTCreator;
import at.peppol.validation.utils.createrules.utils.Utils;

import com.phloc.commons.GlobalDebug;

public final class MainCreateValidationRules {
  public static void main (final String [] args) throws Exception {
    GlobalDebug.setDebugModeDirect (true);
    // Base directory for source rules
    final File aRuleSource = new File ("src/test/resources/rule-source");

    // Add all base directories
    final List <RuleSourceItem> aRuleSourceItems = new ArrayList <RuleSourceItem> ();
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atgov")).addBussinessRule ("businessrules/atgov-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atnat")).addBussinessRule ("businessrules/atnat-T10-BusinessRules-v01.ods"));
    // XSLT creation takes forever
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biicore")).addBussinessRule ("businessrules/biicore-T01-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/biicore-T10-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/biicore-T14-BusinessRules-v01.ods")
                                                                                .addBussinessRule ("businessrules/biicore-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biiprofiles")).addBussinessRule ("businessrules/biiprofiles-T10-BusinessRules-v01.ods")
                                                                                    .addBussinessRule ("businessrules/biiprofiles-T14-BusinessRules-v01.ods")
                                                                                    .addBussinessRule ("businessrules/biiprofiles-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "biirules")).addCodeList ("businessrules/biirules-CodeLists-v01.ods")
                                                                                 .addBussinessRule ("businessrules/biirules-T01-BusinessRules-v02.ods",
                                                                                                    "T01")
                                                                                 .addBussinessRule ("businessrules/biirules-T02-BusinessRules-v01.ods")
                                                                                 .addBussinessRule ("businessrules/biirules-T03-BusinessRules-v01.ods")
                                                                                 .addBussinessRule ("businessrules/biirules-T10-BusinessRules-v02.ods",
                                                                                                    "T10")
                                                                                 .addBussinessRule ("businessrules/biirules-T14-BusinessRules-v01.ods",
                                                                                                    "T14")
                                                                                 .addBussinessRule ("businessrules/biirules-T15-BusinessRules-v01.ods",
                                                                                                    "T15"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "dknat")).addBussinessRule ("businessrules/dknat-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "eugen")).addCodeList ("businessrules/eugen-CodeLists-v01.ods")
                                                                              .addBussinessRule ("businessrules/eugen-T01-BusinessRules-v02.ods")
                                                                              .addBussinessRule ("businessrules/eugen-T10-BusinessRules-v01.ods",
                                                                                                 "T10")
                                                                              .addBussinessRule ("businessrules/eugen-T14-BusinessRules-v01.ods",
                                                                                                 "T14")
                                                                              .addBussinessRule ("businessrules/eugen-T15-BusinessRules-v01.ods",
                                                                                                 "T15")
                                                                              .addBussinessRule ("businessrules/eugen-T19-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "itnat")).addBussinessRule ("businessrules/itnat-T10-BusinessRules-v03.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nogov")).addBussinessRule ("businessrules/nogov-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nonat")).addCodeList ("businessrules/nonat-T17-CodeLists-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T15-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T17-BusinessRules-v01.ods",
                                                                                                 "T17"));

    if (true) {
      // Create all codelists (GC + CVA)
      for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
        // Process all code lists
        for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ())
          new CodeListCreator ().createCodeLists (aCodeList);
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

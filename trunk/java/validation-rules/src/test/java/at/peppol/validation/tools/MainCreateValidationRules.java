package at.peppol.validation.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.peppol.validation.tools.sch.RuleSourceItem;
import at.peppol.validation.tools.sch.SchematronCreator;

public final class MainCreateValidationRules {
  public static void main (final String [] args) throws IOException {
    // Base directory for source rules
    final File aRuleSource = new File ("src/test/resources/rule-source");

    // Add all base directories
    final List <RuleSourceItem> aRuleSourceItems = new ArrayList <RuleSourceItem> ();
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atgov")).addBussinessRule ("businessrules/atgov-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "atnat")).addBussinessRule ("businessrules/atnat-T10-BusinessRules-v01.ods"));

    // Start execution
    SchematronCreator.createSchematrons (aRuleSourceItems);

    Utils.log ("Finished building validation rules");
  }
}

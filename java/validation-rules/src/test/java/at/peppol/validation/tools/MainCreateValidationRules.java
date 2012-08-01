package at.peppol.validation.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;
import at.peppol.validation.tools.sch.RuleSourceBusinessRule;
import at.peppol.validation.tools.sch.RuleSourceItem;
import at.peppol.validation.tools.sch.SchematronCreator;

import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

public final class MainCreateValidationRules {
  public static void main (final String [] args) throws IOException {
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
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "dknat")).addBussinessRule ("businessrules/dknat-T10-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "itnat")).addBussinessRule ("businessrules/itnat-T10-BusinessRules-v03.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nogov")).addBussinessRule ("businessrules/nogov-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nogov-T15-BusinessRules-v01.ods"));
    aRuleSourceItems.add (new RuleSourceItem (new File (aRuleSource, "nonat")).addBussinessRule ("businessrules/nonat-T10-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T14-BusinessRules-v01.ods")
                                                                              .addBussinessRule ("businessrules/nonat-T15-BusinessRules-v01.ods"));

    // Create Schematron
    SchematronCreator.createSchematrons (aRuleSourceItems);

    // Now create the validation XSLTs
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Creating XSLT files for " + aRuleSourceItem.getID ());
      // Process all business rules
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRuleFiles ())
        for (final File aSCHFile : aBusinessRule.getAllResultSchematronFiles ()) {
          Utils.log ("  Creating XSLT for " + aSCHFile.getName ());
          final Document aXSLTDoc = SchematronResourceSCHCache.createSchematronXSLTProvider (new FileSystemResource (aSCHFile),
                                                                                             null,
                                                                                             null)
                                                              .getXSLTDocument ();

          final File aXSLTFile = new File (aSCHFile.getParentFile (),
                                           FilenameHelper.getWithoutExtension (aSCHFile.getName ()) + ".xslt");
          Utils.log ("  Writing file " + aXSLTFile.getName ());
          SimpleFileIO.writeFile (aXSLTFile,
                                  XMLWriter.getXMLString (aXSLTDoc),
                                  XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
        }
    }

    Utils.log ("Finished building validation rules");
  }
}

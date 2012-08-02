package at.peppol.validation.tools.sch;

import java.io.File;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;

import at.peppol.validation.schematron.xslt.ISchematronXSLTProvider;
import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;
import at.peppol.validation.tools.RuleSourceItem;
import at.peppol.validation.tools.utils.Utils;

import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

@Immutable
public final class XSLTCreator {
  private XSLTCreator () {}

  public static void createXSLTs (final List <RuleSourceItem> aRuleSourceItems) {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      Utils.log ("Creating XSLT files for " + aRuleSourceItem.getID ());
      // Process all business rules
      for (final RuleSourceBusinessRule aBusinessRule : aRuleSourceItem.getAllBusinessRules ())
        for (final File aSCHFile : aBusinessRule.getAllResultSchematronFiles ()) {
          Utils.log ("  Creating XSLT for " + aSCHFile.getName ());

          final ISchematronXSLTProvider aXSLTProvider = SchematronResourceSCHCache.createSchematronXSLTProvider (new FileSystemResource (aSCHFile));
          if (aXSLTProvider == null) {
            // Error message already emitted!
            continue;
          }
          final Document aXSLTDoc = aXSLTProvider.getXSLTDocument ();

          final File aXSLTFile = new File (FilenameHelper.getWithoutExtension (aSCHFile.getPath ()) + ".xslt");
          if (SimpleFileIO.writeFile (aXSLTFile,
                                      XMLWriter.getXMLString (aXSLTDoc),
                                      XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ).isFailure ())
            throw new IllegalStateException ("Failed to write " + aXSLTFile);
        }
    }
  }

}

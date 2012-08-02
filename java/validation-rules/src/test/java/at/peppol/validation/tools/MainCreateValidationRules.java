package at.peppol.validation.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.oasis.genericode.v10.CodeListDocument;
import org.oasis.genericode.v10.Column;
import org.oasis.genericode.v10.ColumnSet;
import org.oasis.genericode.v10.Identification;
import org.oasis.genericode.v10.ObjectFactory;
import org.oasis.genericode.v10.Row;
import org.oasis.genericode.v10.SimpleCodeList;
import org.oasis.genericode.v10.UseType;
import org.oasis.genericode.v10.Value;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import at.peppol.validation.tools.sch.SchematronCreator;
import at.peppol.validation.tools.sch.XSLTCreator;
import at.peppol.validation.tools.sch.odf.ODFUtils;

import com.phloc.genericode.Genericode10CodeListMarshaller;
import com.phloc.genericode.Genericode10Utils;

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

    final ObjectFactory aFactory = new ObjectFactory ();
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      // Process all code lists
      for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ()) {
        Utils.log ("Reading code list file " + aCodeList.getSourceFile ());
        final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aCodeList.getSourceFile ());
        for (int nSheetIndex = 0; nSheetIndex < aSpreadSheet.getSheetCount (); ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          final String sSheetName = aSheet.getTableName ();
          if (!sSheetName.equals ("CVA")) {
            final File aGCFile = aCodeList.getGCFile (sSheetName);
            Utils.log ("  Creating codelist file " + aGCFile.getName ());
            final String sShortname = ODFUtils.getText (aSheet, 0, 1);
            final String sVersion = ODFUtils.getText (aSheet, 1, 1);
            final String sAgency = ODFUtils.getText (aSheet, 2, 1);
            final String sLocationURI = ODFUtils.getText (aSheet, 3, 1);
            // final String sLocale = _getText (aSheet, 4, 1);

            // Start creating codelist
            final CodeListDocument aGC = aFactory.createCodeListDocument ();

            // create identification
            final Identification aIdentification = aFactory.createIdentification ();
            aIdentification.setShortName (Genericode10Utils.createShortName (sShortname));
            aIdentification.setVersion (sVersion);
            aIdentification.setCanonicalUri (sAgency);
            aIdentification.setCanonicalVersionUri (sAgency + "-" + sVersion);
            aIdentification.getLocationUri ().add (sLocationURI);
            aGC.setIdentification (aIdentification);

            // Build column set
            final ColumnSet aColumnSet = aFactory.createColumnSet ();
            final Column aCodeColumn = Genericode10Utils.createColumn ("code",
                                                                       UseType.REQUIRED,
                                                                       "Code",
                                                                       null,
                                                                       "normalizedString");
            final Column aNameColumn = Genericode10Utils.createColumn ("name", UseType.OPTIONAL, "Name", null, "string");
            aColumnSet.getColumnChoice ().add (aCodeColumn);
            aColumnSet.getColumnChoice ().add (aNameColumn);
            aColumnSet.getKeyChoice ().add (Genericode10Utils.createKey ("codeKey", "CodeKey", null, aCodeColumn));
            aGC.setColumnSet (aColumnSet);

            // Add values
            final SimpleCodeList aSimpleCodeList = aFactory.createSimpleCodeList ();
            int nRow = 4;
            while (!ODFUtils.isEmpty (aSheet, 0, nRow)) {
              final String sCode = ODFUtils.getText (aSheet, 0, nRow);
              final String sValue = ODFUtils.getText (aSheet, 1, nRow);

              final Row aRow = aFactory.createRow ();
              Value aValue = aFactory.createValue ();
              aValue.setColumnRef (aCodeColumn);
              aValue.setSimpleValue (Genericode10Utils.createSimpleValue (sCode));
              aRow.getValue ().add (aValue);

              aValue = aFactory.createValue ();
              aValue.setColumnRef (aNameColumn);
              aValue.setSimpleValue (Genericode10Utils.createSimpleValue (sValue));
              aRow.getValue ().add (aValue);

              aSimpleCodeList.getRow ().add (aRow);
              ++nRow;
            }
            aGC.setSimpleCodeList (aSimpleCodeList);

            if (new Genericode10CodeListMarshaller ().write (aGC, aGCFile).isFailure ())
              throw new IllegalStateException ("Failed to write " + aGCFile);
          }
        }

        final Table aCVASheet = aSpreadSheet.getSheetByName ("CVA");
        if (aCVASheet != null) {
          Utils.log ("  CVA codelist!");
          int nRow = 2;
          while (!ODFUtils.isEmpty (aCVASheet, 0, nRow)) {
            final String sTransaction = ODFUtils.getText (aCVASheet, 0, nRow);
            final String sID = ODFUtils.getText (aCVASheet, 1, nRow);
            final String sItem = ODFUtils.getText (aCVASheet, 2, nRow);
            final String sScope = ODFUtils.getText (aCVASheet, 3, nRow);
            final String sValue = ODFUtils.getText (aCVASheet, 4, nRow);
            final String sMessage = ODFUtils.getText (aCVASheet, 5, nRow);
            final String sSeverity = ODFUtils.getText (aCVASheet, 6, nRow);
            ++nRow;
          }
        }
      }
    }

    if (false) {
      // Create Schematron
      SchematronCreator.createSchematrons (aRuleSourceItems);

      // Now create the validation XSLTs
      XSLTCreator.createXSLTs (aRuleSourceItems);
    }

    Utils.log ("Finished building validation rules");
  }
}

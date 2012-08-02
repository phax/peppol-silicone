package at.peppol.validation.tools.codelist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.concurrent.Immutable;

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

import at.peppol.validation.tools.RuleSourceCodeList;
import at.peppol.validation.tools.RuleSourceItem;
import at.peppol.validation.tools.Utils;
import at.peppol.validation.tools.sch.odf.ODFUtils;

import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.genericode.Genericode10CodeListMarshaller;
import com.phloc.genericode.Genericode10Utils;

@Immutable
public final class CodeListCreator {
  private CodeListCreator () {}

  public static void createCodeLists (final List <RuleSourceItem> aRuleSourceItems) throws Exception {
    final ObjectFactory aFactory = new ObjectFactory ();
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      // Process all code lists
      for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ()) {
        Utils.log ("Reading code list file " + aCodeList.getSourceFile ());
        final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aCodeList.getSourceFile ());
        final List <File> aGCFiles = new ArrayList <File> ();
        // For all sheets
        for (int nSheetIndex = 0; nSheetIndex < aSpreadSheet.getSheetCount (); ++nSheetIndex) {
          final Table aSheet = aSpreadSheet.getSheetByIndex (nSheetIndex);
          final String sSheetName = aSheet.getTableName ();

          // Ignore CVA sheet
          if (!sSheetName.equals ("CVA")) {
            final File aGCFile = aCodeList.getGCFile (sSheetName);
            Utils.log ("  Creating codelist file " + aGCFile.getName ());
            final String sShortname = ODFUtils.getText (aSheet, 0, 1);
            final String sVersion = ODFUtils.getText (aSheet, 1, 1);
            final String sAgency = ODFUtils.getText (aSheet, 2, 1);
            final String sLocationURI = ODFUtils.getText (aSheet, 3, 1);
            // final String sLocale = _getText (aSheet, 4, 1);

            // Start creating Genericode
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
            aGCFiles.add (aGCFile);
          }
        }

        // Handle CVA sheets
        final Table aCVASheet = aSpreadSheet.getSheetByName ("CVA");
        if (aCVASheet != null) {
          Utils.log ("  Creating CVAs");
          int nRow = 2;
          final Map <String, CVA> aCVAs = new TreeMap <String, CVA> ();
          while (!ODFUtils.isEmpty (aCVASheet, 0, nRow)) {
            final String sTransaction = ODFUtils.getText (aCVASheet, 0, nRow);
            final String sID = ODFUtils.getText (aCVASheet, 1, nRow);
            final String sItem = ODFUtils.getText (aCVASheet, 2, nRow);
            final String sScope = ODFUtils.getText (aCVASheet, 3, nRow);
            final String sValue = ODFUtils.getText (aCVASheet, 4, nRow);
            final String sMessage = ODFUtils.getText (aCVASheet, 5, nRow);
            final String sSeverity = ODFUtils.getText (aCVASheet, 6, nRow);

            CVA aCVA = aCVAs.get (sTransaction);
            if (aCVA == null) {
              aCVA = new CVA (sTransaction, aGCFiles);
              aCVAs.put (sTransaction, aCVA);
            }
            aCVA.addContext (sID, sItem, sScope, sValue, sSeverity, sMessage);

            ++nRow;
          }

          // Start creating CVA files
          for (final CVA aCVA : aCVAs.values ()) {
            final File aCVAFile = aCodeList.getCVAFile (aCVA.getTransaction ());
            Utils.log ("    Creating " + aCVAFile);

            final IMicroDocument aDoc = new MicroDocument ();
            aDoc.appendComment ("This file is generated automatically! Do NOT edit!");
            aDoc.appendComment ("CVA file for " + aCodeList.getID () + " and transaction " + aCVA.getTransaction ());
          }
        }
      }
    }
  }
}

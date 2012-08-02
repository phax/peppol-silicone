package at.peppol.validation.tools.codelist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.oasis.cva.v10.Context;
import org.oasis.cva.v10.ContextValueAssociation;
import org.oasis.cva.v10.Contexts;
import org.oasis.cva.v10.Message;
import org.oasis.cva.v10.ValueList;
import org.oasis.cva.v10.ValueLists;
import org.oasis.genericode.v10.CodeListDocument;
import org.oasis.genericode.v10.Column;
import org.oasis.genericode.v10.ColumnSet;
import org.oasis.genericode.v10.Identification;
import org.oasis.genericode.v10.Row;
import org.oasis.genericode.v10.SimpleCodeList;
import org.oasis.genericode.v10.UseType;
import org.oasis.genericode.v10.Value;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.Document;

import at.peppol.validation.tools.RuleSourceItem;
import at.peppol.validation.tools.Utils;
import at.peppol.validation.tools.odf.ODFUtils;

import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.phloc.commons.xml.transform.DefaultTransformURIResolver;
import com.phloc.commons.xml.transform.TransformSourceFactory;
import com.phloc.commons.xml.transform.XMLTransformerFactory;
import com.phloc.cva.CVA10Marshaller;
import com.phloc.genericode.Genericode10CodeListMarshaller;
import com.phloc.genericode.Genericode10Utils;

@Immutable
public final class CodeListCreator {
  private CodeListCreator () {}

  private static void _createCVAandGC (final RuleSourceCodeList aCodeList, final List <String> aUsedTransaction) throws Exception {
    Utils.log ("Reading code list file " + aCodeList.getSourceFile ());
    final SpreadsheetDocument aSpreadSheet = SpreadsheetDocument.loadDocument (aCodeList.getSourceFile ());

    // Handle CVA sheets
    final Set <String> aAllReferencedCodeListNames = new HashSet <String> ();
    final Table aCVASheet = aSpreadSheet.getSheetByName ("CVA");
    Utils.log ("  Reading CVA data");
    int nRow = 2;
    final Map <String, CVAData> aCVAs = new TreeMap <String, CVAData> ();
    while (!ODFUtils.isEmpty (aCVASheet, 0, nRow)) {
      final String sTransaction = ODFUtils.getText (aCVASheet, 0, nRow);
      final String sID = ODFUtils.getText (aCVASheet, 1, nRow);
      final String sItem = ODFUtils.getText (aCVASheet, 2, nRow);
      final String sScope = ODFUtils.getText (aCVASheet, 3, nRow);
      final String sCodeListName = ODFUtils.getText (aCVASheet, 4, nRow);
      final String sMessage = ODFUtils.getText (aCVASheet, 5, nRow);
      final String sSeverity = ODFUtils.getText (aCVASheet, 6, nRow);

      // Save context per transaction
      CVAData aCVAData = aCVAs.get (sTransaction);
      if (aCVAData == null) {
        aCVAData = new CVAData (sTransaction);
        aCVAs.put (sTransaction, aCVAData);
      }
      aCVAData.addContext (sID, sItem, sScope, sCodeListName, sSeverity, sMessage);

      // Remember that we require a codelist
      aAllReferencedCodeListNames.add (sCodeListName);

      ++nRow;
    }

    // Start creating CVA files (for each transaction)
    for (final CVAData aCVAData : aCVAs.values ()) {
      final File aCVAFile = aCodeList.getCVAFile (aCVAData.getTransaction ());
      Utils.log ("    Creating " + aCVAFile.getName ());

      final org.oasis.cva.v10.ObjectFactory aFactory = new org.oasis.cva.v10.ObjectFactory ();
      final ContextValueAssociation aCVA = aFactory.createContextValueAssociation ();
      aCVA.setName (FilenameHelper.getBaseName (aCVAFile));

      // Create ValueLists
      final Map <String, ValueList> aValueListMap = new HashMap <String, ValueList> ();
      final ValueLists aValueLists = aFactory.createValueLists ();
      // Emit only the code lists, that are used in the contexts
      for (final String sCodeListName : aCVAData.getAllUsedCodeListNames ()) {
        final ValueList aValueList = aFactory.createValueList ();
        aValueList.setId (sCodeListName);
        aValueList.setUri (aCodeList.getGCFile (sCodeListName).getName ());
        aValueLists.getValueList ().add (aValueList);
        aValueListMap.put (aValueList.getId (), aValueList);
      }
      aCVA.setValueLists (aValueLists);

      // Create Contexts
      final Contexts aContexts = aFactory.createContexts ();
      for (final CVAContextData aCVAContextData : aCVAData.getAllContexts ()) {
        final Context aContext = aFactory.createContext ();
        aContext.setAddress (aCVAContextData.getItem ());
        aContext.getValues ().add (aValueListMap.get (aCVAContextData.getCodeListName ()));
        aContext.setMark (aCVAContextData.getSeverity ());
        final Message aMessage = aFactory.createMessage ();
        aMessage.getContent ().add ("[" + aCVAContextData.getID () + "]-" + aCVAContextData.getMessage ());
        aContext.getMessage ().add (aMessage);
        aContexts.getContext ().add (aContext);
      }
      aCVA.setContexts (aContexts);
      if (new CVA10Marshaller ().write (aCVA, aCVAFile).isFailure ())
        throw new IllegalStateException ("Failed to write " + aCVAFile);
      aUsedTransaction.add (aCVAData.getTransaction ());
    }

    // Create only the GC files that are referenced from the CVA sheet
    Utils.log ("  Reading codelists");
    for (final String sCodeListName : aAllReferencedCodeListNames) {
      final Table aSheet = aSpreadSheet.getSheetByName (sCodeListName);
      final File aGCFile = aCodeList.getGCFile (sCodeListName);
      Utils.log ("    Creating " + aGCFile.getName ());

      // Read data
      final String sShortname = ODFUtils.getText (aSheet, 0, 1);
      final String sVersion = ODFUtils.getText (aSheet, 1, 1);
      final String sAgency = ODFUtils.getText (aSheet, 2, 1);
      final String sLocationURI = ODFUtils.getText (aSheet, 3, 1);

      // Start creating Genericode
      final org.oasis.genericode.v10.ObjectFactory aFactory = new org.oasis.genericode.v10.ObjectFactory ();
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
      nRow = 4;
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

  private static Templates s_aCVA2SCH;

  private static void _createSchematronXSLTs (final RuleSourceCodeList aCodeList, final List <String> aUsedTransaction) throws TransformerException {
    Utils.log ("  Converting CVA files to Schematron XSLT");
    // Create only once
    if (s_aCVA2SCH == null) {
      final TransformerFactory aTF = XMLTransformerFactory.createTransformerFactory (null,
                                                                                     new DefaultTransformURIResolver ());
      s_aCVA2SCH = aTF.newTemplates (TransformSourceFactory.create (new File ("src/test/resources/rule-utils/Crane-cva2schXSLT.xsl")));
    }
    // Convert the CVA files for all transactions
    for (final String sTransaction : aUsedTransaction) {
      final File aCVAFile = aCodeList.getCVAFile (sTransaction);
      final File aResultXSLT = aCodeList.getXSLTFile (sTransaction);
      Utils.log ("    Creating " + aResultXSLT.getName ());
      final Transformer aTransformer = s_aCVA2SCH.newTransformer ();
      final Document aSCHDoc = XMLFactory.newDocument ();
      aTransformer.transform (TransformSourceFactory.create (aCVAFile), new DOMResult (aSCHDoc));
      SimpleFileIO.writeFile (aResultXSLT, XMLWriter.getXMLString (aSCHDoc), XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
    }
  }

  public static void createCodeLists (final List <RuleSourceItem> aRuleSourceItems) throws Exception {
    for (final RuleSourceItem aRuleSourceItem : aRuleSourceItems) {
      // Process all code lists
      for (final RuleSourceCodeList aCodeList : aRuleSourceItem.getAllCodeLists ()) {
        // Create .CVA and .GC files
        final List <String> aUsedTransaction = new ArrayList <String> ();
        _createCVAandGC (aCodeList, aUsedTransaction);

        // Convert CVAs to Schematron XSLTs
        _createSchematronXSLTs (aCodeList, aUsedTransaction);
      }
    }
  }
}

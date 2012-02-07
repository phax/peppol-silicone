/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oasis_open.docs.codelist.ns.genericode._1.CodeListDocument;
import org.oasis_open.docs.codelist.ns.genericode._1.Row;
import org.oasis_open.docs.codelist.ns.genericode._1.UseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.SimpleDocumentIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.identifier.actorid.IIdentifierIssuingAgency;
import at.peppol.commons.identifier.docid.IPEPPOLDocumentIdentifierParts;
import at.peppol.commons.identifier.docid.IPredefinedDocumentIdentifier;
import at.peppol.commons.identifier.docid.PEPPOLDocumentIdentifierParts;
import at.peppol.commons.identifier.procid.IPredefinedProcessIdentifier;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.version.Version;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.phloc.genericode.Genericode10Marshaller;
import com.phloc.genericode.excel.ExcelReadOptions;
import com.phloc.genericode.excel.ExcelSheetToCodeList;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;


/**
 * Utility class to create the Genericode files from the Excel code list. Also
 * creates Java source files with the predefined identifiers.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class CreateCodelistsFilesFromExcel {
  private static final Logger s_aLogger = LoggerFactory.getLogger (CreateCodelistsFilesFromExcel.class);
  private static final Version CODELIST_VERSION = new Version (1, 0, 2);
  private static final String EXCEL_FILE = "src/main/codelists/PEPPOL Code Lists 1.0.2.xls";
  private static final String SHEET_PARTICIPANT = "Participant";
  private static final String SHEET_DOCUMENT = "Document";
  private static final String SHEET_PROCESS = "Process";
  private static final String RESULT_DIRECTORY = "src/main/resources/codelists/";
  private static final JCodeModel s_aCodeModel = new JCodeModel ();
  private static JDefinedClass s_jEnumPredefinedDoc;

  private static void _writeGenericodeFile (final CodeListDocument aCodeList, final String sFileName) throws FileNotFoundException {
    final Document aDoc = Genericode10Marshaller.writeCodeList (aCodeList);
    if (aDoc == null)
      throw new IllegalStateException ("Failed to serialize code list");
    final FileOutputStream aFOS = new FileOutputStream (sFileName);
    if (XMLWriter.writeToStream (aDoc, aFOS, XMLWriterSettings.DEFAULT_XML_SETTINGS).isFailure ())
      throw new IllegalStateException ("Failed to write file " + sFileName);
    s_aLogger.info ("Wrote Genericode file " + sFileName);
  }

  private static void _emitIdentifierIssuingAgency (final Sheet aParticipantSheet) throws URISyntaxException,
                                                                                  FileNotFoundException,
                                                                                  UnsupportedEncodingException {
    final ExcelReadOptions aReadOptions = new ExcelReadOptions ().setLinesToSkip (1).setLineIndexShortName (0);
    aReadOptions.addColumn (0, "schemeid", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (1, "iso6523", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (2, "schemeagency", UseType.OPTIONAL, "string", false);
    aReadOptions.addColumn (3, "deprecated", UseType.REQUIRED, "boolean", false);
    final CodeListDocument aCodeList = ExcelSheetToCodeList.convertToSimpleCodeList (aParticipantSheet,
                                                                                     aReadOptions,
                                                                                     "PeppolIdentifierIssuingAgencies",
                                                                                     CODELIST_VERSION.getAsString (),
                                                                                     new URI ("urn:peppol.eu:names:identifier:issuingagencies"),
                                                                                     new URI ("urn:peppol.eu:names:identifier:issuingagencies-1.0"),
                                                                                     null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolIdentifierIssuingAgencies.gc");

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendComment ("This file was automatically generated. Do NOT edit!");
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
    for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
      final String sCode = GenericodeUtils.getRowValue (aRow, "schemeid");
      final String sAgency = GenericodeUtils.getRowValue (aRow, "schemeagency");
      final String sISO6523 = GenericodeUtils.getRowValue (aRow, "iso6523");
      final String sDeprecated = GenericodeUtils.getRowValue (aRow, "deprecated");
      final boolean bDeprecated = StringHelper.parseBool (sDeprecated, false);

      final IMicroElement eAgency = eRoot.appendElement ("issuingAgency");
      eAgency.setAttribute ("schemeid", sCode);
      eAgency.setAttribute ("agencyname", sAgency);
      eAgency.setAttribute ("iso6523", sISO6523);
      if (bDeprecated)
        eAgency.setAttribute ("deprecated", Boolean.TRUE.toString ());
    }
    final String sXML = MicroWriter.getXMLString (aDoc);
    SimpleFileIO.writeFile (new File (RESULT_DIRECTORY + "PeppolIdentifierIssuingAgencies.xml"),
                            sXML.getBytes (CCharset.CHARSET_UTF_8));

    // Create Java source
    try {
      final JDefinedClass jEnum = s_aCodeModel._package ("eu.peppol.busdox.identifier.actorid")
                                              ._enum ("EPredefinedIdentifierIssuingAgency")
                                              ._implements (IIdentifierIssuingAgency.class);
      jEnum.javadoc ().add ("This file is generated. Do NOT edit!");

      // enum constants
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
        final String sSchemeID = GenericodeUtils.getRowValue (aRow, "schemeid");
        final String sAgency = GenericodeUtils.getRowValue (aRow, "schemeagency");
        final String sISO6523 = GenericodeUtils.getRowValue (aRow, "iso6523");
        final String sDeprecated = GenericodeUtils.getRowValue (aRow, "deprecated");
        final boolean bDeprecated = StringHelper.parseBool (sDeprecated, false);

        if (StringHelper.hasNoText (sSchemeID))
          throw new IllegalArgumentException ("schemeID");
        if (sSchemeID.indexOf (' ') >= 0)
          throw new IllegalArgumentException ("Scheme IDs are not supposed to contain spaces!");
        if (StringHelper.hasNoText (sISO6523))
          throw new IllegalArgumentException ("ISO6523Code");
        if (!RegExHelper.stringMatchesPattern ("[0-9]{4}", sISO6523))
          throw new IllegalArgumentException ("The ISO 6523 code '" + sISO6523 + "' does not consist of 4 numbers");

        final JEnumConstant jEnumConst = jEnum.enumConstant (RegExHelper.makeIdentifier (sSchemeID));
        jEnumConst.arg (JExpr.lit (sSchemeID));
        jEnumConst.arg (sAgency == null ? JExpr._null () : JExpr.lit (sAgency));
        jEnumConst.arg (JExpr.lit (sISO6523));
        jEnumConst.arg (bDeprecated ? JExpr.TRUE : JExpr.FALSE);
      }

      // fields
      final JFieldVar fSchemeID = jEnum.field (JMod.PRIVATE, String.class, "m_sSchemeID");
      final JFieldVar fSchemeAgency = jEnum.field (JMod.PRIVATE, String.class, "m_sSchemeAgency");
      final JFieldVar fISO6523 = jEnum.field (JMod.PRIVATE, String.class, "m_sISO6523");
      final JFieldVar fDeprecated = jEnum.field (JMod.PRIVATE, boolean.class, "m_bDeprecated");

      // Constructor
      final JMethod jCtor = jEnum.constructor (JMod.PRIVATE);
      final JVar jSchemeID = jCtor.param (JMod.FINAL, String.class, "sSchemeID");
      jSchemeID.annotate (Nonnull.class);
      jSchemeID.annotate (Nonempty.class);
      final JVar jSchemeAgency = jCtor.param (JMod.FINAL, String.class, "sSchemeAgency");
      jSchemeAgency.annotate (Nullable.class);
      final JVar jISO6523 = jCtor.param (JMod.FINAL, String.class, "sISO6523");
      jISO6523.annotate (Nonnull.class);
      jISO6523.annotate (Nonempty.class);
      final JVar jDeprecated = jCtor.param (JMod.FINAL, boolean.class, "bDeprecated");
      jCtor.body ()
           .assign (fSchemeID, jSchemeID)
           .assign (fSchemeAgency, jSchemeAgency)
           .assign (fISO6523, jISO6523)
           .assign (fDeprecated, jDeprecated);

      JMethod m = jEnum.method (JMod.PUBLIC, String.class, "getSchemeID");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fSchemeID);

      m = jEnum.method (JMod.PUBLIC, String.class, "getSchemeAgency");
      m.annotate (Nullable.class);
      m.body ()._return (fSchemeAgency);

      m = jEnum.method (JMod.PUBLIC, String.class, "getISO6523Code");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fISO6523);

      final JMethod mCreateIdentifierValue = jEnum.method (JMod.PUBLIC, String.class, "createIdentifierValue");
      mCreateIdentifierValue.annotate (Nonnull.class);
      mCreateIdentifierValue.annotate (Nonempty.class);
      JVar jValue = mCreateIdentifierValue.param (JMod.FINAL, String.class, "sIdentifier");
      jValue.annotate (Nonnull.class);
      jValue.annotate (Nonempty.class);
      mCreateIdentifierValue.body ()._return (fISO6523.plus (JExpr.lit (":")).plus (jValue));

      m = jEnum.method (JMod.PUBLIC, SimpleParticipantIdentifier.class, "createParticipantIdentifier");
      m.annotate (Nonnull.class);
      jValue = m.param (JMod.FINAL, String.class, "sIdentifier");
      jValue.annotate (Nonnull.class);
      jValue.annotate (Nonempty.class);
      m.body ()._return (s_aCodeModel.ref (SimpleParticipantIdentifier.class)
                                     .staticInvoke ("createWithDefaultScheme")
                                     .arg (JExpr.invoke (mCreateIdentifierValue).arg (jValue)));

      m = jEnum.method (JMod.PUBLIC, boolean.class, "isDeprecated");
      m.body ()._return (fDeprecated);
    }
    catch (final Exception ex) {
      s_aLogger.warn ("Failed to create source", ex);
    }
  }

  private static void _emitDocumentIdentifiers (final Sheet aDocumentSheet) throws URISyntaxException,
                                                                           FileNotFoundException,
                                                                           UnsupportedEncodingException {
    final ExcelReadOptions aReadOptions = new ExcelReadOptions ().setLinesToSkip (1).setLineIndexShortName (0);
    aReadOptions.addColumn (0, "name", UseType.OPTIONAL, "string", false);
    aReadOptions.addColumn (1, "docid", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (2, "specurl", UseType.OPTIONAL, "string", false);
    final CodeListDocument aCodeList = ExcelSheetToCodeList.convertToSimpleCodeList (aDocumentSheet,
                                                                                     aReadOptions,
                                                                                     "PeppolDocumentIdentifier",
                                                                                     CODELIST_VERSION.getAsString (),
                                                                                     new URI ("urn:peppol.eu:names:identifier:document"),
                                                                                     new URI ("urn:peppol.eu:names:identifier:document-1.0"),
                                                                                     null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolDocumentIdentifier.gc");

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendComment ("This file was automatically generated. Do NOT edit!");
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
    for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
      final String sDocID = GenericodeUtils.getRowValue (aRow, "docid");
      final String sName = GenericodeUtils.getRowValue (aRow, "name");

      final IMicroElement eAgency = eRoot.appendElement ("document");
      eAgency.setAttribute ("id", sDocID);
      eAgency.setAttribute ("name", sName);
    }
    final String sXML = MicroWriter.getXMLString (aDoc);
    SimpleFileIO.writeFile (new File (RESULT_DIRECTORY + "PeppolDocumentIdentifier.xml"),
                            sXML.getBytes (CCharset.CHARSET_UTF_8));

    // Create Java source
    try {
      s_jEnumPredefinedDoc = s_aCodeModel._package ("eu.peppol.busdox.identifier.docid")
                                         ._enum ("EPredefinedDocumentIdentifier")
                                         ._implements (IPredefinedDocumentIdentifier.class);
      s_jEnumPredefinedDoc.javadoc ().add ("This file is generated. Do NOT edit!");

      // Add all enum constants
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
        final String sDocID = GenericodeUtils.getRowValue (aRow, "docid");
        final String sName = GenericodeUtils.getRowValue (aRow, "name");

        // Split ID in it's pieces
        final IPEPPOLDocumentIdentifierParts aDocIDParts = PEPPOLDocumentIdentifierParts.extractFromString (sDocID);

        // Assemble extensions
        final JInvocation jExtensions = s_aCodeModel.ref (ContainerHelper.class).staticInvoke ("newList");
        for (final String sExtensionID : aDocIDParts.getExtensionIDs ())
          jExtensions.arg (JExpr.lit (sExtensionID));

        final JEnumConstant jEnumConst = s_jEnumPredefinedDoc.enumConstant (RegExHelper.makeIdentifier (sDocID));
        jEnumConst.arg (JExpr._new (s_aCodeModel.ref (PEPPOLDocumentIdentifierParts.class))
                             .arg (JExpr.lit (aDocIDParts.getRootNS ()))
                             .arg (JExpr.lit (aDocIDParts.getLocalName ()))
                             .arg (JExpr.lit (aDocIDParts.getTransactionID ()))
                             .arg (jExtensions)
                             .arg (JExpr.lit (aDocIDParts.getVersion ())));
        jEnumConst.arg (JExpr.lit (sName));
        jEnumConst.javadoc ().add (sDocID);
      }

      // fields
      final JFieldVar fParts = s_jEnumPredefinedDoc.field (JMod.PRIVATE,
                                                           IPEPPOLDocumentIdentifierParts.class,
                                                           "m_aParts");
      final JFieldVar fID = s_jEnumPredefinedDoc.field (JMod.PRIVATE, String.class, "m_sID");
      final JFieldVar fCommonName = s_jEnumPredefinedDoc.field (JMod.PRIVATE, String.class, "m_sCommonName");

      // Constructor
      final JMethod jCtor = s_jEnumPredefinedDoc.constructor (JMod.PRIVATE);
      final JVar jParts = jCtor.param (JMod.FINAL, IPEPPOLDocumentIdentifierParts.class, "aParts");
      jParts.annotate (Nonnull.class);
      final JVar jCommonName = jCtor.param (JMod.FINAL, String.class, "sCommonName");
      jCommonName.annotate (Nonnull.class);
      jCommonName.annotate (Nonempty.class);
      jCtor.body ()
           .assign (fParts, jParts)
           .assign (fCommonName, jCommonName)
           .assign (fID, fParts.invoke ("getAsDocumentIdentifierValue"));

      JMethod m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getScheme");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (s_aCodeModel.ref (CIdentifier.class).staticRef ("DEFAULT_DOCUMENT_IDENTIFIER_SCHEME"));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getValue");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fID);

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getRootNS");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getLocalName");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getSubTypeIdentifier");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getTransactionID");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC,
                                       s_aCodeModel.ref (List.class).narrow (String.class),
                                       "getExtensionIDs");
      m.annotate (Nonnull.class);
      m.annotate (ReturnsMutableCopy.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getAsUBLCustomizationID");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getVersion");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fParts.invoke (m.name ()));

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getCommonName");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fCommonName);

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, String.class, "getAsDocumentIdentifierValue");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fID);

      m = s_jEnumPredefinedDoc.method (JMod.PUBLIC, SimpleDocumentIdentifier.class, "getAsDocumentIdentifier");
      m.annotate (Nonnull.class);
      m.body ()._return (JExpr._new (s_aCodeModel.ref (SimpleDocumentIdentifier.class)).arg (JExpr._this ()));
    }
    catch (final Exception ex) {
      s_aLogger.warn ("Failed to create source", ex);
    }
  }

  private static void _emitProcessIdentifier (final Sheet aProcessSheet) throws URISyntaxException,
                                                                        FileNotFoundException,
                                                                        UnsupportedEncodingException {
    final ExcelReadOptions aReadOptions = new ExcelReadOptions ().setLinesToSkip (1).setLineIndexShortName (0);
    aReadOptions.addColumn (0, "name", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (1, "id", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (2, "bisid", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (3, "docids", UseType.OPTIONAL, "string", false);
    final CodeListDocument aCodeList = ExcelSheetToCodeList.convertToSimpleCodeList (aProcessSheet,
                                                                                     aReadOptions,
                                                                                     "PeppolProcessIdentifier",
                                                                                     CODELIST_VERSION.getAsString (),
                                                                                     new URI ("urn:peppol.eu:names:identifier:process"),
                                                                                     new URI ("urn:peppol.eu:names:identifier:process-1.0"),
                                                                                     null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolProcessIdentifier.gc");

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendComment ("This file was automatically generated. Do NOT edit!");
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
    for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
      final String sID = GenericodeUtils.getRowValue (aRow, "id");
      final String sBISID = GenericodeUtils.getRowValue (aRow, "bisid");
      final String sDocIDs = GenericodeUtils.getRowValue (aRow, "docids");
      // Split the document identifier string into a list of single strings,
      // and
      // check if each of them is a valid predefined document identifier
      final List <String> aDocIDs = RegExHelper.splitToList (sDocIDs, "\n");
      final IMicroElement eAgency = eRoot.appendElement ("process");
      eAgency.setAttribute ("id", sID);
      eAgency.setAttribute ("bisid", sBISID);
      for (final String sDocID : aDocIDs)
        eAgency.appendElement ("document").setAttribute ("id", sDocID);
    }
    final String sXML = MicroWriter.getXMLString (aDoc);
    SimpleFileIO.writeFile (new File (RESULT_DIRECTORY + "PeppolProcessIdentifier.xml"),
                            sXML.getBytes (CCharset.CHARSET_UTF_8));

    // Create Java source
    try {
      final JDefinedClass jEnum = s_aCodeModel._package ("eu.peppol.busdox.identifier.procid")
                                              ._enum ("EPredefinedProcessIdentifier")
                                              ._implements (IPredefinedProcessIdentifier.class);
      jEnum.javadoc ().add ("This file is generated. Do NOT edit!");

      // enum constants
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ()) {
        final String sID = GenericodeUtils.getRowValue (aRow, "id");
        final String sBISID = GenericodeUtils.getRowValue (aRow, "bisid");
        final String sDocIDs = GenericodeUtils.getRowValue (aRow, "docids");

        final JEnumConstant jEnumConst = jEnum.enumConstant (RegExHelper.makeIdentifier (sID));
        jEnumConst.arg (JExpr.lit (sID));
        jEnumConst.arg (JExpr.lit (sBISID));
        final JArray jArray = JExpr.newArray (s_jEnumPredefinedDoc);
        for (final String sDocID : RegExHelper.splitToList (sDocIDs, "\n"))
          jArray.add (s_jEnumPredefinedDoc.staticRef (RegExHelper.makeIdentifier (sDocID)));
        jEnumConst.arg (jArray);
        jEnumConst.javadoc ().add (sID);
      }

      // fields
      final JFieldVar fID = jEnum.field (JMod.PRIVATE, String.class, "m_sID");
      final JFieldVar fBISID = jEnum.field (JMod.PRIVATE, String.class, "m_sBSIID");
      final JFieldVar fDocIDs = jEnum.field (JMod.PRIVATE, s_jEnumPredefinedDoc.array (), "m_aDocIDs");

      // Constructor
      final JMethod jCtor = jEnum.constructor (JMod.PRIVATE);
      final JVar jID = jCtor.param (JMod.FINAL, String.class, "sID");
      jID.annotate (Nonnull.class);
      jID.annotate (Nonempty.class);
      final JVar jBISID = jCtor.param (JMod.FINAL, String.class, "sBISID");
      jBISID.annotate (Nonnull.class);
      jBISID.annotate (Nonempty.class);
      final JVar jDocIDs = jCtor.param (JMod.FINAL, s_jEnumPredefinedDoc.array (), "aDocIDs");
      jDocIDs.annotate (Nonnull.class);
      jDocIDs.annotate (Nonempty.class);
      jCtor.body ().assign (fID, jID).assign (fBISID, jBISID).assign (fDocIDs, jDocIDs);

      JMethod m = jEnum.method (JMod.PUBLIC, String.class, "getScheme");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (s_aCodeModel.ref (CIdentifier.class).staticRef ("DEFAULT_PROCESS_IDENTIFIER_SCHEME"));

      m = jEnum.method (JMod.PUBLIC, String.class, "getValue");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fID);

      m = jEnum.method (JMod.PUBLIC, String.class, "getBISID");
      m.annotate (Nonnull.class);
      m.annotate (Nonempty.class);
      m.body ()._return (fBISID);

      m = jEnum.method (JMod.PUBLIC,
                        s_aCodeModel.ref (List.class).narrow (s_aCodeModel.ref (IPredefinedDocumentIdentifier.class)
                                                                          .wildcard ()),
                        "getDocumentIdentifiers");
      m.annotate (Nonnull.class);
      m.annotate (ReturnsMutableCopy.class);
      m.body ()._return (s_aCodeModel.ref (ContainerHelper.class).staticInvoke ("newList").arg (fDocIDs));

      m = jEnum.method (JMod.PUBLIC, SimpleProcessIdentifier.class, "getAsProcessIdentifier");
      m.annotate (Nonnull.class);
      m.body ()._return (JExpr._new (s_aCodeModel.ref (SimpleProcessIdentifier.class)).arg (JExpr._this ()));
    }
    catch (final Exception ex) {
      s_aLogger.warn ("Failed to create source", ex);
    }
  }

  public static void main (final String [] args) throws IOException, URISyntaxException {
    // Where is the Excel?
    final IReadableResource aXls = new FileSystemResource (EXCEL_FILE);
    if (!aXls.exists ())
      throw new IllegalStateException ("The Excel file could not be found!");

    // Interprete as Excel
    final Workbook aWB = new HSSFWorkbook (aXls.getInputStream ());

    // Check whether all required sheets are present
    final Sheet aParticipantSheet = aWB.getSheet (SHEET_PARTICIPANT);
    if (aParticipantSheet == null)
      throw new IllegalStateException ("The " + SHEET_PARTICIPANT + " sheet could not be found!");
    final Sheet aDocumentSheet = aWB.getSheet (SHEET_DOCUMENT);
    if (aDocumentSheet == null)
      throw new IllegalStateException ("The " + SHEET_DOCUMENT + " sheet could not be found!");
    final Sheet aProcessSheet = aWB.getSheet (SHEET_PROCESS);
    if (aProcessSheet == null)
      throw new IllegalStateException ("The " + SHEET_PROCESS + " sheet could not be found!");

    // Convert participants
    _emitIdentifierIssuingAgency (aParticipantSheet);

    // Convert document identifiers
    _emitDocumentIdentifiers (aDocumentSheet);

    // Convert processes identifiers
    _emitProcessIdentifier (aProcessSheet);

    // Write all Java source files
    s_aCodeModel.build (new File ("src/main/java"));
  }
}

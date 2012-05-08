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
/**
 * Copyright (C) 2006-2012 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.peppol.commons.tools;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.oasis_open.docs.codelist.ns.genericode._1.CodeListDocument;
import org.oasis_open.docs.codelist.ns.genericode._1.Column;
import org.oasis_open.docs.codelist.ns.genericode._1.Row;
import org.oasis_open.docs.codelist.ns.genericode._1.SimpleCodeList;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.io.file.filter.FilenameFilterFactory;
import com.phloc.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.lang.EnumHelper;
import com.phloc.commons.name.IHasDisplayName;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.genericode.Genericode10CodeListMarshaller;
import com.phloc.genericode.Genericode10Utils;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;

public class MainCreateEnumsGenericode {
  private static final String COLID_NAME = "name";
  private static final String COLID_CODE = "code";
  private static final JCodeModel s_aCodeModel = new JCodeModel ();

  private static void _createGenericode10 (final File aFile, final CodeListDocument aCodeList10) throws JClassAlreadyExistsException {
    final SimpleCodeList aSimpleCodeList = aCodeList10.getSimpleCodeList ();
    if (aSimpleCodeList == null) {
      System.out.println ("  does not contain a SimpleCodeList!");
      return;
    }
    final Column aColCode = Genericode10Utils.getColumnOfID (aCodeList10.getColumnSet (), COLID_CODE);
    if (aColCode == null) {
      System.out.println ("  No '" + COLID_CODE + "' column found");
      return;
    }
    if (!Genericode10Utils.isKeyColumn (aCodeList10.getColumnSet (), COLID_CODE)) {
      System.out.println ("  Column '" + COLID_CODE + "' is not a key");
      return;
    }
    final Column aColName = Genericode10Utils.getColumnOfID (aCodeList10.getColumnSet (), COLID_NAME);
    if (aColName == null) {
      System.out.println ("  No '" + COLID_NAME + "' column found");
      return;
    }

    final JDefinedClass jEnum = s_aCodeModel._package ("at.peppol.commons.codelist")
                                            ._enum ("E" +
                                                    RegExHelper.makeIdentifier (aCodeList10.getIdentification ()
                                                                                           .getShortName ()
                                                                                           .getValue ()))
                                            ._implements (s_aCodeModel.ref (IHasID.class).narrow (String.class))
                                            ._implements (IHasDisplayName.class);
    jEnum.javadoc ().add ("This file is generated from Genericode file " + aFile.getName () + ". Do NOT edit!");

    for (final Row aRow : aCodeList10.getSimpleCodeList ().getRow ()) {
      final String sCode = Genericode10Utils.getRowValue (aRow, COLID_CODE);
      final String sName = Genericode10Utils.getRowValue (aRow, COLID_NAME);

      final String sIdentifier = RegExHelper.makeIdentifier (sName.toUpperCase (Locale.US)).replaceAll ("__", "_");
      final JEnumConstant jEnumConst = jEnum.enumConstant (sIdentifier);
      jEnumConst.arg (JExpr.lit (sCode));
      jEnumConst.arg (JExpr.lit (sName));
    }

    // fields
    final JFieldVar fID = jEnum.field (JMod.PRIVATE | JMod.FINAL, String.class, "m_sID");
    final JFieldVar fDisplayName = jEnum.field (JMod.PRIVATE | JMod.FINAL, String.class, "m_sDisplayName");

    // Constructor
    final JMethod jCtor = jEnum.constructor (JMod.PRIVATE);
    JVar jID = jCtor.param (JMod.FINAL, String.class, "sID");
    jID.annotate (Nonnull.class);
    jID.annotate (Nonempty.class);
    final JVar jDisplayName = jCtor.param (JMod.FINAL, String.class, "sDisplayName");
    jDisplayName.annotate (Nonnull.class);
    jCtor.body ().assign (fID, jID).assign (fDisplayName, jDisplayName);

    // public String getID ()
    JMethod m = jEnum.method (JMod.PUBLIC, String.class, "getID");
    m.annotate (Nonnull.class);
    m.annotate (Nonempty.class);
    m.body ()._return (fID);

    // public String getDisplayName ()
    m = jEnum.method (JMod.PUBLIC, String.class, "getDisplayName");
    m.annotate (Nonnull.class);
    m.body ()._return (fDisplayName);

    // public static E... getFromIDOrNull (@Nullable String sID)
    m = jEnum.method (JMod.PUBLIC | JMod.STATIC, jEnum, "getFromIDOrNull");
    m.annotate (Nullable.class);
    jID = m.param (JMod.FINAL, String.class, "sID");
    jID.annotate (Nullable.class);
    m.body ()._return (s_aCodeModel.ref (EnumHelper.class)
                                   .staticInvoke ("getFromIDOrNull")
                                   .arg (JExpr.dotclass (jEnum))
                                   .arg (jID));

    // public static String getDisplayNameFromIDOrNull (@Nullable String sID)
    m = jEnum.method (JMod.PUBLIC | JMod.STATIC, String.class, "getDisplayNameFromIDOrNull");
    m.annotate (Nullable.class);
    jID = m.param (JMod.FINAL, String.class, "sID");
    jID.annotate (Nullable.class);
    final JVar jValue = m.body ().decl (JMod.FINAL, jEnum, "eValue", jEnum.staticInvoke ("getFromIDOrNull").arg (jID));
    m.body ()._return (JOp.cond (JOp.eq (jValue, JExpr._null ()), JExpr._null (), jValue.invoke ("getDisplayName")));
  }

  public static void main (final String [] args) throws JClassAlreadyExistsException, IOException {
    for (final File aFile : FileSystemRecursiveIterator.create (new File ("src/main/resources/codelists/ubl"),
                                                                FilenameFilterFactory.getEndsWithFilter (".gc"))) {
      System.out.println (aFile.getName ());
      final CodeListDocument aCodeList10 = new Genericode10CodeListMarshaller ().read (new FileSystemResource (aFile));
      if (aCodeList10 != null)
        _createGenericode10 (aFile, aCodeList10);
    }
    s_aCodeModel.build (new File ("src/main/java"));
  }
}

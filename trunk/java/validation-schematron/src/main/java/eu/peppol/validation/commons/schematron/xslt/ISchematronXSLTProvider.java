/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
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

package eu.peppol.validation.commons.schematron.xslt;

import javax.annotation.Nullable;
import javax.xml.transform.Templates;

import org.w3c.dom.Document;

/**
 * Interface for a factory creating Schematron validators from XSLT. Sometimes
 * the pre-built XSLTs are already available, and sometimes they need to be
 * built from the underlying Schematron file.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface ISchematronXSLTProvider {
  /**
   * @return <code>true</code> if the Schematron was successfully interpreted,
   *         <code>false</code> otherwise.
   */
  boolean isValidSchematron ();

  /**
   * @return The produced XSLT document from the Schematron document or
   *         <code>null</code> if preprocessing failed.
   * @see #isValidSchematron()
   */
  @Nullable
  Document getXSLTDocument ();

  /**
   * @return The precompiled XSLT template to be used.
   */
  @Nullable
  Templates getXSLTTemplates ();
}

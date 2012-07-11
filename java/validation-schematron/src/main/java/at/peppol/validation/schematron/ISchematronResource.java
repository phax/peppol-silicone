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
package at.peppol.validation.schematron;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import org.w3c.dom.Document;

import at.peppol.validation.schematron.svrl.SVRLReader;

import com.phloc.commons.id.IHasID;
import com.phloc.commons.io.IReadableResource;

/**
 * Base interface for a Schematron resource. The implementation can e.g. be a
 * SCH file that needs preprocessing to XSLT or an already precompiled XSLT
 * file.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface ISchematronResource extends IHasID <String> {
  /**
   * @return The non-<code>null</code> resource from which to read the
   *         Schematron rules.
   */
  @Nonnull
  IReadableResource getResource ();

  /**
   * @return <code>true</code> if this Schematron can be used to validate XML
   *         instances. If not, the Schematron is invalid and the log files must
   *         be investigated.
   */
  boolean isValidSchematron ();

  /**
   * Apply the schematron validation on the passed XML resource and return an
   * SVRL XML document.
   * 
   * @param aXMLResource
   *        The XML resource to validate via Schematron. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the passed resource does not exist or the non-
   *         <code>null</code> SVRL document otherwise.
   * @throws Exception
   *         In case the transformation somehow goes wrong.
   * @see SVRLReader#readXML(org.w3c.dom.Node) on how to convert the document
   *      into a domain object
   */
  @Nullable
  Document applySchematronValidation (@Nonnull IReadableResource aXMLResource) throws Exception;

  /**
   * Apply the schematron validation on the passed XML source and return an SVRL
   * XML document.
   * 
   * @param aXMLSource
   *        The XML source to validate via Schematron. May not be
   *        <code>null</code>.
   * @return The SVRL XML document containing the result. May be
   *         <code>null</code> when interpreting the Schematron failed.
   * @throws Exception
   *         In case the transformation somehow goes wrong.
   * @see SVRLReader#readXML(org.w3c.dom.Node) on how to convert the document
   *      into a domain object
   */
  @Nullable
  Document applySchematronValidation (@Nonnull Source aXMLSource) throws Exception;
}

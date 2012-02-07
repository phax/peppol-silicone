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
package eu.peppol.validation.commons.schematron.svrl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.IsSPIInterface;

/**
 * Implement this SPI interface to beautify SVRL error messages by replacing
 * namespaceURLs with common prefixes.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@IsSPIInterface
public interface ISVRLLocationBeautifierSPI {
  /**
   * Beautify the passed combination.
   *
   * @param sNamespaceURI
   *        Namespace URI
   * @param sLocalName
   *        Element local name
   * @return <code>null</code> to indicate that this object does not know how to
   *         handle the namespace. Otherwise the replacement text should be
   *         returned.<br>
   *         Example for UBL: Input parameters
   *         <code>urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2</code>
   *         and <code>Party</code>. The result may be <code>cac:Party</code> as
   *         <i>cac</i> is the common namespace prefix for the passed namespace
   *         URI.
   */
  @Nullable
  String getReplacementText (@Nonnull String sNamespaceURI, @Nonnull String sLocalName);
}

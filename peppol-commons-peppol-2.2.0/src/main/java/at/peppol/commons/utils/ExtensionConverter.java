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
package at.peppol.commons.utils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.busdox.servicemetadata.publishing._1.ExtensionType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.phloc.commons.xml.serialize.EXMLSerializeDocType;
import com.phloc.commons.xml.serialize.EXMLSerializeIndent;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * This class is used for converting between a String representation of the
 * extension element and the "ExtensionType" complex type.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ExtensionConverter {
  private ExtensionConverter () {}

  /**
   * Convert the passed extension type to a string representation.
   * 
   * @param aExtension
   *        The extension to be converted. May be <code>null</code>.
   * @return <code>null</code> if no extension was passed - the XML
   *         representation of the extension otherwise.
   */
  @Nullable
  public static String convert (@Nullable final ExtensionType aExtension) {
    // If there is no extension present, nothing to convert
    if (aExtension == null)
      return null;

    // Get the extension content
    final Object aExtensionElement = aExtension.getAny ();
    if (aExtensionElement == null)
      return null;

    // Handle nodes directly
    if (aExtensionElement instanceof Node)
      return XMLWriter.getNodeAsString ((Node) aExtensionElement,
                                        new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE)
                                                                .setIndent (EXMLSerializeIndent.NONE));

    // FIXME the extension may contain multiple elements (e.g. lists)
    throw new IllegalArgumentException ("Don't know how to convert the extension element of type " +
                                        aExtension.getClass ().getName ());
  }

  /**
   * Convert the passed XML string to an extension.
   * 
   * @param sXML
   *        the XML representation to be converted.
   * @return <code>null</code> if the passed XML could not be converted to an
   *         extension object.
   */
  @Nullable
  public static ExtensionType convert (@Nullable final String sXML) {
    if (sXML != null) {
      try {
        // Try to interprete as XML
        final Document aDoc = XMLReader.readXMLDOM (sXML);
        if (aDoc != null) {
          final ExtensionType aExtension = new ObjectFactory ().createExtensionType ();
          aExtension.setAny (aDoc.getDocumentElement ());
          return aExtension;
        }
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException ("Error in parsing extension XML", ex);
      }
    }

    return null;
  }
}

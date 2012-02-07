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
package org.busdox.transport.lime.soapheader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
final class HeaderParser {
  private HeaderParser () {}

  public static String [] getTextNodes (final XMLStreamReader xmlr) {
    final XMLInputFactory xmlif = XMLInputFactory.newInstance ();
    xmlif.setProperty (XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
    xmlif.setProperty (XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    xmlif.setProperty (XMLInputFactory.IS_COALESCING, Boolean.FALSE);

    final List <String> textNodes = new ArrayList <String> ();

    try {
      while (xmlr.hasNext ()) {
        final String textNode = getText (xmlr);
        if (textNode != null) {
          textNodes.add (textNode);
        }
        xmlr.next ();
      }
    }
    catch (final XMLStreamException ex) {
      if (ex.getNestedException () != null) {
        ex.getNestedException ().printStackTrace ();
      }
    }
    return textNodes.toArray (new String [] {});
  }

  private static String getText (final XMLStreamReader xmlr) {
    return xmlr.hasText () ? xmlr.getText () : null;
  }
}

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
package at.peppol.transport.lime.server;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.peppol.transport.lime.Identifiers;
import at.peppol.transport.lime.soapheader.SOAPHelper;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class SoapResponseHeaderHandler implements SOAPHandler <SOAPMessageContext> {

  public boolean handleMessage (final SOAPMessageContext messageContext) {
    final SOAPMessage msg = messageContext.getMessage ();

    if (SOAPHelper.isOutboundMessage (messageContext)) {
      try {
        final SOAPEnvelope envelope = msg.getSOAPPart ().getEnvelope ();
        final NodeList aChildNodes = envelope.getBody ().getChildNodes ();
        if (aChildNodes != null &&
            aChildNodes.item (0).getChildNodes () != null &&
            aChildNodes.item (0).getChildNodes ().item (0) != null &&
            aChildNodes.item (0).getChildNodes ().item (0).getNodeName ().indexOf ("Headers") >= 0) {
          final SOAPHeader header = attachIncommingHeaders (envelope);
          moveHeaderFromBodyToSoapHeader (envelope, header);
        }
        msg.saveChanges ();
      }
      catch (final SOAPException e) {
        e.printStackTrace ();
        return false;
      }
    }
    return true;
  }

  public boolean handleFault (final SOAPMessageContext messageContext) {
    return true;
  }

  public void close (final MessageContext messageContext) {}

  public Set <QName> getHeaders () {
    return null;
  }

  private static SOAPHeader attachIncommingHeaders (final SOAPEnvelope envelope) throws SOAPException {
    SOAPHeader header = null;
    if (envelope.getHeader () != null) {
      final Iterator <?> iter = envelope.getHeader ().extractAllHeaderElements ();
      envelope.getHeader ().detachNode ();
      header = envelope.addHeader ();
      SOAPHeaderElement soapHeaderElement = null;
      for (; iter.hasNext ();) {
        soapHeaderElement = (SOAPHeaderElement) iter.next ();
        header.addChildElement ((SOAPHeaderElement) soapHeaderElement.cloneNode (true));
      }
    }
    else {
      header = envelope.addHeader ();
    }
    return header;
  }

  private static void moveHeaderFromBodyToSoapHeader (final SOAPEnvelope envelope, final SOAPHeader header) throws DOMException,
                                                                                                           SOAPException {
    final Node node = envelope.getBody ().getChildNodes ().item (0).getChildNodes ().item (0);
    for (int i = 0; i < node.getChildNodes ().getLength (); i++) {
      final Node childNode = node.getChildNodes ().item (i);
      final SOAPElement soapElement = header.addHeaderElement (envelope.createName (childNode.getLocalName (),
                                                                                    "",
                                                                                    childNode.getNamespaceURI ()));
      if (childNode.getChildNodes () != null && childNode.getChildNodes ().getLength () > 0) {
        for (int j = 0; j < childNode.getChildNodes ().getLength (); j++) {
          final Node curChildNode = childNode.getChildNodes ().item (j);
          soapElement.appendChild (curChildNode.cloneNode (true));
        }
      }
      final NamedNodeMap attributes = childNode.getAttributes ();
      if (attributes != null) {
        for (int a = 0; a < attributes.getLength (); a++) {
          if (attributes.item (a).getLocalName ().equals (Identifiers.SCHEME_ATTR)) {
            soapElement.setAttribute (attributes.item (a).getLocalName (), attributes.item (a).getNodeValue ());
          }
        }
      }
    }
    envelope.getBody ().getChildNodes ().item (0).removeChild (node);
  }
}

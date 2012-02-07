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
package org.busdox.transport.lime.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime.EndpointReferenceInterface;
import org.busdox.transport.lime.Identifiers;
import org.busdox.transport.lime.MessageException;
import org.busdox.transport.lime.MessageInterface;
import org.busdox.transport.lime.OutboxInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;

import com.phloc.commons.string.StringHelper;


/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class Outbox implements OutboxInterface {
  private static final Logger log = LoggerFactory.getLogger (Outbox.class);

  public String sendMessage (final IReadonlyUsernamePWCredentials credentials,
                             final MessageInterface message,
                             final EndpointReferenceInterface endpointReferenceInterface) throws MessageException {
    checkCredentials (credentials);

    try {
      Resource port = new WebservicePort ().getServicePort (endpointReferenceInterface.getAddress (),
                                                            credentials.getUsername (),
                                                            credentials.getPassword ());
      // new SoapHeaderMapper().setupHandlerChain((BindingProvider) port,
      // message.getSender(), message.getReciever(), message.getDocumentType(),
      // message.getProcessType(), endpointReferenceInterface.getChannelID(),
      // null, null);
      final Create create = new Create ();

      /*
       * final ParticipantIdentifierType sender = new
       * SimpleParticipantIdentifier (message.getSender ()); final
       * ParticipantIdentifierType reciever = new SimpleParticipantIdentifier
       * (message.getReciever ()); final DocumentIdentifierType documentType =
       * new SimpleDocumentIdentifier (message.getDocumentType ());
       */
      final ProcessIdentifierType processType = new ProcessIdentifierType ();
      if (message.getProcessType () != null && StringHelper.hasTextAfterTrim (message.getProcessType ().getValue ())) {
        processType.setValue (message.getProcessType ().getValue ());
        processType.setScheme (message.getProcessType ().getScheme ());
      }
      else {
        processType.setValue (Identifiers.BUSDOX_NO_PROCESS);
        processType.setScheme (Identifiers.BUSDOX_PROCID_TRANSPORT);
      }

      final CreateResponse createResponse = port.create (create);
      /**
       * FIXME correct service call!<br>
       * null, endpointReferenceInterface.getChannelID (), reciever, sender,
       * documentType, processType);
       */
      // CreateResponse createResponse = port.create(create, null, null, null,
      // null, null, null);
      // CreateResponse createResponse = port.create(create);

      final Document endpointDoc = getEndpointReferenceDocument (createResponse);
      final XPath xpath = XPathFactory.newInstance ().newXPath ();
      final String endpointAddress = getNodeValue (xpath, "/EndpointReference/Address/text()", endpointDoc);
      final String channelID = getNodeValue (xpath,
                                             "/EndpointReference/ReferenceParameters/ChannelIdentifier/text()",
                                             endpointDoc);
      final String messageID = getNodeValue (xpath,
                                             "/EndpointReference/ReferenceParameters/MessageIdentifier/text()",
                                             endpointDoc);

      port = new WebservicePort ().getServicePort (endpointAddress,
                                                   credentials.getUsername (),
                                                   credentials.getPassword ());
      new SoapHeaderMapper ().setupHandlerChain ((BindingProvider) port, channelID, messageID);

      final Put put = new Put ();
      final List <Object> objects = put.getAny ();
      objects.add (message.getDocument ().getDocumentElement ());
      port.put (put);

      return message.getMessageID ();
    }
    catch (final Exception e) {
      log.warn ("Outbox error", e);
      throw new MessageException (e);
    }
  }

  @Nullable
  private static String getNodeValue (final XPath xpath, final String xPathExpr, final Document documentMetadata) throws XPathExpressionException,
                                                                                                                 DOMException {
    String strValue = null;
    final NodeList nodes = (NodeList) xpath.evaluate (xPathExpr, documentMetadata, XPathConstants.NODESET);
    if (nodes != null && nodes.getLength () > 0) {
      strValue = nodes.item (0).getNodeValue ();
    }
    return strValue;
  }

  private static Document getEndpointReferenceDocument (final CreateResponse createResponse) throws ParserConfigurationException,
                                                                                            IOException,
                                                                                            SAXException {
    org.w3._2009._02.ws_tra.ResourceCreated resourceCreated = (org.w3._2009._02.ws_tra.ResourceCreated) createResponse.getAny ();
    if (resourceCreated == null) {
      resourceCreated = createResponse.getResourceCreated ();
    }
    final W3CEndpointReference w3CEndpointReference = resourceCreated.getEndpointReference ().get (0);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    w3CEndpointReference.writeTo (new StreamResult (baos));
    final String endPointXML = baos.toString ();
    baos.close ();
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    documentBuilderFactory.setNamespaceAware (false);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
    final Document endpointDoc = documentBuilder.parse (new InputSource (new StringReader (endPointXML)));
    return endpointDoc;
  }

  private static void checkCredentials (@Nonnull final IReadonlyUsernamePWCredentials credentials) throws MessageException {
    if (credentials == null)
      throw new MessageException ("Credentials can not be a null value");

    if (StringHelper.hasNoTextAfterTrim (credentials.getUsername ()) ||
        StringHelper.hasNoTextAfterTrim (credentials.getPassword ())) {
      throw new MessageException ("Credentials are invalid, username=" +
                                  credentials.getUsername () +
                                  " password=" +
                                  credentials.getPassword ());
    }
  }
}

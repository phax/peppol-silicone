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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.lime.EndpointReferenceInterface;
import org.busdox.transport.lime.InboxInterface;
import org.busdox.transport.lime.MessageException;
import org.busdox.transport.lime.MessageInterface;
import org.busdox.transport.lime.MessageReferenceInterface;
import org.busdox.transport.lime._1.Entry;
import org.busdox.transport.lime._1.PageListType;
import org.busdox.transport.lime.soapheader.SoapHeaderReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.peppol.commons.identifier.SimpleDocumentIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.peppol.start.Identifiers;
import eu.peppol.start.MessageMetadata;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class Inbox implements InboxInterface {
  private static final Logger log = LoggerFactory.getLogger (Inbox.class);

  public List <MessageReferenceInterface> getMessageList (final IReadonlyUsernamePWCredentials credentials,
                                                          final EndpointReferenceInterface origEndpointReference) throws MessageException {
    final EndpointReference endpointReference = new EndpointReference ();
    endpointReference.setAddress (origEndpointReference.getAddress ());
    endpointReference.setChannelID (origEndpointReference.getChannelID ());

    validateCredentials (credentials);
    try {
      final List <Element> referenceParameters = createChannelReferenceParameter (endpointReference);
      final ArrayList <MessageReferenceInterface> messages = new ArrayList <MessageReferenceInterface> ();
      boolean morePages = true;
      do {
        morePages = getSinglePage (endpointReference, referenceParameters, credentials, messages);
      } while (morePages);
      return messages;
    }
    catch (final Exception e) {
      log.warn ("Inbox error", e);
      throw new MessageException (e);
    }
  }

  public List <MessageReferenceInterface> getMessageListPage (final IReadonlyUsernamePWCredentials credentials,
                                                              final EndpointReferenceInterface endpointReference,
                                                              final int pageNumber) throws MessageException {
    validateCredentials (credentials);
    try {
      final ArrayList <MessageReferenceInterface> messages = new ArrayList <MessageReferenceInterface> ();
      getSinglePage (endpointReference, null, credentials, messages);
      return messages;
    }
    catch (final Exception e) {
      throw new MessageException (e);
    }
  }

  public MessageInterface getMessage (final IReadonlyUsernamePWCredentials credentials,
                                      final MessageReferenceInterface messageReferenceInterface) throws MessageException {
    validateCredentials (credentials);
    try {
      final Resource port = new WebservicePort ().getServicePort (messageReferenceInterface.getEndpointReference ()
                                                                                           .getAddress (),
                                                                  credentials.getUsername (),
                                                                  credentials.getPassword ());

      // TODO is this necessary?
      new SoapHeaderMapper ().setupHandlerChain ((BindingProvider) port,
                                                 messageReferenceInterface.getEndpointReference ().getChannelID (),
                                                 messageReferenceInterface.getMessageID ());

      final GetResponse getResponse = port.get (null);
      final List <Object> objects = getResponse.getAny ();

      // Logger.getLogger(Outbox.class.getName()).log(Level.WARNING,
      // "objects size: "+objects.size());

      // writeDocumentToFile(((Element) objects.get(1)).getOwnerDocument());

      if (objects != null && objects.size () == 1) {
        final Document document = ((Element) objects.get (0)).getOwnerDocument ();
        final Message message = new Message ();
        message.setDocument (document);
        message.setMessageID (messageReferenceInterface.getMessageID ());
        setMessageMetadata (port, messageReferenceInterface, message);
        return message;
      }
      throw new MessageException ("No message found with id: " + messageReferenceInterface.getMessageID ());
    }
    catch (final Exception e) {
      log.warn ("Inbox error: ", e);
      throw new MessageException (e);
    }
  }

  public void deleteMessage (final IReadonlyUsernamePWCredentials credentials,
                             final MessageReferenceInterface messageReferenceInterface) throws MessageException {
    validateCredentials (credentials);
    try {
      final Resource port = new WebservicePort ().getServicePort (messageReferenceInterface.getEndpointReference ()
                                                                                           .getAddress (),
                                                                  credentials.getUsername (),
                                                                  credentials.getPassword ());
      new SoapHeaderMapper ().setupHandlerChain ((BindingProvider) port,
                                                 messageReferenceInterface.getEndpointReference ().getChannelID (),
                                                 messageReferenceInterface.getMessageID ());
      port.delete (null);
    }
    catch (final Exception e) {
      throw new MessageException (e);
    }
  }

  private static List <Element> createChannelReferenceParameter (final EndpointReferenceInterface endpointReference) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final Element node = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, "ChannelIdentifier");
    node.setTextContent (endpointReference.getChannelID ());
    return ContainerHelper.newList (node);
  }

  private static void validateCredentials (final IReadonlyUsernamePWCredentials credentials) throws MessageException {
    if (credentials == null) {
      throw new MessageException ("Credentials can not be a null value");
    }
    if (StringHelper.hasNoTextAfterTrim (credentials.getUsername ()) ||
        StringHelper.hasNoTextAfterTrim (credentials.getPassword ())) {
      throw new MessageException ("Credentials are invalid, username=" +
                                  credentials.getUsername () +
                                  " password=" +
                                  credentials.getPassword ());
    }
  }

  // TODO MessageReferenceInterface skal ændres til at indeholde en
  // endpointreference og reference parameters
  private static boolean getSinglePage (final EndpointReferenceInterface endpointReference,
                                        @Nullable final List <Element> referenceParameters,
                                        final IReadonlyUsernamePWCredentials credentials,
                                        final ArrayList <MessageReferenceInterface> messages) throws Exception,
                                                                                             JAXBException,
                                                                                             DOMException {
    boolean morePages = false;
    final Resource port = new WebservicePort ().getServicePort (endpointReference.getAddress (),
                                                                credentials.getUsername (),
                                                                credentials.getPassword ());
    new SoapHeaderMapper ().setupHandlerChain ((BindingProvider) port, null, null, referenceParameters);
    final GetResponse aGetResponse = port.get (null);
    if (aGetResponse != null && aGetResponse.getAny () != null && aGetResponse.getAny ().size () == 1) {
      final Unmarshaller unmarshaller = JAXBContextCache.getInstance ()
                                                        .getFromCache (PageListType.class)
                                                        .createUnmarshaller ();
      final Node aResponseAnyNode = (Node) aGetResponse.getAny ().get (0);
      final PageListType pageList = unmarshaller.unmarshal (aResponseAnyNode, PageListType.class).getValue ();
      if (pageList != null && pageList.getEntryList () != null) {
        for (final Entry entry : pageList.getEntryList ().getEntry ()) {
          final MessageReferenceInterface messageReference = new MessageReference ();
          messageReference.setEndpointReference (endpointReference);
          // Element element = (Element)
          // ((JAXBElement)entry.getEndpointReference().getReferenceParameters().getAny().get(1)).getValue();
          // messageReference.setMessageId((String)
          // element.getChildNodes().item(0).getNodeValue());
          final Element aReferenceParam1 = W3CEndpointReferenceUtils.getReferenceParameter (entry.getEndpointReference (),
                                                                                            1);
          messageReference.setMessageId (aReferenceParam1.getTextContent ());
          messages.add (messageReference);
        }
        if (pageList.getNextPageIdentifier () != null &&
            pageList.getNextPageIdentifier ().getEndpointReference () != null) {
          final W3CEndpointReference aNextPageER = pageList.getNextPageIdentifier ().getEndpointReference ();
          endpointReference.setAddress (W3CEndpointReferenceUtils.getAddress (aNextPageER));
          referenceParameters.clear ();
          referenceParameters.addAll (W3CEndpointReferenceUtils.getReferenceParameters (aNextPageER));
          morePages = true;
        }
      }
    }
    return morePages;
  }

  private static void setMessageMetadata (final Resource port,
                                          final MessageReferenceInterface messageReferenceInterface,
                                          final Message message) throws Exception {
    final HeaderList hl = (HeaderList) ((BindingProvider) port).getResponseContext ()
                                                               .get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
    MessageMetadata soapHeader = SoapHeaderReader.getSoapHeader (hl);
    soapHeader = new MessageMetadata (messageReferenceInterface.getMessageID (),
                                      soapHeader.getChannelID (),
                                      soapHeader.getSenderID (),
                                      soapHeader.getRecipientID (),
                                      soapHeader.getDocumentTypeID (),
                                      soapHeader.getProcessID ());
    message.setSender (new SimpleParticipantIdentifier (soapHeader.getSenderID ()));
    message.setReciever (new SimpleParticipantIdentifier (soapHeader.getRecipientID ()));
    message.setDocumentType (new SimpleDocumentIdentifier (soapHeader.getDocumentTypeID ()));
    message.setProcessType (new SimpleProcessIdentifier (soapHeader.getProcessID ()));
  }
}

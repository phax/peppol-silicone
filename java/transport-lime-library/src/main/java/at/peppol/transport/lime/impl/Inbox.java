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
package at.peppol.transport.lime.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.lime._1.Entry;
import org.busdox.transport.lime._1.PageListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.Identifiers;
import at.peppol.transport.MessageMetadataHelper;
import at.peppol.transport.lime.IEndpointReference;
import at.peppol.transport.lime.IInbox;
import at.peppol.transport.lime.IMessage;
import at.peppol.transport.lime.IMessageReference;
import at.peppol.transport.lime.MessageException;
import at.peppol.transport.lime.soapheader.SoapHeaderMapper;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class Inbox implements IInbox {
  private static final Logger s_aLogger = LoggerFactory.getLogger (Inbox.class);

  public List <IMessageReference> getMessageList (final IReadonlyUsernamePWCredentials aCredentials,
                                                  final IEndpointReference aEndpointReference) throws MessageException {
    _validateCredentials (aCredentials);
    try {
      final List <Element> aReferenceParameters = _createChannelReferenceParameter (aEndpointReference);
      final List <IMessageReference> aMessages = new ArrayList <IMessageReference> ();
      boolean bMorePages;
      do {
        bMorePages = _getSinglePage (aEndpointReference, aReferenceParameters, aCredentials, aMessages);
      } while (bMorePages);
      return aMessages;
    }
    catch (final Exception e) {
      s_aLogger.warn ("Inbox error", e);
      throw new MessageException (e);
    }
  }

  public List <IMessageReference> getMessageListPage (final IReadonlyUsernamePWCredentials aCredentials,
                                                      final IEndpointReference aEndpointReference,
                                                      final int nPageNumber) throws MessageException {
    _validateCredentials (aCredentials);
    try {
      final List <IMessageReference> aMessages = new ArrayList <IMessageReference> ();
      _getSinglePage (aEndpointReference, null, aCredentials, aMessages);
      return aMessages;
    }
    catch (final Exception e) {
      throw new MessageException (e);
    }
  }

  public IMessage getMessage (final IReadonlyUsernamePWCredentials aCredentials,
                              final IMessageReference aMessageReference) throws MessageException {
    _validateCredentials (aCredentials);
    try {
      // get a specifc message
      final Resource aPort = LimeHelper.getServicePort (aMessageReference.getEndpointReference ().getAddress (),
                                                        aCredentials);
      SoapHeaderMapper.setupHandlerChain ((BindingProvider) aPort,
                                          aMessageReference.getEndpointReference ().getChannelID (),
                                          aMessageReference.getMessageID ());

      // no body required
      final GetResponse aGetResponse = aPort.get (null);
      final List <Object> aObjects = aGetResponse.getAny ();

      if (ContainerHelper.getSize (aObjects) == 1) {
        final Document document = ((Node) ContainerHelper.getFirstElement (aObjects)).getOwnerDocument ();
        final Message aMessage = new Message ();
        aMessage.setDocument (document);
        aMessage.setMessageID (aMessageReference.getMessageID ());
        _setMessageMetadata (aPort, aMessage);
        return aMessage;
      }
      throw new MessageException ("No message found with id: " + aMessageReference.getMessageID ());
    }
    catch (final Exception e) {
      s_aLogger.warn ("Inbox error: ", e);
      throw new MessageException (e);
    }
  }

  public void deleteMessage (final IReadonlyUsernamePWCredentials aCredentials,
                             final IMessageReference aMessageReference) throws MessageException {
    _validateCredentials (aCredentials);
    try {
      // Delete a specific message
      final Resource aPort = LimeHelper.getServicePort (aMessageReference.getEndpointReference ().getAddress (),
                                                        aCredentials);
      SoapHeaderMapper.setupHandlerChain ((BindingProvider) aPort,
                                          aMessageReference.getEndpointReference ().getChannelID (),
                                          aMessageReference.getMessageID ());
      aPort.delete (null);
    }
    catch (final Exception e) {
      throw new MessageException (e);
    }
  }

  @Nonnull
  private static List <Element> _createChannelReferenceParameter (final IEndpointReference aEndpointReference) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final Element node = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, "ChannelIdentifier");
    node.setTextContent (aEndpointReference.getChannelID ());
    return ContainerHelper.newList (node);
  }

  private static void _validateCredentials (final IReadonlyUsernamePWCredentials credentials) throws MessageException {
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

  // TODO MessageReferenceInterface skal ændres til at indeholde en
  // endpointreference og reference parameters
  private static boolean _getSinglePage (final IEndpointReference aEndpointReference,
                                         @Nullable final List <Element> aReferenceParameters,
                                         final IReadonlyUsernamePWCredentials aCredentials,
                                         final List <IMessageReference> aMessages) throws Exception,
                                                                                  JAXBException,
                                                                                  DOMException {
    // Get a message list
    final Resource aPort = LimeHelper.getServicePort (aEndpointReference.getAddress (), aCredentials);
    SoapHeaderMapper.setupHandlerChain ((BindingProvider) aPort, null, null, aReferenceParameters);
    final GetResponse aGetResponse = aPort.get (null);

    boolean bMorePages = false;
    if (aGetResponse != null && aGetResponse.getAny () != null && aGetResponse.getAny ().size () == 1) {
      final Unmarshaller unmarshaller = JAXBContextCache.getInstance ()
                                                        .getFromCache (PageListType.class)
                                                        .createUnmarshaller ();
      final Node aResponseAnyNode = (Node) aGetResponse.getAny ().get (0);
      final PageListType pageList = unmarshaller.unmarshal (aResponseAnyNode, PageListType.class).getValue ();
      if (pageList != null && pageList.getEntryList () != null) {
        for (final Entry entry : pageList.getEntryList ().getEntry ()) {
          final IMessageReference messageReference = new MessageReference ();
          messageReference.setEndpointReference (aEndpointReference);
          // Element element = (Element)
          // ((JAXBElement)entry.getEndpointReference().getReferenceParameters().getAny().get(1)).getValue();
          // messageReference.setMessageId((String)
          // element.getChildNodes().item(0).getNodeValue());
          final Element aReferenceParam1 = W3CEndpointReferenceUtils.getReferenceParameter (entry.getEndpointReference (),
                                                                                            1);
          messageReference.setMessageId (aReferenceParam1.getTextContent ());
          aMessages.add (messageReference);
        }
        if (pageList.getNextPageIdentifier () != null &&
            pageList.getNextPageIdentifier ().getEndpointReference () != null) {
          final W3CEndpointReference aNextPageER = pageList.getNextPageIdentifier ().getEndpointReference ();
          aEndpointReference.setAddress (W3CEndpointReferenceUtils.getAddress (aNextPageER));
          aReferenceParameters.clear ();
          aReferenceParameters.addAll (W3CEndpointReferenceUtils.getReferenceParameters (aNextPageER));
          bMorePages = true;
        }
      }
    }
    return bMorePages;
  }

  private static void _setMessageMetadata (final Resource port, final Message message) throws Exception {
    final HeaderList aHeaderList = (HeaderList) ((BindingProvider) port).getResponseContext ()
                                                                        .get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
    final IMessageMetadata aMetadata = MessageMetadataHelper.createMetadataFromHeaders (aHeaderList);
    message.setSender (aMetadata.getSenderID ());
    message.setReciever (aMetadata.getRecipientID ());
    message.setDocumentType (aMetadata.getDocumentTypeID ());
    message.setProcessType (aMetadata.getProcessID ());
  }
}

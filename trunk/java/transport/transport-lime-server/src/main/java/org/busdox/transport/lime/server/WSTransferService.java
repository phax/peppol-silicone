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
package org.busdox.transport.lime.server;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.CBusDox;
import org.busdox.transport.base.Identifiers;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime._1.MessageUndeliverableType;
import org.busdox.transport.lime._1.ObjectFactory;
import org.busdox.transport.lime._1.ReasonCodeType;
import org.busdox.transport.lime.server.exception.MessageIdReusedException;
import org.busdox.transport.lime.server.exception.MessageMetadataException;
import org.busdox.transport.lime.server.exception.RecipientUnreachableException;
import org.busdox.transport.messagestore.Channel;
import org.busdox.transport.smp.MetadataLookupException;
import org.busdox.transport.smp.MetadataPublisherClient;
import org.busdox.transport.soapheader.MessageMetaData;
import org.busdox.transport.soapheader.SoapHeaderHandler;
import org.busdox.transport.soapheader.SoapHeaderReader;
import org.busdox.transport.start.client.AccessPointClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Delete;
import org.w3._2009._02.ws_tra.DeleteResponse;
import org.w3._2009._02.ws_tra.Get;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.PutResponse;
import org.w3._2009._02.ws_tra.Resource;
import org.w3._2009._02.ws_tra.ResourceCreated;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phloc.commons.CGlobal;
import com.phloc.commons.string.StringHelper;

import eu.peppol.busdox.ipmapper.ConfiguredDNSMapper;
import eu.peppol.common.ConfigFile;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService (serviceName = "wstransferService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = CBusDox.START_WSDL_PATH)
@HandlerChain (file = "WSTransferService_handler.xml")
public class WSTransferService {
  public static final String FAULT_UNKNOWN_ENDPOINT = "The endpoint is not known";
  public static final String FAULT_CHANNEL_FULL = "The channel is not accepting messages for this destination";
  public static final String FAULT_SECURITY_ERROR = "There is a security error in processing this request";
  public static final String FAULT_SERVER_ERROR = "ServerError";
  public static final String SERVICENAME = "wstransferService";

  // used to check if lime-ap has to process a local or remote call
  public static final String LIME_AP_SERVICEURL_PROP = "org.busdox.transport.lime.ap.own_url";

  private static final Logger log = LoggerFactory.getLogger (WSTransferService.class);

  static {
    CBusDox.setMetroDebugSystemProperties (true);
  }

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  @javax.annotation.Resource
  private WebServiceContext webServiceContext;

  public GetResponse get (@SuppressWarnings ("unused") final Get body) {
    final String realPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                               .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
    final String channelID = SoapHeaderReader.getChannelID (webServiceContext);
    final String messageID = SoapHeaderReader.getMessageID (webServiceContext);
    final String pageNumber = SoapHeaderReader.getPageNumber (webServiceContext);

    final GetResponse aGetResponse = new GetResponse ();
    try {
      if (StringHelper.hasNoText (messageID))
        addPageListToResponse (pageNumber, realPath, channelID, aGetResponse);
      else
        addSingleMessageToResponse (realPath, channelID, messageID, aGetResponse);
    }
    catch (final Exception e) {
      log.error ("Error on get", e);
    }
    return aGetResponse;
  }

  /**
   * Delete
   *
   * @param body
   *        delete body
   * @return response
   */
  public DeleteResponse delete (final Delete body) {
    try {
      final String channelID = SoapHeaderReader.getChannelID (webServiceContext);
      final String messageID = SoapHeaderReader.getMessageID (webServiceContext);
      final String realPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                                 .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
      new Channel (realPath).deleteDocument (channelID, messageID);
    }
    catch (final Exception ex) {
      log.error (null, ex);
    }
    return new DeleteResponse ();
  }

  public CreateResponse create (@SuppressWarnings ("unused") final Create body,
                                @SuppressWarnings ("unused") final String messageIdHeader,
                                final String channelIdHeader,
                                final org.busdox.transport.identifiers._1.ParticipantIdentifierType recipientIdHeader,
                                final org.busdox.transport.identifiers._1.ParticipantIdentifierType senderIdHeader,
                                final org.busdox.transport.identifiers._1.DocumentIdentifierType documentIdHeader,
                                final org.busdox.transport.identifiers._1.ProcessIdentifierType processIdHeader) {
    final String messageID = "uuid:" + UUID.randomUUID ().toString ();
    MessageMetaData soapHeader = null;
    final String thisURLstr = getOwnUrl () + SERVICENAME;
    try {
      soapHeader = getMessageMetadata (senderIdHeader,
                                       recipientIdHeader,
                                       documentIdHeader,
                                       processIdHeader,
                                       messageID,
                                       channelIdHeader);
      if (soapHeader == null) {
        throw new MessageMetadataException ("Message metadata fields are missing");
      }
      soapHeader.setMessageID (messageID);

      final boolean isNewID = ResourceMemoryStore.getInstance ().createResource (messageID, thisURLstr, soapHeader);
      if (!isNewID) {
        throw new MessageIdReusedException ("Message id " + messageID + " is reused");
      }
    }
    catch (final MessageMetadataException ex) {
      throwSoapFault (FAULT_SERVER_ERROR, ex);
    }
    catch (final MessageIdReusedException ex) {
      throwSoapFault (FAULT_SERVER_ERROR, ex);
    }
    catch (final Exception ex) {
      throwSoapFault (FAULT_SERVER_ERROR, ex);
    }
    return getCreateResponse (thisURLstr, soapHeader, messageID);
  }

  public PutResponse put (final Put body) {
    final String messageID = SoapHeaderReader.getMessageID (webServiceContext);
    final String ownLIMEServiceURLStr = getOwnUrl () + SERVICENAME;
    final MessageMetaData soapHdr = ResourceMemoryStore.getInstance ().getMessage (messageID, ownLIMEServiceURLStr);

    try {
      final String recipientAccessPointURLstr = getAccessPointUrl (soapHdr.getRecipient (),
                                                                   soapHdr.getDocumentInfoType (),
                                                                   soapHdr.getProcessType ());
      final String senderAccessPointURLstr = getAccessPointUrl (soapHdr.getSender (),
                                                                soapHdr.getDocumentInfoType (),
                                                                soapHdr.getProcessType ());

      if (recipientAccessPointURLstr.equalsIgnoreCase (senderAccessPointURLstr)) {
        logRequest ("This is a local request - sending directly to inbox",
                    ownLIMEServiceURLStr,
                    soapHdr,
                    "INBOX: " + soapHdr.getRecipient ().getValue ());
        sendToInbox (soapHdr, body);
      }
      else {
        logRequest ("This is a request for a remote access point",
                    senderAccessPointURLstr,
                    soapHdr,
                    recipientAccessPointURLstr);
        sendToAccessPoint (body, recipientAccessPointURLstr, soapHdr, ownLIMEServiceURLStr);
      }
    }
    catch (final RecipientUnreachableException ex) {
      sendMessageUndeliverable (ex, messageID, ReasonCodeType.TRANSPORT_ERROR, soapHdr);
      throwSoapFault (FAULT_UNKNOWN_ENDPOINT, ex);
    }
    catch (final MetadataLookupException ex) {
      sendMessageUndeliverable (ex, messageID, ReasonCodeType.METADATA_ERROR, soapHdr);
      throwSoapFault (FAULT_UNKNOWN_ENDPOINT, ex);
    }
    catch (final Exception ex) {
      sendMessageUndeliverable (ex, messageID, ReasonCodeType.OTHER_ERROR, soapHdr);
      throwSoapFault (FAULT_SERVER_ERROR, ex);
    }
    return new PutResponse ();
  }

  private static CreateResponse getCreateResponse (final String thisURLstr,
                                                   final MessageMetaData soapHeader,
                                                   final String messageID) {
    final CreateResponse createResponse = new CreateResponse ();
    final W3CEndpointReference w3CEndpointReference = getW3CEndpointReference (thisURLstr, soapHeader, messageID);
    final ResourceCreated resourceCreated = new ResourceCreated ();
    resourceCreated.getEndpointReference ().add (w3CEndpointReference);
    createResponse.setResourceCreated (resourceCreated);
    return createResponse;
  }

  private static W3CEndpointReference getW3CEndpointReference (final String thisURLstr,
                                                               final MessageMetaData soapHeader,
                                                               final String messageID) {
    final String endpointReferenceXML = "<wsa:EndpointReference xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" >"
                                        + "<wsa:Address>" +
                                        thisURLstr +
                                        "</wsa:Address>" +
                                        "<wsa:ReferenceParameters>" +
                                        "<" +
                                        Identifiers.CHANNELID +
                                        " xmlns=\"" +
                                        Identifiers.NAMESPACE_TRANSPORT_IDS +
                                        "\">" +
                                        soapHeader.getChannelID () +
                                        "</" +
                                        Identifiers.CHANNELID +
                                        ">" +
                                        "<" +
                                        Identifiers.MESSAGEID +
                                        " xmlns=\"" +
                                        Identifiers.NAMESPACE_TRANSPORT_IDS +
                                        "\">" +
                                        messageID +
                                        "</" +
                                        Identifiers.MESSAGEID +
                                        ">" +
                                        "</wsa:ReferenceParameters>" +
                                        "</wsa:EndpointReference>";
    final StreamSource source = new StreamSource (new StringReader (endpointReferenceXML));
    final W3CEndpointReference w3CEndpointReference = new W3CEndpointReference (source);
    return w3CEndpointReference;
  }

  private void sendMessageUndeliverable (final Exception ex,
                                         final String messageID,
                                         final ReasonCodeType reasonCodeType,
                                         final MessageMetaData messageMetadata) {
    if (messageMetadata == null) {
      log.error ("No message metadata found. Unable to send MessageUndeliverable for Message ID: " + messageID);
    }
    else {
      try {
        log.warn ("Unable to send MessageUndeliverable for Message ID: " + messageID + " Reason: " + ex.getMessage ());

        final MessageUndeliverableType messageUndeliverableType = m_aObjFactory.createMessageUndeliverableType ();
        messageUndeliverableType.setMessageIdentifier (messageID);
        messageUndeliverableType.setReasonCode (reasonCodeType);
        messageUndeliverableType.setDetails ("(" +
                                             messageMetadata.getRecipient ().getValue () +
                                             "," +
                                             messageMetadata.getRecipient ().getScheme () +
                                             ") " +
                                             ex.getMessage ());

        final ParticipantIdentifierType recipient = messageMetadata.getSender ();
        messageMetadata.setRecipient (recipient);
        messageMetadata.setSender (Identifiers.MESSAGEUNDELIVERABLE_SENDER);
        messageMetadata.setDocumentInfoType (Identifiers.MESSAGEUNDELIVERABLE_DOCUMENT);
        messageMetadata.setProcessType (Identifiers.MESSAGEUNDELIVERABLE_PROCESS);

        final Put put = new Put ();
        final List <Object> objects = put.getAny ();

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
        final Document document = documentBuilder.newDocument ();
        final DOMResult domResult = new DOMResult (document);
        final Marshaller marshaller = JAXBContext.newInstance (MessageUndeliverableType.class).createMarshaller ();
        marshaller.marshal (m_aObjFactory.createMessageUndeliverable (messageUndeliverableType), domResult);

        objects.add (document.getDocumentElement ());
        sendToInbox (messageMetadata, put);
      }
      catch (final Exception ex1) {
        log.error ("Unable to send MessageUndeliverable for Message ID: " + messageID, ex1);
      }
    }
  }

  private static void throwSoapFault (final String faultMessage, final Exception e) throws RuntimeException {
    try {
      log.info ("Server error", e);
      final SOAPFault soapFault = SOAPFactory.newInstance ().createFault ();
      soapFault.setFaultString (faultMessage);
      soapFault.setFaultCode (new QName (SOAPConstants.URI_NS_SOAP_ENVELOPE, "Sender"));
      soapFault.setFaultActor ("LIME AP");
      throw new SOAPFaultException (soapFault);
    }
    catch (final Exception e2) {
      throw new RuntimeException ("Problem processing SOAP Fault on service-side." + e2.getMessage ());
    }
  }

  private static void addSingleMessageToResponse (final String realPath,
                                                  final String channelID,
                                                  final String messageID,
                                                  final GetResponse getResponse) throws Exception {
    final Document document = new Channel (realPath).getDocument (channelID, messageID);
    final Document documentMetadata = new Channel (realPath).getDocumentMetadata (channelID, messageID);
    final List <Object> objects = getResponse.getAny ();
    objects.add (documentMetadata.getDocumentElement ());
    objects.add (document.getDocumentElement ());
  }

  private String getOwnUrl () {
    final ServletRequest servletRequest = (ServletRequest) webServiceContext.getMessageContext ()
                                                                            .get (MessageContext.SERVLET_REQUEST);
    final String contextPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                                  .get (MessageContext.SERVLET_CONTEXT)).getContextPath ();
    final String thisAccessPointURLstr = servletRequest.getScheme () +
                                         "://" +
                                         servletRequest.getServerName () +
                                         ":" +
                                         servletRequest.getLocalPort () +
                                         contextPath +
                                         '/';
    return thisAccessPointURLstr;
  }

  private void addPageListToResponse (final String pageNumber,
                                      final String realPath,
                                      final String channelID,
                                      final GetResponse getResponse) throws Exception {
    final String thisRelayServiceURLstr = getOwnUrl () + SERVICENAME;
    int intPageNumber = 0;
    if (pageNumber != null) {
      if (pageNumber.trim ().length () > 0) {
        intPageNumber = Integer.parseInt (pageNumber);
      }
    }
    final Document document = new MessagePage ().getPageList (intPageNumber,
                                                              thisRelayServiceURLstr,
                                                              new Channel (realPath),
                                                              channelID);
    if (document != null) {
      final List <Object> objects = getResponse.getAny ();
      objects.add (document.getDocumentElement ());
    }
  }

  public void setupHandlerChain (final BindingProvider bindingProvider,
                                 @SuppressWarnings ("unused") final SoapHeaderHandler soapHeaderHandler) {
    final Binding binding = bindingProvider.getBinding ();
    @SuppressWarnings ("rawtypes")
    final List <Handler> handlerList = binding.getHandlerChain ();
    handlerList.add (new SoapResponseHeaderHandler ());
    binding.setHandlerChain (handlerList);
  }

  private static void logRequest (final String action,
                                  final String ownUrl,
                                  final MessageMetaData soapHdr,
                                  final String nextUrl) {
    final String NEWLINE = CGlobal.LINE_SEPARATOR;

    final StringBuilder strbuf = new StringBuilder ();
    strbuf.append ("REQUEST start--------------------------------------------------");
    strbuf.append (NEWLINE + "Action: " + action);
    strbuf.append (NEWLINE + "Own URL: " + ownUrl);
    strbuf.append (NEWLINE + "Sending to : " + nextUrl);
    strbuf.append (NEWLINE + "Messsage ID: " + soapHdr.getMessageID ());
    strbuf.append (NEWLINE + "Sender ID: " + soapHdr.getSender ().getValue ());
    strbuf.append (NEWLINE + "Sender type: " + soapHdr.getSender ().getScheme ());
    strbuf.append (NEWLINE + "Recipient ID: " + soapHdr.getRecipient ().getValue ());
    strbuf.append (NEWLINE + "Recipient type: " + soapHdr.getRecipient ().getScheme ());
    strbuf.append (NEWLINE + "Document ID: " + soapHdr.getDocumentInfoType ().getValue ());
    strbuf.append (NEWLINE + "Document type: " + soapHdr.getDocumentInfoType ().getScheme ());
    strbuf.append (NEWLINE + "Process ID: " + soapHdr.getProcessType ().getValue ());
    strbuf.append (NEWLINE + "Process type: " + soapHdr.getProcessType ().getScheme ());
    strbuf.append (NEWLINE + "REQUEST end----------------------------------------------------" + NEWLINE);

    log.info (strbuf.toString ());
  }

  private static void sendToAccessPoint (final Put body,
                                         final String recipientAccessPointURLstr,
                                         final MessageMetaData soapHdr,
                                         final String thisRelayServiceURLstr) throws Exception, MalformedURLException {
    final Create createBody = new Create ();
    createBody.getAny ().addAll (body.getAny ());
    final AccessPointClient accessPointClient = new AccessPointClient ();
    final Resource port = accessPointClient.getPort (recipientAccessPointURLstr);
    accessPointClient.send (port, soapHdr, thisRelayServiceURLstr, createBody);
    accessPointClient.close (port);
  }

  private void sendToInbox (final MessageMetaData soapHdr, final Put body) throws RecipientUnreachableException {
    final String channelID = soapHdr.getRecipient ().getValue ();
    if (channelID == null) {
      throw new RecipientUnreachableException ("Unknown recipient at LIME-AP: " + soapHdr.getRecipient ());
    }
    final String messageID = SoapHeaderReader.getMessageID (webServiceContext);

    log.info ("Recipient: " + soapHdr.getRecipient () + "ChannelID: " + channelID);

    try {
      final List <Object> objects = body.getAny ();
      if (objects != null && objects.size () == 1) {
        final Element element = (Element) objects.iterator ().next ();
        final Document document = element.getOwnerDocument ();
        Document metadataDocument;

        metadataDocument = SoapHeaderReader.createSoapHeaderDocument (soapHdr);

        final String realPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                                   .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
        new Channel (realPath).saveDocument (channelID, messageID, metadataDocument, document);
      }
    }
    catch (final Exception ex) {
      log.error (null, ex);
      throw new RecipientUnreachableException (ex);
    }
  }

  private static MessageMetaData getMessageMetadata (final org.busdox.transport.identifiers._1.ParticipantIdentifierType senderIdHeader,
                                                     final org.busdox.transport.identifiers._1.ParticipantIdentifierType recipientIdHeader,
                                                     final org.busdox.transport.identifiers._1.DocumentIdentifierType documentIdHeader,
                                                     final org.busdox.transport.identifiers._1.ProcessIdentifierType processIdHeader,
                                                     final String messageID,
                                                     final String channelIdHeader) {
    MessageMetaData soapHdr = null;
    if (senderIdHeader != null && recipientIdHeader != null && documentIdHeader != null && processIdHeader != null) {
      soapHdr = new MessageMetaData (senderIdHeader,
                                     recipientIdHeader,
                                     documentIdHeader,
                                     processIdHeader,
                                     messageID,
                                     channelIdHeader);
    }
    return soapHdr;
  }

  private static String getAccessPointUrl (final ParticipantIdentifierType recipientId,
                                           final DocumentIdentifierType documentId,
                                           final ProcessIdentifierType processId) throws MetadataLookupException {
    final String smlEndpointAddress = ConfigFile.getInstance ()
                                                .getString ("org.busdox.transport.metadatapublisher.url");
    final MetadataPublisherClient aSMPClient = new MetadataPublisherClient (smlEndpointAddress,
                                                                            recipientId,
                                                                            documentId,
                                                                            new ConfiguredDNSMapper ());
    return aSMPClient.getEndpointAddress (processId);
  }
}

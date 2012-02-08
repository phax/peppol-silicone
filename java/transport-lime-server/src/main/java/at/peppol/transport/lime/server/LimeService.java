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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
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
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime._1.MessageUndeliverableType;
import org.busdox.transport.lime._1.ObjectFactory;
import org.busdox.transport.lime._1.ReasonCodeType;
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
import org.w3._2009._02.ws_tra.ResourceCreated;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.peppol.busdox.CBusDox;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;
import at.peppol.transport.MessageMetadataHelper;
import at.peppol.transport.lime.Identifiers;
import at.peppol.transport.lime.server.exception.MessageIdReusedException;
import at.peppol.transport.lime.server.exception.RecipientUnreachableException;
import at.peppol.transport.lime.soapheader.SoapHeaderHandler;
import at.peppol.transport.lime.soapheader.SoapHeaderReader;
import at.peppol.transport.start.client.AccessPointClient;

import com.phloc.commons.CGlobal;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService (serviceName = "limeService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = "WEB-INF/wsdl/peppol-lime-1.0.wsdl")
@HandlerChain (file = "WSTransferService_handler.xml")
public class LimeService {
  public static final String FAULT_UNKNOWN_ENDPOINT = "The endpoint is not known";
  public static final String FAULT_CHANNEL_FULL = "The channel is not accepting messages for this destination";
  public static final String FAULT_SECURITY_ERROR = "There is a security error in processing this request";
  public static final String FAULT_SERVER_ERROR = "ServerError";
  public static final String SERVICENAME = LimeService.class.getAnnotation (WebService.class).serviceName ();

  private static final Logger s_aLogger = LoggerFactory.getLogger (LimeService.class);

  static {
    if (false)
      CBusDox.setMetroDebugSystemProperties (true);
  }

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  @Resource
  private WebServiceContext webServiceContext;

  @Nonnull
  private HeaderList _getInboundHeaderList () {
    return (HeaderList) webServiceContext.getMessageContext ().get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
  }

  @Nonnull
  private static W3CEndpointReference _createW3CEndpointReference (final String sOurAPURL,
                                                                   final String sChannelID,
                                                                   final String sMessageID) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final List <Element> aReferenceParameters = new ArrayList <Element> ();
    Element aElement = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, Identifiers.CHANNELID);
    aElement.appendChild (aDummyDoc.createTextNode (sChannelID));
    aReferenceParameters.add (aElement);
    aElement = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, Identifiers.MESSAGEID);
    aElement.appendChild (aDummyDoc.createTextNode (sMessageID));
    aReferenceParameters.add (aElement);

    return W3CEndpointReferenceUtils.createEndpointReference (sOurAPURL, aReferenceParameters);
  }

  @Nonnull
  private static CreateResponse _createCreateResponse (final String sOurAPURL,
                                                       final String sChannelID,
                                                       final String sMessageID) {
    final W3CEndpointReference w3CEndpointReference = _createW3CEndpointReference (sOurAPURL, sChannelID, sMessageID);

    final CreateResponse createResponse = new CreateResponse ();
    final ResourceCreated resourceCreated = new ResourceCreated ();
    resourceCreated.getEndpointReference ().add (w3CEndpointReference);
    createResponse.setResourceCreated (resourceCreated);
    return createResponse;
  }

  @Nonnull
  public CreateResponse create (@SuppressWarnings ("unused") final Create body) {
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    IMessageMetadata aMetadata = null;
    final String sOurAPURL = getOwnUrl () + SERVICENAME;

    try {
      // Grabs the list of headers from the SOAP message
      final HeaderList aHeaderList = _getInboundHeaderList ();
      aMetadata = MessageMetadataHelper.createMetadataFromHeadersWithCustomMessageID (aHeaderList, sMessageID);

      if (ResourceMemoryStore.getInstance ().createResource (sMessageID, sOurAPURL, aMetadata).isUnchanged ())
        throw new MessageIdReusedException ("Message id " + sMessageID + " is reused");
    }
    catch (final Exception ex) {
      throw _createSoapFault (FAULT_SERVER_ERROR, ex);
    }

    // Will not happen
    if (aMetadata == null)
      throw _createSoapFault (FAULT_SERVER_ERROR, new IllegalStateException ());

    return _createCreateResponse (sOurAPURL, aMetadata.getChannelID (), sMessageID);
  }

  @Nonnull
  public PutResponse put (final Put body) {
    final HeaderList aHeaderList = _getInboundHeaderList ();
    final String sMessageID = MessageMetadataHelper.getMessageID (aHeaderList);
    final String sOwnAPURL = getOwnUrl () + SERVICENAME;
    final IMessageMetadata aMetadata = ResourceMemoryStore.getInstance ().getMessage (sMessageID, sOwnAPURL);

    try {
      final String recipientAccessPointURLstr = getAccessPointUrl (aMetadata.getRecipientID (),
                                                                   aMetadata.getDocumentTypeID (),
                                                                   aMetadata.getProcessID ());
      final String senderAccessPointURLstr = getAccessPointUrl (aMetadata.getSenderID (),
                                                                aMetadata.getDocumentTypeID (),
                                                                aMetadata.getProcessID ());

      if (recipientAccessPointURLstr.equalsIgnoreCase (senderAccessPointURLstr)) {
        logRequest ("This is a local request - sending directly to inbox",
                    sOwnAPURL,
                    aMetadata,
                    "INBOX: " + aMetadata.getRecipientID ().getValue ());
        sendToInbox (aMetadata, body);
      }
      else {
        logRequest ("This is a request for a remote access point",
                    senderAccessPointURLstr,
                    aMetadata,
                    recipientAccessPointURLstr);
        sendToAccessPoint (body, recipientAccessPointURLstr, aMetadata);
      }
    }
    catch (final RecipientUnreachableException ex) {
      sendMessageUndeliverable (ex, sMessageID, ReasonCodeType.TRANSPORT_ERROR, aMetadata);
      throw _createSoapFault (FAULT_UNKNOWN_ENDPOINT, ex);
    }
    catch (final Exception ex) {
      sendMessageUndeliverable (ex, sMessageID, ReasonCodeType.OTHER_ERROR, aMetadata);
      throw _createSoapFault (FAULT_SERVER_ERROR, ex);
    }
    return new PutResponse ();
  }

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
      s_aLogger.error ("Error on get", e);
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
      s_aLogger.error ("Error deleting document", ex);
    }
    return new DeleteResponse ();
  }

  private void sendMessageUndeliverable (final Exception ex,
                                         final String messageID,
                                         final ReasonCodeType reasonCodeType,
                                         final IMessageMetadata messageMetadata) {
    if (messageMetadata == null) {
      s_aLogger.error ("No message metadata found. Unable to send MessageUndeliverable for Message ID: " + messageID);
    }
    else {
      try {
        s_aLogger.warn ("Unable to send MessageUndeliverable for Message ID: " +
                        messageID +
                        " Reason: " +
                        ex.getMessage ());

        final MessageUndeliverableType messageUndeliverableType = m_aObjFactory.createMessageUndeliverableType ();
        messageUndeliverableType.setMessageIdentifier (messageID);
        messageUndeliverableType.setReasonCode (reasonCodeType);
        messageUndeliverableType.setDetails ("(" +
                                             messageMetadata.getRecipientID ().getValue () +
                                             "," +
                                             messageMetadata.getRecipientID ().getScheme () +
                                             ") " +
                                             ex.getMessage ());

        final IMessageMetadata aRealMetadata = new MessageMetadata (messageMetadata.getMessageID (),
                                                                    messageMetadata.getChannelID (),
                                                                    Identifiers.MESSAGEUNDELIVERABLE_SENDER,
                                                                    messageMetadata.getSenderID (),
                                                                    Identifiers.MESSAGEUNDELIVERABLE_DOCUMENT,
                                                                    Identifiers.MESSAGEUNDELIVERABLE_PROCESS);

        final Put put = new Put ();
        final List <Object> objects = put.getAny ();

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
        final Document document = documentBuilder.newDocument ();
        final DOMResult domResult = new DOMResult (document);
        final Marshaller marshaller = JAXBContext.newInstance (MessageUndeliverableType.class).createMarshaller ();
        marshaller.marshal (m_aObjFactory.createMessageUndeliverable (messageUndeliverableType), domResult);

        objects.add (document.getDocumentElement ());
        sendToInbox (aRealMetadata, put);
      }
      catch (final Exception ex1) {
        s_aLogger.error ("Unable to send MessageUndeliverable for Message ID: " + messageID, ex1);
      }
    }
  }

  @Nonnull
  private static SOAPFaultException _createSoapFault (final String faultMessage, final Exception e) throws RuntimeException {
    try {
      s_aLogger.info ("Server error", e);
      final SOAPFault soapFault = SOAPFactory.newInstance ().createFault ();
      soapFault.setFaultString (faultMessage);
      soapFault.setFaultCode (new QName (SOAPConstants.URI_NS_SOAP_ENVELOPE, "Sender"));
      soapFault.setFaultActor ("LIME AP");
      return new SOAPFaultException (soapFault);
    }
    catch (final SOAPException e2) {
      throw new RuntimeException ("Problem processing SOAP Fault on service-side", e2);
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
                                  final IMessageMetadata soapHdr,
                                  final String nextUrl) {
    final String NEWLINE = CGlobal.LINE_SEPARATOR;

    final StringBuilder strbuf = new StringBuilder ();
    strbuf.append ("REQUEST start--------------------------------------------------");
    strbuf.append (NEWLINE + "Action: " + action);
    strbuf.append (NEWLINE + "Own URL: " + ownUrl);
    strbuf.append (NEWLINE + "Sending to : " + nextUrl);
    strbuf.append (NEWLINE + "Messsage ID: " + soapHdr.getMessageID ());
    strbuf.append (NEWLINE + "Sender ID: " + soapHdr.getSenderID ().getValue ());
    strbuf.append (NEWLINE + "Sender type: " + soapHdr.getSenderID ().getScheme ());
    strbuf.append (NEWLINE + "Recipient ID: " + soapHdr.getRecipientID ().getValue ());
    strbuf.append (NEWLINE + "Recipient type: " + soapHdr.getRecipientID ().getScheme ());
    strbuf.append (NEWLINE + "Document ID: " + soapHdr.getDocumentTypeID ().getValue ());
    strbuf.append (NEWLINE + "Document type: " + soapHdr.getDocumentTypeID ().getScheme ());
    strbuf.append (NEWLINE + "Process ID: " + soapHdr.getProcessID ().getValue ());
    strbuf.append (NEWLINE + "Process type: " + soapHdr.getProcessID ().getScheme ());
    strbuf.append (NEWLINE + "REQUEST end----------------------------------------------------" + NEWLINE);

    s_aLogger.info (strbuf.toString ());
  }

  private static void sendToAccessPoint (final Put body,
                                         final String recipientAccessPointURLstr,
                                         final IMessageMetadata soapHdr) throws Exception {
    final Create createBody = new Create ();
    createBody.getAny ().addAll (body.getAny ());
    AccessPointClient.send (recipientAccessPointURLstr, soapHdr, createBody);
  }

  private void sendToInbox (final IMessageMetadata soapHdr, final Put body) throws RecipientUnreachableException {
    final String channelID = soapHdr.getRecipientID ().getValue ();
    if (channelID == null) {
      throw new RecipientUnreachableException ("Unknown recipient at LIME-AP: " + soapHdr.getRecipientID ());
    }
    final String messageID = SoapHeaderReader.getMessageID (webServiceContext);

    s_aLogger.info ("Recipient: " + soapHdr.getRecipientID () + "; ChannelID: " + channelID);

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
      s_aLogger.error (null, ex);
      throw new RecipientUnreachableException (ex);
    }
  }

  private static String getAccessPointUrl (final ParticipantIdentifierType recipientId,
                                           final DocumentIdentifierType documentId,
                                           final ProcessIdentifierType processId) throws Exception {
    return new SMPServiceCaller (recipientId, ESML.PRODUCTION).getEndpointAddress (recipientId, documentId, processId);
  }
}

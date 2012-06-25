package eu.peppol.inbound.server;

import java.security.cert.X509Certificate;

import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;

import org.busdox._2010._02.channel.fault.StartException;
import org.slf4j.MDC;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Delete;
import org.w3._2009._02.ws_tra.DeleteResponse;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Get;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.PutResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadataHelper;

import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.peppol.inbound.util.Log;
import eu.peppol.outbound.smp.SmpLookupManager;
import eu.peppol.start.identifier.Configuration;
import eu.peppol.start.identifier.KeystoreManager;
import eu.peppol.start.persistence.MessageRepository;
import eu.peppol.start.persistence.MessageRepositoryFactory;

@WebService (serviceName = "accessPointService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = "WEB-INF/wsdl/accessPointService/wsdl_v2.0.wsdl")
@BindingType (value = SOAPBinding.SOAP11HTTP_BINDING)
@Addressing
public class accessPointService {

  @javax.annotation.Resource
  private WebServiceContext webServiceContext;

  @Action (input = "http://www.w3.org/2009/02/ws-tra/Create",
           output = "http://www.w3.org/2009/02/ws-tra/CreateResponse",
           fault = { @FaultAction (className = org.w3._2009._02.ws_tra.FaultMessage.class,
                                   value = "http://busdox.org/2010/02/channel/fault") })
  public CreateResponse create (final Create body) throws FaultMessage {
    try {
      final IMessageMetadata messageHeader = getPeppolMessageHeader ();
      Log.info ("Received PEPPOL SOAP Header:" + messageHeader);

      // TODO: Verifies the SOAP header and rejects illegal messages

      // Injects current context into SLF4J Mapped Diagnostic Context
      setUpSlf4JMDC (messageHeader);
      verifyThatThisDocumentIsForUs (messageHeader);
      final Document document = ((Element) body.getAny ().get (0)).getOwnerDocument ();

      // Invokes the message persistence
      persistMessage (messageHeader, document);

      final CreateResponse createResponse = new CreateResponse ();

      getMemoryUsage ();
      return createResponse;

    }
    catch (final Exception e) {
      Log.error ("Problem while handling inbound document: " + e.getMessage (), e);
      throw new FaultMessage ("Unexpected error in document handling: " + e.getMessage (), new StartException (), e);
    }
    finally {
      MDC.clear ();
    }
  }

  /**
   * Extracts metadata from the SOAP Header, i.e. the routing information and
   * invokes a pluggable message persistence in order to allow for storage of
   * the meta data and the message itself.
   * 
   * @param document
   *        the XML document.
   */
  void persistMessage (final IMessageMetadata messageHeader, final Document document) {

    // Invokes whatever has been configured in META-INF/services/.....
    try {

      final String inboundMessageStore = Configuration.getInstance ().getInboundMessageStore ();
      // Locates a message repository using the META-INF/services mechanism
      final MessageRepository messageRepository = MessageRepositoryFactory.getInstance ();
      // Persists the message
      messageRepository.saveInboundMessage (inboundMessageStore, messageHeader, document);

    }
    catch (final Throwable e) {
      Log.error ("Unable to persist", e);
    }
  }

  private IMessageMetadata getPeppolMessageHeader () {
    final MessageContext messageContext = webServiceContext.getMessageContext ();
    final HeaderList headerList = (HeaderList) messageContext.get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
    final IMessageMetadata peppolMessageHeader = MessageMetadataHelper.createMetadataFromHeaders (headerList);
    return peppolMessageHeader;
  }

  void setUpSlf4JMDC (final IMessageMetadata messageHeader) {
    MDC.put ("msgId", messageHeader.getMessageID ());
    MDC.put ("senderId", messageHeader.getSenderID ().getValue ());
    MDC.put ("channelId", messageHeader.getChannelID ());
  }

  private void verifyThatThisDocumentIsForUs (final IMessageMetadata messageHeader) {

    try {
      final X509Certificate recipientCertificate = new SmpLookupManager ().getEndpointCertificate (messageHeader.getRecipientID (),
                                                                                                   messageHeader.getDocumentTypeID ());

      if (new KeystoreManager ().isOurCertificate (recipientCertificate)) {
        Log.info ("SMP lookup OK");
      }
      else {
        Log.info ("SMP lookup indicates that document was sent to the wrong access point");
        throw new FaultMessage ("This message was sent to the wrong Access Point", new StartException ());
      }
    }
    catch (final Exception e) {
      Log.info ("SMP lookup fails, we assume the message is for us");
    }
  }

  public GetResponse get (@SuppressWarnings ("unused") final Get body) {
    throw new UnsupportedOperationException ();
  }

  public PutResponse put (@SuppressWarnings ("unused") final Put body) {
    throw new UnsupportedOperationException ();
  }

  public DeleteResponse delete (@SuppressWarnings ("unused") final Delete body) {
    throw new UnsupportedOperationException ();
  }

  private static final long MEMORY_THRESHOLD = 10;
  private static long lastUsage = 0;

  /**
   * returns a String describing current memory utilization. In addition
   * unusually large changes in memory usage will be logged.
   */
  public static String getMemoryUsage () {

    System.gc ();
    final Runtime runtime = Runtime.getRuntime ();
    final long freeMemory = runtime.freeMemory ();
    final long totalMemory = runtime.totalMemory ();
    final long usedMemory = totalMemory - freeMemory;
    final long mega = 1048576;
    final long usedInMegabytes = usedMemory / mega;
    final long totalInMegabytes = totalMemory / mega;
    final String memoryStatus = usedInMegabytes +
                                "M / " +
                                totalInMegabytes +
                                "M / " +
                                (runtime.maxMemory () / mega) +
                                "M";

    if (usedInMegabytes <= lastUsage - MEMORY_THRESHOLD || usedInMegabytes >= lastUsage + MEMORY_THRESHOLD) {
      final String threadName = Thread.currentThread ().getName ();
      System.out.println ("%%% [" + threadName + "] Memory usage: " + memoryStatus);
      lastUsage = usedInMegabytes;
    }

    return memoryStatus;
  }
}

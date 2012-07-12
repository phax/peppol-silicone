package eu.peppol.outbound.api;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3._2009._02.ws_tra.Create;
import org.w3c.dom.Document;

import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;

import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.outbound.smp.SmpLookupManager;
import eu.peppol.outbound.soap.SoapDispatcher;
import eu.peppol.outbound.util.Log;

/**
 * The Oxalis START outbound module contains all necessary code for sending
 * PEPPOL business documents via the START protocol to a receiving Access Point.
 * <p/>
 * A DocumentSender is the publicly available interface class for sending
 * documents. A particular DocumentSender is dedicated to a particular document
 * and process type. The class is thread-safe.
 * <p/>
 * There are 2 main variants of the sendInvoice method. The first variant uses
 * SMP to find the destination AP. If the SMP lookup fails then the document
 * will not be sent. The second variant sends a document to a specified AP. In
 * this case eu SMP lookup is involved.
 * <p/>
 * User: nigel Date: Oct 17, 2011 Time: 4:42:01 PM
 */
public class DocumentSender {

  private final DocumentIdentifierType m_aDocumentTypeIdentifier;
  private final ProcessIdentifierType m_aPeppolProcessTypeId;
  private final boolean m_bSoapLogging;
  private final SoapDispatcher m_aSoapDispatcher;

  DocumentSender (final DocumentIdentifierType documentTypeIdentifier,
                  final ProcessIdentifierType processId,
                  final boolean soapLogging) {
    this.m_aDocumentTypeIdentifier = documentTypeIdentifier;
    this.m_aPeppolProcessTypeId = processId;
    this.m_bSoapLogging = soapLogging;
    this.m_aSoapDispatcher = new SoapDispatcher ();
  }

  /**
   * sends a PEPPOL business document to a named recipient. The Access Point of
   * the recipient will be identified by SMP lookup.
   * 
   * @param xmlDocument
   *        the PEPPOL business document to be sent
   * @param sender
   *        the participant id of the document sender
   * @param recipient
   *        the participant id of the document receiver
   * @param channelId
   *        holds the PEPPOL ChannelID to be used
   * @return message id assigned
   */
  public String sendInvoice (final InputStream xmlDocument,
                             final String sender,
                             final String recipient,
                             final String channelId) {
    return sendInvoice (xmlDocument, sender, recipient, getEndpointAddress (recipient), channelId);
  }

  /**
   * sends a PEPPOL business document to a named recipient. The Access Point of
   * the recipient will be identified by SMP lookup.
   * 
   * @param xmlDocument
   *        the PEPPOL business document to be sent
   * @param sender
   *        the participant id of the document sender
   * @param recipient
   *        the participant id of the document receiver
   * @param channelId
   *        holds the PEPPOL ChannelID to be used
   * @return message id assigned
   */
  public String sendInvoice (final File xmlDocument, final String sender, final String recipient, final String channelId) {
    return sendInvoice (xmlDocument, sender, recipient, getEndpointAddress (recipient), channelId);
  }

  /**
   * sends a PEPPOL business document to a named recipient. The destination
   * parameter specifies the address of the recipients Access Point. No SMP
   * lookup will be involved.
   * 
   * @param xmlDocument
   *        the PEPPOL business document to be sent
   * @param sender
   *        the participant id of the document sender
   * @param recipient
   *        the participant id of the document receiver
   * @param destination
   *        the address of the recipient's access point
   * @param channelId
   *        holds the PEPPOL ChannelID to be used
   * @return message id assigned
   */
  public String sendInvoice (final InputStream xmlDocument,
                             final String sender,
                             final String recipient,
                             final URL destination,
                             final String channelId) {
    log (destination);
    Document document;
    try {
      document = XMLReader.readXMLDOM (xmlDocument);
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to parse xml document from " + sender + " to " + recipient + "; " + e, e);
    }
    return send (document, sender, recipient, destination, channelId);
  }

  /**
   * sends a PEPPOL business document to a named recipient. The destination
   * parameter specifies the address of the recipients Access Point. No SMP
   * lookup will be involved.
   * 
   * @param xmlDocument
   *        the PEPPOL business document to be sent
   * @param sender
   *        the participant id of the document sender
   * @param recipient
   *        the participant id of the document receiver
   * @param destination
   *        the address of the recipient's access point
   * @param channelId
   *        holds the PEPPOL ChannelID to be used
   * @return message id (UUID) assigned
   */
  public String sendInvoice (final File xmlDocument,
                             final String sender,
                             final String recipient,
                             final URL destination,
                             final String channelId) {
    log (destination);
    Document document;
    try {
      document = XMLReader.readXMLDOM (xmlDocument);
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to parse XML Document in file " + xmlDocument + "; " + e, e);
    }
    return send (document, sender, recipient, destination, channelId);
  }

  private URL getEndpointAddress (final String recipient) {
    return SmpLookupManager.getEndpointAddress (getParticipantId (recipient), m_aDocumentTypeIdentifier);
  }

  private static ParticipantIdentifierType getParticipantId (final String sender) {
    final SimpleParticipantIdentifier aID = SimpleParticipantIdentifier.createWithDefaultScheme (sender);
    if (!aID.isValid ())
      throw new IllegalArgumentException ("Invalid participant " + sender);

    return aID;
  }

  private void log (final URL destination) {
    Log.info ("Document destination is " + destination);
  }

  private String send (final Document document,
                       final String sender,
                       final String recipient,
                       final URL destination,
                       final String channelId) {
    System.setProperty ("com.sun.xml.ws.client.ContentNegotiation", "none");
    System.setProperty ("com.sun.xml.wss.debug", "FaultDetail");

    Log.debug ("Constructing document body");
    final ParticipantIdentifierType senderId = getParticipantId (sender);
    final ParticipantIdentifierType recipientId = getParticipantId (recipient);
    final Create soapBody = new Create ();
    soapBody.getAny ().add (document.getDocumentElement ());

    Log.debug ("Constructing SOAP header");
    final IMessageMetadata messageHeader = new MessageMetadata ("uuid:" + UUID.randomUUID ().toString (),
                                                                channelId,
                                                                senderId,
                                                                recipientId,
                                                                m_aDocumentTypeIdentifier,
                                                                m_aPeppolProcessTypeId);

    m_aSoapDispatcher.enableSoapLogging (m_bSoapLogging);

    m_aSoapDispatcher.send (destination, messageHeader, soapBody);

    return messageHeader.getMessageID ();
  }
}

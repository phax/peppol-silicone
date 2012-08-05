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
package eu.peppol.outbound.api;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.peppol.busdox.CBusDox;
import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;
import at.peppol.transport.start.client.AccessPointClient;

import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.outbound.smp.SmpLookupManager;

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
public final class DocumentSender {
  private static final Logger s_aLogger = LoggerFactory.getLogger ("oxalis-out");

  private final IReadonlyDocumentTypeIdentifier m_aDocumentTypeID;
  private final IReadonlyProcessIdentifier m_aProcessID;
  private final boolean m_bSoapLogging;

  DocumentSender (final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                  final IReadonlyProcessIdentifier aProcessId,
                  final boolean bSoapLogging) {
    m_aDocumentTypeID = aDocumentTypeID;
    m_aProcessID = aProcessId;
    m_bSoapLogging = bSoapLogging;
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
  @Nullable
  public String sendInvoice (final InputStream xmlDocument,
                             final String sender,
                             final String recipient,
                             final String channelId) {
    return sendInvoice (xmlDocument, sender, recipient, _getEndpointAddress (recipient), channelId);
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
  @Nullable
  public String sendInvoice (final File xmlDocument, final String sender, final String recipient, final String channelId) {
    return sendInvoice (xmlDocument, sender, recipient, _getEndpointAddress (recipient), channelId);
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
  @Nullable
  public String sendInvoice (final InputStream xmlDocument,
                             final String sender,
                             final String recipient,
                             final URL destination,
                             final String channelId) {
    Document document;
    try {
      document = XMLReader.readXMLDOM (xmlDocument);
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to parse xml document from " + sender + " to " + recipient, e);
    }
    return _send (document, sender, recipient, destination, channelId);
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
    Document document;
    try {
      document = XMLReader.readXMLDOM (xmlDocument);
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to parse XML Document in file " + xmlDocument, e);
    }
    return _send (document, sender, recipient, destination, channelId);
  }

  private URL _getEndpointAddress (final String recipient) {
    return SmpLookupManager.getEndpointAddress (_getParticipantId (recipient), m_aDocumentTypeID, m_aProcessID);
  }

  @Nonnull
  private static ParticipantIdentifierType _getParticipantId (final String sender) {
    final SimpleParticipantIdentifier aID = SimpleParticipantIdentifier.createWithDefaultScheme (sender);
    if (!aID.isValid ())
      throw new IllegalArgumentException ("Invalid participant " + sender);

    return aID;
  }

  private String _send (final Document document,
                        final String sender,
                        final String recipient,
                        final URL destination,
                        final String channelId) {
    System.setProperty ("com.sun.xml.ws.client.ContentNegotiation", "none");
    System.setProperty ("com.sun.xml.wss.debug", "FaultDetail");

    s_aLogger.debug ("Constructing document body");
    final ParticipantIdentifierType senderId = _getParticipantId (sender);
    final ParticipantIdentifierType recipientId = _getParticipantId (recipient);

    s_aLogger.debug ("Constructing SOAP header");
    final IMessageMetadata messageHeader = new MessageMetadata ("uuid:" + UUID.randomUUID ().toString (),
                                                                channelId,
                                                                senderId,
                                                                recipientId,
                                                                m_aDocumentTypeID,
                                                                m_aProcessID);

    CBusDox.enableSoapLogging (m_bSoapLogging);
    if (AccessPointClient.send (destination.toExternalForm (), messageHeader, document).isFailure ())
      return null;
    return messageHeader.getMessageID ();
  }
}

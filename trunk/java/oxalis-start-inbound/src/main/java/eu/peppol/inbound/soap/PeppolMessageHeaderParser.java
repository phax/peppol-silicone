package eu.peppol.inbound.soap;

import static eu.peppol.start.identifier.IdentifierName.CHANNEL_ID;
import static eu.peppol.start.identifier.IdentifierName.DOCUMENT_ID;
import static eu.peppol.start.identifier.IdentifierName.MESSAGE_ID;
import static eu.peppol.start.identifier.IdentifierName.PROCESS_ID;
import static eu.peppol.start.identifier.IdentifierName.RECIPIENT_ID;
import static eu.peppol.start.identifier.IdentifierName.SENDER_ID;

import javax.xml.namespace.QName;

import at.peppol.commons.identifier.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;

import com.sun.xml.ws.api.message.HeaderList;

import eu.peppol.start.identifier.IdentifierName;
import eu.peppol.start.identifier.PeppolMessageHeader;

/**
 * Parses the PEPPOL SOAP Headers into a simple structure, which contains the
 * meta data for the message being transferred.
 * 
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 04.12.11 Time: 19:47
 */
public class PeppolMessageHeaderParser {

  private static final String NAMESPACE_TRANSPORT_IDS = "http://busdox.org/transport/identifiers/1.0/";

  public static PeppolMessageHeader parseSoapHeaders (final HeaderList headerList) {
    final PeppolMessageHeader m = new PeppolMessageHeader ();

    m.setMessageId (getContent (headerList, MESSAGE_ID));
    m.setChannelId (getContent (headerList, CHANNEL_ID));
    m.setRecipientId (SimpleParticipantIdentifier.createWithDefaultScheme (getContent (headerList,
                                                                                       RECIPIENT_ID.stringValue ())));
    m.setSenderId (SimpleParticipantIdentifier.createWithDefaultScheme (getContent (headerList,
                                                                                    SENDER_ID.stringValue ())));
    m.setDocumentTypeIdentifier (SimpleDocumentTypeIdentifier.createWithDefaultScheme (getContent (headerList,
                                                                                                   DOCUMENT_ID)));
    m.setPeppolProcessTypeId (SimpleProcessIdentifier.createWithDefaultScheme (getContent (headerList, PROCESS_ID)));

    return m;
  }

  private static QName getQName (final IdentifierName identifierName) {
    return getQName (identifierName.stringValue ());
  }

  private static QName getQName (final String headerName) {
    return new QName (NAMESPACE_TRANSPORT_IDS, headerName);
  }

  private static String getContent (final HeaderList headerList, final IdentifierName identifierName) {
    return headerList.get (getQName (identifierName), false).getStringContent ();
  }

  private static String getContent (final HeaderList headerList, final String identifierName) {
    return headerList.get (getQName (identifierName), false).getStringContent ();
  }
}

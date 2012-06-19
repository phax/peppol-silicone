package eu.peppol.start.identifier;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 04.12.11 Time: 18:43
 */
public class PeppolMessageHeader {

  String messageId;
  String channelId;
  ParticipantIdentifierType recipientId;
  ParticipantIdentifierType senderId;
  DocumentIdentifierType documentTypeIdentifier;
  ProcessIdentifierType peppolProcessTypeId;

  public String getMessageId () {
    return messageId;
  }

  public void setMessageId (final String messageId) {
    this.messageId = messageId;
  }

  public String getChannelId () {
    return channelId;
  }

  public void setChannelId (final String channelId) {
    this.channelId = channelId;
  }

  public ParticipantIdentifierType getRecipientId () {
    return recipientId;
  }

  public void setRecipientId (final ParticipantIdentifierType recipientId) {
    this.recipientId = recipientId;
  }

  public ParticipantIdentifierType getSenderId () {
    return senderId;
  }

  public void setSenderId (final ParticipantIdentifierType senderId) {
    this.senderId = senderId;
  }

  public DocumentIdentifierType getDocumentTypeIdentifier () {
    return documentTypeIdentifier;
  }

  public void setDocumentTypeIdentifier (final DocumentIdentifierType documentTypeIdentifier) {
    this.documentTypeIdentifier = documentTypeIdentifier;
  }

  public ProcessIdentifierType getPeppolProcessTypeId () {
    return peppolProcessTypeId;
  }

  public void setPeppolProcessTypeId (final ProcessIdentifierType peppolProcessTypeId) {
    this.peppolProcessTypeId = peppolProcessTypeId;
  }

  @Override
  public String toString () {
    final StringBuilder sb = new StringBuilder ();
    sb.append ("PeppolMessageHeader");
    sb.append ("{messageId=").append (messageId);
    sb.append (", channelId=").append (channelId);
    sb.append (", recipientId=").append (recipientId);
    sb.append (", senderId=").append (senderId);
    sb.append (", documentTypeIdentifier=").append (documentTypeIdentifier);
    sb.append (", peppolProcessTypeId=").append (peppolProcessTypeId);
    sb.append ('}');
    return sb.toString ();
  }
}

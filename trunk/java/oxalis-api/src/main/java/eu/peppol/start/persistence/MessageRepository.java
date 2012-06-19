package eu.peppol.start.persistence;

import org.w3c.dom.Document;

import at.peppol.transport.IMessageMetadata;

/**
 * Repository of messages received. The access point will invoke objects
 * implementing this interface once and only once upon initialization.
 * <p>
 * Implementations are required to be thread safe.
 * </p>
 * 
 * @author Steinar Overbeck Cook Created by User: steinar Date: 28.11.11 Time:
 *         20:55
 */
public interface MessageRepository {

  /**
   * Saves the supplied message after successful reception, using the given
   * arguments.
   * 
   * @param inboundMessageStore
   *        the full path to the directory in which the inbound messages should
   *        be stored. The value of this parameter is specified by the property
   *        <code>oxalis.inbound.message.store</code> and
   *        <code>oxalis.outbound.message.store</code>, which may be configured
   *        either as a system property, in <code>oxalis.properties</code> or
   *        <code>oxalis-web.properties</code>
   * @param peppolMessageHeader
   *        represents the message headers used for routing
   * @param document
   *        represents the message received, which should be persisted.
   */
  void saveInboundMessage (String inboundMessageStore, IMessageMetadata peppolMessageHeader, Document document);
}

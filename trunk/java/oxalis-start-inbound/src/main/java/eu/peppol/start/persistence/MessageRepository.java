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

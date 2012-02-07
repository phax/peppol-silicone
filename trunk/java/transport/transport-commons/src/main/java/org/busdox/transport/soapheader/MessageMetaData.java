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
package org.busdox.transport.soapheader;

import java.util.Date;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class MessageMetaData {
  private ParticipantIdentifierType m_aSender;
  private ParticipantIdentifierType m_aRecipient;
  private DocumentIdentifierType m_aDocumentInfoType;
  private ProcessIdentifierType m_aProcessType;
  private String m_sMessageID;
  private String m_sChannelID;
  private final Date m_aCreateDate = new Date ();

  public MessageMetaData (final ParticipantIdentifierType sender,
                          final ParticipantIdentifierType recipient,
                          final DocumentIdentifierType documentInfoType,
                          final ProcessIdentifierType processType,
                          final String messageID,
                          final String channelID) {
    m_aSender = sender;
    m_aRecipient = recipient;
    m_aDocumentInfoType = documentInfoType;
    m_aProcessType = processType;
    m_sMessageID = messageID;
    m_sChannelID = channelID;
  }

  public void setMessageID (final String messageID) {
    m_sMessageID = messageID;
  }

  public String getChannelID () {
    return m_sChannelID;
  }

  public Date getCreateDate () {
    return m_aCreateDate;
  }

  public String getMessageID () {
    return m_sMessageID;
  }

  public DocumentIdentifierType getDocumentInfoType () {
    return m_aDocumentInfoType;
  }

  public ProcessIdentifierType getProcessType () {
    return m_aProcessType;
  }

  public ParticipantIdentifierType getRecipient () {
    return m_aRecipient;
  }

  public ParticipantIdentifierType getSender () {
    return m_aSender;
  }

  public void setRecipient (final ParticipantIdentifierType recipient) {
    m_aRecipient = recipient;
  }

  public void setSender (final ParticipantIdentifierType sender) {
    m_aSender = sender;
  }

  public void setDocumentInfoType (final DocumentIdentifierType documentInfoType) {
    m_aDocumentInfoType = documentInfoType;
  }

  public void setProcessType (final ProcessIdentifierType processType) {
    m_aProcessType = processType;
  }

  public void setChannelID (final String channelID) {
    m_sChannelID = channelID;
  }
}

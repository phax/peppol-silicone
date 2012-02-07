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
package eu.peppol.start;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;

import com.phloc.commons.compare.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

import eu.peppol.busdox.identifier.SimpleDocumentIdentifier;
import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;

/**
 * A MessageMetadata object is used to storage the message addressing data
 * incoming in the SOAP header through a SOAPHeaderObject object.
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class MessageMetadata implements IMessageMetadata {
  /** The message identifier. */
  private final String m_sMessageID;

  /** The channel identifier. */
  private final String m_sChannelID;

  /** Sender identifier value. */
  private final SimpleParticipantIdentifier m_aSenderID;

  /** Recipient identifier value. */
  private final SimpleParticipantIdentifier m_aRecipientID;

  /** Document identifier value. */
  private final SimpleDocumentIdentifier m_aDocumentID;

  /** Process identifier value. */
  private final SimpleProcessIdentifier m_aProcessID;

  /**
   * Set the values of the MessageMetadata object received in a SOAPHeaderObject
   * object.
   *
   * @param sMessageID
   *        message ID
   * @param sChannelID
   *        channel ID
   * @param aSender
   *        sender ID
   * @param aRecipient
   *        recipient ID
   * @param aDocumentID
   *        document type ID
   * @param aProcessID
   *        process ID
   */
  public MessageMetadata (@Nullable final String sMessageID,
                          @Nullable final String sChannelID,
                          @Nonnull final IReadonlyParticipantIdentifier aSender,
                          @Nonnull final IReadonlyParticipantIdentifier aRecipient,
                          @Nonnull final IReadonlyDocumentIdentifier aDocumentID,
                          @Nonnull final IReadonlyProcessIdentifier aProcessID) {
    m_sMessageID = sMessageID;
    m_sChannelID = sChannelID;
    m_aSenderID = new SimpleParticipantIdentifier (aSender);
    m_aRecipientID = new SimpleParticipantIdentifier (aRecipient);
    m_aDocumentID = new SimpleDocumentIdentifier (aDocumentID);
    m_aProcessID = new SimpleProcessIdentifier (aProcessID);
  }

  /**
   * Get message identifier value.
   *
   * @return the messageId the value of the message identifier.
   */
  @Nullable
  public final String getMessageID () {
    return m_sMessageID;
  }

  /**
   * @return the channelId
   */
  @Nullable
  public final String getChannelID () {
    return m_sChannelID;
  }

  /**
   * @return the senderValue
   */
  @Nonnull
  public final ParticipantIdentifierType getSenderID () {
    return m_aSenderID;
  }

  /**
   * @return the recipientValue
   */
  @Nonnull
  public final ParticipantIdentifierType getRecipientID () {
    return m_aRecipientID;
  }

  /**
   * @return the documentIdValue
   */
  @Nonnull
  public final DocumentIdentifierType getDocumentID () {
    return m_aDocumentID;
  }

  /**
   * @return the processIdValue
   */
  @Nonnull
  public final ProcessIdentifierType getProcessID () {
    return m_aProcessID;
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof MessageMetadata))
      return false;
    final MessageMetadata rhs = (MessageMetadata) o;
    return EqualsUtils.nullSafeEquals (m_sMessageID, rhs.m_sMessageID) &&
           EqualsUtils.nullSafeEquals (m_sChannelID, rhs.m_sChannelID) &&
           m_aSenderID.equals (rhs.m_aSenderID) &&
           m_aRecipientID.equals (rhs.m_aRecipientID) &&
           m_aDocumentID.equals (rhs.m_aDocumentID) &&
           m_aProcessID.equals (rhs.m_aProcessID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sMessageID)
                                       .append (m_sChannelID)
                                       .append (m_aSenderID)
                                       .append (m_aRecipientID)
                                       .append (m_aDocumentID)
                                       .append (m_aProcessID)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("messageID", m_sMessageID)
                                       .append ("channelID", m_sChannelID)
                                       .append ("senderID", m_aSenderID)
                                       .append ("recipientID", m_aRecipientID)
                                       .append ("documentID", m_aDocumentID)
                                       .append ("processID", m_aProcessID)
                                       .toString ();
  }
}

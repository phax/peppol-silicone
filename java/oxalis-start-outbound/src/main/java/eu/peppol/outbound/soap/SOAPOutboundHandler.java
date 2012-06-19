/*
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
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
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
package eu.peppol.outbound.soap;

import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3._2009._02.ws_tra.ObjectFactory;

import at.peppol.commons.identifier.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.transport.IMessageMetadata;

import com.phloc.commons.jaxb.JAXBContextCache;

import eu.peppol.outbound.util.Log;

/**
 * The SOAPOutboundHandler class is used to handle an outbound SOAP message in
 * order to include the BUSDOX defined headers.
 * 
 * @author Dante Malaga(dante@alfa1lab.com) Jose Gorvenia
 *         Narvaez(jose@alfa1lab.com)
 */
@SuppressWarnings ({ "AccessStaticViaInstance" })
public class SOAPOutboundHandler implements SOAPHandler <SOAPMessageContext> {

  private final IMessageMetadata messageHeader;

  public SOAPOutboundHandler (final IMessageMetadata messageHeader) {
    this.messageHeader = messageHeader;
  }

  public Set <QName> getHeaders () {
    return null;
  }

  public boolean handleMessage (final SOAPMessageContext soapMessageContext) {

    try {

      Log.debug ("SOAP outbound handler called");
      addSoapHeader (soapMessageContext);
      return true;

    }
    catch (final Exception e) {
      throw new RuntimeException ("Error occurred while marshalling SOAP headers", e);
    }
  }

  private void addSoapHeader (final SOAPMessageContext soapMessageContext) throws SOAPException, JAXBException {

    final Boolean isOutboundMessage = (Boolean) soapMessageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    if (isOutboundMessage.booleanValue ()) {

      Log.debug ("Adding BUSDOX headers to SOAP-envelope");

      final SOAPEnvelope envelope = soapMessageContext.getMessage ().getSOAPPart ().getEnvelope ();
      SOAPHeader header = envelope.getHeader ();

      if (header == null) {
        header = envelope.addHeader ();
      }

      final ObjectFactory objectFactory = new ObjectFactory ();

      final String channelId = messageHeader.getChannelID ();
      final String messageId = messageHeader.getMessageID ();
      final ParticipantIdentifierType senderId = new SimpleParticipantIdentifier (messageHeader.getSenderID ());
      final ParticipantIdentifierType recipientId = new SimpleParticipantIdentifier (messageHeader.getRecipientID ());
      final DocumentIdentifierType documentId = new SimpleDocumentTypeIdentifier (messageHeader.getDocumentTypeID ());
      final ProcessIdentifierType processId = new SimpleProcessIdentifier (messageHeader.getProcessID ());

      Marshaller marshaller = JAXBContextCache.getInstance ().getFromCache (String.class).createMarshaller ();
      marshaller.marshal (objectFactory.createMessageIdentifier (messageId), new DOMResult (header));

      final JAXBElement <?> auxChannelId = objectFactory.createChannelIdentifier (channelId);
      auxChannelId.setNil (true);
      marshaller.marshal (auxChannelId, new DOMResult (header));

      marshaller = JAXBContextCache.getInstance ().getFromCache (ParticipantIdentifierType.class).createMarshaller ();
      marshaller.marshal (objectFactory.createRecipientIdentifier (recipientId), new DOMResult (header));
      marshaller.marshal (objectFactory.createSenderIdentifier (senderId), new DOMResult (header));

      marshaller = JAXBContextCache.getInstance ().getFromCache (DocumentIdentifierType.class).createMarshaller ();
      marshaller.marshal (objectFactory.createDocumentIdentifier (documentId), new DOMResult (header));

      marshaller = JAXBContextCache.getInstance ().getFromCache (ProcessIdentifierType.class).createMarshaller ();
      marshaller.marshal (objectFactory.createProcessIdentifier (processId), new DOMResult (header));
    }
  }

  public boolean handleFault (final SOAPMessageContext context) {
    return true;
  }

  public void close (final MessageContext context) {}
}

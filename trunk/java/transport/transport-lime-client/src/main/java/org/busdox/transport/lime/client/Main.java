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
package org.busdox.transport.lime.client;

import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.busdox.CBusDox;
import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;
import org.busdox.transport.lime.api.impl.Factory;
import org.busdox.transport.lime.api.interfaces.EndpointReferenceInterface;
import org.busdox.transport.lime.api.interfaces.InboxInterface;
import org.busdox.transport.lime.api.interfaces.MessageException;
import org.busdox.transport.lime.api.interfaces.MessageInterface;
import org.busdox.transport.lime.api.interfaces.MessageReferenceInterface;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.busdox.identifier.ReadonlyParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleDocumentIdentifier;
import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;
import eu.peppol.busdox.identifier.docid.EPredefinedDocumentIdentifier;
import eu.peppol.busdox.identifier.procid.EPredefinedProcessIdentifier;
import eu.peppol.common.IReadonlyUsernamePWCredentials;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class Main {
  public static final int POLL_SLEEP_MS = 3000;
  private static boolean s_bLeaveMessages = false;
  private static final IReadonlyParticipantIdentifier SENDER = ReadonlyParticipantIdentifier.createWithDefaultScheme ("9914:atu415427xx");
  private static final IReadonlyParticipantIdentifier RECEIVER = ReadonlyParticipantIdentifier.createWithDefaultScheme ("9914:iwannatest");
  private static final IReadonlyDocumentIdentifier DOCID = EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Order_2__Order__urn_www_cenbii_eu_transaction_biicoretrdm001_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0;

  /*
   * ("busdox-docid-qns",
   * "urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple"
   * + "##urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0:" +
   * "#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0");
   */

  private static final IReadonlyProcessIdentifier PROCESS = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii06_ver1_0;

  // ("cenbii-procid-ubl", "urn:www.cenbii.eu:profile:bii06:ver1.0");

  public static void main (final String [] args) throws Exception {
    CBusDox.setMetroDebugSystemProperties (true);
    final String apUrl2 = "http://localhost:8080/busdox-transport-lime-server/wstransferService";
    // any xml will do
    final String xmlFile = "src/test/java/xml/CENBII-Order-maximal.xml";

    new Main ();
    Main.testSend (apUrl2, xmlFile, SENDER, RECEIVER);
    /*
     * new Main().testReadAndDelete(apUrl1, receiver); new
     * Main().testMessageUndeliverable(apUrl2, xmlFile, sender, receiver);
     */
  }

  @SuppressWarnings ("unused")
  private static void testReadAndDelete (final String apUrl, final IReadonlyIdentifier receiverID) throws Exception {
    final String channelID = receiverID.getValue ();
    final EndpointReferenceInterface endpointReference = Factory.createEndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    testPollForMessages (endpointReference, s_bLeaveMessages);
  }

  @SuppressWarnings ("unused")
  private static void testMessageUndeliverable (final String apUrl,
                                                final String xmlFilename,
                                                final IReadonlyParticipantIdentifier senderID,
                                                final IReadonlyParticipantIdentifier receiverID) throws Exception {
    final IReadonlyParticipantIdentifier unFindable = new SimpleParticipantIdentifier (receiverID.getScheme (),
                                                                                       receiverID.getValue () +
                                                                                           "UNKNOWN");
    final String channelID = senderID.getValue ();
    final EndpointReferenceInterface endpointReference = Factory.createEndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    try {
      final MessageInterface message = createSampleMessage (xmlFilename, senderID, unFindable, DOCID, PROCESS);
      testSendMessage (message, endpointReference);
    }
    catch (final Exception e) {
      // Exception is OK. The method sendMessage fails because of invalid
      // recipient
    }

    final String lastMessage = testGetLastMessage (endpointReference);
    testGetMessage (lastMessage, endpointReference);
    testDeleteMessage (lastMessage, endpointReference);
  }

  private static String testSend (final String apUrl,
                                  final String xmlFilename,
                                  final IReadonlyParticipantIdentifier senderID,
                                  final IReadonlyParticipantIdentifier receiverID) throws Exception {
    final String channelID = senderID.getValue ();
    final EndpointReferenceInterface endpointReference = Factory.createEndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    final MessageInterface message = createSampleMessage (xmlFilename, senderID, receiverID, DOCID, PROCESS);
    final String messageID = testSendMessage (message, endpointReference);
    return messageID;
  }

  private static IReadonlyUsernamePWCredentials createCredentials () {

    return Factory.createCredentials ("peppol", "peppol");
  }

  private static String testSendMessage (final MessageInterface message,
                                         final EndpointReferenceInterface endpointReference) throws MessageException {
    String messageid = null;
    for (int i = 0; i < 1; i++) {

      messageid = Factory.createOutbox ().sendMessage (createCredentials (), message, endpointReference);
      System.out.println ("OUTBOX - MESSAGE DELIVERED: " + messageid);
    }
    return messageid;
  }

  private static void testGetMessage (final String messageID, final EndpointReferenceInterface endpointReference) throws MessageException,
                                                                                                                 TransformerException,
                                                                                                                 TransformerFactoryConfigurationError {
    final MessageReferenceInterface messageReference = Factory.createMessageReference ();
    messageReference.setMessageId (messageID);
    messageReference.setEndpointReference (endpointReference);
    final MessageInterface fetchedMessage = Factory.createInbox ().getMessage (createCredentials (), messageReference);
    if (fetchedMessage != null) {
      System.out.println ("INBOX - MESSAGE: " + messageID);
      System.out.println (fetchedMessage);
      streamMessage (fetchedMessage, System.out);
    }
    else {
      System.out.println ("INBOX - MESSAGE NOT FOUND: " + messageID);
    }
  }

  private static String testGetLastMessage (final EndpointReferenceInterface endpointReference) throws MessageException,
                                                                                               TransformerFactoryConfigurationError {
    String lastMessage = null;
    final List <MessageReferenceInterface> messageReferences = Factory.createInbox ()
                                                                      .getMessageList (createCredentials (),
                                                                                       endpointReference);
    if (messageReferences != null && messageReferences.size () > 0) {
      for (final MessageReferenceInterface messageReference : messageReferences) {
        System.out.println ("INBOX - MESSAGE: " + messageReference.getMessageID ());
        lastMessage = messageReference.getMessageID ();
      }
    }
    else {
      System.out.println ("INBOX - NO MESSAGES");
    }
    return lastMessage;
  }

  private static void testDeleteMessage (final String messageID, final EndpointReferenceInterface endpointReference) throws MessageException,
                                                                                                                    TransformerFactoryConfigurationError {
    final MessageReferenceInterface messageReference = Factory.createMessageReference ();
    messageReference.setMessageId (messageID);
    messageReference.setEndpointReference (endpointReference);
    Factory.createInbox ().deleteMessage (createCredentials (), messageReference);
    System.out.println ("INBOX - MESSAGE DELETED: " + messageID);
  }

  private static void testPollForMessages (final EndpointReferenceInterface endpointReference,
                                           final boolean leaveMessages) throws TransformerConfigurationException,
                                                                       TransformerException {
    final InboxInterface inbox = Factory.createInbox ();
    final long millis = POLL_SLEEP_MS;
    try {
      final List <MessageReferenceInterface> messageReferences = inbox.getMessageList (createCredentials (),
                                                                                       endpointReference);
      if (messageReferences != null && messageReferences.size () > 0) {
        System.out.println ("INBOX - RETRIEVED " +
                            messageReferences.size () +
                            " MESSAGES AT " +
                            (new Date ()).toString ());
        final int index = 1;
        for (final MessageReferenceInterface messageReference : messageReferences) {
          try {
            final MessageInterface message = inbox.getMessage (createCredentials (), messageReference);
            streamMessage (message, System.out);
            System.out.println ("INBOX - MESSAGE (" + index + "/" + messageReferences.size () + ")");
            System.out.println (message);
            if (!leaveMessages) {
              // clean up
              inbox.deleteMessage (createCredentials (), messageReference);
              System.out.println ("INBOX - MESSAGE DELETED: " + message.getMessageID ());
            }
            System.out.println ();
          }
          catch (final MessageException ex) {
            System.out.println ("INBOX - MESSAGE GET THROWS EXCEPTION: " + ex.getMessage ());
          }
        }
        Thread.sleep (millis);
      }
      else {
        System.out.println ("INBOX - EMPTY");
      }
    }
    catch (final InterruptedException ex) {
      System.out.println ("Caught InterruptedException: " + ex.getMessage ());
    }
    catch (final MessageException ex) {
      System.out.println ("Caught MessageException: " + ex.getMessage ());
    }
  }

  private static Document loadXMLFromFile (final String filename) throws SAXException {
    return XMLReader.readXMLDOM (new FileSystemResource (filename));
  }

  private static MessageInterface createSampleMessage (final String xmlFilename,
                                                       final IReadonlyParticipantIdentifier senderID,
                                                       final IReadonlyParticipantIdentifier receiverID,
                                                       final IReadonlyDocumentIdentifier documentID,
                                                       final IReadonlyProcessIdentifier processID) throws SAXException {

    final MessageInterface message = Factory.createMessage ();
    message.setDocument (loadXMLFromFile (xmlFilename));
    message.setDocumentType (new SimpleDocumentIdentifier (documentID));
    message.setSender (new SimpleParticipantIdentifier (senderID));
    message.setReciever (new SimpleParticipantIdentifier (receiverID));
    message.setProcessType (new SimpleProcessIdentifier (processID));
    return message;
  }

  private static void streamMessage (final MessageInterface fetchedMessage, final PrintStream out) throws TransformerConfigurationException,
                                                                                                  TransformerException {
    final TransformerFactory transformerFactory = TransformerFactory.newInstance ();
    final Transformer transformer = transformerFactory.newTransformer ();
    final DOMSource source = new DOMSource (fetchedMessage.getDocument ());
    final StreamResult result = new StreamResult (out);
    transformer.transform (source, result);
  }
}

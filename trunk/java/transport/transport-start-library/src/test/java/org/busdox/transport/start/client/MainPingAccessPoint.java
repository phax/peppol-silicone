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
package org.busdox.transport.start.client;

import java.util.List;
import java.util.UUID;

import org.busdox.CBusDox;
import org.busdox.transport.base.Identifiers;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.smp.MetadataPublisherClient;
import org.busdox.transport.soapheader.MessageMetaData;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.Resource;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.peppol.busdox.identifier.SimpleDocumentIdentifier;
import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;
import eu.peppol.busdox.sml.ESML;
import eu.peppol.busdox.sml.ISMLInfo;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger<br>
 *         PEPPOL.AT, BRZ, Andreas Haberl
 */
public class MainPingAccessPoint {
  private static final ISMLInfo SML_INFO = ESML.PRODUCTION;

  public static void main (final String [] args) throws Exception {
    // enable debugging info
    CBusDox.setMetroDebugSystemProperties (true);
    _pingAccessPoint (new ClassPathResource ("test.xml"));
  }

  private static void _pingAccessPoint (final IReadableResource xmlFile) throws Exception {
    MessageMetaData messageMetaData = _createMessageMetaData ();
    final String apURL = true ? "http://localhost:8090/accesspointService" : _getAccessPointUrl (messageMetaData);
    messageMetaData = _createPingMessageMetaData ();
    _testService (apURL, xmlFile, messageMetaData);
  }

  private static MessageMetaData _createMessageMetaData () {
    final String senderValue = "0010:5798000000002";
    final String recipientValue = "0010:5798000000004";
    final String documentIdValue = "AcceptCatalogue##UBL-2.0";
    final String processIdValue = "BII01";

    final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme (senderValue);
    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (recipientValue);
    final DocumentIdentifierType aDocumentType = SimpleDocumentIdentifier.createWithDefaultScheme (documentIdValue);
    final ProcessIdentifierType aProcessIdentifierType = SimpleProcessIdentifier.createWithDefaultScheme (processIdValue);
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    final MessageMetaData soapHdr = new MessageMetaData (aSender,
                                                         aRecipient,
                                                         aDocumentType,
                                                         aProcessIdentifierType,
                                                         sMessageID,
                                                         null);
    return soapHdr;
  }

  private static String _getAccessPointUrl (final MessageMetaData aMessageMetadata) throws Exception {
    final MetadataPublisherClient metadataPublisherClient = new MetadataPublisherClient (SML_INFO.getDNSZone (),
                                                                                         aMessageMetadata.getRecipient (),
                                                                                         aMessageMetadata.getDocumentInfoType ());
    return metadataPublisherClient.getEndpointAddress (aMessageMetadata.getProcessType ());
  }

  private static MessageMetaData _createPingMessageMetaData () {
    final ParticipantIdentifierType sender = Identifiers.PING_SENDER;
    final ParticipantIdentifierType recipient = Identifiers.PING_RECIPIENT;
    final DocumentIdentifierType documentType = Identifiers.PING_DOCUMENT;
    final ProcessIdentifierType processIdentifierType = Identifiers.PING_PROCESS;
    final String messageID = "uuid:" + UUID.randomUUID ().toString ();
    final MessageMetaData soapHdr = new MessageMetaData (sender,
                                                         recipient,
                                                         documentType,
                                                         processIdentifierType,
                                                         messageID,
                                                         null);
    return soapHdr;
  }

  private static void _testService (final String accessPointURLstr,
                                    final IReadableResource xmlFile,
                                    final MessageMetaData messageMetadata) throws Exception {
    final Create createBody = new Create ();
    final List <Object> objects = createBody.getAny ();
    objects.add (XMLReader.readXMLDOM (xmlFile.getInputStream ()).getDocumentElement ());

    final AccessPointClient ap = new AccessPointClient ();
    final Resource port = ap.getPort (accessPointURLstr);
    ap.send (port, messageMetadata, accessPointURLstr, createBody);
    ap.close (port);
  }
}

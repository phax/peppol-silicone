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
package at.peppol.transport.start.client;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;

import at.peppol.busdox.CBusDox;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.streams.StringInputStream;
import com.phloc.commons.xml.serialize.XMLReader;

/**
 * Test sending a document to a receiver
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class MainSendDocument {
  public static final String RECEIVER = "9914:ATU53309209";

  @Nonnull
  private static IMessageMetadata _createMetadata () {
    final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU00000000");
    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (RECEIVER);
    final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, "test-channel", aSender, aRecipient, aDocumentType, aProcessIdentifier);
  }

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aMetadata) throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aMetadata.getRecipientID (), ESML.PRODUCTION);
    // get service info
    return aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
                                              aMetadata.getDocumentTypeID (),
                                              aMetadata.getProcessID ());
  }

  private static void _sendDocument (final IReadableResource aXmlRes) throws Exception {
    final IMessageMetadata aMetadata = _createMetadata ();
    final String sAccessPointURL = false ? "http://localhost:8090/accessPointService" : _getAccessPointUrl (aMetadata);
    final Document aXMLDoc = XMLReader.readXMLDOM (aXmlRes);
    AccessPointClient.send (sAccessPointURL, aMetadata, aXMLDoc);
  }

  public static void main (final String [] args) throws Exception {
    System.setProperty ("java.net.useSystemProxies", "true");
    if (false) {
      System.setProperty ("http.proxyHost", "172.30.9.12");
      System.setProperty ("http.proxyPort", "8080");
      System.setProperty ("https.proxyHost", "172.30.9.12");
      System.setProperty ("https.proxyPort", "8080");
    }

    // enable debugging info?
    CBusDox.setMetroDebugSystemProperties (false);
    if (false) {
      // Debug logging
      SystemProperties.setPropertyValue ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                                         Boolean.toString (false));
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", "true");
      // Metro uses java.util.logging
      java.util.logging.LogManager.getLogManager ()
                                  .readConfiguration (new StringInputStream ("handlers=java.util.logging.ConsoleHandler\r\n"
                                                                                 + "java.util.logging.ConsoleHandler.level=FINEST",
                                                                             CCharset.CHARSET_ISO_8859_1));
      java.util.logging.Logger.getLogger ("com.sun.metro.rx").setLevel (java.util.logging.Level.FINER);
    }
    if (false) {
      // Metro debugging
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.client.after", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.endpoint.before", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.client.after", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before",
                                         "true");
    }
    _sendDocument (new ClassPathResource ("test.xml"));
  }
}

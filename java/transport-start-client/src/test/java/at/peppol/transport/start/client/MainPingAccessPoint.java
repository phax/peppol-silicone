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
import at.peppol.commons.sml.ISMLInfo;
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
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger<br>
 *         PEPPOL.AT, BRZ, Andreas Haberl
 */
public class MainPingAccessPoint {
  private static final ISMLInfo SML_INFO = ESML.PRODUCTION;

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aSOAPHeaderObject) throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aSOAPHeaderObject.getRecipientID (), SML_INFO);
    // get service info
    return aServiceCaller.getEndpointAddress (aSOAPHeaderObject.getRecipientID (),
                                              aSOAPHeaderObject.getDocumentTypeID (),
                                              aSOAPHeaderObject.getProcessID ());
  }

  @Nonnull
  private static IMessageMetadata _createPingMetadata () {
    final ParticipantIdentifierType sender = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU53309209XXX");
    final ParticipantIdentifierType recipient = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU53309209");
    final DocumentIdentifierType documentType = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    final ProcessIdentifierType processIdentifierType = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, null, sender, recipient, documentType, processIdentifierType);
  }

  private static void _testService (final String sAccessPointURLstr,
                                    final IReadableResource aBodyContent,
                                    final IMessageMetadata aMetadata) throws Exception {
    final Document aXMLDoc = XMLReader.readXMLDOM (aBodyContent.getInputStream ());
    AccessPointClient.send (sAccessPointURLstr, aMetadata, aXMLDoc);
  }

  private static void _pingAccessPoint (final IReadableResource xmlFile) throws Exception {
    final IMessageMetadata aMetadata = _createPingMetadata ();
    final String apURL = false ? "http://localhost:8090/accessPointService" : _getAccessPointUrl (aMetadata);
    _testService (apURL, xmlFile, aMetadata);
  }

  public static void main (final String [] args) throws Exception {
    // enable debugging info
    CBusDox.setMetroDebugSystemProperties (false);
    if (false) {
      SystemProperties.setPropertyValue ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                                         Boolean.toString (false));
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", Boolean.toString (true));
      // Metro uses java.util.logging
      java.util.logging.LogManager.getLogManager ()
                                  .readConfiguration (new StringInputStream ("handlers=java.util.logging.ConsoleHandler\r\n"
                                                                                 + "java.util.logging.ConsoleHandler.level=FINEST",
                                                                             CCharset.CHARSET_ISO_8859_1));
      java.util.logging.Logger.getLogger ("com.sun.metro.rx").setLevel (java.util.logging.Level.FINER);
    }
    if (true) {
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.client.after",
                                         Boolean.toString (true));
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.endpoint.before",
                                         Boolean.toString (true));
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.client.after",
                                         Boolean.toString (true));
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before",
                                         Boolean.toString (true));
    }
    _pingAccessPoint (new ClassPathResource ("test.xml"));
  }
}

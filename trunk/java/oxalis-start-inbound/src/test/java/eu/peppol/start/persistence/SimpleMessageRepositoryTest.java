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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.transport.MutableMessageMetadata;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 04.12.11 Time: 21:10
 */
public class SimpleMessageRepositoryTest {

  private MutableMessageMetadata peppolHeader;

  @Before
  public void createPeppolHeader () {
    peppolHeader = new MutableMessageMetadata ();
    peppolHeader.setSenderID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));
    peppolHeader.setRecipientID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));
  }

  @Test
  public void computeDirectoryNameForMessage () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    peppolHeader.setChannelID ("CH2");
    peppolHeader.setSenderID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));
    peppolHeader.setRecipientID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));

    final String tmpdir = "/tmpx";

    final File dirName = simpleMessageRepository.computeDirectoryNameForInboundMessage (tmpdir, peppolHeader);

    assertEquals ("Invalid directory name computed", dirName, new File (tmpdir + "/9908_976098897/CH2/9908_123456789"));
  }

  @Test
  public void computeDirectoryNameForMessageWithNoChannel () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    final String tmpdir = "/tmpx";

    createPeppolHeader (); // Weird stuff happening in TestNG, would have
                           // expected us to have a clean header using
                           // @BeforeTest
    final File dirName = simpleMessageRepository.computeDirectoryNameForInboundMessage (tmpdir, peppolHeader);
    assertEquals ("Invalid directory name computed", dirName, new File (tmpdir + "/9908_976098897/9908_123456789"));
  }

  @Test
  public void testPrepareMessageStore () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    final File tmp = new File ("/tmp/X");
    try {
      tmp.mkdirs ();
      System.err.println (tmp.toString ());
      System.err.flush ();
      final MutableMessageMetadata peppolMessageHeader = new MutableMessageMetadata ();
      peppolMessageHeader.setMessageID ("uuid:c5aa916d-9a1e-4ae8-ba25-0709ec913acb");
      peppolMessageHeader.setSenderID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));
      peppolMessageHeader.setRecipientID (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));

      simpleMessageRepository.prepareMessageDirectory (tmp.toString (), peppolMessageHeader);
    }
    finally {
      tmp.delete ();
    }
  }
}

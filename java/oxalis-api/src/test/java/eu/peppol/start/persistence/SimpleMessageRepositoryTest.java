package eu.peppol.start.persistence;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import eu.peppol.start.identifier.PeppolMessageHeader;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 04.12.11 Time: 21:10
 */
public class SimpleMessageRepositoryTest {

  private PeppolMessageHeader peppolHeader;

  @Before
  public void createPeppolHeader () {
    peppolHeader = new PeppolMessageHeader ();
    peppolHeader.setRecipientId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));
    peppolHeader.setSenderId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));
  }

  @Test
  public void computeDirectoryNameForMessage () {
    final SimpleMessageRepository simpleMessageRepository = new SimpleMessageRepository ();

    peppolHeader.setChannelId ("CH2");
    peppolHeader.setRecipientId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));
    peppolHeader.setSenderId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));

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
      final PeppolMessageHeader peppolMessageHeader = new PeppolMessageHeader ();
      peppolMessageHeader.setMessageId ("uuid:c5aa916d-9a1e-4ae8-ba25-0709ec913acb");
      peppolMessageHeader.setRecipientId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897"));
      peppolMessageHeader.setSenderId (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:123456789"));

      simpleMessageRepository.prepareMessageDirectory (tmp.toString (), peppolMessageHeader);
    }
    finally {
      tmp.delete ();
    }
  }
}

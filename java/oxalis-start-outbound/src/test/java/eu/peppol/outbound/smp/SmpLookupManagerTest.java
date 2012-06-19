package eu.peppol.outbound.smp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.security.cert.X509Certificate;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Test;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IProcessIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import eu.peppol.outbound.util.TestBase;

/**
 * User: nigel Date: Oct 25, 2011 Time: 9:05:52 AM
 */
public class SmpLookupManagerTest extends TestBase {

  private static IDocumentTypeIdentifier invoice = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ();
  // private static ParticipantIdentifierType alfa1lab =
  // Identifiers.getParticipantIdentifier("9902:DK28158815");
  private static ParticipantIdentifierType alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");
  private static ParticipantIdentifierType helseVest = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:983974724");
  private static ParticipantIdentifierType sendRegning = SimpleParticipantIdentifier.createWithDefaultScheme ("9908:976098897");

  @Test
  public void test01 () throws Throwable {
    try {

      URL endpointAddress;
      endpointAddress = new SmpLookupManager ().getEndpointAddress (sendRegning, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://aksesspunkt.sendregning.no/oxalis/accessPointService");

      endpointAddress = new SmpLookupManager ().getEndpointAddress (alfa1lab, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://start-ap.alfa1lab.com:443/accessPointService");

      endpointAddress = new SmpLookupManager ().getEndpointAddress (helseVest, invoice);
      assertEquals (endpointAddress.toExternalForm (), "https://peppolap.ibxplatform.net:8443/accesspointService");

    }
    catch (final Throwable t) {
      signal (t);
    }
  }

  @Test
  public void test02 () throws Throwable {
    try {

      X509Certificate endpointCertificate;
      endpointCertificate = new SmpLookupManager ().getEndpointCertificate (alfa1lab, invoice);
      assertEquals (endpointCertificate.getSerialNumber ().toString (), "97394193891150626641360283873417712042");

      // endpointCertificate = new
      // SmpLookupManager().getEndpointCertificate(helseVest, invoice);
      // assertEquals(endpointCertificate.getSerialNumber().toString(),
      // "37276025795984990954710880598937203007");

    }
    catch (final Throwable t) {
      signal (t);
    }
  }

  /**
   * Tests what happens when the participant is not registered.
   * 
   * @throws Throwable
   */
  @Test
  public void test03 () throws Throwable {

    final ParticipantIdentifierType notRegisteredParticipant = SimpleParticipantIdentifier.createWithDefaultScheme ("1234:45678910");
    try {
      new SmpLookupManager ().getEndpointAddress (notRegisteredParticipant, invoice);
      fail (String.format ("Participant '%s' should not be registered", notRegisteredParticipant));
    }
    catch (final NumberFormatException e) {
      // expected
    }
  }

  /**
     *
     */
  @Test
  public void testGetFirstProcessIdentifier () throws SmpSignedServiceMetaDataException {
    final IProcessIdentifier processTypeIdentifier = SmpLookupManager.getProcessIdentifierForDocumentType (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:810017902"),
                                                                                                           EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ());

    assertEquals (processTypeIdentifier.getValue (), "urn:www.cenbii.eu:profile:bii04:ver1.0");
  }
}

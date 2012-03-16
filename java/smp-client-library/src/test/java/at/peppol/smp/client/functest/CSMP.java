package at.peppol.smp.client.functest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.concurrent.Immutable;

import at.peppol.commons.identifier.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.identifier.actorid.EPredefinedIdentifierIssuingAgency;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.ReadonlyUsernamePWCredentials;
import at.peppol.smp.client.UserId;

import com.phloc.commons.exceptions.InitializationException;

/**
 * Constants for the SMP
 * 
 * @author philip
 */
@Immutable
public final class CSMP {
  // The username to be found in the SMP DB
  private static final String SMP_USERNAME = "username";
  private static final String SMP_PASSWORD = "password";
  public static final IReadonlyUsernamePWCredentials SMP_CREDENTIALS = new ReadonlyUsernamePWCredentials (SMP_USERNAME,
                                                                                                          SMP_PASSWORD);
  public static final UserId SMP_USERID = new UserId (SMP_USERNAME);
  public static final URI SMP_URI;

  public static final SimpleParticipantIdentifier PARTICIPANT_ID = EPredefinedIdentifierIssuingAgency.GLN.createParticipantIdentifier ("12345678");

  // Invoice T10
  public static final SimpleDocumentTypeIdentifier DOCUMENT_ID = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();

  // BIS 4a
  public static final SimpleProcessIdentifier PROCESS_ID = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();

  // init
  static {
    try {
      // Use localhost for local testing only
      SMP_URI = new URI ("http://localhost/");
    }
    catch (final URISyntaxException ex) {
      throw new InitializationException (ex);
    }
  }

  private CSMP () {}
}

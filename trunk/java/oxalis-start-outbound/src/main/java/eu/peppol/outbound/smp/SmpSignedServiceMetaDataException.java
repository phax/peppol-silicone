/* Created by steinar on 23.05.12 at 23:29 */
package eu.peppol.outbound.smp;

import java.net.URL;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpSignedServiceMetaDataException extends Exception {

  private final ParticipantIdentifierType participant;
  private final IDocumentTypeIdentifier documentTypeIdentifier;
  private final URL smpUrl;

  public SmpSignedServiceMetaDataException (final ParticipantIdentifierType participant,
                                            final IDocumentTypeIdentifier documentTypeIdentifier,
                                            final URL smpUrl,
                                            final Exception e) {
    super ("Unable to find information for participant: " +
           participant +
           ", documentType: " +
           documentTypeIdentifier +
           ", at url: " +
           smpUrl +
           " ; " +
           e.getMessage (), e);

    this.participant = participant;
    this.documentTypeIdentifier = documentTypeIdentifier;
    this.smpUrl = smpUrl;
  }

  public ParticipantIdentifierType getParticipant () {
    return participant;
  }

  public IDocumentTypeIdentifier getDocumentTypeIdentifier () {
    return documentTypeIdentifier;
  }

  public URL getSmpUrl () {
    return smpUrl;
  }
}

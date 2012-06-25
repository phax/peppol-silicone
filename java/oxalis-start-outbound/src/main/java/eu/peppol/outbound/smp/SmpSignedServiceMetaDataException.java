/* Created by steinar on 23.05.12 at 23:29 */
package eu.peppol.outbound.smp;

import java.net.URL;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpSignedServiceMetaDataException extends Exception {

  private final ParticipantIdentifierType m_aParticipantID;
  private final IDocumentTypeIdentifier m_aDocumentTypeID;
  private final URL m_aSMPUrl;

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

    this.m_aParticipantID = participant;
    this.m_aDocumentTypeID = documentTypeIdentifier;
    this.m_aSMPUrl = smpUrl;
  }

  public ParticipantIdentifierType getParticipant () {
    return m_aParticipantID;
  }

  public IDocumentTypeIdentifier getDocumentTypeIdentifier () {
    return m_aDocumentTypeID;
  }

  public URL getSmpUrl () {
    return m_aSMPUrl;
  }
}

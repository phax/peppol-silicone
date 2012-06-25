/* Created by steinar on 18.05.12 at 13:35 */
package eu.peppol.smp;

import java.net.URL;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpLookupException extends Throwable {
  ParticipantIdentifierType m_aParticipantId;
  private URL m_aURL;

  public SmpLookupException (final ParticipantIdentifierType participantId, final Exception e) {
    super ("Unable to perform SMP lookup for " + participantId + "; " + e.getMessage (), e);
    this.m_aParticipantId = participantId;
  }

  public SmpLookupException (final ParticipantIdentifierType participantId, final URL servicesUrl, final Exception cause) {
    super ("Unable to fetch data for " + participantId + " from " + servicesUrl + " ;" + cause.getMessage (), cause);
    this.m_aParticipantId = participantId;
    this.m_aURL = servicesUrl;
  }

  public ParticipantIdentifierType getParticipantId () {
    return m_aParticipantId;
  }

  public URL getSmpUrl () {
    return m_aURL;
  }
}

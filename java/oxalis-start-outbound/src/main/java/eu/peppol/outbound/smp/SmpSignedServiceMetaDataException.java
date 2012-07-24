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
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
package eu.peppol.outbound.smp;

import java.net.URL;

import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.process.SimpleProcessIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.smp.client.SMPServiceCaller;

/**
 * User: nigel Date: Oct 25, 2011 Time: 9:01:53 AM
 * 
 * @author Nigel Parker
 * @author Steinar O. Cook
 */
public final class SmpLookupManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger ("oxalis-out");

  private SmpLookupManager () {}

  /**
   * @param participant
   * @param documentTypeIdentifier
   * @return The endpoint address for the participant and DocumentId
   * @throws RuntimeException
   *         If the end point address cannot be resolved for the participant.
   *         This is caused by a {@link java.net.UnknownHostException}
   */
  public static URL getEndpointAddress (final IReadonlyParticipantIdentifier participant,
                                        final IReadonlyDocumentTypeIdentifier documentTypeIdentifier,
                                        final IReadonlyProcessIdentifier aProcessID) {

    try {
      final String address = new SMPServiceCaller (participant, ESML.PRODUCTION).getEndpointAddress (participant,
                                                                                                     documentTypeIdentifier,
                                                                                                     aProcessID);
      s_aLogger.info ("Found endpoint address for " + participant.getValue () + " from SMP: " + address);
      return new URL (address);
    }
    catch (final Exception e) {
      throw new RuntimeException ("SMP returned invalid URL", e);
    }
  }

  public static SimpleProcessIdentifier getProcessIdentifierForDocumentType (final IReadonlyParticipantIdentifier participantId,
                                                                             final IReadonlyDocumentTypeIdentifier documentTypeIdentifier) throws Exception {
    final SignedServiceMetadataType serviceMetaData = new SMPServiceCaller (participantId, ESML.PRODUCTION).getServiceRegistration (participantId,
                                                                                                                                    documentTypeIdentifier);
    // SOAP generated type...
    final ProcessIdentifierType processIdentifier = serviceMetaData.getServiceMetadata ()
                                                                   .getServiceInformation ()
                                                                   .getProcessList ()
                                                                   .getProcess ()
                                                                   .get (0)
                                                                   .getProcessIdentifier ();

    // Converts SOAP generated type into something nicer
    return new SimpleProcessIdentifier (processIdentifier);
  }
}

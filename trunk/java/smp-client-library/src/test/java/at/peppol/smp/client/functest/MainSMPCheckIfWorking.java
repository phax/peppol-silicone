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
package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.smp.client.exception.NotFoundException;

/**
 * Check if an SMP installation is working. Prior to executing this class, make
 * sure that {@link CSMP} and {@link CAP} are filled correctly!
 * 
 * @author philip
 */
public final class MainSMPCheckIfWorking {
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainSMPCheckIfWorking.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Ensure that the service group does not exist
    s_aLogger.info ("Ensuring that the service group is not existing!");
    try {
      aClient.getServiceGroup (CSMP.PARTICIPANT_ID);
      s_aLogger.info ("Deleting existing service group for init");
      aClient.deleteServiceGroup (CSMP.PARTICIPANT_ID, CSMP.SMP_CREDENTIALS);
      s_aLogger.info ("Finished deletion of service group");
    }
    catch (final NotFoundException ex) {
      // ServiceGroup does not exist
    }

    // Create, read and delete the service group
    s_aLogger.info ("Creating the new service group");
    aClient.saveServiceGroup (CSMP.PARTICIPANT_ID, CSMP.SMP_CREDENTIALS);

    s_aLogger.info ("Retrieving the service group");
    final ServiceGroupType aSGT = aClient.getServiceGroup (CSMP.PARTICIPANT_ID);
    if (!IdentifierUtils.areIdentifiersEqual (aSGT.getParticipantIdentifier (), CSMP.PARTICIPANT_ID))
      throw new IllegalStateException ("Participant identifiers are not equal!");

    s_aLogger.info ("Deleting the service group again");
    aClient.deleteServiceGroup (CSMP.PARTICIPANT_ID, CSMP.SMP_CREDENTIALS);

    s_aLogger.info ("Done");
  }
}

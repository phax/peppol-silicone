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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceRegistrationCreate {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceRegistrationCreate.class);

  // SMP ObjectFactory
  private static final ObjectFactory s_aOF = new ObjectFactory ();

  @Nonnull
  private static Date _createDate (final int nYear, final int nMonth, final int nDayOfMonth) {
    final GregorianCalendar aCal = new GregorianCalendar (nYear, nMonth, nDayOfMonth);
    aCal.setTimeZone (TimeZone.getTimeZone ("UTC"));
    return aCal.getTime ();
  }

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Create the service registration
    final ServiceMetadataType aServiceMetadata = s_aOF.createServiceMetadataType ();
    {
      final ServiceInformationType aServiceInformation = s_aOF.createServiceInformationType ();
      {
        final ProcessListType aProcessList = s_aOF.createProcessListType ();
        {
          final ProcessType aProcess = s_aOF.createProcessType ();
          {
            final ServiceEndpointList aServiceEndpointList = s_aOF.createServiceEndpointList ();
            {
              final EndpointType aEndpoint = s_aOF.createEndpointType ();
              aEndpoint.setEndpointReference (CAP.START_AP_ENDPOINTREF);
              aEndpoint.setTransportProfile ("busdox-transport-start");
              aEndpoint.setCertificate (CAP.AP_CERT_STRING);
              aEndpoint.setServiceActivationDate (_createDate (2011, Calendar.JANUARY, 1));
              aEndpoint.setServiceExpirationDate (_createDate (2020, Calendar.DECEMBER, 31));
              aEndpoint.setServiceDescription (CAP.AP_SERVICE_DESCRIPTION);
              aEndpoint.setTechnicalContactUrl (CAP.AP_CONTACT_URL);
              aEndpoint.setTechnicalInformationUrl (CAP.AP_INFO_URL);
              aEndpoint.setMinimumAuthenticationLevel ("1");
              aEndpoint.setRequireBusinessLevelSignature (false);
              aServiceEndpointList.getEndpoint ().add (aEndpoint);
            }
            aProcess.setProcessIdentifier (CSMP.PROCESS_ID);
            aProcess.setServiceEndpointList (aServiceEndpointList);
          }
          aProcessList.getProcess ().add (aProcess);
        }
        aServiceInformation.setDocumentIdentifier (CSMP.DOCUMENT_ID);
        aServiceInformation.setParticipantIdentifier (CSMP.PARTICIPANT_ID);
        aServiceInformation.setProcessList (aProcessList);
      }
      aServiceMetadata.setServiceInformation (aServiceInformation);
    }
    aClient.saveServiceRegistration (aServiceMetadata, CSMP.SMP_CREDENTIALS);

    s_aLogger.info ("Done");
  }
}

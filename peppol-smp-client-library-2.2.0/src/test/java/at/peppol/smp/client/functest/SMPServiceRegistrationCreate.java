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

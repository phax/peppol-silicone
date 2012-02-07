package eu.peppol.registry.smp.client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.state.ESuccess;

import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.docid.EPredefinedDocumentIdentifier;
import eu.peppol.busdox.identifier.procid.EPredefinedProcessIdentifier;
import eu.peppol.busdox.sml.ESML;
import eu.peppol.busdox.sml.ISMLInfo;
import eu.peppol.common.IReadonlyUsernamePWCredentials;
import eu.peppol.common.UsernamePWCredentials;

public final class RegisterAPatSMP {
  public static final Logger s_aLogger = LoggerFactory.getLogger (RegisterAPatSMP.class);

  // Modify the following constants to fit your needs!

  // SMP user name and password (as found in the smp_user table)
  private static final String SMP_USERID = "mySMPUserID";
  private static final String SMP_PASSWORD = "mySMPPassword";

  // The participant you want to register
  private static final String PARTICIPANT_ID = "0088:myGLNNumber";

  // What is the URL of the START service (without any ?wsdl!)
  private static final String AP_ENDPOINTREF = "https://myap/service";

  // The Base64 encoded, DER encoded AP certificate (public key only)
  private static final String AP_CERT_STRING = null;

  // Descriptive string
  private static final String AP_SERVICE_DESCRIPTION = "What does my service do?";

  // Contact email
  private static final String AP_CONTACT_URL = "info@mycompany.com";

  // Contact website
  private static final String AP_INFO_URL = "http://company.url";

  // Is a business level signature required?
  private static final boolean AP_REQUIRE_BUSSINES_LEVEL_SIGNATURE = false;

  // Document type to be registered (e.g. invoice T10)
  private static final EPredefinedDocumentIdentifier DOCTYPE = EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol4a_ver1_0__2_0;

  // Process type to be registered (e.g. BIS4A)
  private static final EPredefinedProcessIdentifier PROCTYPE = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii04_ver1_0;

  // Validity start date (may be present!)
  private static final Date START_DATE = new GregorianCalendar (2012, Calendar.JANUARY, 1).getTime ();

  // Validity end date (may be present!)
  private static final Date END_DATE = new GregorianCalendar (2029, Calendar.DECEMBER, 31).getTime ();

  @Nonnull
  private static ESuccess _registerRecipient (@Nonnull final ISMLInfo aSMLInfo,
                                              @Nonnull final ParticipantIdentifierType aParticipantID,
                                              @Nonnull final EPredefinedDocumentIdentifier eDocumentID,
                                              @Nonnull final EPredefinedProcessIdentifier eProcessID,
                                              @Nonnull final Date aStartDate,
                                              @Nonnull final Date aEndDate,
                                              @Nonnull final IReadonlyUsernamePWCredentials aAuth) {
    if (aParticipantID == null)
      return ESuccess.FAILURE;
    if (eDocumentID == null)
      return ESuccess.FAILURE;
    if (eProcessID == null)
      return ESuccess.FAILURE;
    if (aStartDate == null || aEndDate == null || aStartDate.getTime () > aEndDate.getTime ())
      return ESuccess.FAILURE;

    try {
      // Create object for ServiceGroup registration
      final ObjectFactory aObjFactory = new ObjectFactory ();
      final ServiceGroupType aServiceGroup = aObjFactory.createServiceGroupType ();
      aServiceGroup.setParticipantIdentifier (aParticipantID);

      // 1. create the service group
      final SMPServiceCaller aSMPCaller = new SMPServiceCaller (aParticipantID, aSMLInfo);
      aSMPCaller.saveServiceGroup (aServiceGroup, aAuth);

      // 2. create the service registration
      final ServiceMetadataType aServiceMetadata = aObjFactory.createServiceMetadataType ();
      {
        final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
        {
          final ProcessListType aProcessList = aObjFactory.createProcessListType ();
          {
            final ProcessType aProcess = aObjFactory.createProcessType ();
            {
              final ServiceEndpointList aServiceEndpointList = aObjFactory.createServiceEndpointList ();
              {
                final EndpointType aEndpoint = aObjFactory.createEndpointType ();
                aEndpoint.setEndpointReference (new W3CEndpointReferenceBuilder ().address (AP_ENDPOINTREF).build ());
                aEndpoint.setTransportProfile ("busdox-transport-start");
                aEndpoint.setCertificate (AP_CERT_STRING);
                aEndpoint.setServiceActivationDate (aStartDate);
                aEndpoint.setServiceExpirationDate (aEndDate);
                aEndpoint.setServiceDescription (AP_SERVICE_DESCRIPTION);
                aEndpoint.setTechnicalContactUrl (AP_CONTACT_URL);
                aEndpoint.setTechnicalInformationUrl (AP_INFO_URL);
                aEndpoint.setMinimumAuthenticationLevel ("1");
                aEndpoint.setRequireBusinessLevelSignature (AP_REQUIRE_BUSSINES_LEVEL_SIGNATURE);
                aServiceEndpointList.getEndpoint ().add (aEndpoint);
              }
              aProcess.setProcessIdentifier (eProcessID.getAsProcessIdentifier ());
              aProcess.setServiceEndpointList (aServiceEndpointList);
            }
            aProcessList.getProcess ().add (aProcess);
          }
          aServiceInformation.setDocumentIdentifier (eDocumentID.getAsDocumentIdentifier ());
          aServiceInformation.setParticipantIdentifier (aParticipantID);
          aServiceInformation.setProcessList (aProcessList);
        }
        aServiceMetadata.setServiceInformation (aServiceInformation);
      }
      aSMPCaller.saveServiceRegistration (aServiceMetadata, aAuth);

      return ESuccess.SUCCESS;
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error saving service registration for " + aParticipantID, ex);
      return ESuccess.FAILURE;
    }
  }

  public static void main (final String [] args) {
    _registerRecipient (ESML.PRODUCTION,
                        SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_ID),
                        DOCTYPE,
                        PROCTYPE,
                        START_DATE,
                        END_DATE,
                        new UsernamePWCredentials (SMP_USERID, SMP_PASSWORD));
  }
}

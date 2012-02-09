package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceRegistrationList {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceRegistrationList.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Get the service group reference list
    final SignedServiceMetadataType aSignedServiceMetadata = aClient.getServiceRegistration (CSMP.PARTICIPANT_ID,
                                                                                             CSMP.DOCUMENT_ID);

    if (aSignedServiceMetadata == null)
      s_aLogger.error ("Failed to get service registration for " + CSMP.PARTICIPANT_ID + " and " + CSMP.DOCUMENT_ID);
    else
      s_aLogger.info (SMPUtils.getAsString (aSignedServiceMetadata.getServiceMetadata ()));

    s_aLogger.info ("Done");
  }
}

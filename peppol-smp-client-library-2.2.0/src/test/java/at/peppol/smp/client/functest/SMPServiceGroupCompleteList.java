package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.CompleteServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceGroupCompleteList {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceGroupCompleteList.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Get the service group reference list
    final CompleteServiceGroupType aCompleteServiceGroup = aClient.getCompleteServiceGroup (CSMP.PARTICIPANT_ID);

    if (aCompleteServiceGroup == null)
      s_aLogger.error ("Failed to get complete service group for " + CSMP.PARTICIPANT_ID);
    else {
      s_aLogger.info (SMPUtils.getAsString (aCompleteServiceGroup.getServiceGroup ()));
      for (final ServiceMetadataType aServiceMetadata : aCompleteServiceGroup.getServiceMetadata ())
        s_aLogger.info (SMPUtils.getAsString (aServiceMetadata));
    }

    s_aLogger.info ("Done");
  }
}

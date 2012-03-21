package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.smp.client.exception.NotFoundException;

/**
 * @author philip
 */
public final class SMPServiceGroupList {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceGroupList.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Get the service group information
    ServiceGroupType aServiceGroup = null;
    try {
      aServiceGroup = aClient.getServiceGroup (CSMP.PARTICIPANT_ID);
    }
    catch (final NotFoundException ex) {
      // ServiceGroup does not exist
    }

    if (aServiceGroup == null)
      s_aLogger.error ("Failed to get service group infos for " + CSMP.PARTICIPANT_ID);
    else
      s_aLogger.info (SMPUtils.getAsString (aServiceGroup));

    s_aLogger.info ("Done");
  }
}

package at.peppol.smp.client.functest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceGroupDelete {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceGroupDelete.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Delete the service group
    aClient.deleteServiceGroup (CSMP.PARTICIPANT_ID, CSMP.SMP_CREDENTIALS);

    s_aLogger.info ("Done");
  }
}

package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceGroupCreate {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceGroupCreate.class);

  // SMP ObjectFactory
  private static final ObjectFactory s_aOF = new ObjectFactory ();

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Create the service group
    final ServiceGroupType aServiceGroup = s_aOF.createServiceGroupType ();
    aServiceGroup.setParticipantIdentifier (CSMP.PARTICIPANT_ID);
    aClient.saveServiceGroup (aServiceGroup, CSMP.SMP_CREDENTIALS);

    s_aLogger.info ("Done");
  }
}

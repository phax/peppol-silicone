package at.peppol.smp.client.functest;

import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.smp.client.SMPServiceCaller;

/**
 * @author philip
 */
public final class SMPServiceGroupReferenceList {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceGroupReferenceList.class);

  public static void main (final String [] args) throws Exception {
    // The main SMP client
    final SMPServiceCaller aClient = new SMPServiceCaller (CSMP.SMP_URI);

    // Get the service group reference list
    final ServiceGroupReferenceListType aServiceGroupReferenceList = aClient.getServiceGroupReferenceList (CSMP.SMP_USERID,
                                                                                                           CSMP.SMP_CREDENTIALS);

    if (aServiceGroupReferenceList == null)
      s_aLogger.error ("Failed to get complete service group for " + CSMP.SMP_USERID.getUserIdPercentEncoded ());
    else {
      s_aLogger.info ("All service groups owned by " + CSMP.SMP_USERID.getUserIdPercentEncoded () + ":");
      for (final ServiceGroupReferenceType aServiceGroupReference : aServiceGroupReferenceList.getServiceGroupReference ())
        s_aLogger.info ("  " + aServiceGroupReference.getHref ());
    }

    s_aLogger.info ("Done");
  }
}

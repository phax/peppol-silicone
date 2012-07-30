package at.peppol.smp.client;

import javax.annotation.concurrent.Immutable;

/**
 * This class contains some important SMP constants
 * 
 * @author philip
 */
@Immutable
public final class CSMPIdentifier {
  /** The START transport profile to be used in EndPointType objects */
  public static final String TRANSPORT_PROFILE_START = "busdox-transport-start";

  private CSMPIdentifier () {}
}

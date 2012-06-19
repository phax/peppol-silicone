package eu.peppol.security;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.phloc.commons.CGlobal;

/**
 * User: nigel Date: Dec 6, 2011 Time: 9:08:50 PM
 */
public final class OcspValidatorCache {
  private static final boolean USE_CACHE = true;

  private static long timeout = CGlobal.MILLISECONDS_PER_MINUTE;
  private static Map <BigInteger, Long> validCertificateCache = new HashMap <BigInteger, Long> ();

  public synchronized boolean isKnownValidCertificate (final BigInteger serialNumber) {
    final Long aTimestamp = validCertificateCache.get (serialNumber);
    return aTimestamp != null && (System.currentTimeMillis () - aTimestamp.longValue ()) < timeout;
  }

  public synchronized void setKnownValidCertificate (final BigInteger serialNumber) {
    if (USE_CACHE) {
      validCertificateCache.put (serialNumber, Long.valueOf (System.currentTimeMillis ()));
    }
  }

  void setTimoutForTesting (final long timeoutValue) {
    timeout = timeoutValue;
  }
}

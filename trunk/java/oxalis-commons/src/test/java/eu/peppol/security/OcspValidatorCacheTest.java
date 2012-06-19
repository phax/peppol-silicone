package eu.peppol.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

/**
 * User: nigel Date: Dec 6, 2011 Time: 9:09:13 PM
 */
public class OcspValidatorCacheTest {

  @Test
  public void test01 () throws Exception {

    final OcspValidatorCache cache = new OcspValidatorCache ();
    cache.setTimoutForTesting (10);
    final BigInteger serialNumber = new BigInteger ("1000");
    assertFalse (cache.isKnownValidCertificate (serialNumber));

    cache.setKnownValidCertificate (serialNumber);
    assertTrue (cache.isKnownValidCertificate (serialNumber));

    Thread.sleep (5);
    assertTrue (cache.isKnownValidCertificate (serialNumber));

    Thread.sleep (10);
    assertFalse (cache.isKnownValidCertificate (serialNumber));
  }
}

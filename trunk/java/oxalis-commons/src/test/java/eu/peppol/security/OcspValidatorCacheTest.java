package eu.peppol.security;

import static org.junit.Assert.assertEquals;

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
    assertEquals (cache.isKnownValidCertificate (serialNumber), false);

    cache.setKnownValidCertificate (serialNumber);
    assertEquals (cache.isKnownValidCertificate (serialNumber), true);

    Thread.sleep (5);
    assertEquals (cache.isKnownValidCertificate (serialNumber), true);

    Thread.sleep (10);
    assertEquals (cache.isKnownValidCertificate (serialNumber), false);
  }
}

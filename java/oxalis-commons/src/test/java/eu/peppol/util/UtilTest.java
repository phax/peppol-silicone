/* Created by steinar on 18.05.12 at 13:55 */
package eu.peppol.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class UtilTest {
  @Test
  public void testCalculateMD5 () throws Exception {
    final String hash = Util.calculateMD5 ("9908:810017902");

    assertEquals (hash, "ddc207601e442e1b751e5655d39371cd");

  }
}

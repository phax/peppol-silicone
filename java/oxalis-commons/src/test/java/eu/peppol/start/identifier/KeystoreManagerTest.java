/* Created by steinar on 14.05.12 at 00:10 */
package eu.peppol.start.identifier;

import java.security.cert.TrustAnchor;

import org.junit.Test;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class KeystoreManagerTest {
  @Test
  public void testGetTruststore () throws Exception {
    final KeystoreManager km = new KeystoreManager ();
    final TrustAnchor trustAnchor = km.getTrustAnchor ();
    System.out.println (trustAnchor.getTrustedCert ().getSubjectDN ());
  }

}

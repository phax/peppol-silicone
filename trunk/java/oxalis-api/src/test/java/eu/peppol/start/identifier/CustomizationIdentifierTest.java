/* Created by steinar on 20.05.12 at 12:14 */
package eu.peppol.start.identifier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class CustomizationIdentifierTest {

  @Test
  public void parseEhfKreditNota () {
    final CustomizationIdentifier customizationIdentifier = CustomizationIdentifier.valueOf ("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1");
    assertEquals (customizationIdentifier.getTransactionIdentifier ().toString (),
                  "urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0");
    assertEquals (customizationIdentifier.getFirstExtensionIdentifier ().toString (),
                  "urn:www.cenbii.eu:profile:biixx:ver1.0");
    assertEquals (customizationIdentifier.getSecondExtensionIdentifier ().toString (),
                  "urn:www.difi.no:ehf:kreditnota:ver1");
  }

  @Test
  public void parseApplicationResponse () {
    final String s = "urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0";
    final CustomizationIdentifier customizationIdentifier = CustomizationIdentifier.valueOf (s);
    assertEquals (customizationIdentifier.toString (), s);
    assertEquals (customizationIdentifier.getTransactionIdentifier ().toString (),
                  "urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0");
    assertEquals (customizationIdentifier.getFirstExtensionIdentifier ().toString (),
                  "urn:www.peppol.eu:bis:peppol1a:ver1.0");
  }

  @Test
  public void equalsTest () {
    final String s = "urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0";
    final CustomizationIdentifier c1 = CustomizationIdentifier.valueOf (s);
    final CustomizationIdentifier c2 = CustomizationIdentifier.valueOf (s);

    assertEquals (c1, c2);
  }
}

package eu.peppol.start.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.peppol.start.persistence.IdentifierName;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 29.11.11
 *         Time: 14:26
 */
public class IdentifierNameTest {
  @Test
  public void testValueOfIdentifier () throws Exception {

    for (final IdentifierName id : IdentifierName.values ()) {

      final IdentifierName id2 = IdentifierName.valueOfIdentifier (id.stringValue ());
      assertTrue ("Unknown identifier " + id.name (), id2 == id);
    }
  }
}

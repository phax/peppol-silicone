package ap.peppol.webgui.security.role;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;

public final class RoleTest {
  @Test
  public void testBasic () {
    final Role aRole = new Role ("id1", "Role 1");
    assertEquals ("id1", aRole.getID ());
    assertEquals ("Role 1", aRole.getName ());
  }

  @Test
  public void testMicroConversion () {
    final Role aRole = new Role ("id1", "Role 1");

    // To XML
    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aRole, "role");
    assertNotNull (aElement);

    // From XML
    final Role aRole2 = MicroTypeConverter.convertToNative (aElement, Role.class);
    assertNotNull (aRole2);
    assertEquals ("id1", aRole2.getID ());
    assertEquals ("Role 1", aRole2.getName ());
  }
}

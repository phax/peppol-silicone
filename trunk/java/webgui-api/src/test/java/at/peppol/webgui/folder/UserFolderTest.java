package at.peppol.webgui.folder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.peppol.webgui.AbstractStorageAwareTestCase;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;
import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.commons.string.StringHelper;

/**
 * Test class for class {@link UserFolder}
 * 
 * @author philip
 */
public final class UserFolderTest extends AbstractStorageAwareTestCase {
  @Test
  public void testBasic () {
    final UserFolder aUF = new UserFolder ("any");
    assertTrue (StringHelper.hasText (aUF.getID ()));
    assertEquals ("any", aUF.getDisplayName ());

    // Check empty
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));

    // Add first document
    assertTrue (aUF.addDocument ("a").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Add again - no change
    assertFalse (aUF.addDocument ("a").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Remove invalid document - no change
    assertFalse (aUF.removeDocument ("b").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Remove valid document
    assertTrue (aUF.removeDocument ("a").isChanged ());
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));

    // Remove again - no change
    assertFalse (aUF.removeDocument ("a").isChanged ());
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));
  }

  @Test
  public void testSerialize () {
    final UserFolder aUF = new UserFolder ("any");
    aUF.addDocument ("doc1");
    aUF.addDocument ("doc2");
    final IMicroElement e = MicroTypeConverter.convertToMicroElement (aUF, "x");
    final UserFolder aUF2 = MicroTypeConverter.convertToNative (e, UserFolder.class);
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aUF, aUF2);
  }
}

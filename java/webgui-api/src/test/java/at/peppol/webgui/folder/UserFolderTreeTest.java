package at.peppol.webgui.folder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import at.peppol.webgui.AbstractStorageAwareTestCase;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link UserFolderTree}
 * 
 * @author philip
 */
public final class UserFolderTreeTest extends AbstractStorageAwareTestCase {
  @Test
  public void testAsXML () {
    final UserFolderTree aTree = new UserFolderTree ();
    final String sID1 = aTree.createRootFolder (new UserFolder ("Inbox")).getID ();
    aTree.createFolder (sID1, new UserFolder ("Project A")).addDocument ("doc1");
    aTree.createFolder (sID1, new UserFolder ("Project B")).addDocument ("doc2");
    final String sID2 = aTree.createRootFolder (new UserFolder ("Outbox")).getID ();
    aTree.createFolder (sID2, new UserFolder ("Project C")).addDocument ("doc3");
    aTree.createFolder (sID2, new UserFolder ("Project D")).addDocument ("doc4");

    final IMicroElement e = aTree.getAsXML ();
    assertNotNull (e);

    final UserFolderTree t2 = new UserFolderTree (e);
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aTree, t2);
  }
}

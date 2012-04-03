package at.peppol.webgui.userdata;

import java.util.Set;

import com.phloc.commons.combine.CombinatorStringWithSeparator;
import com.phloc.commons.tree.withid.folder.BasicFolderTree;
import com.phloc.commons.tree.withid.folder.DefaultFolderTreeItem;
import com.phloc.commons.tree.withid.folder.DefaultFolderTreeItemFactory;

public final class UserFolderTree <VALUETYPE> extends
                                              BasicFolderTree <String, VALUETYPE, Set <VALUETYPE>, DefaultFolderTreeItem <String, VALUETYPE, Set <VALUETYPE>>> {
  /**
   * Constructor
   */
  public UserFolderTree () {
    // Use "-" as it can be easily used in URLs
    super (new DefaultFolderTreeItemFactory <String, VALUETYPE, Set <VALUETYPE>> (new CombinatorStringWithSeparator ("-")));
  }
}

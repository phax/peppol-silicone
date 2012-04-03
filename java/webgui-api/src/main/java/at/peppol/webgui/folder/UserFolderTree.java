package at.peppol.webgui.folder;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import at.peppol.webgui.document.IUserDocument;

import com.phloc.commons.combine.CombinatorStringWithSeparator;
import com.phloc.commons.combine.ICombinator;
import com.phloc.commons.tree.withid.folder.AbstractFolderTreeItemFactory;
import com.phloc.commons.tree.withid.folder.BasicFolderTree;
import com.phloc.commons.tree.withid.folder.BasicFolderTreeItem;

/**
 * Represents a single folder with a set of documents.
 * 
 * @author philip
 */
final class UserFolderTreeItem extends BasicFolderTreeItem <String, IUserDocument, Set <IUserDocument>, UserFolderTreeItem> {
  /**
   * Constructor for root object
   * 
   * @param aFactory
   *        The item factory to use.
   */
  public UserFolderTreeItem (@Nonnull final UserFolderTreeItemFactory aFactory) {
    super (aFactory);
  }

  /**
   * Constructor for normal elements
   * 
   * @param aParent
   *        Parent item. May never be <code>null</code> since only the root has
   *        no parent.
   * @param aDataID
   *        The ID of the new item. May not be <code>null</code>.
   */
  public UserFolderTreeItem (@Nonnull final UserFolderTreeItem aParent, @Nonnull final String aDataID) {
    super (aParent, aDataID);
  }

  @Override
  public boolean equals (final Object o) {
    return super.equals (o);
  }

  @Override
  public int hashCode () {
    return super.hashCode ();
  }
}

/**
 * Factory for {@link UserFolderTreeItem} objects.
 * 
 * @author philip
 */
@NotThreadSafe
final class UserFolderTreeItemFactory extends
                                     AbstractFolderTreeItemFactory <String, IUserDocument, Set <IUserDocument>, UserFolderTreeItem> {
  public UserFolderTreeItemFactory (@Nullable final ICombinator <String> aKeyCombinator) {
    super (aKeyCombinator);
  }

  @Override
  protected final UserFolderTreeItem internalCreateRoot () {
    return new UserFolderTreeItem (this);
  }

  @Override
  @Nonnull
  protected UserFolderTreeItem internalCreate (@Nonnull final UserFolderTreeItem aParent, @Nonnull final String aDataID) {
    return new UserFolderTreeItem (aParent, aDataID);
  }

  @Override
  public boolean equals (final Object o) {
    return super.equals (o);
  }

  @Override
  public int hashCode () {
    return super.hashCode ();
  }
}

/**
 * Represents a folder tree having {@link IUserDocument} elements
 * 
 * @author philip
 */
public final class UserFolderTree extends BasicFolderTree <String, IUserDocument, Set <IUserDocument>, UserFolderTreeItem> {
  /**
   * Constructor
   */
  public UserFolderTree () {
    // Use "-" as it can be easily used in URLs
    super (new UserFolderTreeItemFactory (new CombinatorStringWithSeparator ("-")));
  }
}

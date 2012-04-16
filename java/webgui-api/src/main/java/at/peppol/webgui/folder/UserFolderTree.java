package at.peppol.webgui.folder;

import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.document.IUserDocument;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.compare.AbstractComparator;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.parent.IChildrenProvider;
import com.phloc.commons.parent.impl.ChildrenProviderHasChildren;
import com.phloc.commons.parent.impl.ChildrenProviderSortingWithUniqueID;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.tree.utils.walk.TreeWalker;
import com.phloc.commons.tree.utils.xml.MicroTypeConverterTreeXML;
import com.phloc.commons.tree.utils.xml.TreeXMLConverter;
import com.phloc.commons.tree.withid.DefaultTreeItemWithID;
import com.phloc.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;

/**
 * Represents a folder tree having {@link IUserDocument} elements
 * 
 * @author philip
 */
public final class UserFolderTree {
  private static final Logger s_aLogger = LoggerFactory.getLogger (UserFolderTree.class);
  private static final String ELEMENT_USERFOLDER = "userfolder";

  private final DefaultTreeWithGlobalUniqueID <String, UserFolder> m_aTree;

  /**
   * Constructor
   */
  public UserFolderTree () {
    m_aTree = new DefaultTreeWithGlobalUniqueID <String, UserFolder> ();
  }

  UserFolderTree (@Nonnull final IMicroElement aElement) {
    m_aTree = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (aElement,
                                                               new MicroTypeConverterTreeXML <UserFolder> (ELEMENT_USERFOLDER,
                                                                                                           UserFolder.class));
    if (m_aTree == null)
      throw new IllegalStateException ("Deserialization failed!");
  }

  @Nonnull
  public UserFolder createRootFolder (@Nonnull final UserFolder aUserFolder) {
    if (aUserFolder == null)
      throw new NullPointerException ("userFolder");

    return m_aTree.getRootItem ().createChildItem (aUserFolder.getID (), aUserFolder).getData ();
  }

  @Nonnull
  public UserFolder createFolder (@Nonnull @Nonempty final String sParentFolderID, @Nonnull final UserFolder aUserFolder) {
    if (aUserFolder == null)
      throw new NullPointerException ("userFolder");

    // Resolve parent item
    final DefaultTreeItemWithID <String, UserFolder> aParentItem = m_aTree.getItemWithID (sParentFolderID);
    if (aParentItem == null)
      throw new IllegalArgumentException ("No such parent item '" + sParentFolderID + "'");

    // Add to parent
    return aParentItem.createChildItem (aUserFolder.getID (), aUserFolder).getData ();
  }

  @Nonnull
  public EChange deleteFolder (@Nullable final String sFolderID) {
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null || aItem.isRootItem ())
      return EChange.UNCHANGED;
    final EChange eChange = aItem.getParent ().removeChild (sFolderID);
    if (eChange.isUnchanged ())
      s_aLogger.error ("Internal inconsistency in folder tree!");
    return eChange;
  }

  @Nonnull
  public EChange renameFolder (@Nullable final String sFolderID, @Nonnull @Nonempty final String sNewFolderName) {
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null)
      return EChange.UNCHANGED;
    return aItem.getData ().setDisplayName (sNewFolderName);
  }

  public void iterateFolders (@Nonnull final INonThrowingRunnableWithParameter <IUserFolder> aCallback,
                              @Nullable final Comparator <? super UserFolder> aFolderComparator) {
    IChildrenProvider <DefaultTreeItemWithID <String, UserFolder>> aChildrenProvider;
    if (aFolderComparator == null) {
      // No sorting required
      aChildrenProvider = new ChildrenProviderHasChildren <DefaultTreeItemWithID <String, UserFolder>> ();
    }
    else {
      // Sorting is required
      final Comparator <DefaultTreeItemWithID <String, UserFolder>> aItemComparator = new AbstractComparator <DefaultTreeItemWithID <String, UserFolder>> () {
        @Override
        protected int mainCompare (final DefaultTreeItemWithID <String, UserFolder> aItem1,
                                   final DefaultTreeItemWithID <String, UserFolder> aItem2) {
          return aFolderComparator.compare (aItem1.getData (), aItem2.getData ());
        }
      };
      aChildrenProvider = new ChildrenProviderSortingWithUniqueID <String, DefaultTreeItemWithID <String, UserFolder>> (m_aTree,
                                                                                                                        aItemComparator);
    }

    // Main tree walking
    TreeWalker.walkTree (m_aTree,
                         aChildrenProvider,
                         new DefaultHierarchyWalkerCallback <DefaultTreeItemWithID <String, UserFolder>> () {
                           @Override
                           @OverrideOnDemand
                           public void onItemBeforeChildren (@Nullable final DefaultTreeItemWithID <String, UserFolder> aItem) {
                             final IUserFolder aUserFolder = aItem.getData ();
                             aCallback.run (aUserFolder);
                           }
                         });
  }

  @Nonnull
  public EChange assignDocumentToFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    if (aDoc == null)
      throw new NullPointerException ("doc");

    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null)
      return EChange.UNCHANGED;

    return aItem.getData ().addDocument (aDoc.getID ());
  }

  @Nonnull
  public EChange unassignDocumentFromFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    if (aDoc == null)
      throw new NullPointerException ("doc");

    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null)
      return EChange.UNCHANGED;

    return aItem.getData ().removeDocument (aDoc.getID ());
  }

  @Nullable
  public Set <String> getAllAssignedDocumentIDs (@Nullable final String sFolderID) {
    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    return aItem == null ? null : aItem.getData ().getAllDocumentIDs ();
  }

  @Nonnull
  public IMicroElement getAsXML () {
    return TreeXMLConverter.getTreeWithStringIDAsXML (m_aTree,
                                                      new MicroTypeConverterTreeXML <UserFolder> (ELEMENT_USERFOLDER,
                                                                                                  UserFolder.class));
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UserFolderTree))
      return false;
    final UserFolderTree rhs = (UserFolderTree) o;
    return m_aTree.equals (rhs.m_aTree);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_aTree).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("tree", m_aTree).toString ();
  }
}

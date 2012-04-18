package at.peppol.webgui.folder;

import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.peppol.webgui.document.IUserDocument;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.state.EChange;

public interface IUserFolderTree {
  /**
   * Create a new root folder
   * 
   * @param aUserFolder
   *        The user folder to be added. May not be <code>null</code>.
   * @return The passed user folder
   */
  @Nonnull
  IUserFolder createRootFolder (@Nonnull UserFolder aUserFolder);

  /**
   * Create a new non-root folder
   * 
   * @param sParentFolderID
   *        The ID of the parent folder to add the child folder to.
   * @param aUserFolder
   *        The user folder to be added
   * @return The passed user folder
   */
  @Nonnull
  IUserFolder createFolder (@Nonnull @Nonempty String sParentFolderID, @Nonnull UserFolder aUserFolder);

  /**
   * Delete the folder with the specified ID
   * 
   * @param sFolderID
   *        The ID of the folder to be deleted
   * @return {@link EChange}
   */
  @Nonnull
  EChange deleteFolder (@Nullable String sFolderID);

  /**
   * Specify a new name for the passed folder
   * 
   * @param sFolderID
   *        The ID of the folder to be renamed
   * @param sNewFolderName
   *        The new folder name
   * @return {@link EChange}
   */
  @Nonnull
  EChange renameFolder (@Nullable String sFolderID, @Nonnull @Nonempty String sNewFolderName);

  /**
   * Iterate all available folders
   * 
   * @param aCallback
   *        The callback to be invoked for every folder.
   * @param aFolderComparator
   *        An optional comparator to specify the way how folders are sorted on
   *        each level
   */
  void iterateFolders (@Nonnull INonThrowingRunnableWithParameter <IUserFolder> aCallback,
                       @Nullable Comparator <? super UserFolder> aFolderComparator);

  /**
   * Assign an existing document to a folder.
   * 
   * @param sFolderID
   *        The ID of the folder to assign the document to. If it is not
   *        existing, nothing happens.
   * @param aDoc
   *        The document to be assigned. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange assignDocumentToFolder (@Nullable String sFolderID, @Nonnull IUserDocument aDoc);

  /**
   * Unassign an existing document from a folder.
   * 
   * @param sFolderID
   *        The ID of the folder to unassign the document from. If it is not
   *        existing, nothing happens.
   * @param aDoc
   *        The document to be assigned. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange unassignDocumentFromFolder (@Nullable String sFolderID, @Nonnull IUserDocument aDoc);

  /**
   * Get all assigned documents of the specified folder.
   * 
   * @param sFolderID
   *        The ID of the folder to get the documents from.
   * @return <code>null</code> if no such folder exists.
   */
  @Nullable
  Set <String> getAllAssignedDocumentIDs (@Nullable String sFolderID);

  /**
   * @return The whole tree as an XML document.
   */
  @Nonnull
  IMicroElement getAsXML ();
}

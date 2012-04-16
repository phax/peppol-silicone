package at.peppol.webgui.folder;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single folder like "Inbox", "Outbox" or "Drafts"
 * 
 * @author philip
 */
@NotThreadSafe
public final class UserFolder implements IUserFolder {
  private final String m_sID;
  private String m_sDisplayName;
  private final Set <String> m_aDocs = new HashSet <String> ();

  /**
   * Constructor for a new folder
   * 
   * @param sDisplayName
   *        The display name of the folder
   */
  public UserFolder (@Nonnull @Nonempty final String sDisplayName) {
    this (GlobalIDFactory.getNewPersistentStringID (), sDisplayName);
  }

  UserFolder (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDisplayName) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sDisplayName))
      throw new IllegalArgumentException ("displayName");
    m_sID = sID;
    m_sDisplayName = sDisplayName;
  }

  /**
   * @return The globally unique folder ID
   */
  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  /**
   * @return The folder's name
   */
  @Nonnull
  @Nonempty
  public String getDisplayName () {
    return m_sDisplayName;
  }

  /**
   * Change the display name of the folder.
   * 
   * @param sDisplayName
   *        The new display name
   * @return {@link EChange}
   */
  @Nonnull
  public EChange setDisplayName (@Nonnull @Nonempty final String sDisplayName) {
    if (StringHelper.hasNoText (sDisplayName))
      throw new IllegalArgumentException ("displayName");

    if (sDisplayName.equals (m_sDisplayName))
      return EChange.UNCHANGED;
    m_sDisplayName = sDisplayName;
    return EChange.CHANGED;
  }

  public boolean containsDocumentWithID (final String sDocumentID) {
    return m_aDocs.contains (sDocumentID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllDocumentIDs () {
    return ContainerHelper.newSet (m_aDocs);
  }

  @Nonnegative
  public int getDocumentCount () {
    return m_aDocs.size ();
  }

  public boolean hasDocuments () {
    return !m_aDocs.isEmpty ();
  }

  @Nonnull
  public EChange addDocument (@Nonnull final String sDocID) {
    if (StringHelper.hasNoText (sDocID))
      throw new NullPointerException ("docID");
    return EChange.valueOf (m_aDocs.add (sDocID));
  }

  @Nonnull
  public EChange removeDocument (@Nullable final String sDocID) {
    return EChange.valueOf (m_aDocs.remove (sDocID));
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UserFolder))
      return false;
    final UserFolder rhs = (UserFolder) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("ID", m_sID)
                                       .append ("name", m_sDisplayName)
                                       .append ("docs", m_aDocs)
                                       .toString ();
  }
}

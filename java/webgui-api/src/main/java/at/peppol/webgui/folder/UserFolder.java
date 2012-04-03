package at.peppol.webgui.folder;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.name.IHasDisplayName;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;

/**
 * Represents a single folder like "Inbox", "Outbox" or "Drafts"
 * 
 * @author philip
 */
public final class UserFolder implements IHasID <String>, IHasDisplayName {
  private final String m_sID;
  private String m_sDisplayName;

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
  EChange setDisplayName (@Nonnull @Nonempty final String sDisplayName) {
    if (StringHelper.hasNoText (sDisplayName))
      throw new IllegalArgumentException ("displayName");

    if (sDisplayName.equals (m_sDisplayName))
      return EChange.UNCHANGED;
    m_sDisplayName = sDisplayName;
    return EChange.CHANGED;
  }
}

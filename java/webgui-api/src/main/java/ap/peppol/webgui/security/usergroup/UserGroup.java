package ap.peppol.webgui.security.usergroup;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

@NotThreadSafe
public final class UserGroup implements IUserGroup {
  private final String m_sID;
  private String m_sName;
  private final Set <String> m_aUsers = new HashSet <String> ();

  public UserGroup (@Nonnull @Nonempty final String sName) {
    this (GlobalIDFactory.getNewPersistentStringID (), sName);
  }

  public UserGroup (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sName) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("name");
    m_sID = sID;
    m_sName = sName;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName () {
    return m_sName;
  }

  @Nonnull
  public EChange setDisplayName (@Nonnull @Nonempty final String sName) {
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("name");
    if (sName.equals (m_sName))
      return EChange.UNCHANGED;
    m_sName = sName;
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsImmutableObject
  public Set <String> getAllAssignedUserIDs () {
    return ContainerHelper.makeUnmodifiable (m_aUsers);
  }

  public boolean containsUser (final String sUserID) {
    return m_aUsers.contains (sUserID);
  }

  @Nonnull
  public EChange assignUser (@Nonnull final String sUserID) {
    if (StringHelper.hasNoText (sUserID))
      throw new IllegalArgumentException ("userID");

    return EChange.valueOf (m_aUsers.add (sUserID));
  }

  @Nonnull
  public EChange unassignUser (@Nonnull final String sUserID) {
    return EChange.valueOf (m_aUsers.remove (sUserID));
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UserGroup))
      return false;
    final UserGroup rhs = (UserGroup) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("ID", m_sID)
                                       .append ("name", m_sName)
                                       .append ("assignedUsers", m_aUsers)
                                       .toString ();
  }
}

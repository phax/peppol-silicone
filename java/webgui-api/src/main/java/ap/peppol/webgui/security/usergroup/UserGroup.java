package ap.peppol.webgui.security.usergroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import ap.peppol.webgui.security.user.IUser;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

@NotThreadSafe
public final class UserGroup implements IUserGroup {
  private final String m_sID;
  private final String m_sName;
  private final Map <String, IUser> m_aUsers = new HashMap <String, IUser> ();

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
  public Collection <IUser> getAllAssignedUsers () {
    return ContainerHelper.makeUnmodifiable (m_aUsers.values ());
  }

  @Nonnull
  public Set <String> getAllAssignedUserIDs () {
    return ContainerHelper.makeUnmodifiable (m_aUsers.keySet ());
  }

  public boolean containsUser (final String sUserID) {
    return m_aUsers.containsKey (sUserID);
  }

  @Nonnull
  public EChange assignUser (@Nonnull final IUser aUser) {
    if (aUser == null)
      throw new NullPointerException ("user");

    final String sUserID = aUser.getID ();
    if (m_aUsers.containsKey (sUserID))
      return EChange.UNCHANGED;
    m_aUsers.put (sUserID, aUser);
    return EChange.CHANGED;
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

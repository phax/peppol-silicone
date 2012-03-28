package ap.peppol.webgui.security.usergroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import ap.peppol.webgui.api.AbstractManager;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;

/**
 * This class manages the available users.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserGroupManager extends AbstractManager implements IUserGroupManager {
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, UserGroup> m_aUserGroups = new HashMap <String, UserGroup> ();

  public UserGroupManager () {}

  @Nonnull
  public IUserGroup createNewUserGroup (@Nonnull @Nonempty final String sName) {
    // Create user group
    final UserGroup aUserGroup = new UserGroup (sName);

    m_aRWLock.writeLock ().lock ();
    try {
      // Store
      m_aUserGroups.put (aUserGroup.getID (), aUserGroup);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aUserGroup;
  }

  /**
   * Private locked version returning the implementation class
   * 
   * @param sUserGroupID
   *        The ID to be resolved
   * @return May be <code>null</code>
   */
  @Nullable
  private UserGroup _internalGetUserGroupOfID (@Nullable final String sUserGroupID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aUserGroups.get (sUserGroupID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public IUserGroup getUserGroupOfID (@Nullable final String sUserGroupID) {
    return _internalGetUserGroupOfID (sUserGroupID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends IUserGroup> getAllUserGroups () {
    m_aRWLock.readLock ().lock ();
    try {
      return ContainerHelper.newList (m_aUserGroups.values ());
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange deleteUserGroup (@Nullable final String sUserGroupID) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aUserGroups.remove (sUserGroupID) == null)
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange renameUserGroup (@Nullable final String sUserGroupID, @Nonnull @Nonempty final String sNewName) {
    // Resolve user group
    final UserGroup aUserGroup = _internalGetUserGroupOfID (sUserGroupID);
    if (aUserGroup == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aUserGroup.setName (sNewName).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange assignUserToUserGroup (@Nullable final String sUserGroupID, @Nullable final String sUserID) {
    // Resolve user group
    final UserGroup aUserGroup = _internalGetUserGroupOfID (sUserGroupID);
    if (aUserGroup == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aUserGroup.assignUser (sUserID).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unassignUserFromUserGroup (@Nullable final String sUserGroupID, @Nullable final String sUserID) {
    // Resolve user group
    final UserGroup aUserGroup = _internalGetUserGroupOfID (sUserGroupID);
    if (aUserGroup == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aUserGroup.unassignUser (sUserID).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unassignUserFromAllUserGroups (@Nullable final String sUserID) {
    m_aRWLock.writeLock ().lock ();
    try {
      EChange eChange = EChange.UNCHANGED;
      for (final UserGroup aUserGroup : m_aUserGroups.values ())
        eChange = eChange.or (aUserGroup.unassignUser (sUserID));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <IUserGroup> getAllUserGroupsWithAssignedUser (@Nullable final String sUserID) {
    m_aRWLock.readLock ().lock ();
    try {
      final List <IUserGroup> ret = new ArrayList <IUserGroup> ();
      for (final IUserGroup aUserGroup : m_aUserGroups.values ())
        if (aUserGroup.containsUserID (sUserID))
          ret.add (aUserGroup);
      return ret;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange assignRoleToUserGroup (@Nullable final String sUserGroupID, @Nullable final String sRoleID) {
    // Resolve user group
    final UserGroup aUserGroup = _internalGetUserGroupOfID (sUserGroupID);
    if (aUserGroup == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aUserGroup.assignRole (sRoleID).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unassignRoleFromUserGroup (@Nullable final String sUserGroupID, @Nullable final String sRoleID) {
    // Resolve user group
    final UserGroup aUserGroup = _internalGetUserGroupOfID (sUserGroupID);
    if (aUserGroup == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aUserGroup.unassignRole (sRoleID).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unassignRoleFromAllUserGroups (@Nullable final String sRoleID) {
    m_aRWLock.writeLock ().lock ();
    try {
      EChange eChange = EChange.UNCHANGED;
      for (final UserGroup aUserGroup : m_aUserGroups.values ())
        eChange = eChange.or (aUserGroup.unassignRole (sRoleID));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <IUserGroup> getAllUserGroupsWithAssignedRole (@Nullable final String sRoleID) {
    m_aRWLock.readLock ().lock ();
    try {
      final List <IUserGroup> ret = new ArrayList <IUserGroup> ();
      for (final IUserGroup aUserGroup : m_aUserGroups.values ())
        if (aUserGroup.containsRoleID (sRoleID))
          ret.add (aUserGroup);
      return ret;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }
}

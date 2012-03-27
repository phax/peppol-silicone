package ap.peppol.webgui.security.role;

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
public final class RoleManager extends AbstractManager {
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, Role> m_aRoles = new HashMap <String, Role> ();

  public RoleManager () {}

  @Nonnull
  public IRole createNewRole (@Nonnull @Nonempty final String sName) {
    // Create role
    final Role aRole = new Role (sName);

    m_aRWLock.writeLock ().lock ();
    try {
      // Store
      m_aRoles.put (aRole.getID (), aRole);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aRole;
  }

  /**
   * Private locked version returning the implementation class
   * 
   * @param sRoleID
   *        The ID to be resolved
   * @return May be <code>null</code>
   */
  @Nullable
  private Role _internalGetRoleOfID (@Nullable final String sRoleID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aRoles.get (sRoleID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public IRole getRoleOfID (@Nullable final String sRoleID) {
    return _internalGetRoleOfID (sRoleID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends IRole> getAllRoles () {
    m_aRWLock.readLock ().lock ();
    try {
      return ContainerHelper.newList (m_aRoles.values ());
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange deleteRole (@Nullable final String sRoleID) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aRoles.remove (sRoleID) == null)
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange renameRole (@Nullable final String sRoleID, @Nonnull @Nonempty final String sNewName) {
    // Resolve user group
    final Role aRole = _internalGetRoleOfID (sRoleID);
    if (aRole == null)
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try {
      if (aRole.setDisplayName (sNewName).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}

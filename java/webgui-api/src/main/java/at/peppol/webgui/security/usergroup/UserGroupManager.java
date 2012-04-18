/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.webgui.security.usergroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import at.peppol.webgui.io.AbstractDAO;
import at.peppol.webgui.security.CSecurity;
import at.peppol.webgui.security.role.IRoleManager;
import at.peppol.webgui.security.user.IUserManager;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.state.EChange;

/**
 * This class manages the available users.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserGroupManager extends AbstractDAO implements IUserGroupManager {
  private final IUserManager m_aUserMgr;
  private final IRoleManager m_aRoleMgr;
  private final Map <String, UserGroup> m_aUserGroups = new HashMap <String, UserGroup> ();

  public UserGroupManager (@Nonnull final IUserManager aUserMgr, @Nonnull final IRoleManager aRoleMgr) {
    super ("security/usergroups.xml");
    m_aUserMgr = aUserMgr;
    m_aRoleMgr = aRoleMgr;
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onInit () {
    // Administrators user group
    UserGroup aUG = _addUserGroup (new UserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID,
                                                  CSecurity.USERGROUP_ADMINISTRATORS_NAME));
    if (m_aUserMgr.containsUserWithID (CSecurity.USER_ADMINISTRATOR_ID))
      aUG.assignUser (CSecurity.USER_ADMINISTRATOR_ID);
    if (m_aRoleMgr.containsRoleWithID (CSecurity.ROLE_ADMINISTRATOR_ID))
      aUG.assignRole (CSecurity.ROLE_ADMINISTRATOR_ID);

    // Users user group
    aUG = _addUserGroup (new UserGroup (CSecurity.USERGROUP_USERS_ID, CSecurity.USERGROUP_USERS_NAME));
    if (m_aUserMgr.containsUserWithID (CSecurity.USER_USER_ID))
      aUG.assignUser (CSecurity.USER_USER_ID);
    if (m_aRoleMgr.containsRoleWithID (CSecurity.ROLE_USER_ID))
      aUG.assignRole (CSecurity.ROLE_USER_ID);

    // Guests user group
    aUG = _addUserGroup (new UserGroup (CSecurity.USERGROUP_GUESTS_ID, CSecurity.USERGROUP_GUESTS_NAME));
    if (m_aUserMgr.containsUserWithID (CSecurity.USER_GUEST_ID))
      aUG.assignUser (CSecurity.USER_GUEST_ID);
    // no role for this user group

    return EChange.CHANGED;
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc) {
    for (final IMicroElement eUserGroup : aDoc.getDocumentElement ().getChildElements ())
      _addUserGroup (MicroTypeConverter.convertToNative (eUserGroup, UserGroup.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData () {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("usergroups");
    for (final UserGroup aUserGroup : ContainerHelper.getSortedByKey (m_aUserGroups).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aUserGroup, "usergroup"));
    return aDoc;
  }

  @Nonnull
  private UserGroup _addUserGroup (@Nonnull final UserGroup aUserGroup) {
    final String sUserGroupID = aUserGroup.getID ();
    if (m_aUserGroups.containsKey (sUserGroupID))
      throw new IllegalArgumentException ("User group ID " + sUserGroupID + " is already in use!");
    m_aUserGroups.put (sUserGroupID, aUserGroup);
    return aUserGroup;
  }

  @Nonnull
  public IUserGroup createNewUserGroup (@Nonnull @Nonempty final String sName) {
    // Create user group
    final UserGroup aUserGroup = new UserGroup (sName);

    m_aRWLock.writeLock ().lock ();
    try {
      // Store
      _addUserGroup (aUserGroup);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aUserGroup;
  }

  public boolean containsUserGroupWithID (@Nullable final String sUserGroupID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aUserGroups.containsKey (sUserGroupID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
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

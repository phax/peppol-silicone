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

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;

/**
 * Interface for a user group manager
 * 
 * @author philip
 */
public interface IUserGroupManager {
  /**
   * Create a new user group. The passed name must not be unique over all user
   * names.
   * 
   * @param sName
   *        The name of the user group to create. May neither be
   *        <code>null</code> nor empty.
   * @return The created user group.
   */
  @Nonnull
  IUserGroup createNewUserGroup (@Nonnull @Nonempty String sName);

  /**
   * Delete the user group with the specified ID
   * 
   * @param sUserGroupID
   *        The ID of the user group to be deleted. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the user group was deleted,
   *         {@link EChange#UNCHANGED} otherwise
   */
  @Nonnull
  EChange deleteUserGroup (@Nullable String sUserGroupID);

  /**
   * Check if a user group with the specified ID is contained
   * 
   * @param sUserGroupID
   *        The ID of the user group to check
   * @return <code>true</code> if no user group exists, <code>false</code>
   *         otherwise.
   */
  boolean containsUserGroupWithID (@Nullable String sUserGroupID);

  /**
   * Get the user group with the specified ID
   * 
   * @param sUserGroupID
   *        The ID of the user group to search
   * @return <code>null</code> if no such user group exists
   */
  @Nullable
  IUserGroup getUserGroupOfID (@Nullable String sUserGroupID);

  /**
   * @return A non-<code>null</code> list of all user groups
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <? extends IUserGroup> getAllUserGroups ();

  /**
   * Rename the user group with the specified ID
   * 
   * @param sUserGroupID
   *        The ID of the user group. May be <code>null</code>.
   * @param sNewName
   *        The new name of the user group. May neither be <code>null</code> nor
   *        empty.
   * @return {@link EChange#CHANGED} if the user group ID was valid, and the new
   *         name was different from the old name
   */
  @Nonnull
  EChange renameUserGroup (@Nullable String sUserGroupID, @Nonnull @Nonempty String sNewName);

  /**
   * Assign the passed user ID to the passed user group.<br>
   * Note: no validity check must be performed for the user ID
   * 
   * @param sUserGroupID
   *        ID of the user group to assign to
   * @param sUserID
   *        ID of the user to be assigned.
   * @return {@link EChange#CHANGED} if the user group ID was valid, and the
   *         specified user ID was not already contained.
   */
  @Nonnull
  EChange assignUserToUserGroup (@Nullable String sUserGroupID, @Nullable String sUserID);

  /**
   * Unassign the specified user ID from the passed user group ID.
   * 
   * @param sUserGroupID
   *        The ID of the user group to unassign the user ID from
   * @param sUserID
   *        The user ID to be unassigned
   * @return {@link EChange#CHANGED} if the user group ID was resolved, and the
   *         passed user ID was assigned to the user group
   */
  @Nonnull
  EChange unassignUserFromUserGroup (@Nullable String sUserGroupID, @Nullable String sUserID);

  /**
   * Unassign the passed user ID from all user groups.
   * 
   * @param sUserID
   *        ID of the user to be unassigned.
   * @return {@link EChange#CHANGED} if the passed user ID was at least assigned
   *         to one user group.
   */
  @Nonnull
  EChange unassignUserFromAllUserGroups (@Nullable String sUserID);

  /**
   * Get a collection of all user groups to which a certain user is assigned to.
   * 
   * @param sUserID
   *        The user ID to search
   * @return A non-<code>null</code>but may be empty collection with all
   *         matching user groups.
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <IUserGroup> getAllUserGroupsWithAssignedUser (@Nullable String sUserID);

  /**
   * Assign the passed role ID to the user group with the passed ID.<br>
   * Note: the role ID must not be checked for consistency
   * 
   * @param sUserGroupID
   *        The ID of the user group to assign the role to.
   * @param sRoleID
   *        The ID of the role to be assigned
   * @return {@link EChange#CHANGED} if the passed user group ID was resolved,
   *         and the role ID was not already previously contained
   */
  @Nonnull
  EChange assignRoleToUserGroup (@Nullable String sUserGroupID, @Nullable String sRoleID);

  /**
   * Unassign the passed role ID from the passed user group ID
   * 
   * @param sUserGroupID
   *        The ID of the user group to unassign the role ID from
   * @param sRoleID
   *        The role ID to be unassigned.
   * @return {@link EChange#CHANGED} if the user group ID was resolved and the
   *         passed role ID was contained
   */
  @Nonnull
  EChange unassignRoleFromUserGroup (@Nullable String sUserGroupID, @Nullable String sRoleID);

  /**
   * Unassign the passed role ID from existing user groups.
   * 
   * @param sRoleID
   *        The role ID to be unassigned
   * @return {@link EChange#CHANGED} if the passed role ID was contained in at
   *         least one user group
   */
  @Nonnull
  EChange unassignRoleFromAllUserGroups (@Nullable String sRoleID);

  /**
   * Get a collection of all user groups to which a certain role is assigned to.
   * 
   * @param sRoleID
   *        The role ID to search
   * @return A non-<code>null</code>but may be empty collection with all
   *         matching user groups.
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <IUserGroup> getAllUserGroupsWithAssignedRole (@Nullable String sRoleID);
}

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
package at.peppol.webgui.security.role;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;

/**
 * Interface for a role manager.
 * 
 * @author philip
 */
public interface IRoleManager {
  /**
   * Create a new role.
   * 
   * @param sName
   *        The name of the new role
   * @return The created role and never <code>null</code>.
   */
  @Nonnull
  IRole createNewRole (@Nonnull @Nonempty String sName);

  /**
   * Delete the role with the passed ID
   * 
   * @param sRoleID
   *        The role ID to be deleted
   * @return {@link EChange#CHANGED} if the passed role ID was found and deleted
   */
  @Nonnull
  EChange deleteRole (@Nullable String sRoleID);

  /**
   * Check if the role with the specified ID is contained
   * 
   * @param sRoleID
   *        The role ID to be check
   * @return <code>true</code> if such role exists, <code>false</code> otherwise
   */
  boolean containsRoleWithID (@Nullable String sRoleID);

  /**
   * Get the role with the specified ID
   * 
   * @param sRoleID
   *        The role ID to be resolved
   * @return <code>null</code> if no such role exists.
   */
  @Nullable
  IRole getRoleOfID (@Nullable String sRoleID);

  /**
   * @return A non-<code>null</code> collection of all available roles
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <? extends IRole> getAllRoles ();

  /**
   * Rename the role with the passed ID
   * 
   * @param sRoleID
   *        The ID of the role to be renamed
   * @param sNewName
   *        The new name of the role
   * @return {@link EChange#CHANGED} if the passed role ID was found, and the
   *         new name is different from the old name of he role
   */
  @Nonnull
  EChange renameRole (@Nullable String sRoleID, @Nonnull @Nonempty String sNewName);
}

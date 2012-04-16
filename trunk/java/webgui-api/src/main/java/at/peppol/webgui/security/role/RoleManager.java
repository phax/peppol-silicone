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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import at.peppol.webgui.io.AbstractManager;
import at.peppol.webgui.security.CSecurity;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.state.EChange;

/**
 * This class manages the available roles.
 * 
 * @author philip
 */
@ThreadSafe
public final class RoleManager extends AbstractManager implements IRoleManager {
  private final Map <String, Role> m_aRoles = new HashMap <String, Role> ();

  public RoleManager () {
    super ("security/roles.xml");
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onInit () {
    _addRole (new Role (CSecurity.ROLE_ADMINISTRATOR_ID, CSecurity.ROLE_ADMINISTRATOR_NAME));
    _addRole (new Role (CSecurity.ROLE_USER_ID, CSecurity.ROLE_USER_NAME));
    return EChange.CHANGED;
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc) {
    for (final IMicroElement eRole : aDoc.getDocumentElement ().getChildElements ())
      _addRole (MicroTypeConverter.convertToNative (eRole, Role.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData () {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("roles");
    for (final Role aRole : ContainerHelper.getSortedByKey (m_aRoles).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aRole, "role"));
    return aDoc;
  }

  private void _addRole (@Nonnull final Role aRole) {
    final String sRoleID = aRole.getID ();
    if (m_aRoles.containsKey (sRoleID))
      throw new IllegalArgumentException ("Role ID " + sRoleID + " is already in use!");
    m_aRoles.put (sRoleID, aRole);
  }

  @Nonnull
  public IRole createNewRole (@Nonnull @Nonempty final String sName) {
    // Create role
    final Role aRole = new Role (sName);

    m_aRWLock.writeLock ().lock ();
    try {
      // Store
      _addRole (aRole);
      markAsChanged ();
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
    return aRole;
  }

  public boolean containsRoleWithID (@Nullable final String sRoleID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aRoles.containsKey (sRoleID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
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
  public Collection <? extends IRole> getAllRoles () {
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
      if (aRole.setName (sNewName).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}

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

/**
 * Default implementation of the {@link IUserGroup} interface.
 * 
 * @author philip
 */
@NotThreadSafe
public final class UserGroup implements IUserGroup {
  private final String m_sID;
  private String m_sName;
  private final Set <String> m_aUserIDs = new HashSet <String> ();
  private final Set <String> m_aRoleIDs = new HashSet <String> ();

  public UserGroup (@Nonnull @Nonempty final String sName) {
    this (GlobalIDFactory.getNewPersistentStringID (), sName);
  }

  UserGroup (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sName) {
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
  public String getName () {
    return m_sName;
  }

  @Nonnull
  EChange setName (@Nonnull @Nonempty final String sName) {
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("name");
    if (sName.equals (m_sName))
      return EChange.UNCHANGED;
    m_sName = sName;
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsImmutableObject
  public Set <String> getAllContainedUserIDs () {
    return ContainerHelper.makeUnmodifiable (m_aUserIDs);
  }

  public boolean containsUserID (final String sUserID) {
    return m_aUserIDs.contains (sUserID);
  }

  @Nonnull
  EChange assignUser (@Nonnull final String sUserID) {
    if (StringHelper.hasNoText (sUserID))
      throw new IllegalArgumentException ("userID");

    return EChange.valueOf (m_aUserIDs.add (sUserID));
  }

  @Nonnull
  EChange unassignUser (@Nonnull final String sUserID) {
    return EChange.valueOf (m_aUserIDs.remove (sUserID));
  }

  @Nonnull
  @ReturnsImmutableObject
  public Set <String> getAllContainedRoleIDs () {
    return ContainerHelper.makeUnmodifiable (m_aRoleIDs);
  }

  public boolean containsRoleID (final String sRoleID) {
    return m_aRoleIDs.contains (sRoleID);
  }

  @Nonnull
  EChange assignRole (@Nonnull final String sRoleID) {
    if (StringHelper.hasNoText (sRoleID))
      throw new IllegalArgumentException ("userID");

    return EChange.valueOf (m_aRoleIDs.add (sRoleID));
  }

  @Nonnull
  EChange unassignRole (@Nonnull final String sRoleID) {
    return EChange.valueOf (m_aRoleIDs.remove (sRoleID));
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
                                       .append ("assignedUsers", m_aUserIDs)
                                       .append ("assignedRoles", m_aRoleIDs)
                                       .toString ();
  }
}
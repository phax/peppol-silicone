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
package at.peppol.visualization.index;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single resource that is attached to a visualization artefact.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ArtefactResource {
  private final EArtefactResourceType m_eType;
  private final String m_sFilename;

  public ArtefactResource (@Nonnull final EArtefactResourceType eType, @Nonnull @Nonempty final String sFilename) {
    if (eType == null)
      throw new NullPointerException ("type");
    if (StringHelper.hasNoText (sFilename))
      throw new IllegalArgumentException ("filename");
    m_eType = eType;
    m_sFilename = sFilename;
  }

  @Nonnull
  public EArtefactResourceType getType () {
    return m_eType;
  }

  @Nonnull
  @Nonempty
  public String getFilename () {
    return m_sFilename;
  }

  @Nonnull
  public IReadableResource getResource (final String sBaseDir) {
    return new ClassPathResource (sBaseDir + m_sFilename);
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ArtefactResource))
      return false;
    final ArtefactResource rhs = (ArtefactResource) o;
    return m_eType.equals (rhs.m_eType) && m_sFilename.equals (rhs.m_sFilename);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_eType).append (m_sFilename).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("type", m_eType).append ("filename", m_sFilename).toString ();
  }
}

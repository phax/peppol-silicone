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
package eu.peppol.validation.engine.result;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.lang.StackTraceHelper;

import eu.peppol.validation.engine.jaxb.layers.LayerType;
import eu.peppol.validation.engine.jaxb.layers.ValidationType;

/**
 * @version 2.0 27/09/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class ValidationResult {
  private final LayerType m_aLayer;
  private IResourceErrorGroup m_aResults;
  private String m_sErrorType;
  private String m_sErrorMessage;
  private String m_sErrorStackTrace;

  public ValidationResult (@Nonnull final LayerType aLayer) {
    if (aLayer == null)
      throw new NullPointerException ("layer");
    m_aLayer = aLayer;
  }

  public int getCode () {
    return m_aLayer.getCode ();
  }

  @Nullable
  public String getName () {
    return m_aLayer.getName ();
  }

  @Nullable
  public String getValidationGroup () {
    return m_aLayer.getValidationGroup ();
  }

  @Nullable
  public ValidationType getValidationType () {
    return m_aLayer.getValidationType ();
  }

  @Nonnull
  @ReturnsImmutableObject
  public List <String> getValidationDocs () {
    return Collections.unmodifiableList (m_aLayer.getValidationDoc ());
  }

  public void setError (@Nonnull final Exception ex) {
    m_sErrorType = ex.getClass ().getName ();
    m_sErrorMessage = ex.getMessage ();
    m_sErrorStackTrace = StackTraceHelper.getStackAsString (ex);
  }

  @Nullable
  public String getErrorType () {
    return m_sErrorType;
  }

  @Nullable
  public String getErrorMessage () {
    return m_sErrorMessage;
  }

  @Nullable
  public String getErrorStackTrace () {
    return m_sErrorStackTrace;
  }

  public void setResults (@Nullable final IResourceErrorGroup aResults) {
    m_aResults = aResults;
  }

  @Nullable
  public IResourceErrorGroup getResults () {
    return m_aResults;
  }
}

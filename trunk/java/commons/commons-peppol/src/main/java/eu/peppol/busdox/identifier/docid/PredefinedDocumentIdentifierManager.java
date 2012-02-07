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
package eu.peppol.busdox.identifier.docid;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.annotations.ReturnsImmutableObject;

import eu.peppol.busdox.identifier.IdentifierUtils;

/**
 * This class manages the predefined PEPPOL document identifiers the
 * <b>busdox-docid-qns</b> scheme.<br>
 * This class provides sanity methods around
 * {@link EPredefinedDocumentIdentifier} which would be to bogus to generate
 * them.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class PredefinedDocumentIdentifierManager {
  private static final Map <String, IPredefinedDocumentIdentifier> s_aCodes = new HashMap <String, IPredefinedDocumentIdentifier> ();

  static {
    // Add all predefined document identifier
    for (final EPredefinedDocumentIdentifier eDocID : EPredefinedDocumentIdentifier.values ())
      s_aCodes.put (eDocID.getValue (), eDocID);
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final PredefinedDocumentIdentifierManager s_aInstance = new PredefinedDocumentIdentifierManager ();

  private PredefinedDocumentIdentifierManager () {}

  /**
   * @return A non-modifiable list of all PEPPOL document identifiers.
   */
  @Nonnull
  @Nonempty
  @ReturnsImmutableObject
  public static Collection <IPredefinedDocumentIdentifier> getAllDocumentIdentifiers () {
    return Collections.unmodifiableCollection (s_aCodes.values ());
  }

  /**
   * Find the document identifier with the given ID. This search is done case
   * insensitive.
   *
   * @param sDocIDValue
   *        The value to search. Without any identifier scheme! May be
   *        <code>null</code>.
   * @return <code>null</code> if no such document identifier exists.
   */
  @Nullable
  public static IPredefinedDocumentIdentifier getDocumentIdentifierOfID (@Nullable final String sDocIDValue) {
    if (sDocIDValue != null)
      for (final Map.Entry <String, IPredefinedDocumentIdentifier> aEntry : s_aCodes.entrySet ()) {
        // Use case insensitive identifier value comparison
        if (IdentifierUtils.areDocumentIdentifierValuesEqual (sDocIDValue, aEntry.getKey ()))
          return aEntry.getValue ();
      }
    return null;
  }

  /**
   * Check if a document identifier with the given ID exists.
   *
   * @param sDocIDValue
   *        The value to search. Without any identifier scheme! May be
   *        <code>null</code>.
   * @return <code>true</code> if such a document identifier exists,
   *         <code>false</code> otherwise.
   */
  public static boolean containsDocumentIdentifierWithID (@Nullable final String sDocIDValue) {
    return getDocumentIdentifierOfID (sDocIDValue) != null;
  }
}

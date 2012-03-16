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
package at.peppol.commons.tools;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import at.peppol.commons.identifier.docid.IPEPPOLDocumentTypeIdentifierParts;

import com.phloc.commons.annotations.Nonempty;

@Immutable
final class CodeGenerationUtils {
  private static final String SKIP_TRANSACTION_PREFIX = "urn:www.cenbii.eu:transaction:biicoretrdm";
  private static final String SKIP_BIS_PREFIX = "urn:www.peppol.eu:bis:peppol";

  private CodeGenerationUtils () {}

  @Nonnull
  @Nonempty
  public static String createShortcutDocumentTypeIDName (@Nonnull final IPEPPOLDocumentTypeIdentifierParts aDocIDParts) {
    // Create a shortcut constant with a more readable name!
    String sTransactionID = "";
    if (aDocIDParts.getTransactionID ().startsWith (SKIP_TRANSACTION_PREFIX)) {
      sTransactionID = "_T" + aDocIDParts.getTransactionID ().substring (SKIP_TRANSACTION_PREFIX.length ());
      final int nIndex = sTransactionID.indexOf (':');
      if (nIndex >= 0)
        sTransactionID = sTransactionID.substring (0, nIndex);
    }

    String sExtensionID = "";
    for (final String sCurExtensionID : aDocIDParts.getExtensionIDs ())
      if (sCurExtensionID.startsWith (SKIP_BIS_PREFIX)) {
        sExtensionID = "_BIS" + sCurExtensionID.substring (SKIP_BIS_PREFIX.length ());
        final int nIndex = sExtensionID.indexOf (':');
        if (nIndex >= 0)
          sExtensionID = sExtensionID.substring (0, nIndex);
        break;
      }

    return (aDocIDParts.getLocalName () + sTransactionID + sExtensionID).toUpperCase (Locale.US);
  }

  @Nullable
  @Nonempty
  public static String createShortcutBISIDName (@Nonnull final String sBISID) {
    if (!sBISID.startsWith (SKIP_BIS_PREFIX))
      throw new IllegalArgumentException ("Invalid BIS ID: " + sBISID);

    String ret = "BIS" + sBISID.substring (SKIP_BIS_PREFIX.length ());
    final int nIndex = ret.indexOf (':');
    if (nIndex >= 0)
      ret = ret.substring (0, nIndex);
    return ret.toUpperCase (Locale.US);
  }
}

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

package at.peppol.commons.codelist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.lang.EnumHelper;
import com.phloc.commons.name.IHasDisplayName;

/**
 * This file is generated from Genericode file InvoiceTypeCode.gc. Do NOT edit!
 */
public enum EInvoiceTypeCode implements IHasID <String>, IHasDisplayName {

  COMMERCIAL_INVOICE ("380", "Commercial invoice"),
  FACTORED_INVOICE ("393", "Factored invoice");
  private final String m_sID;
  private final String m_sDisplayName;

  private EInvoiceTypeCode (@Nonnull @Nonempty final String sID, @Nonnull final String sDisplayName) {
    m_sID = sID;
    m_sDisplayName = sDisplayName;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public String getDisplayName () {
    return m_sDisplayName;
  }

  @Nullable
  public static EInvoiceTypeCode getFromIDOrNull (@Nullable final String sID) {
    return EnumHelper.getFromIDOrNull (EInvoiceTypeCode.class, sID);
  }

  @Nullable
  public static String getDisplayNameFromIDOrNull (@Nullable final String sID) {
    final EInvoiceTypeCode eValue = EInvoiceTypeCode.getFromIDOrNull (sID);
    return ((eValue == null) ? null : eValue.getDisplayName ());
  }

}

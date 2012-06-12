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

package at.peppol.commons.identifier.procid;

import java.util.List;
import javax.annotation.Nonnull;
import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.docid.IPredefinedDocumentTypeIdentifier;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.version.Version;


/**
 * This file is generated. Do NOT edit!
 * 
 */
public enum EPredefinedProcessIdentifier
    implements IPredefinedProcessIdentifier
{


    /**
     * urn:www.cenbii.eu:profile:bii01:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii01_ver1_0("urn:www.cenbii.eu:profile:bii01:ver1.0", "urn:www.peppol.eu:bis:peppol1a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.CATALOGUE_T019_BIS1A, EPredefinedDocumentTypeIdentifier.APPLICATIONRESPONSE_T057_BIS1A, EPredefinedDocumentTypeIdentifier.APPLICATIONRESPONSE_T058_BIS1A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii03:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii03_ver1_0("urn:www.cenbii.eu:profile:bii03:ver1.0", "urn:www.peppol.eu:bis:peppol3a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.ORDER_T001_BIS3A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii04:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii04_ver1_0("urn:www.cenbii.eu:profile:bii04:ver1.0", "urn:www.peppol.eu:bis:peppol4a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii05:ver1.0
     * @since code list 1.1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii05_ver1_0("urn:www.cenbii.eu:profile:bii05:ver1.0", "urn:www.peppol.eu:bis:peppol5a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A, EPredefinedDocumentTypeIdentifier.CREDITNOTE_T014_BIS5A, EPredefinedDocumentTypeIdentifier.INVOICE_T015_BIS5A }, new Version("1.1.0")),

    /**
     * urn:www.cenbii.eu:profile:bii06:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii06_ver1_0("urn:www.cenbii.eu:profile:bii06:ver1.0", "urn:www.peppol.eu:bis:peppol6a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.ORDER_T001_BIS6A, EPredefinedDocumentTypeIdentifier.ORDERRESPONSESIMPLE_T002_BIS6A, EPredefinedDocumentTypeIdentifier.ORDERRESPONSESIMPLE_T003_BIS6A, EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS6A, EPredefinedDocumentTypeIdentifier.CREDITNOTE_T014_BIS6A, EPredefinedDocumentTypeIdentifier.INVOICE_T015_BIS6A }, new Version("1.0.0"));
    public final static EPredefinedProcessIdentifier BIS1A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii01_ver1_0;
    public final static EPredefinedProcessIdentifier BIS3A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii03_ver1_0;
    public final static EPredefinedProcessIdentifier BIS4A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii04_ver1_0;
    public final static EPredefinedProcessIdentifier BIS5A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii05_ver1_0;
    public final static EPredefinedProcessIdentifier BIS6A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii06_ver1_0;
    private final String m_sID;
    private final String m_sBISID;
    private final EPredefinedDocumentTypeIdentifier[] m_aDocIDs;
    private final Version m_aSince;

    private EPredefinedProcessIdentifier(
        @Nonnull
        @Nonempty
        final String sID,
        @Nonnull
        @Nonempty
        final String sBISID,
        @Nonnull
        @Nonempty
        final EPredefinedDocumentTypeIdentifier[] aDocIDs,
        @Nonnull
        final Version aSince) {
        m_sID = sID;
        m_sBISID = sBISID;
        m_aDocIDs = aDocIDs;
        m_aSince = aSince;
    }

    @Nonnull
    @Nonempty
    public String getScheme() {
        return CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME;
    }

    @Nonnull
    @Nonempty
    public String getValue() {
        return m_sID;
    }

    @Nonnull
    @Nonempty
    public String getBISID() {
        return m_sBISID;
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<? extends IPredefinedDocumentTypeIdentifier> getDocumentTypeIdentifiers() {
        return ContainerHelper.newList(m_aDocIDs);
    }

    @Nonnull
    public SimpleProcessIdentifier getAsProcessIdentifier() {
        return new SimpleProcessIdentifier(this);
    }

    @Nonnull
    public Version getSince() {
        return m_aSince;
    }

}

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

package eu.peppol.busdox.identifier.procid;

import java.util.List;
import javax.annotation.Nonnull;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import eu.peppol.busdox.identifier.CIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;
import eu.peppol.busdox.identifier.docid.EPredefinedDocumentIdentifier;
import eu.peppol.busdox.identifier.docid.IPredefinedDocumentIdentifier;


/**
 * This file is generated. Do NOT edit!
 * 
 */
public enum EPredefinedProcessIdentifier
    implements IPredefinedProcessIdentifier
{


    /**
     * urn:www.cenbii.eu:profile:bii01:ver1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii01_ver1_0("urn:www.cenbii.eu:profile:bii01:ver1.0", "urn:www.peppol.eu:bis:peppol1a:ver1.0", new EPredefinedDocumentIdentifier[] {EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_schema_xsd_Catalogue_2__Catalogue__urn_www_cenbii_eu_transaction_biicoretrdm019_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_ApplicationResponse_2__ApplicationResponse__urn_www_cenbii_eu_transaction_biicoretrdm057_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_ApplicationResponse_2__ApplicationResponse__urn_www_cenbii_eu_transaction_biicoretrdm058_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0 }),

    /**
     * urn:www.cenbii.eu:profile:bii03:ver1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii03_ver1_0("urn:www.cenbii.eu:profile:bii03:ver1.0", "urn:www.peppol.eu:bis:peppol3a:ver1.0", new EPredefinedDocumentIdentifier[] {EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Order_2__Order__urn_www_cenbii_eu_transaction_biicoretrdm001_ver1_0__urn_www_peppol_eu_bis_peppol3a_ver1_0__2_0 }),

    /**
     * urn:www.cenbii.eu:profile:bii04:ver1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii04_ver1_0("urn:www.cenbii.eu:profile:bii04:ver1.0", "urn:www.peppol.eu:bis:peppol4a:ver1.0", new EPredefinedDocumentIdentifier[] {EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol4a_ver1_0__2_0 }),

    /**
     * urn:www.cenbii.eu:profile:bii06:ver1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii06_ver1_0("urn:www.cenbii.eu:profile:bii06:ver1.0", "urn:www.peppol.eu:bis:peppol6a:ver1.0", new EPredefinedDocumentIdentifier[] {EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Order_2__Order__urn_www_cenbii_eu_transaction_biicoretrdm001_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_OrderResponseSimple_2__OrderResponseSimple__urn_www_cenbii_eu_transaction_biicoretrdm002_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_OrderResponseSimple_2__OrderResponseSimple__urn_www_cenbii_eu_transaction_biicoretrdm003_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_CreditNote_2__CreditNote__urn_www_cenbii_eu_transaction_biicoretrdm014_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0, EPredefinedDocumentIdentifier.urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm015_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0 });
    private String m_sID;
    private String m_sBSIID;
    private EPredefinedDocumentIdentifier[] m_aDocIDs;

    private EPredefinedProcessIdentifier(
        @Nonnull
        @Nonempty
        final String sID,
        @Nonnull
        @Nonempty
        final String sBISID,
        @Nonnull
        @Nonempty
        final EPredefinedDocumentIdentifier[] aDocIDs) {
        m_sID = sID;
        m_sBSIID = sBISID;
        m_aDocIDs = aDocIDs;
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
        return m_sBSIID;
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<? extends IPredefinedDocumentIdentifier> getDocumentIdentifiers() {
        return ContainerHelper.newList(m_aDocIDs);
    }

    @Nonnull
    public SimpleProcessIdentifier getAsProcessIdentifier() {
        return new SimpleProcessIdentifier(this);
    }

}

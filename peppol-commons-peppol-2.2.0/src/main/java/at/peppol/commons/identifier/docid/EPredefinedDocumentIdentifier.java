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

package at.peppol.commons.identifier.docid;

import java.util.List;
import javax.annotation.Nonnull;
import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.SimpleDocumentIdentifier;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;


/**
 * This file is generated. Do NOT edit!
 * 
 */
public enum EPredefinedDocumentIdentifier
    implements IPredefinedDocumentIdentifier
{


    /**
     * urn:www.peppol.eu:schema:xsd:VirtualCompanyDossier-1::VirtualCompanyDossier##urn:www.cenbii.eu:transaction:biicoretrdm991:ver0.1:#urn:www.peppol.eu:bis:peppol991a:ver1.0::0.1
     * 
     */
    urn_www_peppol_eu_schema_xsd_VirtualCompanyDossier_1__VirtualCompanyDossier__urn_www_cenbii_eu_transaction_biicoretrdm991_ver0_1__urn_www_peppol_eu_bis_peppol991a_ver1_0__0_1(new PEPPOLDocumentIdentifierParts("urn:www.peppol.eu:schema:xsd:VirtualCompanyDossier-1", "VirtualCompanyDossier", "urn:www.cenbii.eu:transaction:biicoretrdm991:ver0.1", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol991a:ver1.0"), "0.1"), "Virtual Company Dossier"),

    /**
     * urn:www.peppol.eu:schema:xsd:VirtualCompanyDossierPackage-1::VirtualCompanyDossierPackage##urn:www.cenbii.eu:transaction:biicoretrdm992:ver0.1:#urn:www.peppol.eu:bis:peppol992a:ver1.0::0.1
     * 
     */
    urn_www_peppol_eu_schema_xsd_VirtualCompanyDossierPackage_1__VirtualCompanyDossierPackage__urn_www_cenbii_eu_transaction_biicoretrdm992_ver0_1__urn_www_peppol_eu_bis_peppol992a_ver1_0__0_1(new PEPPOLDocumentIdentifierParts("urn:www.peppol.eu:schema:xsd:VirtualCompanyDossierPackage-1", "VirtualCompanyDossierPackage", "urn:www.cenbii.eu:transaction:biicoretrdm992:ver0.1", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol992a:ver1.0"), "0.1"), "Virtual Company Dossier Package"),

    /**
     * urn:www.peppol.eu:schema:xsd:CatalogueTemplate-1::CatalogueTemplate##urn:www.cenbii.eu:transaction:biicoretrdm993:ver0.1:#urn:www.peppol.eu:bis:peppol993a:ver1.0::0.1
     * 
     */
    urn_www_peppol_eu_schema_xsd_CatalogueTemplate_1__CatalogueTemplate__urn_www_cenbii_eu_transaction_biicoretrdm993_ver0_1__urn_www_peppol_eu_bis_peppol993a_ver1_0__0_1(new PEPPOLDocumentIdentifierParts("urn:www.peppol.eu:schema:xsd:CatalogueTemplate-1", "CatalogueTemplate", "urn:www.cenbii.eu:transaction:biicoretrdm993:ver0.1", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol993a:ver1.0"), "0.1"), "Catalogue Template"),

    /**
     * urn:oasis:names:specification:ubl:schema:schema:xsd:Catalogue-2::Catalogue##urn:www.cenbii.eu:transaction:biicoretrdm019:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_schema_xsd_Catalogue_2__Catalogue__urn_www_cenbii_eu_transaction_biicoretrdm019_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:schema:xsd:Catalogue-2", "Catalogue", "urn:www.cenbii.eu:transaction:biicoretrdm019:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol1a:ver1.0"), "2.0"), "PEPPOL Catalogue profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_ApplicationResponse_2__ApplicationResponse__urn_www_cenbii_eu_transaction_biicoretrdm057_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", "ApplicationResponse", "urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol1a:ver1.0"), "2.0"), "PEPPOL Catalogue profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm058:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_ApplicationResponse_2__ApplicationResponse__urn_www_cenbii_eu_transaction_biicoretrdm058_ver1_0__urn_www_peppol_eu_bis_peppol1a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2", "ApplicationResponse", "urn:www.cenbii.eu:transaction:biicoretrdm058:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol1a:ver1.0"), "2.0"), "PEPPOL Catalogue profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_Order_2__Order__urn_www_cenbii_eu_transaction_biicoretrdm001_ver1_0__urn_www_peppol_eu_bis_peppol3a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:Order-2", "Order", "urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol3a:ver1.0"), "2.0"), "PEPPOL Order profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol4a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol4a:ver1.0"), "2.0"), "PEPPOL Invoice profile (UBL)"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_Order_2__Order__urn_www_cenbii_eu_transaction_biicoretrdm001_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:Order-2", "Order", "urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_OrderResponseSimple_2__OrderResponseSimple__urn_www_cenbii_eu_transaction_biicoretrdm002_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2", "OrderResponseSimple", "urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm003:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_OrderResponseSimple_2__OrderResponseSimple__urn_www_cenbii_eu_transaction_biicoretrdm003_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2", "OrderResponseSimple", "urn:www.cenbii.eu:transaction:biicoretrdm003:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm010_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_CreditNote_2__CreditNote__urn_www_cenbii_eu_transaction_biicoretrdm014_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2", "CreditNote", "urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile"),

    /**
     * urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
     * 
     */
    urn_oasis_names_specification_ubl_schema_xsd_Invoice_2__Invoice__urn_www_cenbii_eu_transaction_biicoretrdm015_ver1_0__urn_www_peppol_eu_bis_peppol6a_ver1_0__2_0(new PEPPOLDocumentIdentifierParts("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0", ContainerHelper.newList("urn:www.peppol.eu:bis:peppol6a:ver1.0"), "2.0"), "PEPPOL Procurement profile");
    private IPEPPOLDocumentIdentifierParts m_aParts;
    private String m_sID;
    private String m_sCommonName;

    private EPredefinedDocumentIdentifier(
        @Nonnull
        final IPEPPOLDocumentIdentifierParts aParts,
        @Nonnull
        @Nonempty
        final String sCommonName) {
        m_aParts = aParts;
        m_sCommonName = sCommonName;
        m_sID = m_aParts.getAsDocumentIdentifierValue();
    }

    @Nonnull
    @Nonempty
    public String getScheme() {
        return CIdentifier.DEFAULT_DOCUMENT_IDENTIFIER_SCHEME;
    }

    @Nonnull
    @Nonempty
    public String getValue() {
        return m_sID;
    }

    @Nonnull
    @Nonempty
    public String getRootNS() {
        return m_aParts.getRootNS();
    }

    @Nonnull
    @Nonempty
    public String getLocalName() {
        return m_aParts.getLocalName();
    }

    @Nonnull
    @Nonempty
    public String getSubTypeIdentifier() {
        return m_aParts.getSubTypeIdentifier();
    }

    @Nonnull
    @Nonempty
    public String getTransactionID() {
        return m_aParts.getTransactionID();
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<String> getExtensionIDs() {
        return m_aParts.getExtensionIDs();
    }

    @Nonnull
    @Nonempty
    public String getAsUBLCustomizationID() {
        return m_aParts.getAsUBLCustomizationID();
    }

    @Nonnull
    @Nonempty
    public String getVersion() {
        return m_aParts.getVersion();
    }

    @Nonnull
    @Nonempty
    public String getCommonName() {
        return m_sCommonName;
    }

    @Nonnull
    @Nonempty
    public String getAsDocumentIdentifierValue() {
        return m_sID;
    }

    @Nonnull
    public SimpleDocumentIdentifier getAsDocumentIdentifier() {
        return new SimpleDocumentIdentifier(this);
    }

}

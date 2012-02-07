<?xml version="1.0" encoding="UTF-8"?>
<!--

    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:in="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
    xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
    xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
    xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" 
    exclude-result-prefixes="xsd xd" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Sep 8, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> Oriol Bausa</xd:p>
            <xd:p><xd:b>Purpose:</xd:b> XSL transformation sheet to convert a UBL Invoice instance into a
                BII Core Transaction Data Model 10 UBL Conformant Instance</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- all document types should have matching templates - will determine which output to use -->

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">

        <xsl:apply-templates/>

    </xsl:template>


    <!-- Invoice  -->
    <xsl:template match="in:Invoice">

        <in:Invoice xmlns:in="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
            xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
            xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
            xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2">

            <xsl:apply-templates select="ext:UBLExtensions"/>
            <xsl:apply-templates select="cbc:UBLVersionID"/>
            <xsl:apply-templates select="cbc:CustomizationID"/>
            <xsl:apply-templates select="cbc:ProfileID"/>
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:IssueDate"/>
            <xsl:apply-templates select="cbc:InvoiceTypeCode"/>
            <xsl:apply-templates select="cbc:Note"/>
            <xsl:apply-templates select="cbc:TaxPointDate"/>
            <xsl:apply-templates select="cbc:DocumentCurrencyCode"/>
            <xsl:apply-templates select="cbc:AccountingCost"/>
            <xsl:apply-templates select="cac:InvoicePeriod"/>
            <xsl:apply-templates select="cac:OrderReference"/>
            <xsl:apply-templates select="cac:ContractDocumentReference"/>
            <xsl:apply-templates select="cac:AdditionalDocumentReference"/>
            <xsl:apply-templates select="cac:AccountingSupplierParty"/>
            <xsl:apply-templates select="cac:AccountingCustomerParty"/>
            <xsl:apply-templates select="cac:PayeeParty"/>
            <xsl:apply-templates select="cac:Delivery"/>
            <xsl:apply-templates select="cac:PaymentMeans"/>
            <xsl:apply-templates select="cac:PaymentTerms"/>
            <xsl:apply-templates select="cac:AllowanceCharge"/>
            <xsl:apply-templates select="cac:TaxTotal"/>
            <xsl:apply-templates select="cac:LegalMonetaryTotal"/>
            <xsl:apply-templates select="cac:InvoiceLine"/>
        </in:Invoice>

    </xsl:template>

    <!-- Copy each of the cbc and cac elements to the version 1 namespace with identical content -->
    <xsl:template match="cbc:*">

        <xsl:element name="cbc:{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>

    </xsl:template>

    <xsl:template match="cac:*">

        <xsl:element name="cac:{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>

    </xsl:template>

    <xsl:template match="ext:*">

        <xsl:element name="ext:{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>

    </xsl:template>


    <xsl:template match="cac:InvoicePeriod">
        <xsl:element name="cac:InvoicePeriod">
            <xsl:apply-templates select="cbc:StartDate"/>
            <xsl:apply-templates select="cbc:EndDate"/>
        </xsl:element>
    </xsl:template>

    <xsl:template
        match="cac:OrderReference | cac:PartyIdentification | cac:TaxScheme | cac:FinancialInstitution | cac:SellersItemIdentification | cac:StandarditemIdentification">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:ContractDocumentReference">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:DocumentType"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:AdditionalDocumentReference">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:DocumentType"/>
            <xsl:apply-templates select="cac:Attachment"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Attachment">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:EmbeddedDocumentBinaryObject"/>
            <xsl:apply-templates select="cac:ExternalReference"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:ExternalReference">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:URIID"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:AccountingSupplierParty | cac:AccountingCustomerParty">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cac:Party"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Party">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:EndpointID"/>
            <xsl:apply-templates select="cac:PartyIdentification"/>
            <xsl:apply-templates select="cac:PartyName"/>
            <xsl:apply-templates select="cac:PostalAddress"/>
            <xsl:apply-templates select="cac:PartyTaxScheme"/>
            <xsl:apply-templates select="cac:PartyLegalEntity"/>
            <xsl:apply-templates select="cac:Contact"/>
            <xsl:apply-templates select="cac:Person"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PartyName">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:Name"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PostalAddress">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:Postbox"/>
            <xsl:apply-templates select="cbc:StreetName"/>
            <xsl:apply-templates select="cbc:AdditionalStreetName"/>
            <xsl:apply-templates select="cbc:BuildingNumber"/>
            <xsl:apply-templates select="cbc:Department"/>
            <xsl:apply-templates select="cbc:CityName"/>
            <xsl:apply-templates select="cbc:PostalZone"/>
            <xsl:apply-templates select="cbc:CountrySubentity"/>
            <xsl:apply-templates select="cac:Country"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Address">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:StreetName"/>
            <xsl:apply-templates select="cbc:AdditionalStreetName"/>
            <xsl:apply-templates select="cbc:BuildingNumber"/>
            <xsl:apply-templates select="cbc:Department"/>
            <xsl:apply-templates select="cbc:CityName"/>
            <xsl:apply-templates select="cbc:PostalZone"/>
            <xsl:apply-templates select="cbc:CountrySubentity"/>
            <xsl:apply-templates select="cac:Country"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Country">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:IdentificationCode"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PartyTaxScheme">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:CompanyID"/>
            <xsl:apply-templates select="cac:TaxScheme"/>
        </xsl:element>
    </xsl:template>

    <xsl:template
        match="cac:AccountingSupplierParty/cac:PartyLegalEntity | cac:AccountingCustomerParty/cac:PartyLegalEntity">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:RegistrationName"/>
            <xsl:apply-templates select="cbc:CompanyID"/>
            <xsl:apply-templates select="cac:RegistrationAddress"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PayeeParty/cac:PartyLegalEntity">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:CompanyID"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:RegistrationAddress">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:CityName"/>
            <xsl:apply-templates select="cbc:CountrySubentity"/>
            <xsl:apply-templates select="cac:Country"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Contact">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:Telephone"/>
            <xsl:apply-templates select="cbc:Telefax"/>
            <xsl:apply-templates select="cbc:ElectronicMail"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Person">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:FirstName"/>
            <xsl:apply-templates select="cbc:FamilyName"/>
            <xsl:apply-templates select="cbc:MiddleName"/>
            <xsl:apply-templates select="cbc:JobTitle"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PayeeParty">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cac:PartyIdentification"/>
            <xsl:apply-templates select="cac:PartyName"/>
            <xsl:apply-templates select="cac:PartyLegalEntity"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Delivery">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ActualDeliveryDate"/>
            <xsl:apply-templates select="cac:DeliveryLocation"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:DeliveryLocation">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cac:Address"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PaymentMeans">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:PaymentMeansCode"/>
            <xsl:apply-templates select="cbc:PaymentDueDate"/>
            <xsl:apply-templates select="cbc:PaymentChannelCode"/>
            <xsl:apply-templates select="cbc:PaymentID"/>
            <xsl:apply-templates select="cac:PayeeFinancialAccount"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PayeeFinancialAccount">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cac:FinancialInstitutionBranch"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:FinancialInstitutionBranch">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cac:FinancialInstitution"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:PaymentTerms">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:Note"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:AllowanceCharge">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ChargeIndicator"/>
            <xsl:apply-templates select="cbc:AllowanceChargeReason"/>
            <xsl:apply-templates select="cbc:Amount"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:TaxTotal">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:TaxAmount"/>
            <xsl:apply-templates select="cac:TaxSubtotal"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:TaxSubtotal">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:TaxableAmount"/>
            <xsl:apply-templates select="cbc:TaxAmount"/>
            <xsl:apply-templates select="cac:TaxCategory"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:TaxCategory">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:Percent"/>
            <xsl:apply-templates select="cbc:TaxExemptionReasonCode"/>
            <xsl:apply-templates select="cbc:TaxExemptionReason"/>
            <xsl:apply-templates select="cac:TaxScheme"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:LegalMonetaryTotal">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:LineExtensionAmount"/>
            <xsl:apply-templates select="cbc:TaxExclusiveAmount"/>
            <xsl:apply-templates select="cbc:TaxInclusiveAmount"/>
            <xsl:apply-templates select="cbc:AllowanceTotalAmount"/>
            <xsl:apply-templates select="cbc:ChargeTotalAmount"/>
            <xsl:apply-templates select="cbc:PrepaidAmount"/>
            <xsl:apply-templates select="cbc:PayableRoundingAmount"/>
            <xsl:apply-templates select="cbc:PayableAmount"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:InvoiceLine">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:Note"/>
            <xsl:apply-templates select="cbc:InvoicedQuantity"/>
            <xsl:apply-templates select="cbc:LineExtensionAmount"/>
            <xsl:apply-templates select="cbc:AccountingCost"/>
            <xsl:apply-templates select="cac:OrderLineReference"/>
            <xsl:apply-templates select="cac:AllowanceCharge"/>
            <xsl:apply-templates select="cac:TaxTotal"/>
            <xsl:apply-templates select="cac:Item"/>
            <xsl:apply-templates select="cac:Price"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:OrderLineReference">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:LineID"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:InvoiceLine/cac:TaxTotal">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:TaxAmount"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Item">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:Description"/>
            <xsl:apply-templates select="cbc:Name"/>
            <xsl:apply-templates select="cac:SellersItemIdentification"/>
            <xsl:apply-templates select="cac:StandardItemIdentification"/>
            <xsl:apply-templates select="cac:CommodityClassification"/>
            <xsl:apply-templates select="cac:ClassifiedTaxCategory"/>
            <xsl:apply-templates select="cac:AdditionalItemProperty"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:CommodityClassification">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ItemClassificationCode"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:ClassifiedTaxCategory">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ID"/>
            <xsl:apply-templates select="cbc:Percent"/>
            <xsl:apply-templates select="cac:TaxScheme"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:AdditionalItemProperty">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:Name"/>
            <xsl:apply-templates select="cbc:Value"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Price">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:PriceAmount"/>
            <xsl:apply-templates select="cbc:BaseQuantity"/>
            <xsl:apply-templates select="cac:AllowanceCharge"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="cac:Price/cac:AllowanceCharge">
        <xsl:element name="cac:{local-name()}">
            <xsl:apply-templates select="cbc:ChargeIndicator"/>
            <xsl:apply-templates select="cbc:AllowanceChargeReason"/>
            <xsl:apply-templates select="cbc:MultiplierFactorNumeric"/>
            <xsl:apply-templates select="cbc:Amount"/>
            <xsl:apply-templates select="cbc:BaseAmount"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>

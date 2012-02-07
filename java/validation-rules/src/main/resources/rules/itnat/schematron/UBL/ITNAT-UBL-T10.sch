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
<!-- Schematron binding rules generated automatically. -->
<!-- Data binding to UBL syntax for T10 -->
<!-- (2009). Invinet Sistemes -->
<pattern id="UBL-T10" xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T10">
  <param value="(cac:PartyTaxScheme/cbc:CompanyID[@schemeID = 'IT:VAT'] and cac:PartyName/cbc:Name) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R003"/>
  <param value="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cbc:CountrySubentity and cac:Country/cbc:IdentificationCode) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R005"/>
  <param value="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cbc:CountrySubentity and cac:Country/cbc:IdentificationCode) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R008"/>
  <param value="not(cac:PartyLegalEntity/cbc:CompanyID[@schemeID = 'IT:CC']) or (cac:PartyLegalEntity[cbc:CompanyID/@schemeID = 'IT:CC']/cac:CorporateRegistrationScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentity) or (cac:PartyLegalEntity[cbc:CompanyID/@schemeID = 'IT:CC']/cac:CorporateRegistrationScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentityCode) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R013"/>
  <param value="cbc:InvoiceTypeCode and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R016"/>
  <param value="(cbc:ID and cbc:IssueDate and cbc:DocumentType) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R017"/>
  <param value="(cbc:InvoicedQuantity) and (cbc:InvoicedQuantity/@unitCode) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R024"/>
  <param value="(cac:Price/cbc:PriceAmount) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R031"/>
  <param value="(cbc:ID and cbc:IssueDate and cbc:DocumentType) and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IT'))" name="IT-T10-R032"/>
  <param value="//cac:InvoiceLine" name="Invoice_Line"/>
  <param value="/ubl:Invoice" name="Invoice"/>
  <param value="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" name="Supplier_Party_Address"/>
  <param value="//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" name="Customer_Party_Address"/>
  <param value="//cac:DespatchDocumentReference" name="Transport_Document"/>
  <param value="//cac:TaxRepresentativeParty" name="Tax_Representative_Party"/>
  <param value="//cac:AccountingSupplierParty/cac:Party" name="Supplier_Party"/>
  <param value="//cac:DocumentReference" name="Line_Level_Transport_Document"/>
</pattern>

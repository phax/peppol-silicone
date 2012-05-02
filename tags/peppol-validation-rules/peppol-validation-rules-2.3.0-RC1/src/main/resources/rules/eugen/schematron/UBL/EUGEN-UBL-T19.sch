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
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

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
<!-- Data binding to UBL syntax for T19 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" id="UBL-T19" is-a="T19">
  <param value="(cbc:UBLVersionID)" name="EUGEN-T19-R001"/>
  <param value="(cbc:CustomizationID)" name="EUGEN-T19-R002"/>
  <param value="(cbc:ProfileID)" name="EUGEN-T19-R003"/>
  <param value="(cac:PartyLegalEntity/cbc:CompanyID)" name="EUGEN-T19-R005"/>
  <param value="(cbc:Telephone)" name="EUGEN-T19-R006"/>
  <param value="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" name="EUGEN-T19-R007"/>
  <param value="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID='VAT' and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" name="EUGEN-T19-R009"/>
  <param value="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" name="EUGEN-T19-R010"/>
  <param value="not((cac:StandardItemIdentification)) or (cac:StandardItemIdentification/cbc:ID/@schemeID)" name="EUGEN-T19-R012"/>
  <param value="(cbc:PriceAmount) &gt;=0" name="EUGEN-T19-R013"/>
  <param value="((cac:CommodityClassification/cbc:CommodityCode) and (cac:CommodityClassification/cbc:ItemClassificationCode)) or not(cac:CommodityClassification)" name="EUGEN-T19-R015"/>
  <param value="((//cac:ValidityPeriod) and (/ubl:Catalogue/cac:ValidityPeriod) and (//cac:ValidityPeriod/cbc:StartDate)&gt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:StartDate) and (//cac:ValidityPeriod/cbc:EndDate)&lt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:EndDate)) or not(//cac:ValidityPeriod) or not(/ubl:Catalogue/cac:ValidityPeriod)" name="EUGEN-T19-R016"/>
  <param value="(cbc:Description)" name="EUGEN-T19-R017"/>
  <param value="(cac:ClassifiedTaxCategory/cbc:ID)" name="EUGEN-T19-R018"/>
  <param value="(cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:ID)" name="EUGEN-T19-R019"/>
  <param value="(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode)" name="EUGEN-T19-R020"/>
  <param value="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" name="EUGEN-T19-R023"/>
  <param value="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" name="EUGEN-T19-R024"/>
  <param value="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and  ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID='VAT' and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" name="EUGEN-T19-R025"/>
  <param value="(not(cbc:ID) and (cbc:ContractType)) or (cbc:ID)" name="EUGEN-T19-R027"/>
  <param value="count(cac:ReferencedContract) &lt;=1" name="EUGEN-T19-R028"/>
  <param value="(cbc:StartDate and cbc:EndDate) and not(number(translate(cbc:StartDate,'-','')) &gt; number(translate(cbc:EndDate,'-',''))) or number(translate(cbc:EndDate,'-','')) = number(translate(cbc:StartDate,'-',''))" name="EUGEN-T19-R029"/>
  <param value="(cbc:EndpointID)" name="EUGEN-T19-R030"/>
  <param value="(cbc:EndpointID)" name="EUGEN-T19-R031"/>
  <param value="((cbc:OrderableIndicator=true()) and cbc:OrderableUnit) or (cbc:OrderableIndicator=false()) or not(cbc:OrderableIndicator)" name="EUGEN-T19-R032"/>
  <param value="((cbc:MaximumOrderQuantity) and (cbc:MinimumOrderQuantity) and (number(cbc:MaximumOrderQuantity) &gt;= number(cbc:MinimumOrderQuantity))) or not(cbc:MaximumOrderQuantity) or not(cbc:MinimumOrderQuantity)" name="EUGEN-T19-R033"/>
  <param value="((cbc:MaximumQuantity) and (cbc:MinimumQuantity) and (number(cbc:MaximumQuantity) &gt;= number(cbc:MinimumQuantity))) or not(cbc:MaximumQuantity) or not(cbc:MinimumQuantity)" name="EUGEN-T19-R034"/>
  <param value="((cbc:MinimumOrderQuantity) and (cbc:MinimumOrderQuantity) &gt;=0) or not(cbc:MinimumOrderQuantity)" name="EUGEN-T19-R036"/>
  <param value="(cbc:MaximumOrderQuantity)" name="EUGEN-T19-R037"/>
  <param value="(cbc:MinimumOrderQuantity)" name="EUGEN-T19-R038"/>
  <param value="((cbc:MaximumOrderQuantity) and (cbc:MaximumOrderQuantity) &gt;=0) or not(cbc:MaximumOrderQuantity)" name="EUGEN-T19-R039"/>
  <param value="(cbc:ID)" name="EUGEN-T19-R040"/>
  <param value="(cac:SellersItemIdentification/cbc:ID)" name="EUGEN-T19-R041"/>
  <param value="((cbc:PriceAmount) and (cbc:BaseQuantity)) or not (cbc:PriceAmount)" name="EUGEN-T19-R042"/>
  <param value="//cac:CatalogueLine" name="Catalogue_Line"/>
  <param value="//cac:ValidityPeriod" name="Catalogue_validity_period"/>
  <param value="//cac:ReceiverParty" name="Catalogue_receiver_party"/>
  <param value="//cac:ProviderParty" name="Catalogue_provider_party"/>
  <param value="/ubl:Catalogue" name="Catalogue"/>
  <param value="//cac:SellerSupplierParty/cac:Party/cac:Contact" name="Supplier_Contact"/>
  <param value="//cac:SellerSupplierParty/cac:Party" name="Supplier"/>
  <param value="//cac:SellerSupplierParty/cac:Party/cac:PostalAddress" name="Seller_Address"/>
  <param value="//cac:AdditionalItemProperty" name="Item_Property"/>
  <param value="//cac:DocumentReference" name="Document_Reference"/>
  <param value="//cac:ContractorCustomerParty/cac:Party/cac:PostalAddress" name="Customer_Address"/>
  <param value="//cac:ContractorCustomerParty/cac:Party" name="Customer"/>
  <param value="//cac:ReferencedContract" name="Contract"/>
  <param value="//cac:Party" name="Party"/>
  <param value="//cac:RequiredItemLocationQuantity/cac:Price" name="Item_Price"/>
  <param value="//cac:RequiredItemLocationQuantity" name="Item_Location_Price"/>
  <param value="//cac:Item" name="Item"/>
</pattern>

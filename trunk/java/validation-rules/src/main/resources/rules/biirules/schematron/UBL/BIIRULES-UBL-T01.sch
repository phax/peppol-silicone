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
<!-- Data binding to UBL syntax for T01 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" id="UBL-T01" is-a="T01">
  <param value="(cbc:UBLVersionID)" name="BIIRULE-T01-R001"/>
  <param value="(cbc:CustomizationID)" name="BIIRULE-T01-R002"/>
  <param value="(cbc:ProfileID)" name="BIIRULE-T01-R003"/>
  <param value="(cbc:IssueDate)" name="BIIRULE-T01-R004"/>
  <param value="(cbc:ID)" name="BIIRULE-T01-R005"/>
  <param value="(cbc:ID)" name="BIIRULE-T01-R006"/>
  <param value="(cbc:ID)" name="BIIRULE-T01-R007"/>
  <param value="(cbc:ID) and (cbc:ID != '' )" name="BIIRULE-T01-R008"/>
  <param value="(cac:Party/cac:PartyName/cbc:Name)" name="BIIRULE-T01-R009"/>
  <param value="(cac:Party/cac:PartyName/cbc:Name)" name="BIIRULE-T01-R010"/>
  <param value="(cbc:StartDate and cbc:EndDate) and (number(translate(cbc:StartDate,'-','')) &lt;= number(translate(cbc:EndDate,'-','')))" name="BIIRULE-T01-R011"/>
  <param value="(cac:Party/cac:PostalAddress/cbc:CityName and cac:Party/cac:PostalAddress/cbc:PostalZone) or (cac:Party/cac:PostalAddress/cbc:ID)" name="BIIRULE-T01-R012"/>
  <param value="(cac:Item/cbc:Name) or (cac:Item/cac:StandardItemIdentification/cbc:ID) or (cac:Item/cac:SellersItemIdentification/cbc:ID)" name="BIIRULE-T01-R013"/>
  <param value="(cac:Party/cac:PostalAddress/cbc:CityName and cac:Party/cac:PostalAddress/cbc:PostalZone) or (cac:Party/cac:PostalAddress/cbc:ID)" name="BIIRULE-T01-R014"/>
  <param value="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (following::cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and  ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (following::cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (following::cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID='VAT' and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID='VAT']/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((following::cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" name="BIIRULE-T01-R015"/>
  <param value="(cac:OrderLine/cac:LineItem)" name="BIIRULE-T01-R016"/>
  <param value="(cbc:ID)" name="BIIRULE-T01-R017"/>
  <param value="number(cbc:LineExtensionAmount) = number(round(sum(//cac:LineItem/cbc:LineExtensionAmount) * 10 *10) div 100)" name="BIIRULE-T01-R018"/>
  <param value="(cbc:AllowanceTotalAmount) and number(cbc:AllowanceTotalAmount) = (round(sum(/ubl:Order/cac:AllowanceCharge[cbc:ChargeIndicator=&quot;false&quot;]/cbc:Amount) * 10 * 10) div 100) or not(cbc:AllowanceTotalAmount)" name="BIIRULE-T01-R019"/>
  <param value="(cbc:ChargeTotalAmount) and number(cbc:ChargeTotalAmount) = (round(sum(/ubl:Order/cac:AllowanceCharge[cbc:ChargeIndicator=&quot;true&quot;]/cbc:Amount) * 10 *10) div 100) or not(cbc:ChargeTotalAmount)" name="BIIRULE-T01-R020"/>
  <param value="((cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and (preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount) - number(cbc:AllowanceTotalAmount) + round(sum(preceding::cac:TaxTotal/cbc:TaxAmount) *10 *10) div 100 ) *10 *10) div 100)) or (not(cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and (preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) - number(cbc:AllowanceTotalAmount) + round(sum(preceding::cac:TaxTotal/cbc:TaxAmount) *10 *10) div 100 ) *10*10) div 100)) or ((cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and (preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount)+ round(sum(preceding::cac:TaxTotal/cbc:TaxAmount) *10 *10) div 100 ) * 10 * 10 ) div 100)) or ((cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and not(preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount) - number(cbc:AllowanceTotalAmount) ) * 10 * 10) div 100)) or (not(cbc:ChargeTotalAmount) and (cbc:AllowanceTotalAmount) and not(preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) - number(cbc:AllowanceTotalAmount) ) * 10 * 10) div 100)) or ((cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and not(preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round((number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount)) + 10 * 10) div 100)) or(not(cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and (preceding::cac:TaxTotal/cbc:TaxAmount) and (number(cbc:PayableAmount) = round(( number(cbc:LineExtensionAmount) + round(sum(preceding::cac:TaxTotal/cbc:TaxAmount) *10 *10) div 100 ) * 10 * 10) div 100)) or (not(cbc:ChargeTotalAmount) and not(cbc:AllowanceTotalAmount) and not(preceding::cac:TaxTotal/cbc:TaxAmount) and number(cbc:LineExtensionAmount) = number(cbc:PayableAmount))" name="BIIRULE-T01-R021"/>
  <param value="string-length(string(cbc:Name)) &lt;= 50" name="BIIRULE-T01-R023"/>
  <param value="not((cac:StandardItemIdentification)) or (cac:StandardItemIdentification/cbc:ID/@schemeID)" name="BIIRULE-T01-R024"/>
  <param value="not((cac:CommodityClassification)) or (cac:CommodityClassification/cbc:ItemClassificationCode/@listID)" name="BIIRULE-T01-R025"/>
  <param value="number(.) &gt;= 0" name="BIIRULE-T01-R026"/>
  <param value="not(//@currencyID != //cbc:DocumentCurrencyCode)" name="BIIRULE-T01-R027"/>
  <param value="(cbc:IdentificationCode)" name="BIIRULE-T01-R028"/>
  <param value="number(cbc:TaxAmount) = number(round(sum(//cac:OrderLine/cac:LineItem/cbc:TotalTaxAmount) * 10 * 10) div 100)" name="BIIRULE-T01-R029"/>
  <param value="(cbc:DocumentCurrencyCode)" name="BIIRULE-T01-R030"/>
  <param value="count(//*[substring(name(),string-length(name())-7) = 'Quantity'][@unitCode]) = count(//*[substring(name(),string-length(name())-7) = 'Quantity'])" name="BIIRULE-T01-R031"/>
  <param value="//cac:BuyerCustomerParty" name="Customer"/>
  <param value="//cac:LineItem" name="Order_Line"/>
  <param value="//cac:RequestedDeliveryPeriod" name="Requested_delivery_period"/>
  <param value="/ubl:Order" name="Order"/>
  <param value="//cac:LineItem/cac:Price/cbc:PriceAmount" name="Item_Price"/>
  <param value="//cac:Item" name="Item"/>
  <param value="//cac:SellerSupplierParty" name="Supplier"/>
  <param value="/ubl:Order/cac:TaxTotal" name="Tax_Total"/>
  <param value="//cac:AnticipatedMonetaryTotal" name="Total_Amounts"/>
  <param value="//cac:OriginatorDocumentReference" name="Originator_document"/>
  <param value="//cac:AdditionalDocumentReference" name="Annex"/>
  <param value="//cac:Contract" name="Contract"/>
  <param value="//cac:AllowanceCharge" name="AllowanceCharge"/>
  <param value="//cac:Country" name="Country"/>
</pattern>

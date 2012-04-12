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
<!-- Data binding to UBL syntax for T15 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" id="UBL-T15" is-a="T15">
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="EUGEN-T15-R001"/>
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="EUGEN-T15-R002"/>
  <param value="(cbc:InvoicedQuantity and cbc:InvoicedQuantity/@unitCode)" name="EUGEN-T15-R003"/>
  <param value="((cbc:PaymentMeansCode = '31') and (cac:PayeeFinancialAccount/cbc:ID/@schemeID and cac:PayeeFinancialAccount/cbc:ID/@schemeID = 'IBAN') and (cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID/@schemeID and cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:ID/@schemeID = 'BIC')) or (cbc:PaymentMeansCode != '31') or ((cbc:PaymentMeansCode = '31') and  (not(cac:PayeeFinancialAccount/cbc:ID/@schemeID) or (cac:PayeeFinancialAccount/cbc:ID/@schemeID != 'IBAN')))" name="EUGEN-T15-R004"/>
  <param value="(cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" name="EUGEN-T15-R005"/>
  <param value="(((//cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount) and (cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT')) or not((//cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])) and (local-name(parent:: node())=&quot;Invoice&quot;)) or not(local-name(parent:: node())=&quot;Invoice&quot;)" name="EUGEN-T15-R006"/>
  <param value="((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount) and (cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or not((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])))" name="EUGEN-T15-R007"/>
  <param value="(parent::cac:AllowanceCharge) or (cbc:ID and cbc:Percent) or (cbc:ID = 'AE')" name="EUGEN-T15-R008"/>
  <param value="((cac:TaxCategory/cbc:ID = 'E') and (cac:TaxCategory/cbc:TaxExemptionReason or cac:TaxCategory/cbc:TaxExemptionReasonCode)) or (cac:TaxCategory/cbc:ID != 'E')" name="EUGEN-T15-R009"/>
  <param value="not(cac:PayeeParty) or (cac:PayeeParty/cac:PartyName/cbc:Name)" name="EUGEN-T15-R010"/>
  <param value="(//cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount and cbc:ID) or not((//cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']))" name="EUGEN-T15-R011"/>
  <param value="number(cbc:MultiplierFactorNumeric) &gt;=0" name="EUGEN-T15-R012"/>
  <param value="(cbc:MultiplierFactorNumeric and cbc:BaseAmount) or (not(cbc:MultiplierFactorNumeric) and not(cbc:BaseAmount))" name="EUGEN-T15-R013"/>
  <param value="starts-with(//cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID,//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (//cac:TaxCategory/cbc:ID) = 'AE' or not((//cac:TaxCategory/cbc:ID) = 'AE')" name="EUGEN-T15-R015"/>
  <param value="(((//cac:TaxCategory/cbc:ID) = 'AE')  and not((//cac:TaxCategory/cbc:ID) != 'AE' )) or not((//cac:TaxCategory/cbc:ID) = 'AE') or not(//cac:TaxCategory)" name="EUGEN-T15-R016"/>
  <param value="(//cbc:TaxExclusiveAmount = //cac:TaxTotal/cac:TaxSubtotal[cac:TaxCategory/cbc:ID='AE']/cbc:TaxableAmount) and (//cac:TaxCategory/cbc:ID) = 'AE' or not((//cac:TaxCategory/cbc:ID) = 'AE')" name="EUGEN-T15-R017"/>
  <param value="//cac:TaxTotal/cbc:TaxAmount = 0 and (//cac:TaxCategory/cbc:ID) = 'AE' or not((//cac:TaxCategory/cbc:ID) = 'AE')" name="EUGEN-T15-R018"/>
  <param value="number(cbc:PayableAmount) &gt;= 0" name="EUGEN-T15-R019"/>
  <param value="(cbc:StartDate)" name="EUGEN-T15-R020"/>
  <param value="(cbc:EndDate)" name="EUGEN-T15-R021"/>
  <param value="number(cbc:Amount)&gt;=0" name="EUGEN-T15-R022"/>
  <param value="(cbc:AllowanceChargeReason)" name="EUGEN-T15-R023"/>
  <param value="not(//@currencyID != //cbc:DocumentCurrencyCode)" name="EUGEN-T15-R024"/>
  <param value="//cac:InvoiceLine" name="Invoice_Line"/>
  <param value="//cac:PaymentMeans" name="Payment_Means"/>
  <param value="//cac:AccountingSupplierParty/cac:Party" name="Supplier_Party"/>
  <param value="//cac:TaxCategory" name="Tax_Category"/>
  <param value="//cac:AccountingCustomerParty/cac:Party" name="Customer_Party"/>
  <param value="//cac:Delivery/cac:DeliveryLocation/cac:Address" name="Delivery_Address"/>
  <param value="//cac:InvoicePeriod" name="Invoice_Period"/>
  <param value="/ubl:Invoice" name="Invoice"/>
  <param value="//cac:TaxSubtotal" name="Tax_Subtotal"/>
  <param value="//cac:Item/cac:ClassifiedTaxCategory" name="Classified_Tax_Category"/>
  <param value="//cac:AllowanceCharge" name="Allowance_Charge"/>
  <param value="//cac:LegalMonetaryTotal" name="Total_Amounts"/>
</pattern>

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
<!-- Data binding to UBL syntax for T17 -->
<!-- (2009). Invinet Sistemes -->
<pattern id="UBL-T17" xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T17">
  <param value="(cbc:ID)" name="NONAT-T17-R001"/>
  <param value="(cbc:IssueDate)" name="NONAT-T17-R002"/>
  <param value="(cac:LegalMonetaryTotal/cbc:LineExtensionAmount)" name="NONAT-T17-R003"/>
  <param value="(cac:LegalMonetaryTotal/cbc:PayableAmount)" name="NONAT-T17-R004"/>
  <param value="(cac:ReminderLine)" name="NONAT-T17-R005"/>
  <param value="(cbc:ID)" name="NONAT-T17-R006"/>
  <param value="(cac:BillingReference)" name="NONAT-T17-R007"/>
  <param value="number(child::cbc:LineExtensionAmount) = number(round((sum(//cac:ReminderLine/cbc:DebitLineAmount) - sum(//cac:ReminderLine/cbc:CreditLineAmount)) * 100) div 100)" name="NONAT-T17-R008"/>
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="NONAT-T17-R009"/>
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="NONAT-T17-R010"/>
  <param value="(number(cac:TaxCategory/cbc:Percent) = 0 and (cac:TaxCategory/cbc:TaxExemptionReason or cac:TaxCategory/cbc:TaxExemptionReasonCode)) or  (number(cac:TaxCategory/cbc:Percent) !=0)" name="NONAT-T17-R012"/>
  <param value="not(cac:PayeeParty) or (cac:PayeeParty/cac:PartyName/cbc:Name)" name="NONAT-T17-R013"/>
  <param value="(((//cac:TaxCategory/cbc:ID) = 'AE')  and not((//cac:TaxCategory/cbc:ID) != 'AE' )) or not((//cac:TaxCategory/cbc:ID) = 'AE') or not(//cac:TaxCategory)" name="NONAT-T17-R014"/>
  <param value="//cac:TaxTotal/cbc:TaxAmount = 0 and (//cac:TaxCategory/cbc:ID) = 'AE'  or not((//cac:TaxCategory/cbc:ID) = 'AE' )" name="NONAT-T17-R015"/>
  <param value=". = 'urn:www.cenbii.eu:profile:bii08:ver1.0'" name="NONAT-T17-R016"/>
  <param value="(cbc:UBLVersionID)" name="NONAT-T17-R017"/>
  <param value="(cbc:CustomizationID)" name="NONAT-T17-R018"/>
  <param value="(cbc:ProfileID)" name="NONAT-T17-R019"/>
  <param value="(cac:AccountingSupplierParty/cac:Party/cac:PartyName/cbc:Name)" name="NONAT-T17-R020"/>
  <param value="(cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone) or (cac:PostalAddress/cbc:ID)" name="NONAT-T17-R021"/>
  <param value="(cac:PostalAddress/cac:Country/cbc:IdentificationCode != '')" name="NONAT-T17-R022"/>
  <param value="(cac:PartyLegalEntity/cbc:CompanyID != '')" name="NONAT-T17-R023"/>
  <param value="(cac:PartyIdentification/cbc:ID != '')" name="NONAT-T17-R024"/>
  <param value="(cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name)" name="NONAT-T17-R025"/>
  <param value="(cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone) or (cac:PostalAddress/cbc:ID)" name="NONAT-T17-R026"/>
  <param value="(cac:PartyLegalEntity/cbc:CompanyID != '')" name="NONAT-T17-R027"/>
  <param value="(cac:Contact/cbc:ID != '')" name="NONAT-T17-R028"/>
  <param value="(cbc:TaxAmount)" name="NONAT-T17-R029"/>
  <param value="/ubl:Reminder/cac:TaxTotal" name="Tax_Total"/>
  <param value="//cac:AccountingSupplierParty/cac:Party" name="Supplier_Party"/>
  <param value="//cac:AccountingCustomerParty/cac:Party" name="Customer_Party"/>
  <param value="//cac:TaxSubtotal" name="Tax_Subtotal"/>
  <param value="//cbc:ProfileID" name="Reminder_Profile"/>
  <param value="//cac:ReminderLine" name="Reminder_Line"/>
  <param value="//cac:LegalMonetaryTotal" name="Total_Amounts"/>
  <param value="/ubl:Reminder" name="Reminder"/>
</pattern>

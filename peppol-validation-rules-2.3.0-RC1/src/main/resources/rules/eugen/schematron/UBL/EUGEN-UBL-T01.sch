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
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="EUGEN-T01-R001"/>
  <param value="(cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)" name="EUGEN-T01-R002"/>
  <param value="(cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" name="EUGEN-T01-R003"/>
  <param value="(cbc:AllowanceChargeReason)" name="EUGEN-T01-R004"/>
  <param value="(cbc:Quantity) and (cbc:Quantity/@unitCode)" name="EUGEN-T01-R005"/>
  <param value="number(cbc:Amount) &gt;= 0" name="EUGEN-T01-R006"/>
  <param value="(cbc:StartDate) or (cbc:EndDate) or (cbc:StartDate and cbc:EndDate) and (number(translate(cbc:StartDate,'-','')) &lt;= number(translate(cbc:EndDate,'-','')))" name="EUGEN-T01-R007"/>
  <param value="number(cbc:PayableAmount) &gt;= 0" name="EUGEN-T01-R008"/>
  <param value="number(cbc:LineExtensionAmount) &gt;= 0" name="EUGEN-T01-R009"/>
  <param value="number(cbc:Quantity) &gt;= 0" name="EUGEN-T01-R010"/>
  <param value="//cac:SellerSupplierParty/cac:Party" name="Supplier_Party"/>
  <param value="//cac:BuyerCustomerParty/cac:Party" name="Customer_Party"/>
  <param value="//cac:Delivery/cac:DeliveryLocation/cac:Address" name="Delivery_Address"/>
  <param value="//cac:AllowanceCharge" name="Allowance_Charge"/>
  <param value="//cac:OrderLine/cac:LineItem" name="Order_Line"/>
  <param value="//cac:Delivery/cac:RequestedDeliveryPeriod" name="Delivery_Period"/>
  <param value="//cac:AnticipatedMonetaryTotal" name="Monetary_Total"/>
</pattern>
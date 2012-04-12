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
<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for T01 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T01">
  <rule context="$Supplier_Party">
    <assert test="$EUGEN-T01-R001" flag="warning">[EUGEN-T01-R001]-A supplier postal address in an order SHOULD contain at least street name and number, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Order_Line">
    <assert test="$EUGEN-T01-R005" flag="warning">[EUGEN-T01-R005]-Each order line SHOULD contain the quantity</assert>
    <assert test="$EUGEN-T01-R009" flag="fatal">[EUGEN-T01-R009]-Line extension amount MUST NOT be negative</assert>
    <assert test="$EUGEN-T01-R010" flag="fatal">[EUGEN-T01-R010]-Quantity ordered MUST not be negative</assert>
  </rule>
  <rule context="$Monetary_Total">
    <assert test="$EUGEN-T01-R008" flag="fatal">[EUGEN-T01-R008]-Total payable amount MUST NOT be negative</assert>
  </rule>
  <rule context="$Delivery_Period">
    <assert test="$EUGEN-T01-R007" flag="fatal">[EUGEN-T01-R007]-A delivery period MUST have either the start date or the end date </assert>
  </rule>
  <rule context="$Delivery_Address">
    <assert test="$EUGEN-T01-R003" flag="warning">[EUGEN-T01-R003]-A Delivery address  SHOULD contain at least, city, zip code and country code.</assert>
  </rule>
  <rule context="$Customer_Party">
    <assert test="$EUGEN-T01-R002" flag="warning">[EUGEN-T01-R002]-A customer postal address in an invoice SHOULD contain at least, Street name and number, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Allowance_Charge">
    <assert test="$EUGEN-T01-R004" flag="warning">[EUGEN-T01-R004]-Allowance Charge reason text SHOULD be specified for all allowances and charges</assert>
    <assert test="$EUGEN-T01-R006" flag="fatal">[EUGEN-T01-R006]-An allowance amount MUST NOT be negative.</assert>
  </rule>
</pattern>

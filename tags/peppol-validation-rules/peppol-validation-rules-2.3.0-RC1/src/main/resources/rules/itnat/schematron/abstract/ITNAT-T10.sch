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
<!-- Abstract rules for T10 -->
<!-- (2009). Invinet Sistemes -->
<pattern abstract="true" id="T10" xmlns="http://purl.oclc.org/dsdl/schematron">
  <rule context="$Supplier_Party_Address">
    <assert test="$IT-T10-R005" flag="Fatal">[IT-T10-R005]-A suppliers postal address in an invoice MUST contain at least, Street name, city name, zip code, country subentity and country code.</assert>
  </rule>
  <rule context="$Invoice_Line">
    <assert test="$IT-T10-R024" flag="Fatal">[IT-T10-R024]-Each invoice line MUST contain the quantity and unit of measure.</assert>
    <assert test="$IT-T10-R031" flag="Fatal">[IT-T10-R031]-If the supplier country code is &#8220;IT&#8221;, each invoice line MUST contain the product/service unit price.</assert>
  </rule>
  <rule context="$Invoice">
    <assert test="$IT-T10-R016" flag="Fatal">[IT-T10-R016]-If the supplier country code is &#8220;IT&#8221;, an invoice MUST contain the invoice type.</assert>
  </rule>
  <rule context="$Transport_Document">
    <assert test="$IT-T10-R017" flag="Fatal">[IT-T10-R017]-If the supplier country code is &#8220;IT&#8221;, the reference to the transport document in an invoice MUST contain document identifier, issue date, reference law.</assert>
  </rule>
  <rule context="$Customer_Party_Address">
    <assert test="$IT-T10-R008" flag="Fatal">[IT-T10-R008]-A customer postal address in an invoice MUST contain at least, Street name, city name, zip code, country subentity and country code.</assert>
  </rule>
  <rule context="$Supplier_Party">
    <assert test="$IT-T10-R013" flag="Warning">[IT-T10-R013]-If the supplier country code is &#8220;IT&#8221; and is registered in the Italian Chamber of Commerce, the information about supplier&#8217;s Items Registration Company SHOULD include Country Subentity (as code or text) of Chambers of Commerce of company register.</assert>
  </rule>
  <rule context="$Tax_Representative_Party">
    <assert test="$IT-T10-R003" flag="Warning">[IT-T10-R003]-If Tax Representative Party exists and its country code is &#8220;IT&#8221;, an invoice MUST contain VAT Company Identifier and name of the Tax Representative Party.</assert>
  </rule>
  <rule context="$Line_Level_Transport_Document">
    <assert test="$IT-T10-R032" flag="Fatal">[IT-T10-R032]-If the supplier country code is &#8220;IT&#8221;, the reference to the transport document at line level in an invoice MUST contain document identifier, issue date, reference law.</assert>
  </rule>
</pattern>
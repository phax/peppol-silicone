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
<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for T19 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T19">
  <rule context="$Catalogue_Line">
    <assert test="$EUGEN-T19-R042" flag="warning">[EUGEN-T19-R042]-If Price amount is used than Price Base Quantity SHOUL be higher than zero</assert>
    <assert test="$EUGEN-T19-R040" flag="fatal">[EUGEN-T19-R040]-Contract reference SHOULD always present</assert>
    <assert test="$EUGEN-T19-R039" flag="warning">[EUGEN-T19-R039]-Catalogue line Maximum_quantity SHOULD NOT be negative</assert>
    <assert test="$EUGEN-T19-R038" flag="warning">[EUGEN-T19-R038]-Catalogue line Mimimum_quantity SHOULD be present</assert>
    <assert test="$EUGEN-T19-R037" flag="warning">[EUGEN-T19-R037]-Catalogue line Maximum_quantity SHOULD be present</assert>
    <assert test="$EUGEN-T19-R036" flag="warning">[EUGEN-T19-R036]-Catalogue line Mimimum_quantity SHOULD NOT be negative</assert>
    <assert test="$EUGEN-T19-R033" flag="warning">[EUGEN-T19-R033]-Catalogue line Maximum_quantity SHOULD be greater or equal to the Minimum quantity: it is applied in all the section in  a Catalogue line where are included Maximum and minimum quantity (it is applied at section Max order quantity)</assert>
    <assert test="$EUGEN-T19-R032" flag="fatal">[EUGEN-T19-R032]-If Orderable Indicator is se to Yes than Orderable Unit (text) MUST not be blank</assert>
  </rule>
  <rule context="$Catalogue_provider_party">
    <assert test="$EUGEN-T19-R031" flag="fatal">[EUGEN-T19-R031]-Provider party endpoint identifier MUST be filled in </assert>
  </rule>
  <rule context="$Catalogue_receiver_party">
    <assert test="$EUGEN-T19-R030" flag="fatal">[EUGEN-T19-R030]-Receiver party endpoint identifier MUST be filled in </assert>
  </rule>
  <rule context="$Catalogue_validity_period">
    <assert test="$EUGEN-T19-R029" flag="warning">[EUGEN-T19-R029]-A validity period end date SHOULD be later or equal to a validity period start date</assert>
  </rule>
  <rule context="$Catalogue">
    <assert test="$EUGEN-T19-R028" flag="warning">[EUGEN-T19-R028]-Contract reference SHOULD be only one</assert>
    <assert test="$EUGEN-T19-R003" flag="fatal">[EUGEN-T19-R003]-The profile ID is dependent on the profile in which the transaction is being used.</assert>
    <assert test="$EUGEN-T19-R002" flag="fatal">[EUGEN-T19-R002]-CustomizationID MUST  comply with CEN/BII transactions definitions</assert>
    <assert test="$EUGEN-T19-R001" flag="fatal">[EUGEN-T19-R001]-UBL VersionID MUST define a supported syntaxbinding</assert>
  </rule>
  <rule context="$Contract">
    <assert test="$EUGEN-T19-R027" flag="fatal">[EUGEN-T19-R027]-If Contract Identifier is not specified SHOULD Contract Type text be used for Contract Reference </assert>
  </rule>
  <rule context="$Customer_Address">
    <assert test="$EUGEN-T19-R023" flag="warning">[EUGEN-T19-R023]-A Customer party address in an catalogue SHOULD contain at least Street Name, City name and Zip code and Country code.</assert>
  </rule>
  <rule context="$Customer">
    <assert test="$EUGEN-T19-R025" flag="warning">[EUGEN-T19-R025]-In cross border trade the VAT identifier for the customer should be prefixed with country code.</assert>
    <assert test="$EUGEN-T19-R024" flag="fatal">[EUGEN-T19-R024]-If buyer customer party ID is not specified, buyer party name is mandatory</assert>
  </rule>
  <rule context="$Document_Reference">
    <assert test="$EUGEN-T19-R020" flag="warning">[EUGEN-T19-R020]-Mime code Should be given for embedded binary object accordingly to codelist</assert>
  </rule>
  <rule context="$Item_Location_Price">
    <assert test="$EUGEN-T19-R034" flag="warning">[EUGEN-T19-R034]-Catalogue line Maximum_quantity SHOULD be greater than the Minimum quantity (it is applied to the section Item location.quantity.maximum_quantity) </assert>
    <assert test="$EUGEN-T19-R016" flag="warning">[EUGEN-T19-R016]-Line validity period SHOULD be within the range of the whole catalogue validity period</assert>
  </rule>
  <rule context="$Item_Price">
    <assert test="$EUGEN-T19-R013" flag="fatal">[EUGEN-T19-R013]-Prices of items MUST be positive or equal to zero NOT negative amounts</assert>
  </rule>
  <rule context="$Item">
    <assert test="$EUGEN-T19-R041" flag="warning">[EUGEN-T19-R041]-Sellers_ Item Identification. Item Identification section SHOULD be present</assert>
    <assert test="$EUGEN-T19-R019" flag="warning">[EUGEN-T19-R019]-Item Tax Scheme SHOULD be present</assert>
    <assert test="$EUGEN-T19-R018" flag="warning">[EUGEN-T19-R018]-Item Tax Category SHOULD be present</assert>
    <assert test="$EUGEN-T19-R017" flag="warning">[EUGEN-T19-R017]-Item should have a Description &#8211; Invoice is the NAME!!</assert>
    <assert test="$EUGEN-T19-R015" flag="fatal">[EUGEN-T19-R015]-Item Commodity Classification: both Classification Commodity codes and Item classification code MUST be filled</assert>
    <assert test="$EUGEN-T19-R012" flag="warning">[EUGEN-T19-R012]-If standard identifiers are provided within an item description, an Schema Identifier SHOULD be provided (e.g. GTIN)</assert>
  </rule>
  <rule context="$Party">
    <assert test="$EUGEN-T19-R005" flag="warning">[EUGEN-T19-R005]-Party.Party Tax Scheme. Company Identifier SHOULD be present</assert>
  </rule>
  <rule context="$Seller_Address">
    <assert test="$EUGEN-T19-R010" flag="warning">[EUGEN-T19-R010]-A seller party address in an catalogue SHOULD contain at least Street Name, City name and Zip code and Country code </assert>
  </rule>
  <rule context="$Supplier_Contact">
    <assert test="$EUGEN-T19-R006" flag="warning">[EUGEN-T19-R006]-A party contact telephone text SHOULD be filled in</assert>
  </rule>
  <rule context="$Supplier">
    <assert test="$EUGEN-T19-R009" flag="warning">[EUGEN-T19-R009]-In cross border trade the VAT identifier for the supplier MUST be prefixed with country code.</assert>
    <assert test="$EUGEN-T19-R007" flag="fatal">[EUGEN-T19-R007]-If seller supplier party ID is not specified, seller supplier party name is mandatory</assert>
  </rule>
</pattern>

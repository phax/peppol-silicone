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
<?xml version="1.0" encoding="UTF-8"?><!--
Refer to EC_CodeList.xls for a mapping of error codes to corresponding human readable values
--><schema xmlns="http://purl.oclc.org/dsdl/schematron">
<title>Business rules for e-PRIOR Catalogue</title>
<ns prefix="qdt" uri="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2"/>
  <ns prefix="cct" uri="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2"/>
  <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  <ns prefix="udt" uri="urn:un:unece:uncefact:data:draft:UnqualifiedDataTypesSchemaModule:2"/>
  <ns prefix="stat" uri="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0"/>
<pattern name="address check">
  <rule context="cac:PostalAddress">
    <assert flag="warning" test="not ( normalize-space(./cbc:StreetName) = '' and normalize-space(./cbc:Postbox) = '' )">error.address_check_streetname</assert>
    <assert flag="warning" test="not ( normalize-space(./cbc:CityName) = '' )">error.address_check_cityname</assert>
    <assert flag="warning" test="not ( normalize-space(./cbc:PostalZone) = '' )">error.address_check_postalzone</assert>
    <assert flag="warning" test="not ( normalize-space(./cac:Country) = '' )">error.address_check_country</assert>
  </rule>
</pattern>
<pattern name="id check">
  <rule context="*[local-name()='Catalogue']/cbc:ID">
    <let name="apos" value="&#34;'&#34;"/>
    <assert flag="error" test="not ( normalize-space(.) = '' )">error.id_check</assert>
    <assert flag="error" test="not ( string-length(normalize-space(.)) &gt; 250 )">error.id_length_check</assert>
    <assert flag="error" test="not ( translate(translate(., ' !&#34;&#34;#$%&amp;()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{{|}}~', ''), $apos, '') != '') ">error.id_invalid_char</assert>
  </rule>
</pattern>
<pattern name="referencedcontract check">
  <rule context="*[local-name()='Catalogue']">
    <assert flag="warning" test="not ( count(cac:ReferencedContract) &gt; 1 )">error.referencedcontract_check_number_warning</assert>
    <assert flag="error" test="not ( count(cac:ReferencedContract) &lt; 1 )">error.referencedcontract_check_number_error</assert>
  </rule>
</pattern>
<pattern name="referencedcontractid check">
  <rule context="cac:ReferencedContract">
    <assert flag="warning" test="not ( normalize-space(./cbc:ID) = '' )">error.referencedcontract_check_id</assert>
  </rule>
</pattern>
<pattern name="date check">
  <rule context="cac:ValidityPeriod">
    <let name="startYear" value="number(substring(./cbc:StartDate,1,4))"/>
    <let name="startMonth" value="number(substring(./cbc:StartDate,6,2))"/>
    <let name="startDay" value="number(substring(./cbc:StartDate,9,2))"/>
    <let name="endYear" value="number(substring(./cbc:EndDate,1,4))"/>
    <let name="endMonth" value="number(substring(./cbc:EndDate,6,2))"/>
    <let name="endDay" value="number(substring(./cbc:EndDate,9,2))"/>
    <assert flag="warning" test="(normalize-space(./cbc:StartDate) = '') or (normalize-space(./cbc:EndDate) = '') or ( $startYear &lt; $endYear ) or ( ( $startYear = $endYear ) and ( $startMonth &lt; $endMonth ) ) or ( ( $startYear = $endYear ) and ( $startMonth = $endMonth ) and ( $startDay &lt;= $endDay ) )">error.invalid_date_range</assert>
  </rule>
</pattern>
<pattern name="standarditemidentification check">
  <rule context="cac:StandardItemIdentification">
    <assert flag="warning" test="not ( normalize-space(./cbc:ID/@schemeID) = '' )">error.standarditemidentification_check_schemeid</assert>
  </rule>
</pattern>
<pattern name="priceamount check">
  <rule context="cbc:PriceAmount">
    <assert flag="warning" test="not(format-number(.,'##.00') &lt; 0)">error.priceamount_check</assert>
  </rule>
</pattern>
<pattern name="commodityclassification check">
  <rule context="cac:CommodityClassification">
    <assert flag="error" test="not ( ( normalize-space(./cbc:ItemClassificationCode) = '' ) or ( normalize-space(./cbc:ItemClassificationCode/@listID) = '' ) )">error.commodityclassification_check</assert>
  </rule>
</pattern>
<pattern name="linevalidityperiod check">
  <rule context="cac:LineValidityPeriod">
    <let name="lineStartYear" value="number(substring(./cbc:StartDate,1,4))"/>
    <let name="lineStartMonth" value="number(substring(./cbc:StartDate,6,2))"/>
    <let name="lineStartDay" value="number(substring(./cbc:StartDate,9,2))"/>
    <let name="catalogueStartYear" value="number(substring(../../cac:LineValidityPeriod/cbc:StartDate,1,4))"/>
    <let name="catalogueStartMonth" value="number(substring(../../cac:LineValidityPeriod/cbc:StartDate,6,2))"/>
    <let name="catalogueStartDay" value="number(substring(../../cac:LineValidityPeriod/cbc:StartDate,9,2))"/>
    <let name="lineEndYear" value="number(substring(./cbc:EndDate,1,4))"/>
    <let name="lineEndMonth" value="number(substring(./cbc:EndDate,6,2))"/>
    <let name="lineEndDay" value="number(substring(./cbc:EndDate,9,2))"/>
    <let name="catalogueEndYear" value="number(substring(../../cac:LineValidityPeriod/cbc:EndDate,1,4))"/>
    <let name="catalogueEndMonth" value="number(substring(../../cac:LineValidityPeriod/cbc:EndDate,6,2))"/>
    <let name="catalogueEndDay" value="number(substring(../../cac:LineValidityPeriod/cbc:EndDate,9,2))"/>
    <assert flag="warning" test="(normalize-space(./cbc:StartDate) = '') or (normalize-space(./cbc:EndDate) = '') or ( $catalogueStartYear &lt; $lineStartYear ) or ( ( $catalogueStartYear = $lineStartYear ) and ( $catalogueStartMonth &lt; $lineStartMonth ) ) or ( ( $catalogueStartYear = $lineStartYear ) and ( $catalogueStartMonth = $lineStartMonth ) and ( $catalogueStartDay &lt;= $lineStartDay ) )">error.linevalidityperiod_check</assert>
    <assert flag="warning" test="(normalize-space(./cbc:StartDate) = '') or (normalize-space(./cbc:EndDate) = '') or ( $lineEndYear &lt; $catalogueEndYear ) or ( ( $lineEndYear = $catalogueEndYear ) and ( $lineEndMonth &lt; $catalogueEndMonth ) ) or ( ( $lineEndYear = $catalogueEndYear ) and ( $lineEndMonth = $catalogueEndMonth ) and ( $lineEndDay &lt;= $catalogueEndDay ) )">error.linevalidityperiod_check</assert>
  </rule>
</pattern>
<pattern name="quantity negativity check">
  <rule context="//*[contains(local-name(),'Quantity')]">
    <assert flag="warning" test="not(format-number(.,'##.00') &lt; 0)">error.quantity_negativity_check</assert>
  </rule>
</pattern>
<pattern name="item check">
  <rule context="cac:Item">
    <assert flag="warning" test="not ( normalize-space(./cac:ClassifiedTaxCategory) = '' )">error.item_check_classifiedtaxcategory</assert>
    <assert flag="error" test="not ( normalize-space(./cac:SellersItemIdentification) = '' )">error.item_check_sellersitemidentification_catalogue</assert>
    <assert flag="warning" test="not ( ( normalize-space(./cbc:Description) = '' ) and ( normalize-space(./cbc:Name) = '' ) )">error.item_check_name_or_description</assert>
  </rule>
</pattern>
<pattern name="versionId check">
  <rule context="*[local-name()='Catalogue']/cbc:VersionID">
    <let name="apos" value="&#34;'&#34;"/>
    <assert flag="error" test="not ( normalize-space(.) = '' )">error.versionid_check</assert>
    <assert flag="error" test="not ( string-length(normalize-space(.)) &gt; 5 )">error.versionid_length_check</assert>
    <assert flag="error" test="not ( translate(translate(., ' !&#34;&#34;#$%&amp;()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{{|}}~', ''), $apos, '') != '') ">error.versionid_invalid_char</assert>
  </rule>
</pattern>
<pattern name="sellersItemIdentificationId check">
  <rule context="cac:SellersItemIdentification">
    <let name="apos" value="&#34;'&#34;"/>
    <assert flag="error" test="not ( normalize-space(./cbc:ID) = '' )">error.sellersitemidentificationid_check</assert>
    <assert flag="error" test="not ( string-length(normalize-space(./cbc:ID)) &gt; 250 )">error.sellersitemidentificationid_length_check</assert>
    <assert flag="error" test="not ( translate(translate(./cbc:ID, ' !&#34;&#34;#$%&amp;()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{{|}}~', ''), $apos, '') != '') ">error.sellersitemidentificationid_invalid_char</assert>
  </rule>
</pattern>
    <pattern id="Catalogue_code_list_rules">
<!--
  This implementation supports genericode code lists with no instance
  meta data.
-->
<!--
    Start of synthesis of rules from code list context associations.

    OrderResponseSimple code list rules for the e-PRIOR project at the European Commission (DIGIT) by PwC

Version 0.1
-->

<rule context="cbc:IdentificationCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_countryidentificationcode</assert>
</rule>

<rule context="@currencyID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_currencycode</assert>
</rule>

<rule context="cac:ClassifiedTaxCategory/cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' A AA AB AC AD AE B C E G H O S Z ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_taxcategoryid</assert>
</rule>

<rule context="cac:TaxScheme/cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' VAT ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_taxschemeid</assert>
</rule>

<rule context="cbc:ActionCode" flag="error">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' Add Delete Update ',concat(' ',normalize-space(.),' ') ) ) )" flag="error">error.invalid_codetable_value_actioncode</assert>
</rule>

<rule context="cbc:CommodityCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' CV GN HS ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_commoditycode</assert>
</rule>

<!--
    End of synthesis of rules from code list context associations.
-->
</pattern>
</schema>
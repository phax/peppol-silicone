<?xml version="1.0" encoding="UTF-8"?><!--
Refer to EC_CodeList.xls for a mapping of error codes to corresponding human readable values
--><
<!-- DEPRECATED as only Catalogue.sch is used! philip July 2011 -->
schema xmlns="http://purl.oclc.org/dsdl/schematron">
<title>Business rules for e-PRIOR Catalogue</title>
<ns prefix="qdt" uri="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2"/>
  <ns prefix="cct" uri="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-2"/>
  <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  <ns prefix="udt" uri="urn:un:unece:uncefact:data:draft:UnqualifiedDataTypesSchemaModule:2"/>
  <ns prefix="stat" uri="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0"/>
<pattern name="party check">
  <rule context="cac:Party">
    <assert flag="warning" test="not ( normalize-space(./cac:PostalAddress) = '' )">error.party_check_postaladdress</assert>
    <assert flag="warning" test="not ( normalize-space(./cac:PartyTaxScheme) = '' )">error.party_check_partytaxscheme</assert>
  </rule>
  <rule context="cac:ProviderParty">
    <assert flag="warning" test="not ( normalize-space(./cac:PostalAddress) = '' )">error.party_check_postaladdress</assert>
    <assert flag="warning" test="not ( normalize-space(./cac:PartyTaxScheme) = '' )">error.party_check_partytaxscheme</assert>
  </rule>
  <rule context="cac:ReceiverParty">
    <assert flag="warning" test="not ( normalize-space(./cac:PostalAddress) = '' )">error.party_check_postaladdress</assert>
    <assert flag="warning" test="not ( normalize-space(./cac:PartyTaxScheme) = '' )">error.party_check_partytaxscheme</assert>
  </rule>
</pattern>
<pattern name="partyTaxScheme check">
  <rule context="cac:PartyTaxScheme">
    <assert flag="warning" test="not ( ( normalize-space(./cbc:CompanyID) = '' ) and ( ( local-name(..)='ProviderParty' ) or ( local-name(../..)='SellerSupplierParty' ) ) )">error.party_tax_scheme_check_companyid</assert>
  </rule>
</pattern>
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
<pattern name="quantity range check">
  <rule context="cbc:MaximumQuantity">
    <let name="maximumQuantity" value="number(.)"/>
    <let name="minimumQuantity" value="number(../cbc:MinimumQuantity)"/>
    <assert flag="warning" test="((normalize-space(../cbc:MinimumQuantity) = '') or (normalize-space(.) = '') or ( $minimumQuantity &lt;= $maximumQuantity ) )">error.quantity_range_check</assert>
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

<rule context="cbc:AddressFormatCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' 1 2 3 4 5 6 7 8 9 ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_addressformatcode</assert>
</rule>

<rule context="cbc:AllowanceChargeReasonCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_allowancechargereasoncode</assert>
</rule>

<rule context="cbc:IdentificationCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_countryidentificationcode</assert>
</rule>

<rule context="cbc:TaxExemptionReasonCode" flag="warning">
  <assert test="( ( . = 'AAA Exempt' or . = 'AAB Exempt' or . = 'AAC Exempt' or . = 'AAE Reverse Charge' or . = 'AAF Exempt' or . = 'AAG Exempt' or . = 'AAH Margin Scheme' or . = 'AAI Margin Scheme' or . = 'AAJ Reverse Charge' or . = 'AAK Reverse Charge' or . = 'AAL Reverse Charge Exempt' or . = 'AAM Exempt New Means of Transport' or . = 'AAN Exempt Triangulation' or . = 'AAO Reverse Charge' ) )" flag="warning">error.invalid_codetable_value_taxexemptionreasoncode</assert>
</rule>

<rule context="cbc:TaxTypeCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' StandardRated ZeroRated ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_taxtypecode</assert>
</rule>

<rule context="@currencyID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_currencycode</assert>
</rule>

<rule context="cbc:DocumentTypeCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' 21 22 251 23 220 231 301 380 916 81 311 312 310 9 230 320 406 ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_documenttypecode</assert>
</rule>

<rule context="cac:ClassifiedTaxCategory/cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' A AA AB AC AD AE B C E G H O S Z ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_taxcategoryid</assert>
</rule>

<rule context="cac:TaxCategory/cbc:ID" flag="warning">
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

<rule context="cbc:LifeCycleStatusCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' Available DeletedAnnouncement ItemDeleted NewAnnouncement NewAvailable ItemTemporarilyUnavailable ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_lifecyclestatuscode</assert>
</rule>
<!--
    End of synthesis of rules from code list context associations.
-->
</pattern>
</schema>
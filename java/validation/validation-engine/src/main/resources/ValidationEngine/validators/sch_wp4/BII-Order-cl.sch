<?xml version="1.0" encoding="UTF-8"?>
<!--Order Validation Schematron-->
<schema xmlns:xvml="http://peppol.eu/schemas/xvml/1.0"
        xmlns:gc="http://docs.oasis-open.org/codelist/ns/genericode/1.0/"
        xmlns="http://purl.oclc.org/dsdl/schematron">
  <title>Codelist rules for order</title>
  
  <ns prefix="cac"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  
  <ns prefix="cbc"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  
  
  <pattern id="BII-order-gc" name="BII order:code list rules">
		<!-- CL-001-002	Currencies in an order MUST be coded using ISO currency code -->
		<rule context="//cbc:DocumentCurrencyCode">
			      <assert test="contains(' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ', concat(' ', normalize-space(.), ' '))"
                 flag="fatal">CL-001-002: Currencies in an order MUST be coded using ISO currency code</assert>
		    </rule>
		    <rule context="//cbc:CurrencyCode">
			      <assert test="contains(' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ', concat(' ', normalize-space(.), ' '))"
                 flag="fatal">CL-001-002: Currencies in an order MUST be coded using ISO currency code</assert>
		    </rule>

		    <!--CL-001-003 Country codes in an order MUST be coded using ISO code list 3166-1-->
		<rule context="//cac:Country/cbc:IdentificationCode ">
			      <assert test="contains(' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ', concat(' ', normalize-space(.), ' '))"
                 flag="fatal">CL-001-003: Country codes in an order MUST be coded using ISO code list 3166-1</assert>
		    </rule>
		
		    <!--CL-001-004	Order tax schemes MUST be coded using UN/ECE 5153 code list-->
		<rule context="//cac:TaxScheme/cbc:ID">
			      <assert test="contains(' AAA AAB AAC AAD AAE AAF AAG AAH AAI AAJ AAK AAL ADD BOL CAP CAR COC CST CUD CVD ENV EXC EXP FET FRE GCN GST ILL IMP IND LAC LCN LDP LOC LST MCA MCD OTH PDB PDC PRF SCN SSS STT SUP SUR SWT TAC TOT TOX TTA VAD VAT ', concat(' ', normalize-space(.), ' '))"
                 flag="warning">CL-001-004: Order tax schemes MUST be coded using UN/ECE 5153 code list</assert>
		    </rule>
		
		    <!-- CL-001-006	Order tax categories MUST be coded using UN/ECE 5305 code list -->
		<rule context="//cac:TaxCategory/cbc:ID">
			      <assert test="contains(' A AA AB AC AD AE B C E G H O S Z ', concat(' ', normalize-space(.), ' '))"
                 flag="warning">CL-001-006: Order tax categories MUST be coded using UN/ECE 5305 code list</assert>
		    </rule>

		    <!-- CL-001-010 LatitudeDirectionCode SHOULD be coded using code list ???-->
		<rule context="//cbc:LatitudeDirectionCode">
			      <assert test="contains(' North South ', concat(' ', normalize-space(.), ' '))"
                 flag="warning">CL-001-010: LatitudeDirectionCode SHOULD be coded using code list ???</assert>
		    </rule>

		    <!-- CL-001-011 LongitudeDirectionCode SHOULD be coded using code list ??? -->
		<rule context="//cbc:LongitudeDirectionCode">
			      <assert test="contains(' East West ', concat(' ', normalize-space(.), ' '))"
                 flag="warning">CL-001-010: LatitudeDirectionCode SHOULD be coded using code list ???</assert>
		    </rule>


	  </pattern>

</schema>

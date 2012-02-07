<?xml version="1.0" encoding="utf-8"?><!-- 

        	UBL syntax binding to the BiiCoreTrdm010 defined in the CEN BII Profile BII23 
        	Author: Oriol Bausà - WG3

     --><schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" queryBinding="xslt2">
  <title>CEN BII BII23 BiiCoreTrdm010 bound to UBL</title>
  <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
  <phase id="BiiCoreTrdm010_phase">
    <active pattern="ubl-BiiCoreTrdm010"/>
  </phase>
  <phase id="BII23_phase">
    <active pattern="ubl-BII23"/>
  </phase>
  <phase id="codelist_phase">
    <active pattern="BiiCoreTrdm010codes_only"/>
  </phase>
  <!-- Abstract CEN BII patterns -->
  <!-- ========================= -->
  <?DSDL_INCLUDE_START abstract/BiiCoreTrdm010.sch?><pattern abstract="true" id="BiiCoreTrdm010">
  <rule context="$Invoice_Period">
    <assert test="$BII-T10-001" flag="warning">[BII-T10-001]-An invoice period end date SHOULD be later or equal to an invoice period start date</assert>
  </rule>
  <rule context="$Supplier_Address">
    <assert test="$BII-T10-002" flag="warning">[BII-T10-002]-A supplier address in an invoice SHOULD contain at least City and zip code or have one or more address lines.</assert>
  </rule>
  <rule context="$Supplier">
    <assert test="$BII-T10-003" flag="fatal">[BII-T10-003]-In cross border trade the VAT identifier for the supplier MUST be prefixed with country code.</assert>
  </rule>
  <rule context="$Customer_Address">
    <assert test="$BII-T10-004" flag="warning">[BII-T10-004]-A customer address in an invoice SHOULD contain at least city and zip code or have one or more address lines.</assert>
  </rule>
  <rule context="$Customer">
    <assert test="$BII-T10-005" flag="fatal">[BII-T10-005]-In cross border trade the VAT identifier for the customer should be prefixed with country code.</assert>
  </rule>
  <rule context="$Payment_Means">
    <assert test="$BII-T10-006" flag="warning">[BII-T10-006]-Payment means due date in an invoice SHOULD be later or equal than issue date.</assert>
    <assert test="$BII-T10-007" flag="warning">[BII-T10-007]-If payment means is funds transfer, invoice MUST have a financial account </assert>
    <assert test="$BII-T10-008" flag="warning">[BII-T10-008]-If bank account is IBAN the BIC code SHOULD also be provided.</assert>
  </rule>
  <rule context="$Tax_Total">
    <assert test="$BII-T10-009" flag="fatal">[BII-T10-009]-An invoice MUST have a tax total refering to a single tax schema </assert>
    <assert test="$BII-T10-010" flag="fatal">[BII-T10-010]-Each tax total MUST equal the sum of the subcategory amounts.</assert>
  </rule>
  <rule context="$Total_Amounts">
    <assert test="$BII-T10-011" flag="fatal">[BII-T10-011]-Invoice total line extension amount MUST equal the sum of the line totals</assert>
    <assert test="$BII-T10-012" flag="fatal">[BII-T10-012]-An invoice tax exclusive amount MUST equal the sum of lines plus allowances and charges on header level.</assert>
    <assert test="$BII-T10-013" flag="fatal">[BII-T10-013]-An invoice tax inclusive amount MUST equal the tax exclusive amount plus all tax total amounts and the rounding amount.</assert>
    <assert test="$BII-T10-014" flag="fatal">[BII-T10-014]-Tax inclusive amount in an invoice MUST NOT be negative</assert>
    <assert test="$BII-T10-015" flag="fatal">[BII-T10-015]-If there is a total allowance it MUST be equal to the sum of allowances at document level</assert>
    <assert test="$BII-T10-016" flag="fatal">[BII-T10-016]-If there is a total charges it MUST be equal to the sum of document level charges.</assert>
    <assert test="$BII-T10-017" flag="fatal">[BII-T10-017]-In an invoice, amount due is the tax inclusive amount minus what has been prepaid.</assert>
  </rule>
  <rule context="$Invoice_Line">
    <assert test="$BII-T10-018" flag="fatal">[BII-T10-018]-Invoice line amount MUST be equal to the price amount multiplied by the quantity</assert>
  </rule>
  <rule context="$Item">
    <assert test="$BII-T10-019" flag="warning">[BII-T10-019]-Product names SHOULD NOT exceed 50 characters long</assert>
    <assert test="$BII-T10-020" flag="warning">[BII-T10-020]-If standard identifiers are provided within an item description, an Schema Identifier SHOULD be provided (e.g. GTIN)</assert>
    <assert test="$BII-T10-021" flag="warning">[BII-T10-021]-Classification codes within an item description SHOULD have a List Identifier attribute (e.g. CPV or UNSPSC)</assert>
  </rule>
  <rule context="$Item_Price">
    <assert test="$BII-T10-022" flag="fatal">[BII-T10-022]-Prices of items MUST be positive or zero</assert>
  </rule>
  <rule context="$Allowance_Percentage">
    <assert test="$BII-T10-023" flag="fatal">[BII-T10-023]-An allowance percentage MUST NOT be negative.</assert>
  </rule>
  <rule context="$Allowance">
    <assert test="$BII-T10-024" flag="warning">[BII-T10-024]-In allowances, both or none of percentage and base amount SHOULD be provided</assert>
  </rule>
</pattern><?DSDL_INCLUDE_END abstract/BiiCoreTrdm010.sch?>
  <?DSDL_INCLUDE_START abstract/BII23.sch?><pattern abstract="true" id="BII23">
  <rule context="$Invoice_Profile">
    <assert test="$BII-P23-001" flag="fatal">[BII-P23-001]-The profile ID is dependent on the profile in which the transaction is being used.</assert>
  </rule>
</pattern><?DSDL_INCLUDE_END abstract/BII23.sch?>
  <!-- Data Binding parameters -->
  <!-- ======================= -->
  <?DSDL_INCLUDE_START ubl/BiiCoreTrdm010_ubl.sch?><pattern is-a="BiiCoreTrdm010" id="ubl-BiiCoreTrdm010">
  <param name="BII-T10-001" value="(child::cbc:StartDate and child::cbc:EndDate) and not(number(translate(child::cbc:StartDate,'-','')) &gt; number(translate(child::cbc:EndDate,'-',''))) or number(translate(child::cbc:EndDate,'-','')) = number(translate(child::cbc:StartDate,'-',''))"/>
  <param name="BII-T10-002" value="(child::cbc:CityName and child::cbc:PostalZone) or count(child::cac:AddressLine)&gt;0"/>
  <param name="BII-T10-003" value="(((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID/@schemeID = 'VAT') and starts-with(child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or ((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) "/>
  <param name="BII-T10-004" value="(child::cbc:CityName and child::cbc:PostalZone) or count(child::cac:AddressLine)&gt;0"/>
  <param name="BII-T10-005" value="(((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID/@schemeID = 'VAT') and starts-with(child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or ((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) "/>
  <param name="BII-T10-006" value="(child::cbc:PaymentDueDate and /ubl:Invoice/cbc:IssueDate) and not(number(translate(child::cbc:PaymentDueDate,'-','')) &lt; number(translate(/ubl:Invoice/cbc:IssueDate,'-',''))) or number(translate(child::cbc:PaymentDueDate,'-','')) = number(translate(/ubl:Invoice/cbc:IssueDate,'-',''))"/>
  <param name="BII-T10-007" value="(child::cbc:PaymentMeansCode = '31') and //cac:PayeeFinancialAccount/cbc:ID or (child::cbc:PaymentMeansCode != '31')"/>
  <param name="BII-T10-008" value="(child::cac:PayeeFinancialAccount/cbc:ID/@schemeID and (child::cac:PayeeFinancialAccount/cbc:ID/@schemeID = 'IBAN') and child::cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID) or (child::cac:PayeeFinancialAccount/cbc:ID/@schemeID != 'IBAN') or (not(child::cac:PayeeFinancialAccount/cbc:ID/@schemeID))"/>
  <param name="BII-T10-009" value="count(child::cac:TaxSubtotal)&gt;1 and (child::cac:TaxSubtotal[1]/cac:TaxCategory/cac:TaxScheme/cbc:ID) =(child::cac:TaxSubtotal[2]/cac:TaxCategory/cac:TaxScheme/cbc:ID) or count(child::cac:TaxSubtotal)&lt;=1"/>
  <param name="BII-T10-010" value="number(child::cbc:TaxAmount) = number(sum(child::cac:TaxSubtotal/cbc:TaxAmount))"/>
  <param name="BII-T10-011" value="number(child::cbc:LineExtensionAmount) = number(sum(//cac:InvoiceLine/cbc:LineExtensionAmount))"/>
  <param name="BII-T10-012" value="((child::cbc:ChargeTotalAmount) and (child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = (number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount) - number(cbc:AllowanceTotalAmount)))) or (not(child::cbc:ChargeTotalAmount) and (child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount) - number(cbc:AllowanceTotalAmount))) or ((child::cbc:ChargeTotalAmount) and not(child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount))) or (not(child::cbc:ChargeTotalAmount) and not(child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount)))"/>
  <param name="BII-T10-013" value="((child::cbc:PayableRoundingAmount) and (number(child::cbc:TaxInclusiveAmount) = number(child::cbc:TaxExclusiveAmount) + number(sum(/ubl:Invoice/cac:TaxTotal/cbc:TaxAmount)) + number(child::cbc:PayableRoundingAmount))) or  (number(child::cbc:TaxInclusiveAmount) = number(child::cbc:TaxExclusiveAmount) + number(sum(/ubl:Invoice/cac:TaxTotal/cbc:TaxAmount)))"/>
  <param name="BII-T10-014" value="number(child::cbc:TaxInclusiveAmount) &gt;= 0"/>
  <param name="BII-T10-015" value="(child::cbc:AllowanceTotalAmount) and child::cbc:AllowanceTotalAmount = sum(/ubl:Invoice/cac:AllowanceCharge[child::cbc:ChargeIndicator=&#34;false&#34;]/cbc:Amount) or not(child::cbc:AllowanceTotalAmount)"/>
  <param name="BII-T10-016" value="(child::cbc:ChargeTotalAmount) and child::cbc:ChargeTotalAmount = sum(/ubl:Invoice/cac:AllowanceCharge[child::cbc:ChargeIndicator=&#34;true&#34;]/cbc:Amount) or not(child::cbc:ChargeTotalAmount)"/>
  <param name="BII-T10-017" value="(child::cbc:PrepaidAmount) and (number(cbc:PayableAmount) = number(cbc:TaxInclusiveAmount - cbc:PrepaidAmount)) or child::cbc:PayableAmount = child::cbc:TaxInclusiveAmount"/>
  <param name="BII-T10-018" value="number(child::cbc:LineExtensionAmount) = number(child::cac:Price/cbc:PriceAmount) * number(child::cbc:InvoicedQuantity)"/>
  <param name="BII-T10-019" value="string-length(string(cbc:Name)) &lt;= 50"/>
  <param name="BII-T10-020" value="boolean(child::cac:StandardItemIdentification/cbc:ID/@schemeID)"/>
  <param name="BII-T10-021" value="boolean(child::cac:CommodityClassification/cbc:ItemClassificationCode/@listID)"/>
  <param name="BII-T10-022" value="number(.) &gt;=0"/>
  <param name="BII-T10-023" value="number(.) &gt;=0"/>
  <param name="BII-T10-024" value="(child::cbc:MultiplierFactorNumeric and child::cbc:BaseAmount) or (not(child::cbc:MultiplierFactorNumeric) and not(child::cbc:BaseAmount))"/>
  <param name="Invoice_Period" value="//cac:InvoicePeriod"/>
  <param name="Supplier_Address" value="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
  <param name="Supplier" value="//cac:AccountingSupplierParty"/>
  <param name="Customer_Address" value="//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
  <param name="Customer" value="//cac:AccountingCustomerParty"/>
  <param name="Payment_Means" value="//cac:PaymentMeans"/>
  <param name="Tax_Total" value="/ubl:Invoice/cac:TaxTotal"/>
  <param name="Invoice_Line" value="//cac:InvoiceLine"/>
  <param name="Invoice" value="/ubl:Invoice"/>
  <param name="Item_Price" value="//cac:InvoiceLine/cac:Price/cbc:PriceAmount"/>
  <param name="Item" value="//cac:Item"/>
  <param name="Allowance_Percentage" value="//cac:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:MultiplierFactorNumeric"/>
  <param name="Allowance" value="//cac:AllowanceCharge[cbc:ChargeIndicator='false']"/>
  <param name="Total_Amounts" value="//cac:LegalMonetaryTotal"/>
</pattern><?DSDL_INCLUDE_END ubl/BiiCoreTrdm010_ubl.sch?>
  <?DSDL_INCLUDE_START ubl/BII23_ubl.sch?><pattern is-a="BII23" id="ubl-BII23">
  <param name="BII-P23-001" value=". = 'urn:www.cenbii.eu:profiles:profile23:ver1.0'"/>
  <param name="Invoice_Profile" value="//cbc:ProfileID"/>
</pattern><?DSDL_INCLUDE_END ubl/BII23_ubl.sch?>
  <!-- Code Lists Binding rules -->
  <!-- ======================== -->
  <?DSDL_INCLUDE_START codelist/BiiCoreTrdm010codes_only.sch?><pattern id="BiiCoreTrdm010codes_only">
<!--
  This implementation supports genericode code lists with no instance
  meta data.
-->
<!--
    Start of synthesis of rules from code list context associations.
Version 0.3
-->

<rule context="cbc:InvoiceTypeCode" flag="fatal">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' 380 393 ',concat(' ',normalize-space(.),' ') ) ) )" flag="fatal">[CL-010-001]-An Invoice MUST be tipified with the InvoiceTypeCode code list</assert>
</rule>

<rule context="cbc:DocumentCurrencyCode" flag="fatal">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ',concat(' ',normalize-space(.),' ') ) ) )" flag="fatal">[CL-010-002]-Currencies in an invoice MUST be coded using ISO currency code</assert>
</rule>

<rule context="@currencyID" flag="fatal">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYR BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EEK EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GWP GYD HKD HNL HRK HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LTL LVL LYD MAD MDL MGA MKD MMK MNT MOP MRO MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SKK SLL SOS SRD STD SVC SYP SZL THB TJS TMM TND TOP TRY TTD TWD TZS UAH UGX USD USN USS UYI UYU UZS VEF VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XFU XOF XPD XPF XTS XXX YER ZAR ZMK ZWR ZWD ',concat(' ',normalize-space(.),' ') ) ) )" flag="fatal">[CL-010-003]-Currencies in an invoice MUST be coded using ISO currency code</assert>
</rule>

<rule context="cac:Country//cbc:IdentificationCode" flag="fatal">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',concat(' ',normalize-space(.),' ') ) ) )" flag="fatal">[CL-010-004]-Country codes in an invoice MUST be coded using ISO code list 3166-1</assert>
</rule>

<rule context="cac:TaxScheme//cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AAA AAB AAC AAD AAE AAF AAG AAH AAI AAJ AAK AAL ADD BOL CAP CAR COC CST CUD CVD ENV EXC EXP FET FRE GCN GST ILL IMP IND LAC LCN LDP LOC LST MCA MCD OTH PDB PDC PRF SCN SSS STT SUP SUR SWT TAC TOT TOX TTA VAD VAT ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">[CL-010-005]-Invoice tax schemes MUST be coded using UN/ECE 5153 code list</assert>
</rule>

<rule context="cac:PaymentMeans//cbc:PaymentMeansCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 60 61 62 63 64 65 66 67 70 74 75 76 77 78 91 92 93 94 95 96 97 ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">[CL-010-006]-Payment means in an invoice MUST be coded using CEFACT code list 4461</assert>
</rule>

<rule context="cac:TaxCategory//cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' A AA AB AC AD AE B C E G H O S Z ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">[CL-010-007]-Invoice tax categories MUST be coded using UN/ECE 5305 code list</assert>
</rule>
<!--
    End of synthesis of rules from code list context associations.
-->
</pattern><?DSDL_INCLUDE_END codelist/BiiCoreTrdm010codes_only.sch?>
</schema>
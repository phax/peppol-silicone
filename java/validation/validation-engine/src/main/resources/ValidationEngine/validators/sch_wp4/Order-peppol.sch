<?xml version="1.0" encoding="UTF-8"?>
<!--XVML generated Schematron-->
<schema xmlns:xvml="http://peppol.eu/schemas/xvml/1.0"
        xmlns:gc="http://docs.oasis-open.org/codelist/ns/genericode/1.0/"
        xmlns="http://purl.oclc.org/dsdl/schematron">
  <title>PEPPOL Abstract Rules</title>

	  <ns prefix="cac"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>

	  <ns prefix="cbc"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>


  <pattern id="Peppol_T01" name="T01 - Order transaction">

		<!-- PEP-T01-001	A seller party postal address in an order SHOULD contain at least
									Streetname, building number, city name and zip code. -->
		<rule context="//cac:SellerSupplierParty/cac:Party">
			      <let value="cac:PostalAddress/cbc:CityName" name="city"/>
         <let value="cac:PostalAddress/cbc:PostalZone" name="zip"/>
         <let value="cac:PostalAddress/cbc:StreetName" name="street"/>
         <let value="cac:PostalAddress/cbc:BuildingNumber" name="nr"/>
         <assert test="$city and ($city != '') and $zip and ($zip != '') and $street and ($street != '') and $nr and ($nr != '')"
                 flag="warning">PEP-T01-001: A seller party postal address in an order SHOULD contain at least Streetname, building number, city name and zip code</assert>
		    </rule>

		    <!-- PEP-T01-002	A customer party postal address in an order SHOULD contain at least
									Streetname, building number, city name and zip code. -->
		<rule context="//cac:BuyerCustomerParty/cac:Party">
			      <let value="cac:PostalAddress/cbc:CityName" name="city"/>
         <let value="cac:PostalAddress/cbc:PostalZone" name="zip"/>
         <let value="cac:PostalAddress/cbc:StreetName" name="street"/>
         <let value="cac:PostalAddress/cbc:BuildingNumber" name="nr"/>
         <assert test="$city and ($city != '') and $zip and ($zip != '') and $street and ($street != '') and $nr and ($nr != '')"
                 flag="warning">PEP-T01-002: A customer party postal address in an order SHOULD contain at least, Streetname, building number, city name and zip code</assert>
		    </rule>

		    <!-- PEP-T01-003	  Supplier party name SHOULD be provided. -->
		<rule context="//cac:SellerSupplierParty/cac:Party">
			      <let value="cac:PartyName/cbc:Name" name="name"/>
         <assert test="$name and ($name != '')" flag="warning">PEP-T01-003: Supplier party name SHOULD be provided</assert>
		    </rule>

		    <!-- PEP-T01-004	Customer party name SHOULD be provided. -->
		<rule context="//cac:BuyerCustomerParty/cac:Party">
			      <let value="cac:PartyName/cbc:Name" name="name"/>
         <assert test="$name and ($name != '')" flag="warning">PEP-T01-004: Customer party name SHOULD be provided</assert>
		    </rule>

		    <!-- PEP-T01-005	If an order states delivery address it SHOULD contain at least
									Streetname, building number, city and zip code. -->
		<rule context="//cac:Delivery/cac:DeliveryLocation">
			      <let value="cac:Address/cbc:CityName" name="city"/>
         <let value="cac:Address/cbc:PostalZone" name="zip"/>
         <let value="cac:Address/cbc:StreetName" name="street"/>
         <let value="cac:Address/cbc:BuildingNumber" name="nr"/>
         <assert test="$city and ($city != '') and $zip and ($zip != '') and $street and ($street != '') and $nr and ($nr != '')"
                 flag="warning">PEP-T01-005: If an order states delivery address it SHOULD contain at least, Streetname, building number, city and zip code</assert>
		    </rule>

		    <!-- PEP-T01-006	If the order tax total for VAT is not zero then an Allowances Charges amount
									on document level MUST have Tax category for VAT. -->
<!-- [15:01:06] Georg Birgisson: Order. Details/Order. Tax Total/Tax Total. Tax Amount. Amount = zero WHEREOrder. Tax Total/Tax Total. Tax Subtotal/Tax Subtotal. Tax Category/Tax Category. Tax Scheme/Tax Scheme. Identifier = VAT-->
		<rule context="/*/cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID='VAT']/cbc:TaxAmount">
			      <assert test="(sum(.) = 0) or (sum(/*/cac:AllowanceCharge[cac:TaxCategory/cac:TaxScheme/cbc:ID='VAT']/cbc:Amount) != 0)"
                 flag="fatal">PEP-T01-006: If the order tax total for VAT is not zero then an Allowances Charges amount on document level MUST have Tax category for VAT</assert>
		    </rule>

		    <!-- PEP-T01-007	In an order, payable amount MUST equal total line extension + total charge
									- total allowance + tax total + rounding amount - Prepaid Amount. -->
		<rule context="//cac:AnticipatedMonetaryTotal">
			      <assert test="sum(cbc:PayableAmount) = sum(cbc:LineExtensionAmount) + sum(cbc:ChargeTotalAmount) - sum(cbc:AllowanceTotalAmount) + sum(/*/cac:TaxTotal/cbc:TaxAmount) - sum(cbc:PrepaidAmount) + sum(cbc:PayableRoundingAmount)"
                 flag="fatal">PEP-T01-007: In an order, payable amount MUST equal total line extension + total charge - total allowance + tax total + rounding amount - Prepaid Amount</assert>
		    </rule>

   </pattern>


</schema>

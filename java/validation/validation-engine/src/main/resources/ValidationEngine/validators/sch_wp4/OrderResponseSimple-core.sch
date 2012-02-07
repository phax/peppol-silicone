<?xml version="1.0" encoding="UTF-8"?>
<!--Order Validation Schematron-->
<schema xmlns:xvml="http://peppol.eu/schemas/xvml/1.0"
        xmlns:gc="http://docs.oasis-open.org/codelist/ns/genericode/1.0/"
        xmlns="http://purl.oclc.org/dsdl/schematron">
  <title>Business rules for order</title>
  
  <ns prefix="cac"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  
  <ns prefix="cbc"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  
  
  <pattern id="BiiCoreTrdm002-003" name="Order Response Simple - RejectOrder/AcceptOrder">
	
		<!-- BII-T02-001	Language SHOULD be defined for Note field -->
		<rule context="/*/cbc:Note">
			      <assert test="@languageID" flag="warning">BII-T02-001: Language SHOULD be defined for Order Note field</assert>
		    </rule>
	
		    <!-- BII-T02-002	Only one note field SHOULD be specified-->
		<rule context="/">
			      <assert test="count(/*/cbc:Note) &lt; 2" flag="warning">BII-T02-002: Only one note field SHOULD be specified</assert>
		    </rule>
		
		    <!-- BII-T01-007	If buyer customer party ID is not specified, buyer party name is mandatory -->
		<rule context="//cac:BuyerCustomerParty/cac:Party">
			      <let value="cac:PartyIdentification/cbc:ID" name="id"/>
         <let value="cac:PartyName/cbc:Name" name="name"/>
         <assert test="(($id) and ($id != '' )) or (($name) and ($name != '' ))" flag="fatal">BII-T02-007: If buyer customer party ID is not specified, buyer party name is mandatory</assert>
		    </rule>
		
		    <!-- BII-T01-008	If seller supplier party ID is not specified, supplier party name is mandatory -->
		<rule context="//cac:SellerSupplierParty/cac:Party">
			      <let value="cac:PartyIdentification/cbc:ID" name="id"/>
         <let value="cac:PartyName/cbc:Name" name="name"/>
         <assert test="(($id) and ($id != '' )) or (($name) and ($name != '' ))" flag="fatal">BII-T02-008: If seller supplier party ID is not specified, supplier party name is mandatory</assert>
		    </rule>
		
	  </pattern>

</schema>

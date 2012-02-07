<?xml version="1.0" encoding="UTF-8"?>
<!--XVML generated Schematron-->
<schema xmlns:xvml="http://peppol.eu/schemas/xvml/1.0"
        xmlns:gc="http://docs.oasis-open.org/codelist/ns/genericode/1.0/"
        xmlns="http://purl.oclc.org/dsdl/schematron">
  <title>PEPPOL Abstract Rules</title>
 <ns prefix="cbc"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>

	  <ns prefix="cac"
       uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>

	 

	    <!-- PRE-P01-004	CustomizationID MUST comply with CEN/BII transactions definitions -->
	  <!--pattern name="ID check"-->
		<rule context="cbc:ID">
			      <let value="translate('urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0 urn:www.cenbii.eu:transaction:biifulltrdm001:ver1.0', ':.', '__') "
              name="transactionList"/>
         <assert flag="warning" test="contains($transactionList, translate(., ':.', '__'))" >PRE-P01-004: CallforTender ID MUST  comply with CEN/BII transactions definitions</assert>
		    </rule>
		<!--/pattern -->


 <!-- PRE-P01-005	ContractFolderID must be always indicated as it is the reference code of the procurement procedure
 -->
<pattern  name="contractFolderId check">
<rule context="cbc:ContractFolderID">

<assert flag="error" test="not ( normalize-space(.) = '' )">ContractFolderID: It must be always indicated as it is the reference code of the procurement procedure
</assert>

	</rule>
</pattern>

<!-- PRE-P01-006	Issue date field SHOULD contain valid dates according the format defined (e.g. 28/02/2010 and NOT 30/02/2010 or 99/20/2010)

 -->

	<pattern name="IssueDate check">
 		<rule context="cbc:IssueDate">
 			<let value="normalize-space(.)" name="issue_date"/>
 		  <let name="startYear" value="number(substring(normalize-space(.),1,4))"/>
  		<let name="startMonth" value="number(substring(normalize-space(.),6,2))"/>
  		<let name="startDay" value="number(substring(normalize-space(.),9,2))"/>
  		<assert flag="error" test="not($issue_date='')">error.empty_date_range1</assert>
		  <assert flag="warning" test="not (($startYear &lt; 2010) or ($startMonth &lt; 1) or ($startMonth &gt; 12) or ($startDay  &lt; 1) or ($startDay &gt; 31))">error.invalid_date_range1</assert>
 
 	 </rule>
  </pattern>
  
  
  
  <pattern id="Peppol_PRE_T01" name="Customer Party">

		<!-- PRE-T01-001
Party Name should be specified at document level. -->

		<rule context="//cac:CustomerParty/cac:Party">
			   <let value="cac:PartyName/cbc:Name" name="partyName"/>
			   <let value="cac:PostalAddress/cbc:CityName" name="cityName"/>
         <let value="cac:PostalAddress/cbc:PostalZone" name="zip"/>
         <let value="cac:PostalAddress/cbc:StreetName" name="street"/>
         <let value="cac:PostalAddress/cbc:BuildingNumber" name="nr"/>
      	 <let value="cac:PostalAddress/cac:Country/cbc:IdentificationCode" name="countryCode"/>
      	 <let value="cac:Contact/cbc:Telephone" name="telNumber"/>
          
             
         <assert test="$partyName and ($partyName != '')"
                 flag="warning">PRE-T01-001: Party Name should be specified at document level.</assert>
 				<assert test="$street and ($street != '')"
                 flag="warning">PRE-T01-002: Street Name  should always be indicated.</assert>
		     
 			
 			<assert test="$nr and ($nr != '')"
                 flag="warning">PRE-T01-003: Building Number should always be indicated.</assert>
		                    
            <assert test="$cityName and ($cityName != '')"
                 flag="warning">PRE-T01-004: City Name should always be indicated.</assert>
                 
		     <assert test="$zip and ($zip != '')"
                 flag="warning">PRE-T01-005: Postal Zone should always be indicated.</assert>
		     
		     <assert test="$countryCode and ($countryCode != '')"
                 flag="warning">PRE-T01-006: Country Identification Code should always be indicated.</assert>
		     <assert test="$telNumber and ($telNumber != '')"
                 flag="warning">PRE-T01-008: A party contact telephone text SHOULD be filled in.</assert>
		   
		  
		    </rule>
		    
		  <!---PRE-T01-006: Country Identification Code should always be indicated and be one of the listed ones-->  
		    
		    <rule context="cbc:IdentificationCode" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BL BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">error.invalid_codetable_value_countryidentificationcode</assert>
</rule>
	</pattern>

<!--PRE-T01-007
  If VAT number identifier will be defined as alternative party identifier all over EU, this information MUST be specified for Customer  Party-->


<rule context="//cac:CustomerParty//cac:Party//cac:PartyTaxScheme//cac:TaxScheme/cbc:ID" flag="warning">
  <assert test="( ( not(contains(normalize-space(.),' ')) and contains( ' VAT ',concat(' ',normalize-space(.),' ') ) ) )" flag="warning">PRE-T01-007
  If VAT number identifier will be defined as alternative party identifier all over EU, this information MUST be specified for Customer  Party
</assert>
</rule>

<pattern name="Procurement Project">
	<rule context="//cac:ProcurementProject">
		 <let value="cbc:ID" name="projectId"/>
		  <assert test="$projectId and ($projectId != '')"
                 flag="warning">PRE-T01-009:It should be present expecially if it is used to define lots.</assert>
		</rule>
		
	<rule context="//cac:ProcurementProject/cac:TenderLine">
		<let value="cbc:Quantity" name="quantity"/>
		<let value="cbc:MaximumTaxExclusiveAmount" name="maxTaxExcl"/>
		<let value="cbc:MaximumTaxInclusiveAmount" name="maxTaxExclInc"/>
		
		
		<assert  test = "$quantity &gt; 0" flag="warning">PRE-T01-010	Quantities cannot have a negative value</assert>
		<assert  test = "$maxTaxExcl &gt; 0  or $maxTaxExcl=0" flag="warning">PRE-T01-011	MaximumTaxExclusiveAmount cannot have a negative value</assert>
		<assert  test = "$maxTaxExclInc &gt; 0 or $maxTaxExclInc=0" flag="warning">PRE-T01-012	MaximumTaxInclusiveAmount cannot have a negative value</assert>

	

	</rule>
		
		
		<rule context="//cac:ProcurementProject//cac:Item">
		<let value="."  name="item"/>
		<let value="cbc:Description" name="description"/>
		<let value="cbc:PackQuantity" name="packQuantity"/>
		<let value="cbc:PackSizeNumeric" name="sizeNum"/>
		<let value="cbc:Name" name="itemName"/>
		
		<let value="cac:CommodityClassification" name="classif"/>
		<let value="cac:CommodityClassification/cbc:CommodityCode" name="comodity"/>
		<let value="cac:CommodityClassification/cbc:ItemClassificationCode" name="classCode"/>
		<let value="cac:AdditionalItemProperty"  name="additionalProperty"/>
     
		
		<assert  test = "$item and $item!=''"  flag="error">PRE-T01-013	At least one Item should be present</assert>
		<assert  test = "$description and $description!=''"  flag="warning">PRE-T01-014	Item should have a description</assert>
	<assert  test = "not(( not  ($description) or $description='') and (not($itemName )or $itemName=''))"  flag="warning">PRE-T01-015	If Item Description is not present Item name SHOULD</assert>

<assert  test = "$additionalProperty and $additionalProperty!=''"  flag="error">PRE-T01-017	There should be at least one property</assert>



</rule>

<rule context="//cac:ProcurementProject//cac:Item//cbc:ItemClassificationCode">
			      <let value="string(' CPVZZZ ')" name="itemClassificationList"/>
         <let value="concat(@listID, @listAgencyID)" name="itemClassification"/>
         <assert test="not(@listID) or (contains($itemClassificationList, $itemClassification))"
                 flag="fatal">PRE-T01-016 There MUST be a commody Item classification code represented by one of the codes contained in CPV
</assert>



		    </rule>


	<rule context="//cac:ProcurementProject//cac:TenderLine//cac:Item//cac:AdditionalItemProperty">
	
		<let value="cbc:ID" name="propertyId"/>
		<let value="cbc:Name" name="propName"/>
		<let value="cbc:TestMethod" name="testMethod"/>
		<let value="cbc:Value" name="propValue"/>
		<let value="cbc:UsabilityPeriod" name="usability"/>
		<let value="cbc:ItemPropertyGroup" name="propGroup"/>
		<let value="cac:ItemPropertyRange"  name="propertyRange"/>
			<let value="cbc:ListValue"  name="listValue"/>
			<let value="cac:ItemPropertyGroup"  name="propertyGroup"/>
			
		
	
	
	<assert  test = "$propertyId and $propertyId!=''"  flag="error">PRE-T01-018	There should be at least one property with ID</assert>
	<assert  test = "$propName and $propName!=''"  flag="error">PRE-T01-019	There should be at least one property with name</assert>


<assert  test = "not(( not  ($propertyRange) or $propertyRange='') and (not($listValue )or $listValue='')  )"  flag="warning">	PRE-T01-021	If BBIE Item Property Range information are not filled in the Item Property.List Value.text should be</assert>
		
	<assert  test = "not(( not  ($listValue) or $listValue='') and (not($propertyRange )or $propertyRange='') and ( not  ($propValue) or $propValue='') )"  flag="warning">		PRE-T01-022	If  Item Property.List Value.text information is not filled in the BBIE Item Property Range should be</assert>	
		
		</rule>
		
</pattern>



</schema>
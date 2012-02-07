<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" queryBinding="xslt2"
xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns="http://purl.oclc.org/dsdl/schematron"
xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2">
	<ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  	<ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  	<ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
	<pattern name="Check Content">
		<rule context="cbc:ProfileID">
			<assert flag="error" test="current()= 'PreAward'">Document not belong to PreAward profile.</assert>
		</rule>
	</pattern>
</schema>
<?xml version="1.0" encoding="UTF-8"?>
<!-- 

        	UBL syntax binding to the BiiCoreTrdm010 defined in the CEN BII Profile BII07 
        	Author: Oriol Bausà - WG3

     -->
<schema xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" queryBinding="xslt2" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2">
  <title>CEN BII BII07 BiiCoreTrdm010 bound to UBL</title>
  <ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
  <ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
  <ns prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
  <phase id="BiiCoreTrdm010_phase">
    <active pattern="ubl-BiiCoreTrdm010"/>
  </phase>
  <phase id="BII07_phase">
    <active pattern="ubl-BII07"/>
  </phase>
  <phase id="codelist_phase">
    <active pattern="BiiCoreTrdm010codes_only"/>
  </phase>
  <!-- Abstract CEN BII patterns -->
  <!-- ========================= -->
  <include href="abstract/BiiCoreTrdm010.sch"/>
  <include href="abstract/BII07.sch"/>
  <!-- Data Binding parameters -->
  <!-- ======================= -->
  <include href="ubl/BiiCoreTrdm010_ubl.sch"/>
  <include href="ubl/BII07_ubl.sch"/>
  <!-- Code Lists Binding rules -->
  <!-- ======================== -->
  <include href="codelist/BiiCoreTrdm010codes_only.sch"/>
</schema>

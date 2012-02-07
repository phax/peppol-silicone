<!-- Schematron binding rules generated automatically. -->
<!-- Data binding to UBL syntax for ISL-BII04 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="ISL-BII04" id="ubl-ISL-BII04">
  <param name="BII-IS10-001" value="((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IS') or not(exists(cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))) and exists(cac:Party/cac:PartyLegalEntity/cbc:ID) or (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode != 'IS')"/>
  <param name="BII-IS10-002" value="((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'IS') or not(exists(cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))) and exists(cac:Party/cac:PartyLegalEntity/cbc:ID) or (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode != 'IS')"/>
  <param name="Supplier_Party" value="cac:AccountingSupplierParty"/>
  <param name="Customer_Party" value="cac:AccountingCustomerParty"/>
</pattern>

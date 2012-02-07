<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for ISL-BII04 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="ISL-BII04">
  <rule context="$Supplier_Party">
    <assert test="$BII-IS10-001" flag="fatal">[BII-IS10-001]-If the Supplier party country code is either IS or blank then the party Legal ID must be present.</assert>
  </rule>
  <rule context="$Customer_Party">
    <assert test="$BII-IS10-002" flag="fatal">[BII-IS10-002]-If the Customer party country code is either IS or blank then the party Legal ID must be present.</assert>
  </rule>
</pattern>

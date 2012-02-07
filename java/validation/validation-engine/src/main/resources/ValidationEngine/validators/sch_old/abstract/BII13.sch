<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for BII13 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="BII13">
  <rule context="$Invoice_Profile">
    <assert test="$BII-P13-001" flag="fatal">[BII-P13-001]-The profile ID is dependent on the profile in which the transaction is being used.</assert>
  </rule>
  <rule context="$Invoice">
    <assert test="$BII-P13-002" flag="fatal">[BII-P13-002]-An invoice in Profile 13 MUST have an order reference identifier.</assert>
  </rule>
</pattern>

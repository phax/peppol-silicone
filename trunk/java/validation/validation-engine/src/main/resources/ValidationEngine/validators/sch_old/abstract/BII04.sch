<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for BII04 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="BII04">
  <rule context="$Invoice_Profile">
    <assert test="$BII-P04-001" flag="fatal">[BII-P04-001]-The profile ID is dependent on the profile in which the transaction is being used.</assert>
  </rule>
</pattern>

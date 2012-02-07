<!-- Schematron binding rules generated automatically. -->
<!-- Data binding to UBL syntax for BII19 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="BII19" id="ubl-BII19">
  <param name="BII-P19-001" value=". = 'urn:www.cenbii.eu:profiles:profile19:ver1.0'"/>
  <param name="BII-P19-002" value="local-name(/) = 'Invoice' and boolean(//cac:OrderReference/cbc:ID)"/>
  <param name="Invoice_Profile" value="//cbc:ProfileID"/>
  <param name="Invoice" value="/ubl:Invoice"/>
</pattern>

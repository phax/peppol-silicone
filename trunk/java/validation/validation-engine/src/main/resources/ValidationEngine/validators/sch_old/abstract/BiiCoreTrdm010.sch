<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for BiiCoreTrdm010 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="BiiCoreTrdm010">
  <rule context="$Invoice_Period">
    <assert test="$BII-T10-001" flag="warning">[BII-T10-001]-An invoice period end date SHOULD be later or equal to an invoice period start date</assert>
  </rule>
  <rule context="$Supplier_Address">
    <assert test="$BII-T10-002" flag="warning">[BII-T10-002]-A supplier address in an invoice SHOULD contain at least City and zip code or have one or more address lines.</assert>
  </rule>
  <rule context="$Supplier">
    <assert test="$BII-T10-003" flag="fatal">[BII-T10-003]-In cross border trade the VAT identifier for the supplier MUST be prefixed with country code.</assert>
  </rule>
  <rule context="$Customer_Address">
    <assert test="$BII-T10-004" flag="warning">[BII-T10-004]-A customer address in an invoice SHOULD contain at least city and zip code or have one or more address lines.</assert>
  </rule>
  <rule context="$Customer">
    <assert test="$BII-T10-005" flag="fatal">[BII-T10-005]-In cross border trade the VAT identifier for the customer should be prefixed with country code.</assert>
  </rule>
  <rule context="$Payment_Means">
    <assert test="$BII-T10-006" flag="warning">[BII-T10-006]-Payment means due date in an invoice SHOULD be later or equal than issue date.</assert>
    <assert test="$BII-T10-007" flag="warning">[BII-T10-007]-If payment means is funds transfer, invoice MUST have a financial account </assert>
    <assert test="$BII-T10-008" flag="warning">[BII-T10-008]-If bank account is IBAN the BIC code SHOULD also be provided.</assert>
  </rule>
  <rule context="$Tax_Total">
    <assert test="$BII-T10-009" flag="fatal">[BII-T10-009]-An invoice MUST have a tax total refering to a single tax schema </assert>
    <assert test="$BII-T10-010" flag="fatal">[BII-T10-010]-Each tax total MUST equal the sum of the subcategory amounts.</assert>
  </rule>
  <rule context="$Total_Amounts">
    <assert test="$BII-T10-011" flag="fatal">[BII-T10-011]-Invoice total line extension amount MUST equal the sum of the line totals</assert>
    <assert test="$BII-T10-012" flag="fatal">[BII-T10-012]-An invoice tax exclusive amount MUST equal the sum of lines plus allowances and charges on header level.</assert>
    <assert test="$BII-T10-013" flag="fatal">[BII-T10-013]-An invoice tax inclusive amount MUST equal the tax exclusive amount plus all tax total amounts and the rounding amount.</assert>
    <assert test="$BII-T10-014" flag="fatal">[BII-T10-014]-Tax inclusive amount in an invoice MUST NOT be negative</assert>
    <assert test="$BII-T10-015" flag="fatal">[BII-T10-015]-If there is a total allowance it MUST be equal to the sum of allowances at document level</assert>
    <assert test="$BII-T10-016" flag="fatal">[BII-T10-016]-If there is a total charges it MUST be equal to the sum of document level charges.</assert>
    <assert test="$BII-T10-017" flag="fatal">[BII-T10-017]-In an invoice, amount due is the tax inclusive amount minus what has been prepaid.</assert>
  </rule>
  <rule context="$Invoice_Line">
    <assert test="$BII-T10-018" flag="fatal">[BII-T10-018]-Invoice line amount MUST be equal to the price amount multiplied by the quantity</assert>
  </rule>
  <rule context="$Item">
    <assert test="$BII-T10-019" flag="warning">[BII-T10-019]-Product names SHOULD NOT exceed 50 characters long</assert>
    <assert test="$BII-T10-020" flag="warning">[BII-T10-020]-If standard identifiers are provided within an item description, an Schema Identifier SHOULD be provided (e.g. GTIN)</assert>
    <assert test="$BII-T10-021" flag="warning">[BII-T10-021]-Classification codes within an item description SHOULD have a List Identifier attribute (e.g. CPV or UNSPSC)</assert>
  </rule>
  <rule context="$Item_Price">
    <assert test="$BII-T10-022" flag="fatal">[BII-T10-022]-Prices of items MUST be positive or zero</assert>
  </rule>
  <rule context="$Allowance_Percentage">
    <assert test="$BII-T10-023" flag="fatal">[BII-T10-023]-An allowance percentage MUST NOT be negative.</assert>
  </rule>
  <rule context="$Allowance">
    <assert test="$BII-T10-024" flag="warning">[BII-T10-024]-In allowances, both or none of percentage and base amount SHOULD be provided</assert>
  </rule>
</pattern>

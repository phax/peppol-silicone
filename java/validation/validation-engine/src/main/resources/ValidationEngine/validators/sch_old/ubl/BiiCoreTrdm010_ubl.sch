<!-- Schematron binding rules generated automatically. -->
<!-- Data binding to UBL syntax for BiiCoreTrdm010 -->
<!-- CEN BII WG3 (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="BiiCoreTrdm010" id="ubl-BiiCoreTrdm010">
  <param name="BII-T10-001" value="(child::cbc:StartDate and child::cbc:EndDate) and not(number(translate(child::cbc:StartDate,'-','')) &gt; number(translate(child::cbc:EndDate,'-',''))) or number(translate(child::cbc:EndDate,'-','')) = number(translate(child::cbc:StartDate,'-',''))"/>
  <param name="BII-T10-002" value="(child::cbc:CityName and child::cbc:PostalZone) or count(child::cac:AddressLine)&gt;0"/>
  <param name="BII-T10-003" value="(((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID/@schemeID = 'VAT') and starts-with(child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or ((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) "/>
  <param name="BII-T10-004" value="(child::cbc:CityName and child::cbc:PostalZone) or count(child::cac:AddressLine)&gt;0"/>
  <param name="BII-T10-005" value="(((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID/@schemeID = 'VAT') and starts-with(child::cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or ((child::cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) "/>
  <param name="BII-T10-006" value="(child::cbc:PaymentDueDate and /ubl:Invoice/cbc:IssueDate) and not(number(translate(child::cbc:PaymentDueDate,'-','')) &lt; number(translate(/ubl:Invoice/cbc:IssueDate,'-',''))) or number(translate(child::cbc:PaymentDueDate,'-','')) = number(translate(/ubl:Invoice/cbc:IssueDate,'-',''))"/>
  <param name="BII-T10-007" value="(child::cbc:PaymentMeansCode = '31') and //cac:PayeeFinancialAccount/cbc:ID or (child::cbc:PaymentMeansCode != '31')"/>
  <param name="BII-T10-008" value="(child::cac:PayeeFinancialAccount/cbc:ID/@schemeID and (child::cac:PayeeFinancialAccount/cbc:ID/@schemeID = 'IBAN') and child::cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:ID) or (child::cac:PayeeFinancialAccount/cbc:ID/@schemeID != 'IBAN') or (not(child::cac:PayeeFinancialAccount/cbc:ID/@schemeID))"/>
  <param name="BII-T10-009" value="count(child::cac:TaxSubtotal)&gt;1 and (child::cac:TaxSubtotal[1]/cac:TaxCategory/cac:TaxScheme/cbc:ID) =(child::cac:TaxSubtotal[2]/cac:TaxCategory/cac:TaxScheme/cbc:ID) or count(child::cac:TaxSubtotal)&lt;=1"/>
  <param name="BII-T10-010" value="number(child::cbc:TaxAmount) = number(sum(child::cac:TaxSubtotal/cbc:TaxAmount))"/>
  <param name="BII-T10-011" value="number(child::cbc:LineExtensionAmount) = number(sum(//cac:InvoiceLine/cbc:LineExtensionAmount))"/>
  <param name="BII-T10-012" value="((child::cbc:ChargeTotalAmount) and (child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = (number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount) - number(cbc:AllowanceTotalAmount)))) or (not(child::cbc:ChargeTotalAmount) and (child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount) - number(cbc:AllowanceTotalAmount))) or ((child::cbc:ChargeTotalAmount) and not(child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount) + number(cbc:ChargeTotalAmount))) or (not(child::cbc:ChargeTotalAmount) and not(child::cbc:AllowanceTotalAmount) and (number(cbc:TaxExclusiveAmount) = number(cbc:LineExtensionAmount)))"/>
  <param name="BII-T10-013" value="((child::cbc:PayableRoundingAmount) and (number(child::cbc:TaxInclusiveAmount) = number(child::cbc:TaxExclusiveAmount) + number(sum(/ubl:Invoice/cac:TaxTotal/cbc:TaxAmount)) + number(child::cbc:PayableRoundingAmount))) or  (number(child::cbc:TaxInclusiveAmount) = number(child::cbc:TaxExclusiveAmount) + number(sum(/ubl:Invoice/cac:TaxTotal/cbc:TaxAmount)))"/>
  <param name="BII-T10-014" value="number(child::cbc:TaxInclusiveAmount) &gt;= 0"/>
  <param name="BII-T10-015" value="(child::cbc:AllowanceTotalAmount) and child::cbc:AllowanceTotalAmount = sum(/ubl:Invoice/cac:AllowanceCharge[child::cbc:ChargeIndicator=&quot;false&quot;]/cbc:Amount) or not(child::cbc:AllowanceTotalAmount)"/>
  <param name="BII-T10-016" value="(child::cbc:ChargeTotalAmount) and child::cbc:ChargeTotalAmount = sum(/ubl:Invoice/cac:AllowanceCharge[child::cbc:ChargeIndicator=&quot;true&quot;]/cbc:Amount) or not(child::cbc:ChargeTotalAmount)"/>
  <param name="BII-T10-017" value="(child::cbc:PrepaidAmount) and (number(cbc:PayableAmount) = number(cbc:TaxInclusiveAmount - cbc:PrepaidAmount)) or child::cbc:PayableAmount = child::cbc:TaxInclusiveAmount"/>
  <param name="BII-T10-018" value="number(child::cbc:LineExtensionAmount) = number(child::cac:Price/cbc:PriceAmount) * number(child::cbc:InvoicedQuantity)"/>
  <param name="BII-T10-019" value="string-length(string(cbc:Name)) &lt;= 50"/>
  <param name="BII-T10-020" value="boolean(child::cac:StandardItemIdentification/cbc:ID/@schemeID)"/>
  <param name="BII-T10-021" value="boolean(child::cac:CommodityClassification/cbc:ItemClassificationCode/@listID)"/>
  <param name="BII-T10-022" value="number(.) &gt;=0"/>
  <param name="BII-T10-023" value="number(.) &gt;=0"/>
  <param name="BII-T10-024" value="(child::cbc:MultiplierFactorNumeric and child::cbc:BaseAmount) or (not(child::cbc:MultiplierFactorNumeric) and not(child::cbc:BaseAmount))"/>
  <param name="Invoice_Period" value="//cac:InvoicePeriod"/>
  <param name="Supplier_Address" value="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress"/>
  <param name="Supplier" value="//cac:AccountingSupplierParty"/>
  <param name="Customer_Address" value="//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress"/>
  <param name="Customer" value="//cac:AccountingCustomerParty"/>
  <param name="Payment_Means" value="//cac:PaymentMeans"/>
  <param name="Tax_Total" value="/ubl:Invoice/cac:TaxTotal"/>
  <param name="Invoice_Line" value="//cac:InvoiceLine"/>
  <param name="Invoice" value="/ubl:Invoice"/>
  <param name="Item_Price" value="//cac:InvoiceLine/cac:Price/cbc:PriceAmount"/>
  <param name="Item" value="//cac:Item"/>
  <param name="Allowance_Percentage" value="//cac:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:MultiplierFactorNumeric"/>
  <param name="Allowance" value="//cac:AllowanceCharge[cbc:ChargeIndicator='false']"/>
  <param name="Total_Amounts" value="//cac:LegalMonetaryTotal"/>
</pattern>

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!--

    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.

-->
<xsl:stylesheet version="2.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<xsl:param name="archiveDirParameter" />
  <xsl:param name="archiveNameParameter" />
  <xsl:param name="fileNameParameter" />
  <xsl:param name="fileDirParameter" />
  <xsl:variable name="document-uri">
    <xsl:value-of select="document-uri(/)" />
  </xsl:variable>

<!--PHASES-->


<!--PROLOG-->
<xsl:output indent="yes" method="xml" omit-xml-declaration="no" standalone="yes" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

<!--XSD TYPES FOR XSLT2-->


<!--KEYS AND FUNCTIONS-->


<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-select-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="." />
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="parent::*" />
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=&#39;&#39;">
        <xsl:value-of select="name()" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>*:</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>[namespace-uri()=&#39;</xsl:text>
        <xsl:value-of select="namespace-uri()" />
        <xsl:text>&#39;]</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:variable name="preceding" select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])" />
    <xsl:text>[</xsl:text>
    <xsl:value-of select="1+ $preceding" />
    <xsl:text>]</xsl:text>
  </xsl:template>
  <xsl:template match="@*" mode="schematron-get-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="parent::*" />
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=&#39;&#39;">@<xsl:value-of select="name()" />
</xsl:when>
      <xsl:otherwise>
        <xsl:text>@*[local-name()=&#39;</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>&#39; and namespace-uri()=&#39;</xsl:text>
        <xsl:value-of select="namespace-uri()" />
        <xsl:text>&#39;]</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-2">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="preceding-sibling::*[name(.)=name(current())]">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>
<!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-3">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="parent::*">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>

<!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path" />
  <xsl:template match="text()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat(&#39;.text-&#39;, 1+count(preceding-sibling::text()), &#39;-&#39;)" />
  </xsl:template>
  <xsl:template match="comment()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat(&#39;.comment-&#39;, 1+count(preceding-sibling::comment()), &#39;-&#39;)" />
  </xsl:template>
  <xsl:template match="processing-instruction()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat(&#39;.processing-instruction-&#39;, 1+count(preceding-sibling::processing-instruction()), &#39;-&#39;)" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat(&#39;.@&#39;, name())" />
  </xsl:template>
  <xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:text>.</xsl:text>
    <xsl:value-of select="concat(&#39;.&#39;,name(),&#39;-&#39;,1+count(preceding-sibling::*[name()=name(current())]),&#39;-&#39;)" />
  </xsl:template>

<!--MODE: GENERATE-ID-2 -->
<xsl:template match="/" mode="generate-id-2">U</xsl:template>
  <xsl:template match="*" mode="generate-id-2" priority="2">
    <xsl:text>U</xsl:text>
    <xsl:number count="*" level="multiple" />
  </xsl:template>
  <xsl:template match="node()" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>n</xsl:text>
    <xsl:number count="node()" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="string-length(local-name(.))" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="translate(name(),&#39;:&#39;,&#39;.&#39;)" />
  </xsl:template>
<!--Strip characters-->  <xsl:template match="text()" priority="-1" />

<!--SCHEMA SETUP-->
<xsl:template match="/">
    <svrl:schematron-output schemaVersion="" title="EUGEN T19 bound to UBL" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">UBL-T19</xsl:attribute>
        <xsl:attribute name="name">UBL-T19</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M5" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">EUGEN T19 bound to UBL</svrl:text>

<!--PATTERN UBL-T19-->


	<!--RULE -->
<xsl:template match="//cac:ContractorCustomerParty/cac:Party" mode="M5" priority="1015">
    <svrl:fired-rule context="//cac:ContractorCustomerParty/cac:Party" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and  ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID=&#39;VAT&#39; and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID=&#39;VAT&#39; and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((preceding::cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R025]-In cross border trade the VAT identifier for the customer should be prefixed with country code.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R024]-If buyer customer party ID is not specified, buyer party name is mandatory</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Catalogue" mode="M5" priority="1014">
    <svrl:fired-rule context="/ubl:Catalogue" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:ReferencedContract) &lt;=1" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:ReferencedContract) &lt;=1" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R028]-Contract reference SHOULD be only one</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:ProfileID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:ProfileID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R003]-The profile ID is dependent on the profile in which the transaction is being used.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:CustomizationID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:CustomizationID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R002]-CustomizationID MUST  comply with CEN/BII transactions definitions</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:UBLVersionID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:UBLVersionID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R001]-UBL VersionID MUST define a supported syntaxbinding</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:ProviderParty" mode="M5" priority="1013">
    <svrl:fired-rule context="//cac:ProviderParty" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:EndpointID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R031]-Provider party endpoint identifier MUST be filled in </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:RequiredItemLocationQuantity" mode="M5" priority="1012">
    <svrl:fired-rule context="//cac:RequiredItemLocationQuantity" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:MaximumQuantity) and (cbc:MinimumQuantity) and (number(cbc:MaximumQuantity) &gt;= number(cbc:MinimumQuantity))) or not(cbc:MaximumQuantity) or not(cbc:MinimumQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:MaximumQuantity) and (cbc:MinimumQuantity) and (number(cbc:MaximumQuantity) &gt;= number(cbc:MinimumQuantity))) or not(cbc:MaximumQuantity) or not(cbc:MinimumQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R034]-Catalogue line Maximum_quantity SHOULD be greater than the Minimum quantity (it is applied to the section Item location.quantity.maximum_quantity) </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((//cac:ValidityPeriod) and (/ubl:Catalogue/cac:ValidityPeriod) and (//cac:ValidityPeriod/cbc:StartDate)&gt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:StartDate) and (//cac:ValidityPeriod/cbc:EndDate)&lt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:EndDate)) or not(//cac:ValidityPeriod) or not(/ubl:Catalogue/cac:ValidityPeriod)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((//cac:ValidityPeriod) and (/ubl:Catalogue/cac:ValidityPeriod) and (//cac:ValidityPeriod/cbc:StartDate)&gt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:StartDate) and (//cac:ValidityPeriod/cbc:EndDate)&lt;(/ubl:Catalogue/cac:ValidityPeriod/cbc:EndDate)) or not(//cac:ValidityPeriod) or not(/ubl:Catalogue/cac:ValidityPeriod)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R016]-Line validity period SHOULD be within the range of the whole catalogue validity period</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:Party" mode="M5" priority="1011">
    <svrl:fired-rule context="//cac:Party" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:PartyLegalEntity/cbc:CompanyID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:PartyLegalEntity/cbc:CompanyID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R005]-Party.Party Tax Scheme. Company Identifier SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:ReceiverParty" mode="M5" priority="1010">
    <svrl:fired-rule context="//cac:ReceiverParty" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:EndpointID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:EndpointID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R030]-Receiver party endpoint identifier MUST be filled in </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:RequiredItemLocationQuantity/cac:Price" mode="M5" priority="1009">
    <svrl:fired-rule context="//cac:RequiredItemLocationQuantity/cac:Price" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:PriceAmount) &gt;=0" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:PriceAmount) &gt;=0" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R013]-Prices of items MUST be positive or equal to zero NOT negative amounts</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:ReferencedContract" mode="M5" priority="1008">
    <svrl:fired-rule context="//cac:ReferencedContract" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(not(cbc:ID) and (cbc:ContractType)) or (cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(not(cbc:ID) and (cbc:ContractType)) or (cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R027]-If Contract Identifier is not specified SHOULD Contract Type text be used for Contract Reference </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:SellerSupplierParty/cac:Party/cac:PostalAddress" mode="M5" priority="1007">
    <svrl:fired-rule context="//cac:SellerSupplierParty/cac:Party/cac:PostalAddress" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R010]-A seller party address in an catalogue SHOULD contain at least Street Name, City name and Zip code and Country code </svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:SellerSupplierParty/cac:Party" mode="M5" priority="1006">
    <svrl:fired-rule context="//cac:SellerSupplierParty/cac:Party" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID=&#39;VAT&#39; and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID) and (cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) = (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) or ((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) != (following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode) and cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:ID=&#39;VAT&#39; and starts-with(cac:Party/cac:PartyTaxScheme/cbc:CompanyID,cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)))) or not((cac:Party/cac:PartyTaxScheme[cac:TaxScheme/cbc:ID=&#39;VAT&#39;]/cbc:CompanyID)) or not((cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not((following::cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R009]-In cross border trade the VAT identifier for the supplier MUST be prefixed with country code.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(not(cac:PartyIdentification/cbc:ID) and (cac:PartyName/cbc:Name)) or (cac:PartyIdentification/cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R007]-If seller supplier party ID is not specified, seller supplier party name is mandatory</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:ValidityPeriod" mode="M5" priority="1005">
    <svrl:fired-rule context="//cac:ValidityPeriod" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:StartDate and cbc:EndDate) and not(number(translate(cbc:StartDate,&#39;-&#39;,&#39;&#39;)) &gt; number(translate(cbc:EndDate,&#39;-&#39;,&#39;&#39;))) or number(translate(cbc:EndDate,&#39;-&#39;,&#39;&#39;)) = number(translate(cbc:StartDate,&#39;-&#39;,&#39;&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:StartDate and cbc:EndDate) and not(number(translate(cbc:StartDate,&#39;-&#39;,&#39;&#39;)) &gt; number(translate(cbc:EndDate,&#39;-&#39;,&#39;&#39;))) or number(translate(cbc:EndDate,&#39;-&#39;,&#39;&#39;)) = number(translate(cbc:StartDate,&#39;-&#39;,&#39;&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R029]-A validity period end date SHOULD be later or equal to a validity period start date</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:Item" mode="M5" priority="1004">
    <svrl:fired-rule context="//cac:Item" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:SellersItemIdentification/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:SellersItemIdentification/cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R041]-Sellers_ Item Identification. Item Identification section SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:ClassifiedTaxCategory/cac:TaxScheme/cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R019]-Item Tax Scheme SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:ClassifiedTaxCategory/cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:ClassifiedTaxCategory/cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R018]-Item Tax Category SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:Description)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:Description)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R017]-Item should have a Description – Invoice is the NAME!!</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cac:CommodityClassification/cbc:CommodityCode) and (cac:CommodityClassification/cbc:ItemClassificationCode)) or not(cac:CommodityClassification)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cac:CommodityClassification/cbc:CommodityCode) and (cac:CommodityClassification/cbc:ItemClassificationCode)) or not(cac:CommodityClassification)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R015]-Item Commodity Classification: both Classification Commodity codes and Item classification code MUST be filled</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not((cac:StandardItemIdentification)) or (cac:StandardItemIdentification/cbc:ID/@schemeID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not((cac:StandardItemIdentification)) or (cac:StandardItemIdentification/cbc:ID/@schemeID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R012]-If standard identifiers are provided within an item description, an Schema Identifier SHOULD be provided (e.g. GTIN)</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:SellerSupplierParty/cac:Party/cac:Contact" mode="M5" priority="1003">
    <svrl:fired-rule context="//cac:SellerSupplierParty/cac:Party/cac:Contact" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:Telephone)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:Telephone)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R006]-A party contact telephone text SHOULD be filled in</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:CatalogueLine" mode="M5" priority="1002">
    <svrl:fired-rule context="//cac:CatalogueLine" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:PriceAmount) and (cbc:BaseQuantity)) or not (cbc:PriceAmount)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:PriceAmount) and (cbc:BaseQuantity)) or not (cbc:PriceAmount)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R042]-If Price amount is used than Price Base Quantity SHOUL be higher than zero</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:ID)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:ID)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R040]-Contract reference SHOULD always present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:MaximumOrderQuantity) and (cbc:MaximumOrderQuantity) &gt;=0) or not(cbc:MaximumOrderQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:MaximumOrderQuantity) and (cbc:MaximumOrderQuantity) &gt;=0) or not(cbc:MaximumOrderQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R039]-Catalogue line Maximum_quantity SHOULD NOT be negative</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:MinimumOrderQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:MinimumOrderQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R038]-Catalogue line Mimimum_quantity SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:MaximumOrderQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:MaximumOrderQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R037]-Catalogue line Maximum_quantity SHOULD be present</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:MinimumOrderQuantity) and (cbc:MinimumOrderQuantity) &gt;=0) or not(cbc:MinimumOrderQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:MinimumOrderQuantity) and (cbc:MinimumOrderQuantity) &gt;=0) or not(cbc:MinimumOrderQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R036]-Catalogue line Mimimum_quantity SHOULD NOT be negative</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:MaximumOrderQuantity) and (cbc:MinimumOrderQuantity) and (number(cbc:MaximumOrderQuantity) &gt;= number(cbc:MinimumOrderQuantity))) or not(cbc:MaximumOrderQuantity) or not(cbc:MinimumOrderQuantity)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:MaximumOrderQuantity) and (cbc:MinimumOrderQuantity) and (number(cbc:MaximumOrderQuantity) &gt;= number(cbc:MinimumOrderQuantity))) or not(cbc:MaximumOrderQuantity) or not(cbc:MinimumOrderQuantity)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R033]-Catalogue line Maximum_quantity SHOULD be greater or equal to the Minimum quantity: it is applied in all the section in  a Catalogue line where are included Maximum and minimum quantity (it is applied at section Max order quantity)</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="((cbc:OrderableIndicator=true()) and cbc:OrderableUnit) or (cbc:OrderableIndicator=false()) or not(cbc:OrderableIndicator)" />
      <xsl:otherwise>
        <svrl:failed-assert test="((cbc:OrderableIndicator=true()) and cbc:OrderableUnit) or (cbc:OrderableIndicator=false()) or not(cbc:OrderableIndicator)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">fatal</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R032]-If Orderable Indicator is se to Yes than Orderable Unit (text) MUST not be blank</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:ContractorCustomerParty/cac:Party/cac:PostalAddress" mode="M5" priority="1001">
    <svrl:fired-rule context="//cac:ContractorCustomerParty/cac:Party/cac:PostalAddress" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cbc:StreetName and cbc:CityName and cbc:PostalZone and cac:Country/cbc:IdentificationCode)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R023]-A Customer party address in an catalogue SHOULD contain at least Street Name, City name and Zip code and Country code.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="//cac:DocumentReference" mode="M5" priority="1000">
    <svrl:fired-rule context="//cac:DocumentReference" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode)" />
      <xsl:otherwise>
        <svrl:failed-assert test="(cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[EUGEN-T19-R020]-Mime code Should be given for embedded binary object accordingly to codelist</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M5" priority="-1" />
  <xsl:template match="@*|node()" mode="M5" priority="-2">
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>
</xsl:stylesheet>

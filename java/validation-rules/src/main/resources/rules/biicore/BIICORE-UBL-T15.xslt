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
<xsl:stylesheet version="2.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    <svrl:schematron-output schemaVersion="" title="BIICORE T15 bound to UBL" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">UBL-T15</xsl:attribute>
        <xsl:attribute name="name">UBL-T15</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M5" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">BIICORE T15 bound to UBL</svrl:text>

<!--PATTERN UBL-T15-->


	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party" mode="M5" priority="1008">
    <svrl:fired-rule context="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R398]-Element &#39;PartyIdentification&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyName)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyName)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R399]-Element &#39;PartyName&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyTaxScheme)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyTaxScheme)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R400]-Element &#39;PartyTaxScheme&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:PayeeParty" mode="M5" priority="1007">
    <svrl:fired-rule context="/ubl:Invoice/cac:PayeeParty" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R413]-Element &#39;PartyIdentification&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyName)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyName)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R414]-Element &#39;PartyName&#39; may occur at maximum 1 times</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M5" priority="1006">
    <svrl:fired-rule context="/ubl:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cbc:ID)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cbc:ID)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R415]-Element &#39;ID&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:InvoiceLine" mode="M5" priority="1005">
    <svrl:fired-rule context="/ubl:InvoiceLine" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:TaxTotal)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:TaxTotal)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R405]-Element &#39;TaxTotal&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:Price)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:Price)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R406]-Element &#39;Price&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party" mode="M5" priority="1004">
    <svrl:fired-rule context="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyIdentification)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R401]-Element &#39;PartyIdentification&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyName)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyName)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R402]-Element &#39;PartyName&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PostalAddress)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PostalAddress)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R403]-Element &#39;PostalAddress&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:PartyTaxScheme)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:PartyTaxScheme)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R404]-Element &#39;PartyTaxScheme&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:InvoiceLine/cac:Item" mode="M5" priority="1003">
    <svrl:fired-rule context="/ubl:Invoice/cac:InvoiceLine/cac:Item" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cbc:Description)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cbc:Description)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R407]-Element &#39;Description&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cbc:Name)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cbc:Name)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R408]-Element &#39;Name&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:ClassifiedTaxCategory)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:ClassifiedTaxCategory)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R409]-Element &#39;ClassifiedTaxCategory&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:InvoiceLine/cac:Price" mode="M5" priority="1002">
    <svrl:fired-rule context="/ubl:Invoice/cac:InvoiceLine/cac:Price" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cac:AllowanceCharge)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cac:AllowanceCharge)&lt;=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R410]-Element &#39;AllowanceCharge&#39; may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice" mode="M5" priority="1001">
    <svrl:fired-rule context="/ubl:Invoice" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;)" />
      <xsl:otherwise>
        <svrl:failed-assert test="contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R000]-This XML instance is NOT a BiiTrdm015 transaction</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(count(//*[not(text())]) &gt; 0)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(count(//*[not(text())]) &gt; 0)" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R001]-An invoice SHOULD not contain empty elements.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R002]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R003]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:IssueTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:IssueTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R004]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:TaxCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:TaxCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R005]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PricingCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PricingCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R006]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PaymentCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PaymentCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R007]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:PaymentAlternativeCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:PaymentAlternativeCurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R008]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R009]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cbc:LineCountNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cbc:LineCountNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R010]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R011]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DespatchDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DespatchDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R012]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ReceiptDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ReceiptDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R013]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OriginatorDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OriginatorDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R014]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Signature) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Signature) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R015]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BuyerCustomerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BuyerCustomerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R016]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:SellerSupplierParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:SellerSupplierParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R017]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxRepresentativeParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxRepresentativeParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R018]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:DeliveryTerms) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:DeliveryTerms) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R019]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PrepaidPayment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PrepaidPayment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R020]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R021]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PricingExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PricingExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R022]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R023]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentAlternativeExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentAlternativeExchangeRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R024]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:StartTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:StartTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R025]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:EndTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:EndTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R026]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:DurationMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:DurationMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R027]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:Description) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:Description) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R028]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoicePeriod/cbc:DescriptionCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoicePeriod/cbc:DescriptionCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R029]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:SalesOrderID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:SalesOrderID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R030]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R031]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R032]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R033]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:IssueTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:IssueTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R034]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cbc:CustomerReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cbc:CustomerReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R035]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:OrderReference/cac:DocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:OrderReference/cac:DocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R036]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:CooyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:CooyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R037]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R038]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R039]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:DocumentTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:DocumentTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R040]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R041]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:ContractDocumentReference/cac:Attachment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:ContractDocumentReference/cac:Attachment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R042]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R043]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R044]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:IssueDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R045]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R046]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R047]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R048]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R049]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R050]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R051]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:AdditionalAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:AdditionalAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R052]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cbc:DataSendingCapability) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cbc:DataSendingCapability) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R053]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:DespatchContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:DespatchContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R054]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:AccountingContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:AccountingContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R055]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:SellerContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:SellerContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R056]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R057]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R058]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R059]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R060]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R061]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R062]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R063]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R064]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R065]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R066]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R067]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R068]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R069]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R070]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R071]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R072]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R073]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R074]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R075]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R076]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R077]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R078]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R079]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R080]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R081]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R082]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R083]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R084]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R085]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R086]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R087]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R088]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R089]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R090]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R091]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R092]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R093]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R094]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R095]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R096]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R097]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R098]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R099]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R100]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R101]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R102]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R103]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R104]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R105]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R106]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R107]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R108]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R109]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R110]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R111]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R112]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R113]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R114]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R115]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R116]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R117]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R118]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R119]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R120]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R121]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R122]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R123]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cbc:AdditionalAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cbc:AdditionalAccountID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R124]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:DeliveryContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:DeliveryContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R125]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:AccountingContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:AccountingContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R126]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:BuyerContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:BuyerContact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R127]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R128]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R129]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R130]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R131]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R132]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R133]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R134]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R135]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R136]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R137]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R138]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R139]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R140]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R141]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R142]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R143]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R144]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R145]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R146]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R147]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R148]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R149]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R150]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R151]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R152]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R153]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R154]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R155]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R156]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R157]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R158]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R159]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R160]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R161]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R162]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R163]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R164]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R165]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R166]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R167]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R168]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R169]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R170]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R171]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R172]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R173]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R174]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R175]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R176]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R177]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R178]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R179]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R180]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R181]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R182]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R183]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R184]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R185]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R186]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R187]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R188]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R189]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R190]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R191]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R192]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:MarkCareIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R193]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:MarkAttentionIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R194]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:WebsiteURI) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R195]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cbc:LogoReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R196]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Language) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R197]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PostalAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PostalAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R198]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PhysicalLocation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R199]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyTaxScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyTaxScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R200]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R201]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R202]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R203]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Contact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Contact) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R204]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:Person) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:Person) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R205]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PayeeParty/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PayeeParty/cac:AgentParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R206]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R207]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:Quantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:Quantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R208]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:MinimumQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:MinimumQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R209]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:MaximumQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:MaximumQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R210]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:ActualDeliveryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:ActualDeliveryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R211]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:LatestDeliveryDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:LatestDeliveryDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R212]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:LatestDeliveryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:LatestDeliveryTime) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R213]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cbc:TrackingID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cbc:TrackingID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R214]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R215]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:Description) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:Description) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R216]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R217]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R218]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R219]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R220]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R221]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R222]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R223]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R224]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R225]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R226]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R227]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R228]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R229]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R230]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R231]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R232]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R233]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R234]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R235]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R236]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R237]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R238]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R239]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R240]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R241]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R242]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R243]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:Delivery/cac:DeliveryLocation/cac:Despatch) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:Delivery/cac:DeliveryLocation/cac:Despatch) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R244]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R245]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:InstructionID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:InstructionID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R246]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cbc:InstructionNote) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cbc:InstructionNote) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R247]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CardAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CardAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R248]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayerFinancialAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayerFinancialAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R249]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:CreditAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:CreditAccount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R250]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R251]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R252]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R253]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R254]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R255]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R256]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R257]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R258]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R259]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PaymentMeansID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PaymentMeansID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R260]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R261]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:ReferenceEventCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:ReferenceEventCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R262]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:SettlementDiscountPercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:SettlementDiscountPercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R263]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:PenaltySurchargePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:PenaltySurchargePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R264]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cbc:Amount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cbc:Amount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R265]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:SettlementPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:SettlementPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R266]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:PaymentTerms/cac:PenaltyPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:PaymentTerms/cac:PenaltyPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R267]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R268]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R269]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R270]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R271]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R272]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:BaseAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:BaseAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R273]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R274]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R275]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R276]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R277]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R278]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cbc:RoundingAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cbc:RoundingAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R279]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cbc:TaxEvidenceIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cbc:TaxEvidenceIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R280]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R281]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R282]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R283]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R284]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R285]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R286]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R287]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R288]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R289]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R290]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R291]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R292]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R293]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R294]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R295]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R296]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R297]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:TaxPointDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:TaxPointDate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R298]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R299]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cbc:FreeOfChargeIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cbc:FreeOfChargeIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R300]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R301]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DespatchLineReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DespatchLineReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R302]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:ReceiptLineReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:ReceiptLineReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R303]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:BillingReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:BillingReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R304]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:DocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:DocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R305]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:PricingReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:PricingReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R306]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OriginatorParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OriginatorParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R307]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Delivery) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Delivery) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R308]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:PaymentTerms) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:PaymentTerms) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R309]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R310]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R311]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R312]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R313]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R314]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R315]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R316]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R317]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R318]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R319]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R320]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R321]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R322]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R323]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:PackQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:PackQuantity) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R324]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R325]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R326]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R327]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R328]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:Keyword) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:Keyword) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R329]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:BrandName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:BrandName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R330]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cbc:ModelName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cbc:ModelName) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R331]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R332]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R333]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R334]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R335]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R336]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R337]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R338]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R339]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R340]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R341]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R342]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R343]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R344]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R345]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R346]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R347]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R348]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:OriginCountry) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:OriginCountry) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R349]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TransactionConditions) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TransactionConditions) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R350]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:HazardousItem) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:HazardousItem) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R351]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R352]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R353]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:OriginAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:OriginAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R354]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:ItemInstance) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:ItemInstance) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R355]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R356]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R357]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R358]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R359]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R360]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R361]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R362]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R363]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R364]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R365]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R366]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R367]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R368]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R369]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R370]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cbc:PriceType) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cbc:PriceType) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R371]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R372]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R373]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:PriceList) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:PriceList) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R374]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R375]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R376]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R377]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R378]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R379]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R380]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)  and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R381]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R382]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R383]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R384]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R385]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R386]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:SelfBilledCreditNoteDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:SelfBilledCreditNoteDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R387]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:DebitNoteDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:DebitNoteDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R388]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:ReminderDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:ReminderDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R389]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:AdditionalDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:AdditionalDocumentReference) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R390]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:BillingReferenceLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:BillingReferenceLine) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R391]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R392]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R393]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R394]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:CopyIndicator) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R395]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:XPath) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R396]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(cac:BillingReference/cac:CreditNoteDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject) and contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R397]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M5" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:LegalMonetaryTotal" mode="M5" priority="1000">
    <svrl:fired-rule context="/ubl:Invoice/cac:LegalMonetaryTotal" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cbc:TaxExclusiveAmount)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cbc:TaxExclusiveAmount)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R411]-Element &#39;TaxExclusiveAmount&#39; must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="count(cbc:TaxInclusiveAmount)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" />
      <xsl:otherwise>
        <svrl:failed-assert test="count(cbc:TaxInclusiveAmount)=1 and contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;) or not (contains(preceding::cbc:CustomizationID, &#39;urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0&#39;))" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T15-R412]-Element &#39;TaxInclusiveAmount&#39; must occur exactly 1 times.</svrl:text>
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

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
<!-- Schematron rules generated automatically. -->
<!-- Abstract rules for T14 -->
<!-- (2009). Invinet Sistemes -->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T14">
  <rule context="$Supplier_Party">
    <assert test="$NOGOV-T14-R001" flag="warning">[NOGOV-T14-R001]-A contact reference identifier SHOULD be provided for AccountingSupplierParty according to EHF.</assert>
    <assert test="$NOGOV-T14-R008" flag="fatal">[NOGOV-T14-R008]-Country code for the supplier address MUST be provided according to EHF.</assert>
  </rule>
  <rule context="$Item">
    <assert test="$NOGOV-T14-R002" flag="warning">[NOGOV-T14-R002]-The sellers ID for the item SHOULD be provided according to EHF.</assert>
  </rule>
  <rule context="$Customer_Party">
    <assert test="$NOGOV-T14-R004" flag="fatal">[NOGOV-T14-R004]-PartyLegalEntity for AccountingCustomerParty SHOULD be provided according to EHF.</assert>
    <assert test="$NOGOV-T14-R006" flag="warning">[NOGOV-T14-R006]-A customer number for AccountingCustomerParty SHOULD be provided according to EHF.</assert>
    <assert test="$NOGOV-T14-R007" flag="fatal">[NOGOV-T14-R007]-A contact reference identifier MUST be provided for AccountingCustomerParty according to EHF.</assert>
  </rule>
  <rule context="$CreditNote_Line">
    <assert test="$NOGOV-T14-R003" flag="warning">[NOGOV-T14-R003]-The unit qualifier of the CreditNote quantity SHOULD be provided according to EHF.</assert>
  </rule>
</pattern>

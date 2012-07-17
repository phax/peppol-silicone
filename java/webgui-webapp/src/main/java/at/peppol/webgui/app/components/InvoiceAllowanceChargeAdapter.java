/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.webgui.app.components;

import java.math.BigDecimal;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PercentType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

public class InvoiceAllowanceChargeAdapter extends AllowanceChargeType {
  
  public InvoiceAllowanceChargeAdapter() {
    
    setID(new IDType());
    setChargeIndicator(new ChargeIndicatorType ());
    setAllowanceChargeReason(new AllowanceChargeReasonType ());
    setAmount(new AmountType ());

    TaxCategoryType taxCategory = new TaxCategoryType ();
    taxCategory.setID (new IDType ());
    taxCategory.setPercent (new PercentType ());
    
    TaxSchemeType taxScheme = new TaxSchemeType();
    taxScheme.setID (new IDType ());
    taxCategory.setTaxScheme (taxScheme);
    
    getTaxCategory().add(taxCategory);
    
  }
  
  public void setAllowanceChargeID(String v) {
    getID().setValue (v);
  }
  
  public String getAllowanceChargeID() {
    return getID().getValue ();
  }  
  
  public void setIndicator(Boolean v) {
    getChargeIndicator().setValue (v);
  }
  
  public Boolean getIndicator() {
    if(getChargeIndicator().isValue ())
      return true;
    return false;
  }
  
  public void setReason(String v) {
    getAllowanceChargeReason().setValue (v);
  }
  
  public String getReason() {
    return getAllowanceChargeReason().getValue ();
  }    
  
  public void setChargeAmount(BigDecimal v) {
    getAmount().setValue (v);
  }
  
  public BigDecimal getChargeAmount() {
    return getAmount().getValue ();
  }      
  
  public void setTaxCategoryID(String id) {
    getTaxCategory().get(0).getID().setValue(id);
  }
  
  public String getTaxCategoryID() {
      return getTaxCategory().get(0).getID().getValue();
  }  
  
  public void setTaxCategoryPercent(BigDecimal v) {
    getTaxCategory().get(0).getPercent ().setValue (v);
  }
  
  public BigDecimal getTaxCategoryPercent() {
      return getTaxCategory().get(0).getPercent ().getValue();
  } 
  
  public void setTaxCategorySchemeID(String id) {
    getTaxCategory().get(0).getTaxScheme ().getID ().setValue(id);
  }
  
  public String getTaxCategorySchemeID() {
      return getTaxCategory().get(0).getTaxScheme().getID ().getValue();
  }    
  
}

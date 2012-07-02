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

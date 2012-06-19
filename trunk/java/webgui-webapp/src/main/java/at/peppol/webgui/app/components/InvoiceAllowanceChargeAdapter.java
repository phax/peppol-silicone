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

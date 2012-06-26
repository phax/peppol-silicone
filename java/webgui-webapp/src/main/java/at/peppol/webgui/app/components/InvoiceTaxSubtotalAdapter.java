package at.peppol.webgui.app.components;

import java.math.BigDecimal;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PercentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExemptionReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmountType;

@SuppressWarnings ("serial")
public class InvoiceTaxSubtotalAdapter extends TaxSubtotalType {
  private String tableLineID;
  
  public InvoiceTaxSubtotalAdapter() {
    tableLineID = "";
    setTaxableAmount (new TaxableAmountType ());
    setTaxAmount (new TaxAmountType ());
    TaxCategoryType tc = new TaxCategoryType ();
    tc.setID (new IDType ());
    tc.setPercent (new PercentType ());
    tc.setTaxExemptionReasonCode (new TaxExemptionReasonCodeType ());
    tc.setTaxExemptionReason (new TaxExemptionReasonType ());
    TaxSchemeType ts = new TaxSchemeType ();
    ts.setID (new IDType ());
    tc.setTaxScheme (ts);
    setTaxCategory (tc);
  }
  
  public void setTableLineID(String v) {
    tableLineID = v;
  }
  
  public String getTableLineID () {
    return tableLineID;
  }

  public void setTaxSubTotalTaxableAmount(BigDecimal v) {
    getTaxableAmount ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalTaxableAmount() {
    return getTaxableAmount ().getValue ();
  } 
  
  public void setTaxSubTotalTaxAmount(BigDecimal v) {
    getTaxAmount ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalTaxAmount() {
    return getTaxAmount ().getValue ();
  }    
  
  public void setTaxSubTotalCategoryID(String v) {
    getTaxCategory ().getID ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryID() {
    return getTaxCategory ().getID ().getValue ();
  }
  
  public void setTaxSubTotalCategoryPercent(BigDecimal v) {
    getTaxCategory ().getPercent ().setValue (v);
  }
  
  public BigDecimal getTaxSubTotalCategoryPercent() {
    return getTaxCategory ().getPercent ().getValue ();
  }   
  
  public void setTaxSubTotalCategoryExemptionReasonCode(String v) {
    getTaxCategory ().getTaxExemptionReasonCode ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryExemptionReasonCode() {
    return getTaxCategory ().getTaxExemptionReasonCode ().getValue ();
  }
  
  public void setTaxSubTotalCategoryExemptionReason(String v) {
    getTaxCategory ().getTaxExemptionReason ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryExemptionReason() {
    return getTaxCategory ().getTaxExemptionReason ().getValue ();
  }
  
  public void setTaxSubTotalCategoryTaxSchemeID(String v) {
    getTaxCategory ().getTaxScheme ().getID ().setValue (v);
  }
  
  public String getTaxSubTotalCategoryTaxSchemeID() {
    return getTaxCategory ().getTaxScheme ().getID ().getValue ();
  }
  
}

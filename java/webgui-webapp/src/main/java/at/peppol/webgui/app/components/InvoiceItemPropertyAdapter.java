package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueType;

@SuppressWarnings ("serial")
public class InvoiceItemPropertyAdapter extends ItemPropertyType {
  private String tableLineID;
  
  public InvoiceItemPropertyAdapter() {
    tableLineID = "";
    setName (new NameType ());
    setValue (new ValueType ());
  }
  
  public void setTableLineID(String v) {
    tableLineID = v;
  }
  
  public String getTableLineID () {
    return tableLineID;
  }
  
  public void setItemPropertyName(String v) {
    getName().setValue (v);
  }
  
  public String getItemPropertyName() {
    return getName().getValue ();
  }  
  
  public void setItemPropertyValue(String v) {
    getValue().setValue (v);
  }
  
  public String getItemPropertyValue() {
    return getValue().getValue ();
  }  

} 

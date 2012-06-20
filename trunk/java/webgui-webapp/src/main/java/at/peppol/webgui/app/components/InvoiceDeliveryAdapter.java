package at.peppol.webgui.app.components;


import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;


public class InvoiceDeliveryAdapter extends DeliveryType {
  
  public InvoiceDeliveryAdapter() {
    // Initialization of required fields 
    setID(new IDType());
   
  }
}

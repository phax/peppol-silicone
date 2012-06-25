package at.peppol.webgui.app.components;

import java.util.Date;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ActualDeliveryDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalStreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DepartmentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings ("serial")
public class TabInvoiceDelivery extends Form{
  private InvoiceTabForm parent;
  
  private List<DeliveryType> deliveryList;
  private DeliveryType deliveryItem;
  
  private AddressType deliveryAddress;
  private AddressDetailForm deliveryAddressForm;   
  
  public TabInvoiceDelivery(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }
  
  
  private void initElements() {
    deliveryList = parent.getInvoice().getDelivery ();
    deliveryItem = createDeliveryItem();
    deliveryList.add (deliveryItem);
   
    final GridLayout grid = new GridLayout(2, 2);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Delivery");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Delivery Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceDeliveryTopForm());
    invoiceDetailsPanel.addComponent(deliveryAddressForm);
    grid.addComponent(invoiceDetailsPanel, 0, 0);
    grid.setSizeUndefined();
     
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }  
  
  private DeliveryType createDeliveryItem() {
    final DeliveryType di = new DeliveryType();
   
    final ActualDeliveryDateType actualDeliveryDate = new ActualDeliveryDateType ();
    di.setActualDeliveryDate (actualDeliveryDate);
    
    /* The following are replaced with AddressDetailForm
     * TODO: clean up before final version
     * 
    final AddressType addrType = new AddressType();
    addrType.setStreetName(new StreetNameType());
    addrType.setAdditionalStreetName (new AdditionalStreetNameType());
    addrType.setBuildingNumber (new BuildingNumberType ());
    addrType.setDepartment (new DepartmentType ());
    addrType.setCityName(new CityNameType());
    addrType.setPostalZone(new PostalZoneType());
    addrType.setCountrySubentity (new CountrySubentityType ());
    
    CountryType ct = new CountryType ();
    ct.setIdentificationCode (new IdentificationCodeType ());
    addrType.setCountry (ct);
    */
    deliveryAddress = new AddressType ();
    deliveryAddressForm = new AddressDetailForm ("Delivery", deliveryAddress);
    di.setDeliveryAddress (deliveryAddress);
    
    final  LocationType dl = new LocationType();
    dl.setID (new IDType ());
    /*dl.setAddress (addrType);*/
    dl.setAddress(deliveryAddress);
    
    di.setDeliveryLocation (dl);
    return di;
  }
    
  
  public Form createInvoiceDeliveryTopForm() {
    final Form invoiceDeliveryTopForm = new Form(new FormLayout(), new InvoiceDeliveryFieldFactory());
    invoiceDeliveryTopForm.setImmediate(true);

    final Date actualDeliveryDate = new Date ();
    invoiceDeliveryTopForm.addItemProperty ("Actual Delivery Date", new ObjectProperty <Date> (actualDeliveryDate));
    invoiceDeliveryTopForm.addItemProperty ("Delivery Location ID", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getID (), "value") );
    
    // The following replaced by AddressDetailForm
    /*
    invoiceDeliveryTopForm.addItemProperty ("Address Street Name", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "streetName.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Additional Street Name", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "additionalStreetName.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Department", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "department.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Building Number", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "buildingNumber.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address City Name", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "cityName.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Postal Zone", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "postalZone.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Country Subentity", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "countrySubentity.value") );
    invoiceDeliveryTopForm.addItemProperty ("Address Country ID", new NestedMethodProperty(deliveryItem.getDeliveryLocation().getAddress (), "country.identificationCode.value") );
    */
    
    return invoiceDeliveryTopForm;
  }  
  
  class InvoiceDeliveryFieldFactory implements FormFieldFactory {

    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
        // Identify the fields by their Property ID.
        final String pid = (String) propertyId;
        if ("Actual Delivery Date".equals(pid)) {
          //TODO: process date here...
          
        }
        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }  
  
}

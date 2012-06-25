package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalStreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DepartmentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;

@SuppressWarnings ("serial")
public class AddressDetailForm extends Panel {
  
  private final AddressType addressBean;
  private final String addressPrefix;
  
  public AddressDetailForm(String addressPrefix, AddressType addressBean) {
      this.addressPrefix = addressPrefix;
      this.addressBean = addressBean;
      
      initElements();
  }  

  private void initElements() {
    setCaption(addressPrefix + " Address");
    setStyleName("light");
    
    PropertysetItem addressItemSet = new PropertysetItem();
    
    //initialize
    AddressType address = new AddressType();
    address.setStreetName(new StreetNameType());
    address.setAdditionalStreetName (new AdditionalStreetNameType());
    address.setBuildingNumber (new BuildingNumberType ());
    address.setDepartment (new DepartmentType ());
    address.setCityName(new CityNameType());
    address.setPostalZone(new PostalZoneType());
    address.setCountrySubentity (new CountrySubentityType ());
    
    address.setCountry(new CountryType());
    address.getCountry().setIdentificationCode(new IdentificationCodeType());
    
    //make fields
    addressItemSet.addItemProperty("Street Name", new NestedMethodProperty(address, "streetName.value"));
    addressItemSet.addItemProperty ("Additional Street Name", new NestedMethodProperty(address, "additionalStreetName.value") );
    addressItemSet.addItemProperty ("Department", new NestedMethodProperty(address, "department.value") );
    addressItemSet.addItemProperty("City Name", new NestedMethodProperty(address, "cityName.value"));
    addressItemSet.addItemProperty("Postal Zone", new NestedMethodProperty(address, "postalZone.value"));
    addressItemSet.addItemProperty ("Country Subentity", new NestedMethodProperty(address, "countrySubentity.value") );
    addressItemSet.addItemProperty("Country ID", new NestedMethodProperty(address, "country.identificationCode.value"));
    
    //make form
    final Form addressForm = new Form();
    addressForm.setFormFieldFactory(new AddressFieldFactory());
    addressForm.setItemDataSource(addressItemSet);
    addressForm.setImmediate(true);
    
    addComponent(addressForm);
  }  
  
  class AddressFieldFactory implements FormFieldFactory {

     @Override
     public Field createField(Item item, Object propertyId, Component uiContext) {
       // Identify the fields by their Property ID.
       String pid = (String) propertyId;

       Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
       if (field instanceof AbstractTextField){
           ((AbstractTextField) field).setNullRepresentation("");
       }
       
       return field;
     }
  }  
 
}

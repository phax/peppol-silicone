package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
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
  //TODO: setAddress form (common both in Ivoice and Order)
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
    
    final AddressType address = new AddressType();
    //addressBean.setPostalAddress(address);
    address.setStreetName(new StreetNameType());
    address.setCityName(new CityNameType());
    address.setPostalZone(new PostalZoneType());
    address.setCountry(new CountryType());
    address.getCountry().setIdentificationCode(new IdentificationCodeType());
  
    addressItemSet.addItemProperty("Street Name",
                        new NestedMethodProperty(address, "streetName.value"));
    addressItemSet.addItemProperty("City",
                        new NestedMethodProperty(address, "cityName.value"));
    addressItemSet.addItemProperty("Postal Zone",
                        new NestedMethodProperty(address, "postalZone.value"));
    addressItemSet.addItemProperty("Country",
                        new NestedMethodProperty(address, "country.identificationCode.value"));
    
  
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
       if ("Agency Name".equals(pid)) {
         final PartyAgencyIDSelect select = new PartyAgencyIDSelect("Agency Name");
         select.addListener(new Property.ValueChangeListener() {
           @Override
           public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
               addressBean.getID ()
                       .setSchemeAgencyName(select.getSelectedAgencyName());
           }
         });
         return select;
       }

       Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
       if (field instanceof AbstractTextField){
           ((AbstractTextField) field).setNullRepresentation("");
       }
       
       return field;
     }
  }  
 
}

package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;

public class ItemPropertyForm extends Panel {
  private final ItemPropertyType itemPropertyBean;
  private final String itemPropertyPrefix;
  
  public ItemPropertyForm(String itemPropertyPrefix, ItemPropertyType itemPropertyBean) {
      this.itemPropertyPrefix = itemPropertyPrefix;
      this.itemPropertyBean = itemPropertyBean;
      
      initElements();
  }  
  
  private void initElements() {
    setCaption(itemPropertyPrefix + " Item Property");
    setStyleName("light");
    
    PropertysetItem propertyItemSet = new PropertysetItem();
    
    //initialize
    ItemPropertyType itemProperty = new ItemPropertyType();
    itemProperty.setName (new NameType ());
    itemProperty.setValue (new ValueType ());
    
    //make fields
    propertyItemSet.addItemProperty("Property Name", new NestedMethodProperty(itemProperty, "name.value"));
    propertyItemSet.addItemProperty ("Property Value", new NestedMethodProperty(itemProperty, "value.value") );
    
    //make form
    final Form itemForm = new Form();
    itemForm.setFormFieldFactory(new ItemPropertyFieldFactory());
    itemForm.setItemDataSource(propertyItemSet);
    itemForm.setImmediate(true);
    
    addComponent(itemForm);
  }  
  
  class ItemPropertyFieldFactory implements FormFieldFactory {

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

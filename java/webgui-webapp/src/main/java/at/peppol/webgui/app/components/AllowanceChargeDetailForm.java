package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.MultiplierFactorNumericType;
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
import com.vaadin.ui.Select;

@SuppressWarnings ("serial")
public class AllowanceChargeDetailForm extends Panel {
  
  private final InvoiceAllowanceChargeAdapter allowanceChargeBean;
  private final String titlePrefix;
  
  public AllowanceChargeDetailForm(String titlePrefix, InvoiceAllowanceChargeAdapter allowanceChargeBean) {
      this.titlePrefix = titlePrefix;
      this.allowanceChargeBean = allowanceChargeBean;
      
      initElements();
  }  
  
  private void initElements() {
    setCaption(titlePrefix + " Allowance / Charge");
    setStyleName("light");
    
    PropertysetItem itemSet = new PropertysetItem();
    
    //initialize
    InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter ();
    ac.setChargeIndicator (new ChargeIndicatorType ());
    ac.setAllowanceChargeReason (new AllowanceChargeReasonType ());
    ac.setMultiplierFactorNumeric (new MultiplierFactorNumericType ());
    ac.setAmount (new AmountType ());
    ac.setBaseAmount (new BaseAmountType ());
    
   
    //make fields
    itemSet.addItemProperty ("Allowance/Charge Indicator", new NestedMethodProperty(ac, "indicator") );
    itemSet.addItemProperty ("Allowance/Charge Reason", new NestedMethodProperty(ac, "reason") );
    //itemSet.addItemProperty ("Allowance/Charge Multiplier Factor", new NestedMethodProperty(ac, "InvLinePriceAllowanceChargeMultiplierFactorNumeric") );
    itemSet.addItemProperty ("Allowance/Charge Amount", new NestedMethodProperty(ac, "chargeAmount") );
    //itemSet.addItemProperty ("Allowance/Charge Base Amount", new NestedMethodProperty(ac, "InvLinePriceAllowanceChargeBaseAmount") );
    

    //make form
    final Form allowanceChargeForm = new Form();
    allowanceChargeForm.setFormFieldFactory(new AllowanceChargeFieldFactory());
    allowanceChargeForm.setItemDataSource(itemSet);
    allowanceChargeForm.setImmediate(true);
    
    addComponent(allowanceChargeForm);
  }  

  class AllowanceChargeFieldFactory implements FormFieldFactory {

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
      // Identify the fields by their Property ID.
      String pid = (String) propertyId;
      if ("Allowance/Charge Indicator".equals(pid)) {
        Select indicatorSelect = new Select("Charge or Allowance?");
        indicatorSelect.setNullSelectionAllowed(false);
        indicatorSelect.addItem (true);
        indicatorSelect.addItem (false);
        indicatorSelect.setItemCaption(true, "Charge");
        indicatorSelect.setItemCaption(false, "Allowance");
        
        return indicatorSelect;
      } 
      Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
      if (field instanceof AbstractTextField){
          ((AbstractTextField) field).setNullRepresentation("");
      }
      
      return field;
    }
  }   
  
  
}

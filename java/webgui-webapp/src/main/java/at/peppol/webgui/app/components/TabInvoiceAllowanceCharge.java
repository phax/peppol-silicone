package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


@SuppressWarnings ("serial")
public class TabInvoiceAllowanceCharge extends Form {
  private InvoiceTabForm parent;
  private List<AllowanceChargeType> allowanceChargeList;
  private InvoiceAllowanceChargeAdapter allowanceChargeItem;
  
  private InvoiceAllowanceChargeAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceAllowanceChargeTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceAllowanceCharge(InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;
    initElements();
  }

  private void initElements() {
    allowanceChargeList = parent.getInvoice().getAllowanceCharge ();
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    table = new InvoiceAllowanceChargeTable(parent.getInvoice().getAllowanceCharge ());
    table.setSelectable(true);
    table.setImmediate(true);
    table.setNullSelectionAllowed(false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.addStyleName ("striped strong");
    
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    //buttons Add, Edit, Delete
    Button addBtn = new Button("Add New", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        
        addMode = true;
        hiddenContent.removeAllComponents ();
        allowanceChargeItem = createAllowanceChargeItem();
        
        Label formLabel = new Label("<h3>Adding new allowance charge line</h3>", Label.CONTENT_XHTML);
        
        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent(createInvoiceAllowanceChargeForm());
        
        //Save new line button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent(new Button("Save allowance charge line",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            //update table (and consequently add new item to allowanceChargeList list)
            table.addAllowanceChargeLine (allowanceChargeItem);
            //hide form
            hiddenContent.setVisible(false);
            addMode = false;
          }
        }));
        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            hiddenContent.removeAllComponents ();
            //hide form
            hiddenContent.setVisible(false);
            addMode = false;
          }
        }));
        
        hiddenContent.addComponent(buttonLayout);
        
        //hiddenContent.setVisible(!hiddenContent.isVisible());
        hiddenContent.setVisible(true);
      }
    });    
    Button editBtn = new Button("Edit Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot edit while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          final String sid = (String)table.getContainerProperty(rowId,"ID.value").getValue();
          
          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();
          
          //get selected item
          allowanceChargeItem = (InvoiceAllowanceChargeAdapter) allowanceChargeList.get (table.getIndexFromID (sid));
          //clone it to original item
          originalItem = new InvoiceAllowanceChargeAdapter ();
          originalItem.setID (allowanceChargeItem.getID ());
          originalItem.setIndicator (allowanceChargeItem.getIndicator ());
          originalItem.setReason (allowanceChargeItem.getReason ());
          originalItem.setChargeAmount (allowanceChargeItem.getChargeAmount ());
          originalItem.setTaxCategoryID (allowanceChargeItem.getTaxCategoryID ());
          originalItem.setTaxCategoryPercent (allowanceChargeItem.getTaxCategoryPercent ());
          originalItem.setTaxCategorySchemeID (allowanceChargeItem.getTaxCategorySchemeID ());
          
          Label formLabel = new Label("<h3>Editing allowance charge line</h3>", Label.CONTENT_XHTML);
          
          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent(createInvoiceAllowanceChargeForm());
          
          //Save new line button
          HorizontalLayout buttonLayout = new HorizontalLayout();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              //update table (and consequently edit item to allowanceChargeList list)
              table.setAllowanceChargeLine (sid, allowanceChargeItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              hiddenContent.removeAllComponents ();
              
              table.setAllowanceChargeLine (sid, originalItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          
          hiddenContent.addComponent(buttonLayout);
          
          //hiddenContent.setVisible(!hiddenContent.isVisible());
          hiddenContent.setVisible(true);          
          // TODO: PUT THIS IN FUNCTION ENDS
        }
        else {
          parent.getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }

      }
    });    
    Button deleteBtn = new Button("Delete Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot delete while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          if(table.getContainerProperty(rowId,"ID.value").getValue() != null){
            String sid = (String)table.getContainerProperty(rowId,"ID.value").getValue();
            table.removeAllowanceChargeLine (sid);
          }
        }
        else {
          parent.getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
          
        }
      }
    });    
    
    VerticalLayout buttonsContainer = new VerticalLayout();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addBtn);
    buttonsContainer.addComponent (editBtn);
    buttonsContainer.addComponent (deleteBtn);
    
    Panel outerPanel = new Panel("Allowance Charge"); 
    
    grid.addComponent(tableContainer, 0, 0);
    grid.addComponent(buttonsContainer, 1, 0);    
    
    outerPanel.addComponent (grid);
    outerLayout.addComponent(outerPanel);
   
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    
    outerLayout.addComponent(formLayout);
    // ---- HIDDEN FORM ENDS -----
    
    setLayout(outerLayout);
    grid.setSizeUndefined();
    outerPanel.requestRepaintAll();
  }

  public Form createInvoiceAllowanceChargeForm() {
    final Form invoiceAllowanceChargeForm = new Form(new FormLayout(), new InvoiceAllowanceChargeFieldFactory());
    invoiceAllowanceChargeForm.setImmediate(true);

    NestedMethodProperty mp = new NestedMethodProperty(allowanceChargeItem, "ID.value");
    if(!editMode){
      IDType num = new IDType();
      num.setValue (String.valueOf (allowanceChargeList.size ()+1));
      allowanceChargeItem.setID(num);
    }
    else {
      mp.setReadOnly (true);
    }
    
    //invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new NestedMethodProperty(allowanceChargeItem, "ID.value") );
    invoiceAllowanceChargeForm.addItemProperty ("Line ID #", mp );
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Indicator", new NestedMethodProperty(allowanceChargeItem, "indicator") );
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Reason", new NestedMethodProperty(allowanceChargeItem, "reason") );
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Amount", new NestedMethodProperty(allowanceChargeItem, "chargeAmount") );
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category ID", new NestedMethodProperty(allowanceChargeItem, "taxCategoryID") );
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category Percent", new NestedMethodProperty(allowanceChargeItem, "taxCategoryPercent") );
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category Scheme ID", new NestedMethodProperty(allowanceChargeItem, "taxCategorySchemeID") );

    return invoiceAllowanceChargeForm;
  }  
  
  private InvoiceAllowanceChargeAdapter createAllowanceChargeItem() {
    InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter();
   
    ac.setID (new IDType ());
    //ac.setChargeIndicator (new ChargeIndicatorType ());
    ac.setIndicator (false);
    //ac.setAllowanceChargeReason (new AllowanceChargeReasonType ());
    ac.setReason ("");
    //ac.setAmount (new AmountType ());
    ac.setChargeAmount (new BigDecimal(0));
    ac.setTaxCategoryID ("");
    ac.setTaxCategoryPercent (new BigDecimal(0));
    ac.setTaxCategorySchemeID ("");
    
    return ac;
  }  
  
  
  class InvoiceAllowanceChargeFieldFactory implements FormFieldFactory {

    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
        // Identify the fields by their Property ID.
        final String pid = (String) propertyId;

        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }   
}

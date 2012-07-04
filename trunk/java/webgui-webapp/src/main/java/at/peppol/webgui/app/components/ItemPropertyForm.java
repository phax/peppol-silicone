package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueType;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class ItemPropertyForm extends Panel {
  private final String itemPropertyPrefix;
  
  private List<ItemPropertyType> itemPropertyBeanList;
  private InvoiceItemPropertyAdapter itemPropertyBean;
  
  private InvoiceItemPropertyAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceItemPropertyTable table;
  private VerticalLayout hiddenContent;

  
  public ItemPropertyForm(String itemPropertyPrefix, List<ItemPropertyType> itemPropertyBeanList) {
      this.itemPropertyPrefix = itemPropertyPrefix;
      this.itemPropertyBeanList = itemPropertyBeanList;
      addMode = false;
      editMode = false;
      
      initElements();
  }
  
  private void initElements() {

    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    table = new InvoiceItemPropertyTable(itemPropertyBeanList);
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
        itemPropertyBean = createItemPropertyItem();
        
        Label formLabel = new Label("<h3>Adding new item property line</h3>", Label.CONTENT_XHTML);
        
        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent(createInvoiceItemPropertyForm());
        
        //Save new line button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent(new Button("Save item property line",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            //update table (and consequently add new item to allowanceChargeList list)
            table.addItemPropertyLine (itemPropertyBean);
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
        hiddenContent.setVisible(true);
      }
    });    
    Button editBtn = new Button("Edit Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            getParent().getWindow ().showNotification("Info", "You cannot edit while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          final String sid = (String)table.getContainerProperty(rowId,"tableLineID").getValue();
          
          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();
          
          //get selected item
          itemPropertyBean = (InvoiceItemPropertyAdapter) itemPropertyBeanList.get (table.getIndexFromID (sid));
          //clone it to original item
          originalItem = new InvoiceItemPropertyAdapter ();
          cloneInvoiceItemPropertyItem(itemPropertyBean, originalItem);
          
          Label formLabel = new Label("<h3>Editing item property line</h3>", Label.CONTENT_XHTML);
          
          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent(createInvoiceItemPropertyForm());
          
          //Save new line button
          HorizontalLayout buttonLayout = new HorizontalLayout();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              //update table (and consequently edit item to allowanceChargeList list)
              table.setItemPropertyLine (sid, itemPropertyBean);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              hiddenContent.removeAllComponents ();
              
              table.setItemPropertyLine (sid, originalItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          
          hiddenContent.addComponent(buttonLayout);
          
          hiddenContent.setVisible(true);          
          // TODO: PUT THIS IN FUNCTION ENDS
        }
        else {
          getParent ().getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }

      }
    });    
    Button deleteBtn = new Button("Delete Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            getParent ().getWindow ().showNotification("Info", "You cannot delete while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          if(table.getContainerProperty(rowId,"tableLineID").getValue() != null){
            String sid = (String)table.getContainerProperty(rowId,"tableLineID").getValue();
            table.removeItemPropertyLine (sid);
          }
        }
        else {
          getParent().getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
          
        }
      }
    });    

    
    VerticalLayout buttonsContainer = new VerticalLayout();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addBtn);
    buttonsContainer.addComponent (editBtn);
    buttonsContainer.addComponent (deleteBtn);
    
    Panel outerPanel = new Panel(itemPropertyPrefix + " Item Property"); 
    //outerPanel.setStyleName("light");     
    

   
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    
    // ---- HIDDEN FORM ENDS -----
    
    grid.setSizeUndefined();
    grid.addComponent(tableContainer, 0, 0);
    grid.addComponent(buttonsContainer, 1, 0); 
    
    outerPanel.addComponent (grid);
    outerPanel.addComponent (formLayout);
    outerLayout.addComponent(outerPanel);
    outerPanel.requestRepaintAll();
    
    VerticalLayout mainLayout = new VerticalLayout();
    final VerticalLayout showHideContentLayout = new VerticalLayout();
    showHideContentLayout.addComponent(outerPanel);
    HorizontalLayout showHideButtonLayout = new HorizontalLayout();
    Button btn = new Button("Show/Hide Additional Item Property",new Button.ClickListener(){
      @Override
      public void buttonClick (ClickEvent event) {
        // TODO Auto-generated method stub
        showHideContentLayout.setVisible(!showHideContentLayout.isVisible());
      }
    });
    showHideButtonLayout.setWidth("100%");
    showHideButtonLayout.addComponent(btn);
    showHideButtonLayout.setComponentAlignment (btn, Alignment.MIDDLE_RIGHT);
    
    mainLayout.addComponent(showHideButtonLayout);
    mainLayout.addComponent(showHideContentLayout);
    showHideContentLayout.setVisible(false);    
    
    addComponent(mainLayout);

  }  
  
  public Form createInvoiceItemPropertyForm() {
    final Form invoiceItemPropertyForm = new Form(new FormLayout(), new ItemPropertyFieldFactory());
    invoiceItemPropertyForm.setImmediate(true);

    NestedMethodProperty mp = new NestedMethodProperty(itemPropertyBean, "tableLineID");
    if(!editMode){
      itemPropertyBean.setTableLineID(String.valueOf (itemPropertyBeanList.size ()+1));
    }
    else {
      mp.setReadOnly (true);
    }
    
    //invoiceItemPropertyForm.addItemProperty ("Line ID #", new NestedMethodProperty(itemPropertyBean, "ID.value") );
    invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Name", new NestedMethodProperty(itemPropertyBean, "ItemPropertyName") );
    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Value", new NestedMethodProperty(itemPropertyBean, "ItemPropertyValue") );

    return invoiceItemPropertyForm;
  }  
  
  private InvoiceItemPropertyAdapter createItemPropertyItem() {
    InvoiceItemPropertyAdapter ac = new InvoiceItemPropertyAdapter();
    
    ac.setTableLineID ("");
    ac.setItemPropertyName ("");
    ac.setItemPropertyValue ("");
    
    return ac;
  }  
  
  private void cloneInvoiceItemPropertyItem(InvoiceItemPropertyAdapter srcItem, InvoiceItemPropertyAdapter dstItem)
  {
    dstItem.setTableLineID (srcItem.getTableLineID ());
    dstItem.setItemPropertyName (srcItem.getItemPropertyName ());
    dstItem.setItemPropertyValue (srcItem.getItemPropertyValue ());
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




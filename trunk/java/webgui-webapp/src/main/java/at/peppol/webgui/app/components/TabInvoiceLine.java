package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoicedQuantityType;

import at.peppol.webgui.app.components.TabInvoiceAllowanceCharge.InvoiceAllowanceChargeFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings ("serial")
public class TabInvoiceLine extends Form {
  private InvoiceTabForm parent;
  private List<InvoiceLineType> invoiceLineList;
  private InvoiceLineAdapter invoiceLineItem;
  
  private InvoiceLineAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceLineTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceLine(InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;
    initElements();
  }

  private void initElements() {
    invoiceLineList = parent.getInvoice().getInvoiceLine ();
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    table = new InvoiceLineTable(parent.getInvoice().getInvoiceLine ());
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
        invoiceLineItem = createInvoiceLineItem();
        
        Label formLabel = new Label("<h3>Adding new invoice line</h3>", Label.CONTENT_XHTML);
        
        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent(createInvoiceLineForm());
        
        //Save new line button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent(new Button("Save invoice line",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            //update table (and consequently add new item to invoiceList list)
            table.addInvoiceLine (invoiceLineItem);
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
          invoiceLineItem = (InvoiceLineAdapter) invoiceLineList.get (table.getIndexFromID (sid));
          //clone it to original item
          originalItem = new InvoiceLineAdapter ();
          cloneInvoiceLineItem(invoiceLineItem, originalItem);
          
          Label formLabel = new Label("<h3>Editing invoice line</h3>", Label.CONTENT_XHTML);
          
          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent(createInvoiceLineForm());
          
          //Save new line button
          HorizontalLayout buttonLayout = new HorizontalLayout();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              //update table (and consequently edit item to allowanceChargeList list)
              table.setInvoiceLine (sid, invoiceLineItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              hiddenContent.removeAllComponents ();
              
              table.setInvoiceLine (sid, originalItem);
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
            table.removeInvoiceLine (sid);
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
    
    Panel outerPanel = new Panel("Invoice Line"); 
    
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

  public Form createInvoiceLineForm() {
    final Form invoiceLineForm = new Form(new FormLayout(), new InvoiceLineFieldFactory());
    invoiceLineForm.setImmediate(false);

    NestedMethodProperty mp = new NestedMethodProperty(invoiceLineItem, "ID.value");
    if(!editMode){
      IDType num = new IDType();
      num.setValue (String.valueOf (invoiceLineList.size ()+1));
      invoiceLineItem.setID(num);
    }
    else {
      mp.setReadOnly (true);
    }
    
    //TODO: Redesign (break this function to multiple others...) the form with show/hide panels etc
    
    //invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new NestedMethodProperty(allowanceChargeItem, "ID.value") );
    invoiceLineForm.addItemProperty ("Line ID #", mp );
    invoiceLineForm.addItemProperty ("Line Note", new NestedMethodProperty(invoiceLineItem, "invLineNote") );
    invoiceLineForm.addItemProperty ("Invoiced Quantity", new NestedMethodProperty(invoiceLineItem, "invLineInvoicedQuantity") );
    invoiceLineForm.addItemProperty ("Line Extension Amount", new NestedMethodProperty(invoiceLineItem, "invLineLineExtensionAmount") );
    invoiceLineForm.addItemProperty ("Accounting Cost", new NestedMethodProperty(invoiceLineItem, "invLineAccountingCost") );
    

    return invoiceLineForm;
  }  
  
  private InvoiceLineAdapter createInvoiceLineItem() {
    InvoiceLineAdapter ac = new InvoiceLineAdapter();
   
    ac.setID (new IDType ());
    ac.setInvLineNote("");
    ac.setInvLineInvoicedQuantity (new BigDecimal(0));
    ac.setInvLineLineExtensionAmount (new BigDecimal(0));
    ac.setInvLineAccountingCost ("");

    return ac;
  }  
  
  private void cloneInvoiceLineItem(InvoiceLineAdapter srcItem, InvoiceLineAdapter dstItem)
  {
    dstItem.setInvLineID (srcItem.getInvLineID ());
    dstItem.setInvLineNote (srcItem.getInvLineNote ());
    dstItem.setInvLineInvoicedQuantity (srcItem.getInvLineInvoicedQuantity ());
    dstItem.setInvLineLineExtensionAmount (srcItem.getInvLineLineExtensionAmount ());
    dstItem.setInvLineAccountingCost (srcItem.getInvLineAccountingCost ());
  }
  
  class InvoiceLineFieldFactory implements FormFieldFactory {

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
package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
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
  
  public InvoiceAllowanceChargeTable table;
  private VerticalLayout hiddenContent;
  
  
  
  public TabInvoiceAllowanceCharge(InvoiceTabForm parent) {
    this.parent = parent;
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
    table.addListener(new Property.ValueChangeListener() {
      @Override
      public void valueChange (com.vaadin.data.Property.ValueChangeEvent event) {
        // TODO Auto-generated method stub
        //parent.getWindow ().showNotification("Info", table.getValue().toString (), Window.Notification.TYPE_HUMANIZED_MESSAGE);
        
      }
    });
    
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    //buttons Add, Edit, Delete
    Button addBtn = new Button("Add New", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        
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
            //add new item
            allowanceChargeList.add (allowanceChargeItem);
            //update table
            table.addAllowanceChargeLine (allowanceChargeItem);
            //hide form
            hiddenContent.setVisible(false);
          }
        }));
        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            hiddenContent.removeAllComponents ();
            //hide form
            hiddenContent.setVisible(false);
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
        
        //showInvLineWindow();
      }
    });    
    Button deleteBtn = new Button("Delete Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        
        //showInvLineWindow();
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

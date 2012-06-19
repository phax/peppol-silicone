package at.peppol.webgui.app.components;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import at.peppol.webgui.app.components.TabInvoicePayment.InvoicePaymentFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TabInvoiceAllowanceCharge extends Form {
  private InvoiceTabForm parent;
  
  public InvoiceAllowanceChargeTable table;
  private Window popup;
  
  public List<InvoiceLineType> items;
  
  
  
  public TabInvoiceAllowanceCharge(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    table = new InvoiceAllowanceChargeTable(parent.getInvoice().getAllowanceCharge ());
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.addStyleName ("striped strong");
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    Panel outerPanel = new Panel("Allowance Charge"); 
      
    grid.addComponent(tableContainer,0,0);
    
    //TODO: Do not use popup but "hidden" form elements...
    grid.addComponent(new Button("Add new Line", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        //Open modal window to add new invoice line
        //showInvLineWindow();
      }
    }), 1, 0);
    
    outerPanel.addComponent (grid);
    outerLayout.addComponent(outerPanel);
    setLayout(outerLayout);
    grid.setSizeUndefined();
    outerPanel.requestRepaintAll();
  }

  public Form createInvoiceAllowanceChargeTopForm() {
    final Form invoiceAllowanceChargeTopForm = new Form(new FormLayout(), new InvoiceAllowanceChargeFieldFactory());
    invoiceAllowanceChargeTopForm.setImmediate(true);
      
    /* delivery fields here...
    parent.getInvoice().setID (new IDType ());
    invoiceDeliveryTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    */
    return invoiceAllowanceChargeTopForm;
  }  
  
  @SuppressWarnings ("serial")
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

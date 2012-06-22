package at.peppol.webgui.app.components;

import com.vaadin.data.Item;
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

public class TabInvoiceTaxTotal extends Form {
  private InvoiceTabForm parent;
  
  public TabInvoiceTaxTotal(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Tax Total");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Tax Total Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTaxTotalTopForm());
    grid.addComponent(invoiceDetailsPanel, 0, 0, 3, 0);
    grid.setSizeUndefined();
     
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }
  
  
  public Form createInvoiceTaxTotalTopForm() {
    final Form invoiceTaxTotalTopForm = new Form(new FormLayout(), new InvoiceTaxTotalFieldFactory());
    invoiceTaxTotalTopForm.setImmediate(true);
      
    /* delivery fields here...
    parent.getInvoice().setID (new IDType ());
    invoiceDeliveryTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    */
    return invoiceTaxTotalTopForm;
  }  
  
  @SuppressWarnings ("serial")
  class InvoiceTaxTotalFieldFactory implements FormFieldFactory {

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
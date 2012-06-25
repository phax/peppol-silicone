package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableRoundingAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PrepaidAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxInclusiveAmountType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
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

public class TabInvoiceMonetaryTotal extends Form {
  private InvoiceTabForm parent;
  
  private MonetaryTotalType monetaryTotal;  
  
  
  public TabInvoiceMonetaryTotal(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    //monetaryTotal = parent.getInvoice().getLegalMonetaryTotal ();
    monetaryTotal = createMonetaryTotal();
    parent.getInvoice().setLegalMonetaryTotal (monetaryTotal);
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Monetary Total");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Monetary Total Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceMonetaryTotalTopForm());
    grid.addComponent(invoiceDetailsPanel, 0, 0, 3, 0);
    grid.setSizeUndefined();
     
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }
  
  private MonetaryTotalType createMonetaryTotal() {
    final MonetaryTotalType mt = new MonetaryTotalType();
    mt.setLineExtensionAmount (new LineExtensionAmountType ());
    mt.setTaxExclusiveAmount (new TaxExclusiveAmountType ());
    mt.setTaxInclusiveAmount (new TaxInclusiveAmountType ());
    mt.setAllowanceTotalAmount (new AllowanceTotalAmountType ());
    mt.setChargeTotalAmount (new ChargeTotalAmountType ());
    mt.setPrepaidAmount (new PrepaidAmountType ());
    mt.setPayableRoundingAmount (new PayableRoundingAmountType ());
    mt.setPayableAmount (new PayableAmountType ());
    return mt;
  }  
 
  public Form createInvoiceMonetaryTotalTopForm() {
    final Form invoiceMonetaryTotalTopForm = new Form(new FormLayout(), new InvoiceMonetaryTotalFieldFactory());
    invoiceMonetaryTotalTopForm.setImmediate(true);
      
    //TODO: Update fields automatically. Make them read only !
    invoiceMonetaryTotalTopForm.addItemProperty ("Line Extension Amount", new NestedMethodProperty(monetaryTotal.getLineExtensionAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Tax Exclusive Amount", new NestedMethodProperty(monetaryTotal.getTaxExclusiveAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Tax Inclusive Amount", new NestedMethodProperty(monetaryTotal.getTaxInclusiveAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Allowance Total Amount", new NestedMethodProperty(monetaryTotal.getAllowanceTotalAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Charge Total Amount", new NestedMethodProperty(monetaryTotal.getChargeTotalAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Prepaid Amount", new NestedMethodProperty(monetaryTotal.getPrepaidAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Payable Rounding Amount", new NestedMethodProperty(monetaryTotal.getPayableRoundingAmount (), "value") );
    invoiceMonetaryTotalTopForm.addItemProperty ("Payable Amount", new NestedMethodProperty(monetaryTotal.getPayableAmount (), "value") );
    
    return invoiceMonetaryTotalTopForm;
  }  
  
  @SuppressWarnings ("serial")
  class InvoiceMonetaryTotalFieldFactory implements FormFieldFactory {

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

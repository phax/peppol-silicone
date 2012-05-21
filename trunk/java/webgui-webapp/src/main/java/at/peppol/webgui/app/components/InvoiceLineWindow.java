package at.peppol.webgui.app.components;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceLineWindow extends Form {
  private final Window subwindow;
  private InvoiceForm parent;
  private InvoiceLineAdapter invln;
  private Line line;  //Auxiliary class
  
  public InvoiceLineWindow(final InvoiceForm parent) {
    this.parent = parent;
    invln = new InvoiceLineAdapter();
    line = new Line();
    subwindow = new Window("Invoice Line");
    subwindow.setModal(true);
    
    VerticalLayout layout = (VerticalLayout) subwindow.getContent();
    layout.setMargin(true);
    layout.setSpacing(true);

    Button btnClose = new Button("Close", new Button.ClickListener() {
        public void buttonClick(ClickEvent event) {
            (subwindow.getParent()).removeWindow(subwindow);
        }
    });

    Button btnSave = new Button("Save", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        //update GUI table
        
        //invln.getID().setValue(Integer.toString(44));
        //invln.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
        //invln.setSellersItemID("AF-CODE-"+(333));
        //invln.setItemDescription("Item "+552);
        //invln.getItem().getName().setValue("Item Name "+133);
        //invln.setPriceAmount(249);    
        
        /*
        invln.getID().setValue(line.lineId);
        invln.getInvoicedQuantity().setValue(BigDecimal.valueOf(line.invoicedQuantity));
        invln.setSellersItemID(line.sellersItemId);
        invln.setItemDescription(line.itemDescription);
        invln.getItem().getName().setValue(line.itemName);
        invln.setPriceAmount(line.priceAmount);    
        */
        
        parent.getTable().addInvoiceLine (invln);

      }
    });
    
    layout.addComponent(createInvoiceLineForm());
    layout.addComponent(btnSave);
    layout.addComponent(btnClose);
    layout.setComponentAlignment(btnClose, Alignment.BOTTOM_RIGHT);    
  }
  
  @Override
  public Window getWindow () {
    return this.subwindow;
  }
  
  public final Form createInvoiceLineForm() {

    final Form invoiceLineForm = new Form(new FormLayout()/*, new InvoiceFieldFactory()*/);
    invoiceLineForm.setImmediate(true);

    
    /*
    invln.getID().setValue(Integer.toString(44));
    invln.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
    invln.setSellersItemID("AF-CODE-"+(333));
    invln.setItemDescription("Item "+552);
    invln.getItem().getName().setValue("Item Name "+133);
    invln.setPriceAmount(24);    
    */
    
    invln.setPriceAmount(11);
    //parent.getInvoiceType ().setID (new IDType ());
    invoiceLineForm.addItemProperty ("lineId", new NestedMethodProperty(invln.getID (), "value") );
    //invoiceLineForm.addItemProperty ("sellersItemId", new NestedMethodProperty (invln.getSellersItemID (), "value"));
    invoiceLineForm.addItemProperty ("itemName", new NestedMethodProperty (invln.getItem ().getName (), "value"));
    //invoiceLineForm.addItemProperty ("itemDescription", new NestedMethodProperty (invln.getItemDescription (), "value"));
    invoiceLineForm.addItemProperty ("invoicedQuantity", new NestedMethodProperty (invln, "quantity"));
    //invoiceLineForm.addItemProperty ("priceAmount", new NestedMethodProperty (invln.getPriceAmount (), "value"));
    
    
    //invoiceLineForm.addItemProperty ("lineId", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    //invoiceLineForm.addItemProperty ("sellersItemId", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    //invoiceLineForm.addItemProperty ("itemName", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    //invoiceLineForm.addItemProperty ("itemDescription", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    //invoiceLineForm.addItemProperty ("invoicedQuantity", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    //invoiceLineForm.addItemProperty ("priceAmount", new NestedMethodProperty (parent.getInvoiceType ().getID (), "value"));
    
    /*
    invoiceLineForm.addItemProperty ("lineId", new NestedMethodProperty (line.lineId, "value"));
    invoiceLineForm.addItemProperty ("sellersItemId", new NestedMethodProperty (line.sellersItemId, "value"));
    invoiceLineForm.addItemProperty ("itemName", new NestedMethodProperty (line.itemName, "value"));
    invoiceLineForm.addItemProperty ("itemDescription", new NestedMethodProperty (line.itemDescription, "value"));
    invoiceLineForm.addItemProperty ("invoicedQuantity", new NestedMethodProperty (line.invoicedQuantity, "value"));
    invoiceLineForm.addItemProperty ("priceAmount", new NestedMethodProperty (line.priceAmount, "value"));
    */
    
    return invoiceLineForm;
  }  
  
  class Line {
    String lineId;
    String sellersItemId;
    String itemName;
    String itemDescription;
    long invoicedQuantity;
    long priceAmount;
      Line() {
        lineId = "";
        sellersItemId = "";
        itemName = "";
        itemDescription = "";
        invoicedQuantity = 0;
        priceAmount = 10;
      }
  }
  
}
package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;

import com.vaadin.data.Item;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceLineWindow extends Form {
  private TabInvoiceLine parent;
  
  private final Window subwindow;
 
  private InvoiceLineAdapter invln;
//  private Line line;  //Auxiliary class
  
  public InvoiceLineWindow(final TabInvoiceLine parent) {
    this.parent = parent;
    invln = new InvoiceLineAdapter();

//    line = new Line();    
//    BeanItem<InvoiceLineAdapter> invoiceLine = new BeanItem<InvoiceLineAdapter>(invln);
//    parent.setItemDataSource(invoiceLine);
//    BeanItem<Line> invoiceLine = new BeanItem<Line>(line);
//    parent.setItemDataSource(invoiceLine);
    
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
        //update GUI table !!
        ///parent.getTable().addInvoiceLine (invln);
        //update actual invoice line item
        ///parent.items.add (invln);
       
        //close popup
        (subwindow.getParent()).removeWindow(subwindow);
      }
    });
    
    try {
      layout.addComponent(createInvoiceLineForm());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    layout.addComponent(btnSave);
    layout.addComponent(btnClose);
    layout.setComponentAlignment(btnClose, Alignment.BOTTOM_RIGHT);    
  }
  
  @Override
  public Window getWindow () {
    return this.subwindow;
  }
  
  public Form createInvoiceLineForm() throws Exception {

    final Form invoiceLineForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceLineForm.setImmediate(true);
    invln = new InvoiceLineAdapter();
    
    //1. Line ID
    invln.setID (new IDType ());
    invoiceLineForm.addItemProperty ("lineId", new NestedMethodProperty(invln, "ID.value") );
    
    //2. Seller's ID
    invoiceLineForm.addItemProperty ("sellersItemId", new NestedMethodProperty (invln, "sellersItemID"));
    
    //3. Line Item's Name
    invoiceLineForm.addItemProperty ("itemName", new NestedMethodProperty (invln, "item.name.value"));
    
    //0. invoiceLineForm.addItemProperty ("notes", new NestedMethodProperty (invln, "notes"));
    
    //4. Line Item's Description
    invoiceLineForm.addItemProperty ("itemDescription", new NestedMethodProperty (invln, "itemDescription"));
    
    //5. Line Item's Quantity 
    invoiceLineForm.addItemProperty ("invoicedQuantity", new NestedMethodProperty (invln, "invoicedQuantity.value"));

    //6. Line Item's Price
    invoiceLineForm.addItemProperty ("priceAmount", new NestedMethodProperty (invln, "priceAmount"));
   
    return invoiceLineForm;
  }  
  
  class InvoiceFieldFactory implements FormFieldFactory {

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
package at.peppol.webgui.app.components;


import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;

import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class TabInvoiceLine extends Form {
  private InvoiceTabForm parent;
  
  public InvoiceLineTable table;
  private Window popup;
  
  public List<InvoiceLineType> items;

  public TabInvoiceLine (InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    items = parent.getInvoice().getInvoiceLine();
    //add new item takes place inside the popup...  no need to initiate it here...
    //it would be great for editing though ;)
    //TODO: when editing is completed remove these comments and next line
    //items.add(createInvoiceLine());
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    table = new InvoiceLineTable(parent.getInvoice().getInvoiceLine());
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.setWidth ("100%");
    table.addStyleName ("striped strong");
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    Panel outerPanel = new Panel("Invoice Lines"); 
      
    grid.addComponent(tableContainer,0,0);
    
    //TODO: Do not use popup but "hidden" form elements...
    grid.addComponent(new Button("Add new Line", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
        //Open modal window to add new invoice line
        showInvLineWindow();
      }
    }), 1, 0);
    
    tableContainer.setExpandRatio(table, 1.0f);
    
    outerPanel.addComponent (grid);
    outerLayout.addComponent(outerPanel);
    setLayout(outerLayout);
    grid.setSizeUndefined();
    outerPanel.requestRepaintAll();
    
  }  
  
  public InvoiceLineTable getTable() {
    return this.table;
  }
  
  //TODO: when editing is completed remove the following function
  /*
  private InvoiceLineType createInvoiceLine() {
    final InvoiceLineType il = new InvoiceLineType();
    il.setID(new IDType());
    il.getID().setValue("1");
    il.setInvoicedQuantity(new InvoicedQuantityType());
    il.getInvoicedQuantity().setValue(BigDecimal.ZERO);

    return il;
  }
  */
  
  
  public void showInvLineWindow() {
    popup = new InvoiceLineWindow(this).getWindow();
    popup.setResizable (true);
    popup.setHeight("420px");
    popup.setWidth("400px");
    getWindow().addWindow(popup);
               
  }   
  
}

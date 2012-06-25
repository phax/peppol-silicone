/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

/**
 *
 * @author Jerouris
 */

@SuppressWarnings ("serial")
public class InvoiceLineTable extends Table {

  private final List<InvoiceLineType> invoiceLines;
  private final BeanItemContainer<InvoiceLineAdapter> tableLines =
          new BeanItemContainer<InvoiceLineAdapter>(InvoiceLineAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  

  public InvoiceLineTable(List<InvoiceLineType> items) {
    this.invoiceLines = items;
    setContainerDataSource(tableLines);

    addPropertyWithHeader("ID.value", "# ID");
    addPropertyWithHeader("invLineItemName", "Item Name");
    addPropertyWithHeader("invLineInvoicedQuantity", "Invoiced Quantity");
    addPropertyWithHeader("invLineLineExtensionAmount", "Line Extension Amount");
    addPropertyWithHeader("InvLineTaxAmount", "Tax Total Amount");

    setDefinedPropertiesAsVisible();
    setPageLength(4);
  }

  private void addPropertyWithHeader(String property, String headerName) {
    tableLines.addNestedContainerProperty(property);
    setColumnHeader(property, headerName);
    visibleHeaderNames.add(property);
  }

  private void setDefinedPropertiesAsVisible() {
    setVisibleColumns(visibleHeaderNames.toArray());
  }

  public void addInvoiceLine(InvoiceLineAdapter invln) {
    invoiceLines.add(invln);
    tableLines.addBean(invln);   
  }

  
  public void setInvoiceLine(String lineID, InvoiceLineAdapter ln) {
    //use for editing....
    if(getIndexFromID(lineID) > -1){
      invoiceLines.set (getIndexFromID(lineID), ln);
      
      //TODO: Better way to "refresh" the table?
      //tableLines.addBean(ln);
      tableLines.removeAllItems ();
      Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
      while (iterator.hasNext()) {
        InvoiceLineType ac = iterator.next();
        tableLines.addBean ((InvoiceLineAdapter) ac);
      }
    }
  }  
  
  public void removeInvoiceLine(String lineID) {
    Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceLineType ac = iterator.next();
      if (ac.getID ().getValue ().equals (lineID)) {
        tableLines.removeItem (ac);
        invoiceLines.remove (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceLineType ac = iterator.next();
      if (ac.getID ().getValue ().equals (lineID)) {
        int index = invoiceLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }  
}

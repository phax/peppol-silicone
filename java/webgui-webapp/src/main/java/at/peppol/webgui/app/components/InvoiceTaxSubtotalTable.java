package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

@SuppressWarnings ("serial")
public class InvoiceTaxSubtotalTable extends Table {

  private final List <TaxSubtotalType> taxSubtotalLines;
  private final BeanItemContainer<InvoiceTaxSubtotalAdapter> tableLines =
          new BeanItemContainer<InvoiceTaxSubtotalAdapter>(InvoiceTaxSubtotalAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  

  public InvoiceTaxSubtotalTable(List <TaxSubtotalType> list) {
    this.taxSubtotalLines = list;
    setContainerDataSource(tableLines);

    addPropertyWithHeader("TableLineID", "# ID");
    addPropertyWithHeader("TaxSubTotalTaxableAmount", "Taxable Amount");
    addPropertyWithHeader("TaxSubTotalTaxAmount", "Tax Amount");
    addPropertyWithHeader("TaxSubTotalCategoryID", "Tax Category ID");
    addPropertyWithHeader("TaxSubTotalCategoryPercent", "Tax Category Percent");

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

  public void addTaxSubtotalLine(InvoiceTaxSubtotalAdapter invln) {
    taxSubtotalLines.add(invln);
    tableLines.addBean(invln);   
  }

  
  public void setTaxSubtotalLine(String lineID, InvoiceTaxSubtotalAdapter ln) {
    //use for editing....
    if(getIndexFromID(lineID) > -1){
      taxSubtotalLines.set (getIndexFromID(lineID), ln);
      
      //TODO: Better way to "refresh" the table?
      //tableLines.addBean(ln);
      tableLines.removeAllItems ();
      Iterator <TaxSubtotalType> iterator = taxSubtotalLines.iterator ();
      while (iterator.hasNext()) {
        TaxSubtotalType ac = iterator.next();
        tableLines.addBean ((InvoiceTaxSubtotalAdapter) ac);
      }
    }
  }  
  
  public void removeTaxSubtotalLine(String lineID) {
    Iterator <TaxSubtotalType> iterator = taxSubtotalLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceTaxSubtotalAdapter ac = (InvoiceTaxSubtotalAdapter) iterator.next();
      if (ac.getTableLineID ().equals (lineID)) {
        tableLines.removeItem (ac);
        taxSubtotalLines.remove (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator <TaxSubtotalType> iterator = taxSubtotalLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceTaxSubtotalAdapter ac = (InvoiceTaxSubtotalAdapter) iterator.next();
      if (ac.getTableLineID ().equals (lineID)) {
        int index = taxSubtotalLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }  
  
}

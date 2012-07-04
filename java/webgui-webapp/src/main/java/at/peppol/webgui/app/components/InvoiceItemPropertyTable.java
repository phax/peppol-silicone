package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

@SuppressWarnings ("serial")
public class InvoiceItemPropertyTable extends Table {

  private final List <ItemPropertyType> itemPropertyLines;
  private final BeanItemContainer<InvoiceItemPropertyAdapter> tableLines =
          new BeanItemContainer<InvoiceItemPropertyAdapter>(InvoiceItemPropertyAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  
  public InvoiceItemPropertyTable(List <ItemPropertyType> list) {
    this.itemPropertyLines = list;
    setContainerDataSource(tableLines);

    addPropertyWithHeader("TableLineID", "# ID");
    addPropertyWithHeader("ItemPropertyName", "Item Property Name");
    addPropertyWithHeader("ItemPropertyValue", "Item Property Value");

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
  
  public void addItemPropertyLine(InvoiceItemPropertyAdapter invln) {
    itemPropertyLines.add(invln);
    tableLines.addBean(invln);   
  }
  
  public void setItemPropertyLine(String lineID, InvoiceItemPropertyAdapter ln) {
    //use for editing....
    if(getIndexFromID(lineID) > -1){
      itemPropertyLines.set (getIndexFromID(lineID), ln);
      
      //TODO: Better way to "refresh" the table?
      //tableLines.addBean(ln);
      tableLines.removeAllItems ();
      Iterator <ItemPropertyType> iterator = itemPropertyLines.iterator ();
      while (iterator.hasNext()) {
        ItemPropertyType ac = iterator.next();
        tableLines.addBean ((InvoiceItemPropertyAdapter) ac);
      }
    }
  }  
  
  public void removeItemPropertyLine(String lineID) {
    Iterator <ItemPropertyType> iterator = itemPropertyLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceItemPropertyAdapter ac = (InvoiceItemPropertyAdapter) iterator.next();
      if (ac.getTableLineID ().equals (lineID)) {
        tableLines.removeItem (ac);
        itemPropertyLines.remove (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator <ItemPropertyType> iterator = itemPropertyLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceItemPropertyAdapter ac = (InvoiceItemPropertyAdapter) iterator.next();
      if (ac.getTableLineID ().equals (lineID)) {
        int index = itemPropertyLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }    
  
  
}


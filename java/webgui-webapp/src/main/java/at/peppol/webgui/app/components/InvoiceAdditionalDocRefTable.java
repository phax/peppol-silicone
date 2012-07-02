package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;



import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class InvoiceAdditionalDocRefTable extends Table {
 
  private final List <DocumentReferenceType> additionalDocRefLines;
  private final BeanItemContainer<InvoiceAdditionalDocRefAdapter> tableLines =
          new BeanItemContainer<InvoiceAdditionalDocRefAdapter>(InvoiceAdditionalDocRefAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  
  public InvoiceAdditionalDocRefTable(List <DocumentReferenceType> list) {
    this.additionalDocRefLines = list;
    setContainerDataSource(tableLines);

    addPropertyWithHeader("AdditionalDocRefID", "Additional Doc Ref Type ID");
    addPropertyWithHeader("AdditionalDocRefDocumentType", "Additional Doc Ref Type");
    //addPropertyWithHeader("AdditionalDocRefEmbeddedDocumentBinaryObject", "Embedded Doc");
    addPropertyWithHeader("AdditionalDocRefExternalReference", "External Ref URI");

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
  
  public void addAdditionalDocRefLine(InvoiceAdditionalDocRefAdapter invln) {
    additionalDocRefLines.add(invln);
    tableLines.addBean(invln);   
  }
  
  public void setAdditionalDocRefLine(String lineID, InvoiceAdditionalDocRefAdapter ln) {
    //use for editing....
    if(getIndexFromID(lineID) > -1){
      additionalDocRefLines.set (getIndexFromID(lineID), ln);
      
      //TODO: Better way to "refresh" the table?
      //tableLines.addBean(ln);
      tableLines.removeAllItems ();
      Iterator <DocumentReferenceType> iterator = additionalDocRefLines.iterator ();
      while (iterator.hasNext()) {
        DocumentReferenceType ac = iterator.next();
        tableLines.addBean ((InvoiceAdditionalDocRefAdapter) ac);
      }
    }
  }  
  
  public void removeAdditionalDocRefLine(String lineID) {
    Iterator <DocumentReferenceType> iterator = additionalDocRefLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceAdditionalDocRefAdapter ac = (InvoiceAdditionalDocRefAdapter) iterator.next();
      if (ac.getAdditionalDocRefID ().equals (lineID)) {
        tableLines.removeItem (ac);
        additionalDocRefLines.remove (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator <DocumentReferenceType> iterator = additionalDocRefLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceAdditionalDocRefAdapter ac = (InvoiceAdditionalDocRefAdapter) iterator.next();
      if (ac.getAdditionalDocRefID ().equals (lineID)) {
        int index = additionalDocRefLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }    
  
}

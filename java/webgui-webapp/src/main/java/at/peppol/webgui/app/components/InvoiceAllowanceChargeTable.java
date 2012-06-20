package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class InvoiceAllowanceChargeTable extends Table {
  private final List<AllowanceChargeType> allowanceChargeLines;
  private final BeanItemContainer<InvoiceAllowanceChargeAdapter> tableLines =
          new BeanItemContainer<InvoiceAllowanceChargeAdapter>(InvoiceAllowanceChargeAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  private int counter = 1;
  
  public InvoiceAllowanceChargeTable(List<AllowanceChargeType> items) {
    this.allowanceChargeLines = items;
    setContainerDataSource(tableLines);

    //addPropertyWithHeader("chargeIndicator", "Charge Indicator");
    addPropertyWithHeader("indicator", "Charge Indicator");
    //addPropertyWithHeader("allowanceChargeReason","Charge Reason");
    addPropertyWithHeader("reason","Charge Reason");
    //addPropertyWithHeader("amount", "Amount");
    addPropertyWithHeader("chargeAmount", "Amount");
    addPropertyWithHeader("taxCategoryID","Tax Category ID");
    addPropertyWithHeader("taxCategoryPercent","Tax Category Percent");
    addPropertyWithHeader("taxCategorySchemeID","Tax Scheme ID");
    
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
  
  public void addAllowanceChargeLine(InvoiceAllowanceChargeAdapter ln) {
    allowanceChargeLines.add(ln);
    tableLines.addBean(ln);   
  }  
}

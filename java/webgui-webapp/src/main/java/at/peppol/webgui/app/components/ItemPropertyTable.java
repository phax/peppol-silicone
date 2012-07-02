package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

public class ItemPropertyTable extends Table {

  private final List <ItemPropertyType> taxSubtotalLines;
  private final BeanItemContainer<InvoiceTaxSubtotalAdapter> tableLines =
          new BeanItemContainer<InvoiceTaxSubtotalAdapter>(InvoiceTaxSubtotalAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  
  public ItemPropertyTable(List <ItemPropertyType> list) {
    this.taxSubtotalLines = list;
    setContainerDataSource(tableLines);
/*
    addPropertyWithHeader("TableLineID", "# ID");
    addPropertyWithHeader("TaxSubTotalTaxableAmount", "Taxable Amount");
    addPropertyWithHeader("TaxSubTotalTaxAmount", "Tax Amount");
    addPropertyWithHeader("TaxSubTotalCategoryID", "Tax Category ID");
    addPropertyWithHeader("TaxSubTotalCategoryPercent", "Tax Category Percent");

    setDefinedPropertiesAsVisible();
    setPageLength(4);
*/    
  }  
}

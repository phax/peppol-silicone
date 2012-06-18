/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

/**
 *
 * @author Jerouris
 */

public class InvoiceLineTable extends Table {

    private final List<InvoiceLineType> invoiceLines;
    private final BeanItemContainer<InvoiceLineAdapter> tableLines =
            new BeanItemContainer<InvoiceLineAdapter>(InvoiceLineAdapter.class);
    private final List<String> visibleHeaderNames = new ArrayList<String>();
    private int counter = 1;

    public InvoiceLineTable(List<InvoiceLineType> items) {
        this.invoiceLines = items;
        setContainerDataSource(tableLines);

        addPropertyWithHeader("ID.value", "ID");
        addPropertyWithHeader("SellersItemID","Seller's Code");
        addPropertyWithHeader("item.name.value", "Name");
        addPropertyWithHeader("itemDescription", "Description");
        addPropertyWithHeader("quantity","Quantity");
        addPropertyWithHeader("priceAmount","Unit Price");
        
        
        setDefinedPropertiesAsVisible();

       // addInvoiceLine(createInvoiceLine());
        //addInvoiceLine(createInvoiceLine());
        //addInvoiceLine(createInvoiceLine());
        //addInvoiceLine(createInvoiceLine());
        
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

    /*
    private InvoiceLineAdapter createInvoiceLine() {
        
        
        InvoiceLineAdapter inv = new InvoiceLineAdapter();
        inv.getID().setValue(Integer.toString(counter));
        inv.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
        inv.setSellersItemID("AF-CODE-"+(110+counter));
        inv.setItemDescription("Item "+counter);
        inv.getItem().getName().setValue("Item Name "+counter);
        inv.setPriceAmount(23);
        counter++;
        return inv;
    }
    */
}

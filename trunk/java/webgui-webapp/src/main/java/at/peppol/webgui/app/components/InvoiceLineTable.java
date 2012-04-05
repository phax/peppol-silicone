/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;

/**
 *
 * @author Jerouris
 */
public class InvoiceLineTable extends Table {

    private final List<InvoiceLineType> invoiceLines;
    private final BeanItemContainer<InvoiceLineAdapter> tableLines =
            new BeanItemContainer<InvoiceLineAdapter>(InvoiceLineAdapter.class);
    private final List<String> visibleHeaderNames = new ArrayList<String>();

    public InvoiceLineTable(List<InvoiceLineType> items) {
        this.invoiceLines = items;
        setContainerDataSource(tableLines);

        addPropertyWithHeader("ID.value", "ID");
        addPropertyWithHeader("item.name.value", "Name");
        addPropertyWithHeader("itemDescription", "Description");
        addPropertyWithHeader("invoicedQuantity.value","Quantity");
        addPropertyWithHeader("priceAmount","Unit Price");
        
        setDefinedPropertiesAsVisible();

        addInvoiceLine(createInvoiceLine());
        addInvoiceLine(createInvoiceLine());
    }

    private void addPropertyWithHeader(String property, String headerName) {
        tableLines.addNestedContainerProperty(property);
        setColumnHeader(property, headerName);
        visibleHeaderNames.add(property);
    }

    private void setDefinedPropertiesAsVisible() {
        setVisibleColumns(visibleHeaderNames.toArray());
    }

    private void addInvoiceLine(InvoiceLineAdapter invln) {
        tableLines.addBean(invln);
        invoiceLines.add(invln);
    }

    private InvoiceLineAdapter createInvoiceLine() {
        InvoiceLineAdapter inv = new InvoiceLineAdapter();
        inv.getID().setValue("1");
        inv.getInvoicedQuantity().setValue(BigDecimal.valueOf(1));
        inv.setSellersItemID("Sellers ItemId");
        inv.setItemDescription("Item 1");
        inv.getItem().getName().setValue("Item Name");
        inv.setPriceAmount(23);

        return inv;
    }
}

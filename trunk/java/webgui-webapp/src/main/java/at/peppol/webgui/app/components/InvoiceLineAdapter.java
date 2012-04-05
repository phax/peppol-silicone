/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;

/**
 * An adapter class that ignores the list types found in InvoiceLineType and
 * wraps the lists with one element, that represents the first element of these
 * lists using simple Getters and Setters.
 * This is useful for making table representations.
 *
 * @author Jerouris
 */
public class InvoiceLineAdapter extends InvoiceLineType {


    public InvoiceLineAdapter() {
        
        // Initialization of required fields 
        setItem(new ItemType());
        setID(new IDType());
        getItem().setName(new NameType());
        getItem().setSellersItemIdentification(new ItemIdentificationType());
        getItem().getSellersItemIdentification().setID(new IDType());
        setInvoicedQuantity(new InvoicedQuantityType());
        
        setPrice(new PriceType());
        getPrice().setPriceAmount(new PriceAmountType());
    }
    
    public String getItemDescription() {

        if (getItem().getDescription().isEmpty()) {
            return null;
        } else {
            return getItem().getDescription().get(0).getValue();
        }
    }

    public void setItemDescription(String Description) {

        if (getItem().getDescription().isEmpty()) {
            getItem().getDescription().add(new DescriptionType());
        }
        getItem().getDescription().get(0).setValue(Description);
    }
    
    public void setSellersItemID(String id) {
        getItem().getSellersItemIdentification().getID().setValue(id);
    }
    
    public String getSellersItemID() {
        return getItem().getSellersItemIdentification().getID().getValue();
    }
    
    public void setPriceAmount(long amount)
    {
        getPrice().getPriceAmount().setValue(BigDecimal.valueOf(amount));
    }
    
    public long getPriceAmount()
    {
        return getPrice().getPriceAmount().getValue().longValue();
    }
    
}

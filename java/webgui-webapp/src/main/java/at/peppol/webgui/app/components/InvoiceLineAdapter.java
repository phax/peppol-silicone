/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
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
    private final TaxSubtotalType VATTax;


    public InvoiceLineAdapter() {
        
        // Initialization of required fields 
        setItem(new ItemType());
        setID(new IDType());
        getItem().setName(new NameType());
        getItem().setSellersItemIdentification(new ItemIdentificationType());
        getItem().getSellersItemIdentification().setID(new IDType());
        setInvoicedQuantity(new InvoicedQuantityType());
        
        // Price defaults
        setPrice(new PriceType());
        getPrice().setPriceAmount(new PriceAmountType());
        
        // Tax totals and subtotals
        getTaxTotal().add(new TaxTotalType());
        
        //0. is the VAT Tax
        VATTax = new TaxSubtotalType();
        getTaxTotal().get(0).getTaxSubtotal().add(VATTax);
        VATTax.setTaxableAmount(new TaxableAmountType());
        VATTax.setTaxAmount(new TaxAmountType());
        VATTax.setTaxCategory(new TaxCategoryType());
        VATTax.getTaxCategory().setTaxScheme(new TaxSchemeType());
        VATTax.getTaxCategory().getTaxScheme().setID(new IDType());
        VATTax.getTaxCategory().getTaxScheme().getID().setSchemeID("UN/ECE 5153");
        VATTax.getTaxCategory().getTaxScheme().getID().setSchemeAgencyID("6");
        VATTax.getTaxCategory().getTaxScheme().getID().setValue("VAT");
        VATTax.getTaxCategory().setPercent(new PercentType());
        
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

package at.peppol.webgui.app.validator.global;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATAEOtherVAT extends BaseValidation {

	String tab;
	
	public VATAEOtherVAT(InvoiceType inv) {
		super(inv);
		invoice = inv;
		ruleID = "EUGEN-T10-R016";
		errorMessage = "IF VAT = \"AE\" (reverse charge) THEN VAT MAY NOT contain other VAT categories.";
	}
	
	@Override
	public String run() {
		boolean foundVATAE = false;
		boolean foundVATnonAE = false;
		
		List<TaxSubtotalType> list1 = invoice.getTaxTotal().get(0).getTaxSubtotal();
		List<InvoiceLineType> list2 = invoice.getInvoiceLine();
		List<AllowanceChargeType> list3 = invoice.getAllowanceCharge();
		
		for (TaxSubtotalType ts : list1) {
			if (ts.getTaxCategory().getTaxScheme().getID().getValue().equals("VAT")) {  
				if (ts.getTaxCategory().getID().getValue().equals("AE")	) {
					foundVATAE = true;
				}
				else {
					foundVATnonAE = true;
					tab = "Tax Total";
				}
			}
		}
		for (InvoiceLineType line : list2) {
			if (line.getTaxTotal().get(0).getTaxSubtotal().size() > 0) {
				if (line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getTaxScheme().getID().getValue().equals("VAT")) {
					if (line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getID().getValue().equals("AE")) {
						
						foundVATAE = true;
					}
					else {
						foundVATnonAE = true;
						tab = "Invoice lines";
					}
				}
			}
		}
		
		for (AllowanceChargeType ac : list3) {
			if (ac.getTaxCategory().get(0).getTaxScheme().getID().getValue().equals("VAT")) { 
				if (ac.getTaxCategory().get(0).getID().getValue().equals("AE")) {
					foundVATAE = true;
				}
				else {
					foundVATnonAE = true;
					tab = "Allowance/Charge";
				}
			}
		}
		
		
		if (foundVATAE && foundVATnonAE) {
			return error()+" In tab '"+tab+"'";
		}
		
		return null;
	}

}

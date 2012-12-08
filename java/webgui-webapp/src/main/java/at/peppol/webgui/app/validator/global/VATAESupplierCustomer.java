package at.peppol.webgui.app.validator.global;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATAESupplierCustomer extends BaseValidation {

	public VATAESupplierCustomer(InvoiceType inv) {
		super(inv);
		invoice = inv;
		ruleID = "EUGEN-T10-R015";
		errorMessage = "IF VAT = \"AE\" (reverse charge) THEN it MUST contain Supplier VAT id and Customer VAT";
	}
	
	@Override
	public String run() {
		boolean flag = false;
		List<TaxSubtotalType> list1 = invoice.getTaxTotal().get(0).getTaxSubtotal();
		List<InvoiceLineType> list2 = invoice.getInvoiceLine();
		List<AllowanceChargeType> list3 = invoice.getAllowanceCharge();
		
		for (TaxSubtotalType ts : list1) {
			if (ts.getTaxCategory().getTaxScheme().getID().getValue().equals("VAT") && 
				ts.getTaxCategory().getID().getValue().equals("AE")	) {
				
				flag = true;
				break;
			}
		}
		if (!flag) {
			for (InvoiceLineType line : list2) {
				if (line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getTaxScheme().getID().getValue().equals("VAT") &&
					line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getID().getValue().equals("AE")) {
					
					flag = true;
					break;
				}
			}
		}
		if (!flag) {
			for (AllowanceChargeType ac : list3) {
				
				if (ac.getTaxCategory().get(0).getTaxScheme().getID().getValue().equals("VAT") && 
					ac.getTaxCategory().get(0).getID().getValue().equals("AE")) {
					
					flag = true;
					break;
				}
			}
		}
		
		if (flag) {
			if (invoice.getAccountingSupplierParty().getParty().getPartyTaxScheme().get(0).
					getCompanyID().getValue().trim().equals("") ||
				invoice.getAccountingCustomerParty().getParty().getPartyTaxScheme().get(0).
					getCompanyID().getValue().trim().equals(""))
				
				return error();
		}
		
		return null;
	}

}

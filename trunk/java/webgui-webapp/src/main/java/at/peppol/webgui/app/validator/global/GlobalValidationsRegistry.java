package at.peppol.webgui.app.validator.global;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class GlobalValidationsRegistry {
	
	private static List<BaseValidation> list = new ArrayList<BaseValidation>();
	
	public static void setMainComponent(Component c, InvoiceType inv) {
		list.add(new InvoiceLinesNumberValidation(c));
		list.add(new CrossBorderTradeValidation(inv));
		list.add(new VATTotalTaxes(inv));
		list.add(new VATTotalSupplier(inv));
	}
	
	public static List<String> runAll() {
		List<String> resList = new ArrayList<String>();
		for (int i=0;i<list.size();i++) {
			BaseValidation bv = list.get(i);
			String res = bv.run();
			if (res != null)
				resList.add(res);
		}
		
		return resList;
	}
}

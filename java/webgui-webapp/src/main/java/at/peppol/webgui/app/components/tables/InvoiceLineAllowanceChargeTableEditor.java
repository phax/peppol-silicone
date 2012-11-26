package at.peppol.webgui.app.components.tables;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Select;

import at.peppol.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

public class InvoiceLineAllowanceChargeTableEditor extends
		TableEditor<AllowanceChargeType, InvoiceAllowanceChargeAdapter> {

	public InvoiceLineAllowanceChargeTableEditor(boolean editMode) {
		super(editMode);
	}
	
	public InvoiceLineAllowanceChargeTableEditor(boolean editMode, InvoiceType inv) {
		super(editMode,inv);
	}

	@Override
	public Form createTableForm(InvoiceAllowanceChargeAdapter allowanceChargeItem,
			List<AllowanceChargeType> invoiceList) {
		
		final Form invoiceAllowanceChargeForm = new Form (new FormLayout (), new InvoiceLineAllowanceChargeFieldFactory ());
	    invoiceAllowanceChargeForm.setImmediate (true);

	    final NestedMethodProperty mp = new NestedMethodProperty (allowanceChargeItem, "ID.value");
	    if (!editMode) {
	      final IDType num = new IDType ();
	      int max = 0;
	      for (AllowanceChargeType doc : invoiceList) {
	    	  if (Integer.parseInt(doc.getID().getValue()) > max)
	    		  max = Integer.parseInt(doc.getID().getValue());
	      }
	      num.setValue(String.valueOf(max+1));
	      allowanceChargeItem.setID(num);
	    }
	    else {
	      mp.setReadOnly (true);
	    }

	    // invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new
	    // NestedMethodProperty(allowanceChargeItem, "ID.value") );
	    //invoiceAllowanceChargeForm.addItemProperty ("Line ID #", mp);
	    invoiceAllowanceChargeForm.addItemProperty ("Charge Indicator", new NestedMethodProperty (allowanceChargeItem,
	                                                                                              "indicator"));
	    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Reason",
	                                                new NestedMethodProperty (allowanceChargeItem, "reason"));
	    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Amount",
	                                                new NestedMethodProperty (allowanceChargeItem, "chargeAmount"));

	    return invoiceAllowanceChargeForm;

	}
	
	@SuppressWarnings("serial")
	static final class InvoiceLineAllowanceChargeFieldFactory implements FormFieldFactory {

	    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
	      // Identify the fields by their Property ID.
	      final String pid = (String) propertyId;

	      if ("Charge Indicator".equals (pid)) {
	        final Select indicatorSelect = new Select ("Charge or Allowance?");
	        indicatorSelect.setNullSelectionAllowed (false);
	        indicatorSelect.addItem (Boolean.TRUE);
	        indicatorSelect.addItem (Boolean.FALSE);
	        indicatorSelect.setItemCaption (Boolean.TRUE, "Charge");
	        indicatorSelect.setItemCaption (Boolean.FALSE, "Allowance");

	        return indicatorSelect;
	      }

	      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
	      if (field instanceof AbstractTextField) {
	        ((AbstractTextField) field).setNullRepresentation ("");
	      }
	      return field;
	    }
	  }

	@Override
	public InvoiceAllowanceChargeAdapter createItem() {
		InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter ();

	    //ac.setID (new IDType ());
	    ac.setIndicator (Boolean.FALSE);
	    ac.setReason ("");
	    ac.setChargeAmount (BigDecimal.ZERO);

	    ac.getAmount ().setCurrencyID (CurrencyCodeContentType.valueOf (invoice
	                                                                          .getDocumentCurrencyCode ()
	                                                                          .getValue ()));

	    return ac;

	}

	@Override
	public void cloneItem(InvoiceAllowanceChargeAdapter srcItem,
			InvoiceAllowanceChargeAdapter dstItem) {
		
		//dstItem.setAllowanceChargeID (srcItem.getAllowanceChargeID ());
	    dstItem.setIndicator (srcItem.getIndicator ());
	    dstItem.setReason (srcItem.getReason ());
	    dstItem.setChargeAmount (srcItem.getChargeAmount ());
	}

}

package at.peppol.webgui.app.validator.global;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public abstract class BaseValidation {
	protected boolean valid;
	protected String errorMessage;
	protected Component mainComponent;
	protected String ruleID;
	protected InvoiceType invoice;
	
	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public BaseValidation() {
		valid = false;
		errorMessage = "";
		mainComponent = null;
	}
	
	public BaseValidation(Component c) {
		valid = false;
		errorMessage = "";
		mainComponent = c;
	}
	
	public BaseValidation(InvoiceType inv) {
		valid = false;
		invoice = inv;
		errorMessage = "";
		mainComponent = null;
	}
	
	public Component getMainComponent() {
		return mainComponent;
	}

	public void setMainComponent(Component mainComponent) {
		this.mainComponent = mainComponent;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isValid() {
		return valid;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String error() {
		return ruleID+": "+errorMessage;
	}
	
	public abstract String run();
}

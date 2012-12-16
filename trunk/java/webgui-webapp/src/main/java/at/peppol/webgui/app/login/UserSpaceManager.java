package at.peppol.webgui.app.login;

import com.phloc.appbasics.security.user.IUser;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public abstract class UserSpaceManager<T> {
	
	T inbox,outbox,drafts;
	IUser user;
	String context;
	
	public UserSpaceManager(IUser user, String context) {
		this.user = user;
		this.context = context;
	}
	
	public String getContext() {
		return context;
	}
	
	public abstract void createUserSpaces();
	
	public abstract T getBaseUserSpace();
	
	public abstract T getInbox();
	
	public abstract T getOutbox();
	
	public abstract T getDrafts();
	
	public abstract void storeDocumentToUserSpace(InvoiceType doc, T space);
	
	public abstract InvoiceType getDocumentFromUserSpace(String docID, T space);
	
	public abstract int countItemsInSpace(T space);
}

package at.peppol.webgui.app.login;

import java.util.List;

import at.peppol.webgui.app.InvoiceBean;

import com.phloc.appbasics.security.user.IUser;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public abstract class UserFolderManager<T> {
	
	UserFolder<T> inbox,outbox,drafts;
	IUser user;
	String context;
	
	public UserFolderManager(IUser user, String context) {
		this.user = user;
		this.context = context;
	}
	
	public String getContext() {
		return context;
	}
	
	public abstract void createUserFolders();
	
	public abstract UserFolder<T> getUserRootFolder();
	
	public abstract UserFolder<T> getInbox();
	
	public abstract UserFolder<T> getOutbox();
	
	public abstract UserFolder<T> getDrafts();
	
	public abstract void storeDocumentToUserFolder(InvoiceType doc, UserFolder<T> space);
	
	public abstract InvoiceType getDocumentFromUserFolder(String docID, UserFolder<T> space);
	
	public abstract List<InvoiceBean> getInvoicesFromUserFolder(UserFolder<T> space);
	
	public abstract int countItemsInSpace(UserFolder<T> space);
}

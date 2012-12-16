package at.peppol.webgui.app.login;

import java.io.File;
import java.io.FilenameFilter;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.phloc.appbasics.security.user.IUser;

public class UserDirManager extends UserSpaceManager<File> {
	File userDir;
	String fileSep = System.getProperty("file.separator");
	String mainDirString = "users";
	
	public UserDirManager(IUser user, String context) {
		super(user, context);
	}
	
	public void createUserDir() throws Exception {
		userDir = new File(mainDirString + fileSep + user.getEmailAddress());
		if (!userDir.exists()) {
			if (!userDir.mkdirs()) {
				userDir = null;
				throw new Exception("Could not create user directory. Check privildges.");
			}
		}
	}
	
	private void create(File dir) throws Exception {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				dir = null;
				throw new Exception("Could not create directory. Check privildges.");
			}
		}
	}
	
	@Override
	public void createUserSpaces() {
		try {
			createUserDir();
			String userDirPath = userDir.getPath();
			inbox = new File(userDirPath+fileSep+context+fileSep+"inbox");
			outbox = new File(userDirPath+fileSep+context+fileSep+"outbox");
			drafts = new File(userDirPath+fileSep+context+fileSep+"drafts");
	
			create(inbox);
			create(outbox);
			create(drafts);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public File getBaseUserSpace() {
		return userDir;
	}
	
	@Override
	public void storeDocumentToUserSpace(InvoiceType doc, File space) {
		
	}
	
	@Override
	public InvoiceType getDocumentFromUserSpace(String docID, File space) {
		return null;
	}

	@Override
	public File getInbox() {
		return inbox;
	}

	@Override
	public File getOutbox() {
		return outbox;
	}

	@Override
	public File getDrafts() {
		return drafts;
	}
	
	@Override
	public int countItemsInSpace(File box) {
		int ret = 0;
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
			    return fileName.endsWith(".xml");
			}
		};
		
		if (box != null)
			ret = box.list(filter).length;
		
		return ret;
	}

}

package at.peppol.webgui.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import at.peppol.webgui.app.components.InvoiceAdditionalDocRefAdapter;

import com.vaadin.ui.Upload.Receiver;

public class ReceiverClass implements Receiver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5464317783127681079L;
	
	private String uploadDir;
	
	public ReceiverClass(){}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(uploadDir+filename);
		} catch (final java.io.FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return fos;
	}
	
	public void setUploadDir(String dir) {
		uploadDir = dir;
	}
}

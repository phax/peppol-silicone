package at.peppol.webgui.app.utils;

import at.peppol.webgui.app.login.UserSpaceManager;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.terminal.Paintable.RepaintRequestEvent;
import com.vaadin.terminal.Paintable.RepaintRequestListener;


public class CounterThread extends Thread {

	Component c;
	Application app;
	UserSpaceManager um;
	
	public CounterThread(Component c, Application app, UserSpaceManager um) {
		this.c = c;
		this.um = um;
		this.app = app;
	}
	
	public void changeDraftCount() {
		int count = um.countItemsInSpace(um.getDrafts());
		synchronized(app) {
			//String label = c.getCaption();
			//label = label.replaceFirst("[\\d]+", ""+count);
			c.setCaption("Caption: "+System.currentTimeMillis() + "  "+count);
			//c.setCaption(label);
		}
	}
	
	@Override
	public void run() {
		int count = 0;
		try {
			while (true) {
				Thread.sleep(2000);
				changeDraftCount();
				//System.out.println(c.getCaption());
				//c.getWindow().showNotification(c.getCaption());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}

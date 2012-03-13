package at.peppol.webgui.app;

import at.peppol.webgui.app.model.User;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

public class PawgApplication extends Application {
    
    private Window mainWin;
    User user;

  private void buildMainLayout () {
  
      Window main = new Window("PAWG Main"); 
      setMainWindow(main);
      mainWin = main;
  }
  
  private void buildLoginLayout() {
      
      Window login = new Window("PAWG Login"); 
     
      mainWin = login;
      HorizontalLayout h1 = new HorizontalLayout();
      h1.setSizeFull();
      //h1.setWidth("100%");

      FormLayout fl = new FormLayout();
      fl.setSizeUndefined();
      fl.addComponent(new TextField("Username:"));
      fl.addComponent(new TextField("Password:"));

      h1.addComponent(fl);
      h1.setComponentAlignment(fl, Alignment.MIDDLE_CENTER);
      login.addComponent(h1);
      setMainWindow(login);
  }
  
  @Override
  public void init () {
      if (user == null) {
        buildLoginLayout();
      } else {
          buildMainLayout();
      }
  }
}

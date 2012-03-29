/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 *
 * @author Jerouris
 */
public class LoginWindow extends Window implements Button.ClickListener {
    private PasswordField passwordField;
    private TextField usernameField;

    @Override
    public void buttonClick(ClickEvent event) {
        //usernameField.commit();
        
        try {
            PawgApp.getInstance()
                   .authenticate((String) usernameField.getValue(),
                                 (String) passwordField.getValue());
           // getWindow().showNotification("Welcome " + usernameField.getValue());
            
        } catch (Exception ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE,ex.getMessage());
            getWindow().showNotification(ex.getMessage(),Notification.TYPE_ERROR_MESSAGE);
        }
        
    }
    
    public LoginWindow()
    {
        super();
        init();
    }

    private void init() {

        HorizontalLayout h1 = new HorizontalLayout();
        h1.setSizeFull();

        FormLayout fl = new FormLayout();
        fl.setSizeUndefined();
        usernameField = new TextField("Username:");
        usernameField.setImmediate(true);
        fl.addComponent(usernameField);
        passwordField = new PasswordField("Password:");
        passwordField.setImmediate(true);
        fl.addComponent(passwordField);

        Button loginButton = new Button("Login");
        loginButton.addStyleName("default");
        loginButton.addListener(this);
        fl.addComponent(loginButton);

        h1.addComponent(fl);
        h1.setComponentAlignment(fl, Alignment.MIDDLE_CENTER);
        addComponent(h1);

    }
}

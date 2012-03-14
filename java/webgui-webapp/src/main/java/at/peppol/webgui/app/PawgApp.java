package at.peppol.webgui.app;

import at.peppol.webgui.app.model.User;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PawgApp extends Application implements HttpServletRequestListener {

    private static ThreadLocal<PawgApp> threadLocal = new ThreadLocal<PawgApp>();
    User user;

    @Override
    public void init() {
        setInstance(this);
        setTheme("peppol");
        //showLoginWindow();
        showMainAppWindow();
    }
    
    private void showLoginWindow() {
        LoginWindow win = new LoginWindow();
        setMainWindow(win);
    }
    
    private void showMainAppWindow() {
        MainWindow mainWin = new MainWindow();
        removeWindow(getMainWindow());
        setMainWindow(mainWin);
        //getMainWindow().open(new ExternalResource(mainWin.getURL()));
        Logger.getLogger(PawgApp.class.getName()).log(Level.INFO,"Called showmain");
    }

    public static PawgApp getInstance() {
        return threadLocal.get();
    }

    // Set the current application instance 	
    public static void setInstance(PawgApp application) {
        threadLocal.set(application);
    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        PawgApp.setInstance(this);
    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        threadLocal.remove();
    }

    public void authenticate(String username, String password) throws Exception {
        
        if (username.equals("peppol") && password.equals("peppol"))
            showMainAppWindow();
        else 
            throw new Exception("Login Failed");
    
    }

}

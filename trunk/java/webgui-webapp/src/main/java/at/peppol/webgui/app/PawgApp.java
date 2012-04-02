package at.peppol.webgui.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.peppol.webgui.security.AccessManager;
import at.peppol.webgui.security.login.ELoginResult;
import at.peppol.webgui.security.login.LoggedInUserManager;
import at.peppol.webgui.security.user.IUser;

import com.phloc.scopes.web.mgr.WebScopeManager;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

public class PawgApp extends Application implements HttpServletRequestListener {

    private static ThreadLocal<PawgApp> threadLocal = new ThreadLocal<PawgApp>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PawgApp.class);
    private IUser user;
    private final LoggedInUserManager lum = LoggedInUserManager.getInstance();

    @Override
    public void init() {
        // Ensure that no user is logged in
        setInstance(this);
        setTheme("peppol");
        try {
        lum.logoutCurrentUser();
            //showLoginWindow();
              startWithMainWindow();
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    private void showLoginWindow() {
        final LoginWindow win = new LoginWindow();
        setMainWindow(win);
    }

    private void showMainAppWindow() {
        final MainWindow mainWin = new MainWindow();
        if (getMainWindow()!= null)
            removeWindow(getMainWindow());
    
        setMainWindow(mainWin);
        // getMainWindow().open(new ExternalResource(mainWin.getURL()));
        LOGGER.debug("Called Showmain");
    }

    public static PawgApp getInstance() {
        return threadLocal.get();
    }

    // Set the current application instance
    public static void setInstance(final PawgApp application) {
        threadLocal.set(application);
    }

    @Override
    public void onRequestStart(final HttpServletRequest request, final HttpServletResponse response) {
        PawgApp.setInstance(this);
        WebScopeManager.onRequestBegin("pawg", request, response);
    }

    @Override
    public void onRequestEnd(final HttpServletRequest request, final HttpServletResponse response) {
        WebScopeManager.onRequestEnd();
        threadLocal.remove();
    }

    public void authenticate(final String username, final String password) throws Exception {

        final ELoginResult res = lum.loginUser(username, password);

        if (res.isSuccess()) {
            user = AccessManager.getInstance().getUserOfID(lum.getCurrentUserID());
            setUser(user);
            showMainAppWindow();


        } else {
            throw new Exception(res.toString());
        }

    }

    public void logout() {
        lum.logoutCurrentUser();
        close();
    }
    
    private void startWithMainWindow() throws Exception {
        user = AccessManager.getInstance()
                            .getUserOfEmailAddress("user@peppol.eu");
            setUser(user);
            showMainAppWindow();
    }
    
    @Override
    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
                w = new MainWindow(); // it's best to have separate classes for your windows
                w.setName(name);
                addWindow(w);
        }
        return w;
    }
}

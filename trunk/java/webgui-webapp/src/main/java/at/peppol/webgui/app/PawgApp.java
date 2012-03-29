package at.peppol.webgui.app;

import ap.peppol.webgui.security.AccessManager;
import ap.peppol.webgui.security.login.ELoginResult;
import ap.peppol.webgui.security.login.LoggedInUserManager;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ap.peppol.webgui.security.user.IUser;

import com.phloc.scopes.web.mgr.WebScopeManager;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;

public class PawgApp extends Application implements HttpServletRequestListener {

  private static ThreadLocal <PawgApp> threadLocal = new ThreadLocal <PawgApp> ();
  private IUser user;
  private LoggedInUserManager lum = LoggedInUserManager.getInstance();
  
  @Override
  public void init () {
    setInstance (this);
    setTheme ("peppol");
    
    showLoginWindow();
    //showMainAppWindow ();
  }

  private void showLoginWindow () {
    final LoginWindow win = new LoginWindow ();
    setMainWindow (win);
  }

  private void showMainAppWindow () {
    final MainWindow mainWin = new MainWindow ();
    removeWindow (getMainWindow ());
    setMainWindow (mainWin);
    // getMainWindow().open(new ExternalResource(mainWin.getURL()));
    Logger.getLogger (PawgApp.class.getName ()).log (Level.INFO, "Called showmain");
  }

  public static PawgApp getInstance () {
    return threadLocal.get ();
  }

  // Set the current application instance
  public static void setInstance (final PawgApp application) {
    threadLocal.set (application);
  }

  @Override
  public void onRequestStart (final HttpServletRequest request, final HttpServletResponse response) {
    PawgApp.setInstance (this);
    WebScopeManager.onRequestBegin ("pawg", request, response);
  }

  @Override
  public void onRequestEnd (final HttpServletRequest request, final HttpServletResponse response) {
    WebScopeManager.onRequestEnd();
    threadLocal.remove();
  }

  public void authenticate (final String username, final String password) throws Exception {

      
      ELoginResult res = lum.loginUser(username, password);

    if (res.isSuccess())
    {
       showMainAppWindow ();
       user = AccessManager.getInstance().getUserOfID(lum.getCurrentUserID());
       
    } else {
      throw new Exception (res.toString());
    }

  }

}

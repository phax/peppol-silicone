package at.peppol.webgui.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * A special scope listener, that correctly manages global and session scopes
 * 
 * @author philip
 */
public final class ScopeListener implements ServletContextListener, HttpSessionListener {
    @Override
  public void contextInitialized (final ServletContextEvent sce) {
    WebScopeManager.onGlobalBegin (sce.getServletContext ());
  }

    @Override
  public void contextDestroyed (final ServletContextEvent sce) {
    WebScopeManager.onGlobalEnd ();
  }

    @Override
  public void sessionCreated (final HttpSessionEvent se) {
    WebScopeManager.onSessionBegin (se.getSession ());
  }

    @Override
  public void sessionDestroyed (final HttpSessionEvent se) {
    WebScopeManager.onSessionEnd (se.getSession ());
  }
}

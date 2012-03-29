package at.peppol.webgui.app;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import ap.peppol.webgui.api.io.StorageIO;

import com.phloc.commons.GlobalDebug;
import com.phloc.scopes.web.mgr.WebScopeManager;

/**
 * A special scope listener, that correctly manages global and session scopes
 * 
 * @author philip
 */
public final class ScopeListener implements ServletContextListener, HttpSessionListener {
  public static final String INIT_PARAMETER_TRACE = "trace";
  // like Vaadin:
  public static final String INIT_PARAMETER_DEBUG = "Debug";
  // like Vaadin:
  public static final String INIT_PARAMETER_PRODUCTION = "productionMode";
  public static final String INIT_PARAMETER_STORAGE_BASE = "storage-base";

  @Override
  public void contextInitialized (final ServletContextEvent sce) {
    final ServletContext aSC = sce.getServletContext ();
    // set global debug/trace mode
    final boolean bTraceMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_TRACE));
    final boolean bDebugMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_DEBUG));
    final boolean bProductionMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_PRODUCTION));
    GlobalDebug.setTraceModeDirect (bTraceMode);
    GlobalDebug.setDebugModeDirect (bDebugMode);
    GlobalDebug.setProductionModeDirect (bProductionMode);

    // Set the storage base
    StorageIO.initBasePath (aSC.getInitParameter (INIT_PARAMETER_STORAGE_BASE));

    // Init the global context
    WebScopeManager.onGlobalBegin (aSC);
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

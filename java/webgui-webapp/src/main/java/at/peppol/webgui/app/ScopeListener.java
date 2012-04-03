package at.peppol.webgui.app;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import at.peppol.webgui.io.StorageIO;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.idfactory.FileIntIDFactory;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.factory.DefaultWebScopeFactory;
import com.phloc.scopes.web.impl.RequestWebScopeNoFileItems;
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

    // Init the unique ID provider
    GlobalIDFactory.setPersistentIntIDFactory (new FileIntIDFactory (StorageIO.getFile ("id.txt")));

    // Set the non-multipart file item request web scope
    MetaScopeFactory.setWebScopeFactory (new DefaultWebScopeFactory () {
      @Override
      @Nonnull
      public IRequestWebScope createRequestScope (@Nonnull final HttpServletRequest aHttpRequest,
                                                  @Nonnull final HttpServletResponse aHttpResponse) {
        return new RequestWebScopeNoFileItems (aHttpRequest, aHttpResponse);
      }
    });

    // Init the global scope
    WebScopeManager.onGlobalBegin (aSC);
  }

  @Override
  public void contextDestroyed (final ServletContextEvent sce) {
    // Destroy global scope
    WebScopeManager.onGlobalEnd ();
  }

  @Override
  public void sessionCreated (final HttpSessionEvent se) {
    // Create session scope
    WebScopeManager.onSessionBegin (se.getSession ());
  }

  @Override
  public void sessionDestroyed (final HttpSessionEvent se) {
    // Destroy session scope
    WebScopeManager.onSessionEnd (se.getSession ());
  }
}

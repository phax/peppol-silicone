package at.peppol.webgui.app;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import at.peppol.webgui.security.CSecurity;

import com.phloc.appbasics.app.WebFileIO;
import com.phloc.appbasics.security.AccessManager;
import com.phloc.appbasics.security.role.RoleManager;
import com.phloc.appbasics.security.user.UserManager;
import com.phloc.appbasics.security.usergroup.UserGroupManager;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.idfactory.FileIntIDFactory;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.scopes.MetaScopeFactory;
import com.phloc.scopes.web.domain.IRequestWebScope;
import com.phloc.scopes.web.factory.DefaultWebScopeFactory;
import com.phloc.scopes.web.impl.RequestWebScopeNoMultipart;
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

  private static void _initAccessManager () {
    // Call before accessing AccessManager!
    RoleManager.setCreateDefaults (false);
    UserManager.setCreateDefaults (false);
    UserGroupManager.setCreateDefaults (false);

    final AccessManager aAM = AccessManager.getInstance ();

    // Users
    if (!aAM.containsUserWithID (CSecurity.USER_ADMINISTRATOR_ID))
      aAM.createPredefinedUser (CSecurity.USER_ADMINISTRATOR_ID,
                                CSecurity.USER_ADMINISTRATOR_EMAIL,
                                CSecurity.USER_ADMINISTRATOR_EMAIL,
                                CSecurity.USER_ADMINISTRATOR_PASSWORD,
                                CSecurity.USER_ADMINISTRATOR_NAME,
                                null,
                                null,
                                null);
    if (!aAM.containsUserWithID (CSecurity.USER_USER_ID))
      aAM.createPredefinedUser (CSecurity.USER_USER_ID,
                                CSecurity.USER_USER_EMAIL,
                                CSecurity.USER_USER_EMAIL,
                                CSecurity.USER_USER_PASSWORD,
                                CSecurity.USER_USER_NAME,
                                null,
                                null,
                                null);
    if (!aAM.containsUserWithID (CSecurity.USER_GUEST_ID))
      aAM.createPredefinedUser (CSecurity.USER_GUEST_ID,
                                CSecurity.USER_GUEST_EMAIL,
                                CSecurity.USER_GUEST_EMAIL,
                                CSecurity.USER_GUEST_PASSWORD,
                                CSecurity.USER_GUEST_NAME,
                                null,
                                null,
                                null);

    // Roles
    if (!aAM.containsRoleWithID (CSecurity.ROLE_ADMINISTRATOR_ID))
      aAM.createPredefinedRole (CSecurity.ROLE_ADMINISTRATOR_ID, CSecurity.ROLE_ADMINISTRATOR_NAME);
    if (!aAM.containsRoleWithID (CSecurity.ROLE_USER_ID))
      aAM.createPredefinedRole (CSecurity.ROLE_USER_ID, CSecurity.ROLE_USER_NAME);

    // User groups
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_ADMINISTRATORS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USERGROUP_ADMINISTRATORS_NAME);
      aAM.assignUserToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USER_ADMINISTRATOR_ID);
      aAM.assignRoleToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.ROLE_ADMINISTRATOR_ID);
    }
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_USERS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_USERS_ID, CSecurity.USERGROUP_USERS_NAME);
      aAM.assignRoleToUserGroup (CSecurity.USERGROUP_USERS_ID, CSecurity.ROLE_USER_ID);
    }
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_GUESTS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_GUESTS_ID, CSecurity.USERGROUP_GUESTS_NAME);
    }
  }

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
    WebFileIO.initBasePath (aSC.getInitParameter (INIT_PARAMETER_STORAGE_BASE));

    // Init the unique ID provider
    GlobalIDFactory.setPersistentIntIDFactory (new FileIntIDFactory (WebFileIO.getFile ("id.txt")));

    // Set the non-multipart file item request web scope
    MetaScopeFactory.setWebScopeFactory (new DefaultWebScopeFactory () {
      @Override
      @Nonnull
      public IRequestWebScope createRequestScope (@Nonnull final HttpServletRequest aHttpRequest,
                                                  @Nonnull final HttpServletResponse aHttpResponse) {
        return new RequestWebScopeNoMultipart (aHttpRequest, aHttpResponse);
      }
    });

    // Init the global scope
    WebScopeManager.onGlobalBegin (aSC);

    _initAccessManager ();
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

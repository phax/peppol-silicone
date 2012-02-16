package at.peppol.webgui.app.servlet;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.peppol.webgui.app.login.WebGuiLoginManager;

import com.phloc.commons.state.EContinue;
import com.phloc.webbasics.app.security.LoginManager;
import com.phloc.webbasics.servlet.AbstractScopeAwareFilter;

public final class WebGuiLoginFilter extends AbstractScopeAwareFilter {
  private LoginManager m_aLogin;

  @Override
  public void init (final FilterConfig aFilterConfig) throws ServletException {
    m_aLogin = new WebGuiLoginManager ();
  }

  @Override
  @Nonnull
  protected EContinue doFilter (final HttpServletRequest aHttpRequest, final HttpServletResponse aHttpResponse) throws IOException,
                                                                                                               ServletException {
    return m_aLogin.checkUserAndShowLogin (aHttpRequest, aHttpResponse);
  }
}

/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.registry.smp.hook;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.state.ESuccess;

/**
 * Filter which handles post-registration hooks. If a registration was started
 * in {@link AbstractRegistrationHook}, this filter will make sure the
 * registration is ended by calling
 * {@link AbstractRegistrationHook#postUpdate(ESuccess)}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class PostRegistrationFilter implements Filter {
  private static final class ResponseWrapper extends HttpServletResponseWrapper {
    private int m_nStatus = HttpServletResponse.SC_OK;

    public ResponseWrapper (final HttpServletResponse response) {
      super (response);
    }

    @Override
    public void sendError (final int sc) throws IOException {
      super.sendError (sc);
      this.m_nStatus = sc;
    }

    @Override
    public void sendError (final int sc, final String msg) throws IOException {
      super.sendError (sc, msg);
      this.m_nStatus = sc;
    }

    @Override
    public void setStatus (final int sc) {
      super.setStatus (sc);
      this.m_nStatus = sc;
    }

    @Override
    public void setStatus (final int sc, final String sm) {
      super.setStatus (sc, sm);
      this.m_nStatus = sc;
    }

    public int getStatus () {
      return m_nStatus;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (PostRegistrationFilter.class);

  public void init (final FilterConfig arg0) {}

  private static void _notify (@Nonnull final ESuccess eSuccess) throws ServletException {
    final AbstractRegistrationHook q = AbstractRegistrationHook.getQueue ();
    if (q == null)
      return;

    try {
      q.postUpdate (eSuccess);
    }
    catch (final HookException e) {
      throw new ServletException (e);
    }
  }

  public void doFilter (final ServletRequest req, final ServletResponse res, final FilterChain fc) throws IOException,
                                                                                                  ServletException {
    final ResponseWrapper r = new ResponseWrapper ((HttpServletResponse) res);
    try {
      fc.doFilter (req, r);

      if (r.getStatus () >= 400) {
        s_aLogger.debug ("Operation failed, status: " + r.getStatus ());
        _notify (ESuccess.FAILURE);
      }
      else {
        s_aLogger.debug ("Operation ok, status: " + r.getStatus ());
        _notify (ESuccess.SUCCESS);
      }
    }
    catch (final IOException e) {
      s_aLogger.debug ("Got exception " + e);
      _notify (ESuccess.FAILURE);
      throw e;
    }
    catch (final ServletException e) {
      s_aLogger.debug ("Got exception " + e);
      _notify (ESuccess.FAILURE);
      throw e;
    }
    catch (final RuntimeException e) {
      s_aLogger.debug ("Got runtime exception " + e);
      _notify (ESuccess.FAILURE);
      throw e;
    }
  }

  public void destroy () {}
}

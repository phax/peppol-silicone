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
package at.peppol.sml.server.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.sml.server.security.ClientUniqueIDProvider;
import at.peppol.sml.server.security.PeppolClientCertificateValidator;

/**
 * This servlet filter checks each request for a valid client certificate.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ClientCertificateValidationFilter implements Filter {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ClientCertificateValidationFilter.class);

  public void init (final FilterConfig filterConfig) throws ServletException {}

  public void doFilter (final ServletRequest aRequest, final ServletResponse aResponse, final FilterChain chain) throws IOException,
                                                                                                                ServletException {
    final HttpServletRequest aHttpRequest = (HttpServletRequest) aRequest;

    String sClientUniqueID;
    if ("https".equalsIgnoreCase (aHttpRequest.getScheme ())) {
      // Check the client certificate
      if (!PeppolClientCertificateValidator.isClientCertificateValid (aHttpRequest))
        throw new ServletException ("Invalid client certificate passed!");

      // Extract the client unique ID and set it into the request
      sClientUniqueID = ClientUniqueIDProvider.getClientUniqueID (aHttpRequest);
      if (sClientUniqueID == null)
        throw new ServletException ("Error in unique ID from certficate extraction!");
    }
    else {
      // Can only occur when using the http version in the BRZ internal LAN
      sClientUniqueID = "debug-insecure-client-http-only";
    }

    WebRequestClientIdentifier.setClientUniqueID (aHttpRequest, sClientUniqueID);
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Client with ID '" + sClientUniqueID + "' acknowledged");

    // Next filter
    chain.doFilter (aRequest, aResponse);
  }

  public void destroy () {}
}

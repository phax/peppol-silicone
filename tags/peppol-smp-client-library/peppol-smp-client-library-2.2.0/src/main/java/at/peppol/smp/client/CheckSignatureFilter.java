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
package at.peppol.smp.client;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import at.peppol.smp.client.exception.NoSignatureFoundException;

import com.phloc.commons.io.streams.NonBlockingByteArrayInputStream;
import com.phloc.commons.io.streams.StreamUtils;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;


/**
 * This Jersey filter checks if a signature is applied to each response object.
 * TODO: We currently do not check that the certificate is trusted by someone we
 * trust.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CheckSignatureFilter extends ClientFilter {
  private static boolean _checkSignature (@Nonnull @WillClose final InputStream aEntityInputStream) throws Exception {
    try {
      // Get response from servlet
      final DocumentBuilderFactory aFactory = DocumentBuilderFactory.newInstance ();
      aFactory.setNamespaceAware (true);

      final DocumentBuilder builder = aFactory.newDocumentBuilder ();
      final Document aDocument = builder.parse (new InputSource (aEntityInputStream));

      // We make sure that the XML is a Signed.., if not, we don't have to check
      // any certificates.

      // Find Signature element.
      final NodeList aNodeList = aDocument.getElementsByTagNameNS (XMLSignature.XMLNS, "Signature");
      if (aNodeList == null || aNodeList.getLength () == 0)
        throw new NoSignatureFoundException ();

      // Create a DOMValidateContext and specify a KeySelector
      // and document context.
      final X509KeySelector aKeySelector = new X509KeySelector ();
      final DOMValidateContext aValidateContext = new DOMValidateContext (aKeySelector, aNodeList.item (0));
      final XMLSignatureFactory aSignatureFactory = XMLSignatureFactory.getInstance ("DOM");

      // Unmarshal the XMLSignature.
      final XMLSignature aSignature = aSignatureFactory.unmarshalXMLSignature (aValidateContext);

      // Validate the XMLSignature.
      final boolean bCoreValidity = aSignature.validate (aValidateContext);
      return bCoreValidity;
    }
    finally {
      // Close the input stream
      StreamUtils.close (aEntityInputStream);
    }
  }

  @Override
  public ClientResponse handle (final ClientRequest aClientRequest) throws ClientHandlerException {
    // Call the next client handler in the filter chain
    final ClientHandler aNextHandler = super.getNext ();
    final ClientResponse aClientResponse = aNextHandler.handle (aClientRequest);

    // Check for a positive result
    if (aClientRequest.getMethod ().equals ("GET") &&
        aClientResponse.getClientResponseStatus ().equals (ClientResponse.Status.OK)) {
      aClientResponse.bufferEntity ();
      final InputStream aResponseInStream = aClientResponse.getEntityInputStream ();

      /*
       * Store the response internally
       */
      final byte [] aResponseBytes = StreamUtils.getAllBytes (aResponseInStream);
      if (aResponseBytes == null)
        throw new ClientHandlerException ("Could not read entity");

      try {
        // Check the signature
        if (!_checkSignature (new NonBlockingByteArrayInputStream (aResponseBytes)))
          throw new ClientHandlerException ("Signature was not valid");
      }
      catch (final NoSignatureFoundException e) {
        throw new ClientHandlerException ("No signature found", e);
      }
      catch (final Exception ex) {
        throw new ClientHandlerException ("Error in validating signature", ex);
      }

      // Create a new stream such that it can be read by application code.
      aClientResponse.setEntityInputStream (new NonBlockingByteArrayInputStream (aResponseBytes));
    }

    return aClientResponse;
  }
}

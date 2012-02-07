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
package eu.peppol.registry.smp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.io.streams.NonBlockingByteArrayInputStream;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;


/**
 * This class adds a XML DSIG to successful GET's for SignedServiceMetadata
 * objects.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SignatureFilter implements ContainerResponseFilter {

  private KeyStore.PrivateKeyEntry m_aKeyEntry;
  private X509Certificate m_aCert;

  public SignatureFilter () {
    // Load the KeyStore and get the signing key and certificate.
    try {
      final ConfigFile aConfigFile = ConfigFile.getInstance ();
      final String sKeyStoreClassPath = aConfigFile.getString ("xmldsig.keystore.classpath");
      final String sKeyStorePassword = aConfigFile.getString ("xmldsig.keystore.password");
      final String sKeyStoreKeyAlias = aConfigFile.getString ("xmldsig.keystore.key.alias");
      final String sKeyStoreKeyPassword = aConfigFile.getString ("xmldsig.keystore.key.password");

      final KeyStore ks = KeyStoreUtils.loadKeyStoreFromClassPath (sKeyStoreClassPath, sKeyStorePassword);
      m_aKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry (sKeyStoreKeyAlias,
                                                            new KeyStore.PasswordProtection (sKeyStoreKeyPassword.toCharArray ()));
      if (m_aKeyEntry == null)
        throw new IllegalStateException ("Failed to find key '" +
                                         sKeyStoreKeyAlias +
                                         "' in keystore '" +
                                         sKeyStorePassword +
                                         "'. Does the alias exist? Is the password correct?");

      m_aCert = (X509Certificate) m_aKeyEntry.getCertificate ();
    }
    catch (final Exception e) {
      throw new ExceptionInInitializerError (e);
    }
  }

  public ContainerResponse filter (final ContainerRequest request, final ContainerResponse response) {
    /*
     * Make sure that the signature is only added to GET/OK on service metadata.
     */
    if (request.getMethod ().equals ("GET") && response.getResponse ().getStatus () == Status.OK.getStatusCode ()) {
      final int index = request.getPath (false).indexOf ("/services/");
      if (index != -1) {
        if (request.getPath (false).length () > index + "/services/".length ()) {
          response.setContainerResponseWriter (new BufferedAdapter (response.getContainerResponseWriter ()));
        }
      }
    }

    return response;
  }

  private final class BufferedAdapter implements ContainerResponseWriter {
    private final ContainerResponseWriter m_aCRW;
    private ByteArrayOutputStream m_aBAOS;
    private ContainerResponse m_aResponse;

    BufferedAdapter (final ContainerResponseWriter crw) {
      m_aCRW = crw;
    }

    public OutputStream writeStatusAndHeaders (final long contentLength, final ContainerResponse response) throws IOException {
      m_aResponse = response;
      return m_aBAOS = new ByteArrayOutputStream ();
    }

    public void finish () throws IOException {

      final byte [] content = m_aBAOS.toByteArray ();
      final OutputStream out = m_aCRW.writeStatusAndHeaders (-1, m_aResponse);

      // Do security work here wrapping content and writing out
      // XMLDSIG stuff to out
      Document document;
      try {
        // Get response from servlet
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
        factory.setNamespaceAware (true);
        final DocumentBuilder builder = factory.newDocumentBuilder ();
        document = builder.parse (new InputSource (new NonBlockingByteArrayInputStream (content)));
      }
      catch (final Exception e) {
        throw new RuntimeException ("Error in parsing xml", e);
      }

      final Element rootElement = document.getDocumentElement ();

      try {
        _signXML (rootElement);

        out.write (_doc2bytes (rootElement.getParentNode ()));
      }
      catch (final Exception e) {
        throw new RuntimeException ("Error in signing xml", e);
      }
    }

    private byte [] _doc2bytes (final Node node) {
      try {
        final Source source = new DOMSource (node);
        final NonBlockingByteArrayOutputStream out = new NonBlockingByteArrayOutputStream ();
        final Result result = new StreamResult (out);
        final TransformerFactory factory = TransformerFactory.newInstance ();
        final Transformer transformer = factory.newTransformer ();
        transformer.transform (source, result);
        return out.toByteArray ();
      }
      catch (final Exception e) {
        throw new RuntimeException (e);
      }
    }

    private void _signXML (final Element aElementToSign) throws NoSuchAlgorithmException,
                                                        InvalidAlgorithmParameterException,
                                                        MarshalException,
                                                        XMLSignatureException {
      // Create a DOM XMLSignatureFactory that will be used to
      // generate the enveloped signature.
      final XMLSignatureFactory aSignatureFactory = XMLSignatureFactory.getInstance ("DOM");

      // Create a Reference to the enveloped document (in this case,
      // you are signing the whole document, so a URI of "" signifies
      // that, and also specify the SHA1 digest algorithm and
      // the ENVELOPED Transform)
      final Reference aReference = aSignatureFactory.newReference ("",
                                                                   aSignatureFactory.newDigestMethod (DigestMethod.SHA1,
                                                                                                      null),
                                                                   Collections.singletonList (aSignatureFactory.newTransform (Transform.ENVELOPED,
                                                                                                                              (TransformParameterSpec) null)),
                                                                   null,
                                                                   null);

      // Create the SignedInfo.
      final SignedInfo aSingedInfo = aSignatureFactory.newSignedInfo (aSignatureFactory.newCanonicalizationMethod (CanonicalizationMethod.INCLUSIVE,
                                                                                                                   (C14NMethodParameterSpec) null),
                                                                      aSignatureFactory.newSignatureMethod (SignatureMethod.RSA_SHA1,
                                                                                                            null),
                                                                      Collections.singletonList (aReference));

      // Create the KeyInfo containing the X509Data.
      final KeyInfoFactory aKeyInfoFactory = aSignatureFactory.getKeyInfoFactory ();
      final List <Object> aX509Content = new ArrayList <Object> ();
      aX509Content.add (m_aCert.getSubjectX500Principal ().getName ());
      aX509Content.add (m_aCert);
      final X509Data aX509Data = aKeyInfoFactory.newX509Data (aX509Content);
      final KeyInfo aKeyInfo = aKeyInfoFactory.newKeyInfo (Collections.singletonList (aX509Data));

      // Create a DOMSignContext and specify the RSA PrivateKey and
      // location of the resulting XMLSignature's parent element.
      final DOMSignContext dsc = new DOMSignContext (m_aKeyEntry.getPrivateKey (), aElementToSign);

      // Create the XMLSignature, but don't sign it yet.
      final XMLSignature signature = aSignatureFactory.newXMLSignature (aSingedInfo, aKeyInfo);

      // Marshal, generate, and sign the enveloped signature.
      signature.sign (dsc);
    }
  }
}

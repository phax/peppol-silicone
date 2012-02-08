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
package at.peppol.sml.server.management;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.xml.transform.XMLTransformerFactory;


/**
 * Adds an XMLDSIG to every response.<br>
 * Note: referenced from src/main/resources/handlers.xml
 *
 * @author ravnholdt
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class AddSignatureHandler implements LogicalHandler <LogicalMessageContext> {
  private static final Logger log = LoggerFactory.getLogger (AddSignatureHandler.class);

  @Override
  public void close (final MessageContext context) {}

  @Override
  public boolean handleFault (final LogicalMessageContext context) {
    return true;
  }

  @Nonnull
  private static DOMSource _convertToDOMSource (final Source aSource) throws TransformerException {
    final DOMResult aResult = new DOMResult ();
    XMLTransformerFactory.createTransformerFactory (null, null).newTransformer ().transform (aSource, aResult);
    return new DOMSource (aResult.getNode ());
  }

  @Override
  public boolean handleMessage (final LogicalMessageContext context) {
    // Returns if the message is an inbound message.
    final Boolean isOutbound = (Boolean) context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    if (isOutbound.equals (Boolean.FALSE))
      return true;

    final LogicalMessage message = context.getMessage ();
    Source aSource = message.getPayload ();
    if (aSource == null)
      return false;

    boolean bNewPayload = false;
    if (!(aSource instanceof DOMSource)) {
      // Ensure we have a DOM source present
      log.info ("Converting " + aSource + " to DOMSource");
      try {
        aSource = _convertToDOMSource (aSource);
        bNewPayload = true;
      }
      catch (final TransformerException ex) {
        log.error ("Failed to convert source " + aSource + " to DOMSource", ex);
        return false;
      }
    }

    final Node rootNode = ((DOMSource) aSource).getNode ();
    final Element rootElement = ((Document) rootNode).getDocumentElement ();

    try {
      _signXML (rootElement);
    }
    catch (final Exception e) {
      log.error ("Error in signing xml", e);
      return false;
    }

    if (bNewPayload) {
      // Because we converted the payload, we need to re-assign it!
      message.setPayload (aSource);
    }

    return true;
  }

  private static void _signXML (final Element xmlElementToSign) throws Exception {
    // Create a DOM XMLSignatureFactory that will be used to
    // generate the enveloped signature.
    final XMLSignatureFactory fac = XMLSignatureFactory.getInstance ("DOM");

    // Create a Reference to the enveloped document (in this case,
    // you are signing the whole document, so a URI of "" signifies
    // that, and also specify the SHA1 digest algorithm and
    // the ENVELOPED Transform.
    final Reference ref = fac.newReference ("",
                                            fac.newDigestMethod (DigestMethod.SHA1, null),
                                            Collections.singletonList (fac.newTransform (Transform.ENVELOPED,
                                                                                         (TransformParameterSpec) null)),
                                            null,
                                            null);

    // Create the SignedInfo.
    final SignedInfo si = fac.newSignedInfo (fac.newCanonicalizationMethod (CanonicalizationMethod.INCLUSIVE,
                                                                            (C14NMethodParameterSpec) null),
                                             fac.newSignatureMethod (SignatureMethod.RSA_SHA1, null),
                                             Collections.singletonList (ref));

    // Load the KeyStore and get the signing key and certificate.
    final String sKeyStorePath = ConfigFile.getInstance ().getString ("sml.keystore.path");
    final String sKeyStorePW = ConfigFile.getInstance ().getString ("sml.keystore.password");
    final String sKeyStoreAlias = ConfigFile.getInstance ().getString ("sml.keystore.alias");
    final KeyStore ks = KeyStoreUtils.loadKeyStoreFromClassPath (sKeyStorePath, sKeyStorePW);
    final KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry (sKeyStoreAlias,
                                                                                      new KeyStore.PasswordProtection (sKeyStorePW.toCharArray ()));
    final X509Certificate cert = (X509Certificate) keyEntry.getCertificate ();

    // Create the KeyInfo containing the X509Data.
    final KeyInfoFactory kif = fac.getKeyInfoFactory ();
    final List <Object> x509Content = new ArrayList <Object> ();
    x509Content.add (cert.getSubjectX500Principal ().getName ());
    x509Content.add (cert);
    final X509Data xd = kif.newX509Data (x509Content);
    final KeyInfo ki = kif.newKeyInfo (Collections.singletonList (xd));

    // Create a DOMSignContext and specify the RSA PrivateKey and
    // location of the resulting XMLSignature's parent element.
    final DOMSignContext dsc = new DOMSignContext (keyEntry.getPrivateKey (), xmlElementToSign);

    // Create the XMLSignature, but don't sign it yet.
    final XMLSignature signature = fac.newXMLSignature (si, ki);

    // Marshal, generate, and sign the enveloped signature.
    signature.sign (dsc);
  }
}

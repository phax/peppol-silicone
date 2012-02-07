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
package org.busdox.transport.start.client;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.bind.annotation.XmlSchema;
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
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;

import org.busdox.transport.config.StartClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2000._09.xmldsig.ObjectFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.phloc.commons.annotations.VisibleForTesting;
import com.phloc.commons.io.resource.ClassPathResource;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.callback.SAMLCallback;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AuthnContext;
import com.sun.xml.wss.saml.AuthnStatement;
import com.sun.xml.wss.saml.Conditions;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.Subject;
import com.sun.xml.wss.saml.SubjectConfirmation;
import com.sun.xml.wss.saml.SubjectLocality;

import eu.peppol.common.ConfigFile;

/**
 * Note: this class is used from within the ws-tra.xml so be careful on
 * refactoring!
 *
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger<br>
 *         PEPPOL.AT, BRZ, Andreas Haberl
 */
public class SamlCallbackHandler implements CallbackHandler {
  private static final Logger log = LoggerFactory.getLogger (SamlCallbackHandler.class);

  public static String SENDER_ID = "SENDER_ID";
  public static String KEYSTORE_FILE = "TRUSTSTORE_URL";
  public static String KEYSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";
  public static String SAML_TOKEN_ISSUER_NAME = "SAML_TOKEN_ISSUER_NAME";
  public static String KEY_ALIAS = "KEY_ALIAS";
  private final UnsupportedCallbackException unsupported = new UnsupportedCallbackException (null,
                                                                                             "Unsupported Callback Type Encountered");

  public SamlCallbackHandler () {}

  @Override
  public void handle (final Callback [] aCallbacks) throws IOException, UnsupportedCallbackException {
    for (final Callback aCallback : aCallbacks) {
      if (aCallback instanceof SAMLCallback) {
        try {
          final SAMLCallback samlCallback = (SAMLCallback) aCallback;
          // SV = Sender Vouches
          if (samlCallback.getConfirmationMethod ().equals (SAMLCallback.SV_ASSERTION_TYPE)) {
            samlCallback.setAssertionElement (createSenderVouchesSAMLAssertion (samlCallback));
          }
        }
        catch (final Exception ex) {
          ex.printStackTrace ();
        }
      }
      else {
        throw unsupported;
      }
    }
  }

  @VisibleForTesting
  static Element createSenderVouchesSAMLAssertion (final SAMLCallback samlCallback) {
    if (samlCallback.getRuntimeProperties ().get (SAML_TOKEN_ISSUER_NAME) == null) {
      _fixSamlProperties (samlCallback);
    }

    final String sSenderId = (String) samlCallback.getRuntimeProperties ().get (SENDER_ID);
    final String accesspointName = (String) samlCallback.getRuntimeProperties ().get (SAML_TOKEN_ISSUER_NAME);
    String storeFilename = (String) samlCallback.getRuntimeProperties ().get (KEYSTORE_FILE);
    if (storeFilename == null)
      storeFilename = ConfigFile.getInstance ().getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE);
    String storePassword = (String) samlCallback.getRuntimeProperties ().get (KEYSTORE_PASSWORD);
    if (storePassword == null)
      storePassword = ConfigFile.getInstance ().getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE_PASSWORD);
    String samlKeyAlias = (String) samlCallback.getRuntimeProperties ().get (KEY_ALIAS);
    if (samlKeyAlias == null)
      samlKeyAlias = ConfigFile.getInstance ().getString (StartClientProperties.SAML_SIGNATURE_KEY_ALIAS);

    // URL path = this.getClass().getResource(jarKeyStoreFile);
    // String storeFilename = path.toExternalForm().substring(6);
    // String storeFilename = path.toExternalForm();
    log.info ("Start Library: Loading keystore from " + storeFilename);

    Assertion assertion = null;
    try {
      // id must start with letters (WIF.NET)
      final String assertionID = "SamlID" + String.valueOf (System.currentTimeMillis ());
      // neccesary to support <sp:ProtectTokens>
      samlCallback.setAssertionId (assertionID);

      GregorianCalendar c = new GregorianCalendar ();
      final long beforeTime = c.getTimeInMillis ();

      // roll the time by one hour
      final long offsetHours = 60 * 60 * 1000;
      c.setTimeInMillis (beforeTime - offsetHours);
      final GregorianCalendar before = (GregorianCalendar) c.clone ();

      c = new GregorianCalendar ();
      final long afterTime = c.getTimeInMillis ();
      c.setTimeInMillis (afterTime + offsetHours);
      final GregorianCalendar after = (GregorianCalendar) c.clone ();
      final GregorianCalendar issueInstant = new GregorianCalendar ();

      final List <Object> statements = new LinkedList <Object> ();
      final SAMLAssertionFactory factory = SAMLAssertionFactory.newInstance (SAMLAssertionFactory.SAML2_0);
      final NameID senderNameID = factory.createNameID (sSenderId,
                                                        null,
                                                        "http://busdox.org/profiles/serviceMetadata/1.0/UniversalBusinessIdentifier/1.0/");
      final NameID accessPointNameID = factory.createNameID (accesspointName,
                                                             null,
                                                             "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");

      final SubjectConfirmation scf = factory.createSubjectConfirmation (null,
                                                                         "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");
      final Subject subj = factory.createSubject (senderNameID, scf);
      final AuthnContext ctx = factory.createAuthnContext ("urn:oasis:names:tc:SAML:2.0:ac:classes:X509", null);
      final AuthnStatement statement = factory.createAuthnStatement (issueInstant, null, ctx, null, null);
      statements.add (statement);
      final String assuranceLevel = "3";
      statements.add (_getAssuranceLevelStatement (assuranceLevel, factory));
      final Conditions conditions = factory.createConditions (before, after, null, null, null, null);
      assertion = factory.createAssertion (assertionID,
                                           accessPointNameID,
                                           issueInstant,
                                           conditions,
                                           null,
                                           subj,
                                           statements);
      final Certificate accessPointCertificate = _getCertificate (storeFilename, storePassword);
      final Element signedAssertion = sign (assertion,
                                            accessPointCertificate,
                                            getPrivateKey (storeFilename, storePassword, samlKeyAlias));

      return signedAssertion;
    }
    catch (final Exception e) {
      throw new RuntimeException (e);
    }
  }

  private static AttributeStatement _getAssuranceLevelStatement (final String assuranceLevel,
                                                                 final SAMLAssertionFactory samlAssertFactory) throws SAMLException {
    final List <Attribute> attrs = new ArrayList <Attribute> ();
    final List <String> values = new ArrayList <String> ();
    values.add (assuranceLevel);
    final Attribute attr = samlAssertFactory.createAttribute ("urn:eu:busdox:attribute:assurance-level",
                                                              "urn:oasis:names:tc:SAML:2.0:attrname-format:basic",
                                                              values);
    attrs.add (attr);
    final AttributeStatement statement = samlAssertFactory.createAttributeStatement (attrs);
    return statement;
  }

  @SuppressWarnings ("unused")
  private static AuthnStatement _getAuthnStatement (final SAMLAssertionFactory samlAssertFactory,
                                                    final GregorianCalendar issueInstant,
                                                    final GregorianCalendar afterCal) throws SAMLException {
    final AuthnContext ctx = samlAssertFactory.createAuthnContext ();
    final SubjectLocality subjectLocality = null;
    final AuthnStatement authnStatement = samlAssertFactory.createAuthnStatement (issueInstant,
                                                                                  subjectLocality,
                                                                                  ctx,
                                                                                  null,
                                                                                  afterCal);
    return authnStatement;
  }

  private static void _placeSignatureAfterIssuer (final Element assertionElement) throws DOMException {
    final NodeList nodes = assertionElement.getChildNodes ();
    final List <Node> movingNodes = new ArrayList <Node> ();
    for (int i = 1; i < nodes.getLength () - 1; i++)
      movingNodes.add (nodes.item (i));

    for (final Node node : movingNodes)
      assertionElement.removeChild (node);

    for (final Node node : movingNodes)
      assertionElement.appendChild (node);
  }

  private static KeyStore _getKeyStore (final String storeUrl, final String storePassword) throws KeyStoreException,
                                                                                          IOException,
                                                                                          NoSuchAlgorithmException,
                                                                                          CertificateException,
                                                                                          NoSuchProviderException {
    final KeyStore ks = KeyStore.getInstance ("JKS", "SUN");
    final InputStream inputStream = new ClassPathResource (storeUrl).getInputStream ();
    if (inputStream == null)
      throw new IllegalStateException ("Failed to open keystore " + storeUrl);

    log.info ("Keystore inputStream " + inputStream);

    ks.load (inputStream, storePassword.toCharArray ());
    return ks;
  }

  private static Certificate _getCertificate (final String storeUrl, final String storePassword) throws NoSuchAlgorithmException,
                                                                                                CertificateException,
                                                                                                CertificateException,
                                                                                                NoSuchProviderException,
                                                                                                NoSuchProviderException,
                                                                                                IOException,
                                                                                                KeyStoreException {
    final KeyStore keystore = _getKeyStore (storeUrl, storePassword);
    final Enumeration <String> aEnum = keystore.aliases ();
    while (aEnum.hasMoreElements ()) {
      final String sAlias = aEnum.nextElement ();
      return keystore.getCertificate (sAlias);
    }
    throw new IllegalStateException ("Found no certificate in keystore " + storeUrl);
  }

  public static PrivateKey getPrivateKey (final String storeUrl, final String storePassword, final String samlKeyAlias) throws KeyStoreException,
                                                                                                                       NoSuchAlgorithmException,
                                                                                                                       IOException,
                                                                                                                       CertificateException,
                                                                                                                       NoSuchProviderException,
                                                                                                                       UnrecoverableKeyException {
    PrivateKey privateKey = null;
    final KeyStore keystore = _getKeyStore (storeUrl, storePassword);
    final Key key = keystore.getKey (samlKeyAlias, storePassword.toCharArray ());
    if (key instanceof PrivateKey) {
      final Certificate cert = keystore.getCertificate (samlKeyAlias);
      final PublicKey publicKey = cert.getPublicKey ();
      final KeyPair keyPair = new KeyPair (publicKey, (PrivateKey) key);
      privateKey = keyPair.getPrivate ();
    }
    return privateKey;
  }

  public static Element sign (final Assertion assertion, final Certificate cert, final PrivateKey privKey) throws SAMLException {
    try {
      final XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
      return sign (assertion, fac.newDigestMethod (DigestMethod.SHA1, null), SignatureMethod.RSA_SHA1, cert, privKey);
    }
    catch (final Exception ex) {
      throw new SAMLException (ex);
    }
  }

  public static Element sign (final Assertion assertion,
                              final DigestMethod digestMethod,
                              final String signatureMethod,
                              final Certificate cert,
                              final PrivateKey privKey) throws SAMLException {
    try {
      final XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
      final List <Transform> transformList = new ArrayList <Transform> ();

      final Transform tr1 = fac.newTransform (Transform.ENVELOPED, (TransformParameterSpec) null);
      final Transform tr2 = fac.newTransform (CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null);
      transformList.add (tr1);
      transformList.add (tr2);

      final String uri = "#" + assertion.getID ();
      final Reference ref = fac.newReference (uri, digestMethod, transformList, null, null);

      // Create the SignedInfo
      final SignedInfo si = fac.newSignedInfo (fac.newCanonicalizationMethod (CanonicalizationMethod.EXCLUSIVE,
                                                                              (C14NMethodParameterSpec) null),
                                               fac.newSignatureMethod (signatureMethod, null),
                                               Collections.singletonList (ref));

      // Instantiate the document to be signed
      final SOAPPart doc = MessageFactory.newInstance ().createMessage ().getSOAPPart ();
      final KeyInfoFactory kif = fac.getKeyInfoFactory ();

      final X509Data x509Data = kif.newX509Data (Collections.singletonList (cert));
      final KeyInfo ki = kif.newKeyInfo (Collections.singletonList (x509Data));

      final Element assertionElement = assertion.toElement (doc);
      final DOMSignContext dsc = new DOMSignContext (privKey, assertionElement);
      final XMLSignature signature = fac.newXMLSignature (si, ki);
      dsc.putNamespacePrefix (ObjectFactory.class.getPackage ().getAnnotation (XmlSchema.class).namespace (), "ds");

      signature.sign (dsc);
      _placeSignatureAfterIssuer (assertionElement);
      return assertionElement;
    }
    catch (final XWSSecurityException ex) {
      throw new SAMLException (ex);
    }
    catch (final MarshalException ex) {
      throw new SAMLException (ex);
    }
    catch (final NoSuchAlgorithmException ex) {
      throw new SAMLException (ex);
    }
    catch (final SOAPException ex) {
      throw new SAMLException (ex);
    }
    catch (final XMLSignatureException ex) {
      throw new SAMLException (ex);
    }
    catch (final InvalidAlgorithmParameterException ex) {
      throw new SAMLException (ex);
    }
  }

  /**
   * A bug in Metro 2.0 prevents us from using runtime properties on
   * SAMLCallback<br>
   * TODO: checkout Metro use trunk instead of 2.0 release
   */
  private static void _fixSamlProperties (final SAMLCallback samlCallback) {
    final ConfigFile aConfigFile = ConfigFile.getInstance ();
    final String keystoreName = aConfigFile.getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE);
    final String keyStorePassword = aConfigFile.getString (StartClientProperties.SAML_SIGNATURE_KEYSTORE_PASSWORD);
    final String samlTokenIssuer = aConfigFile.getString (StartClientProperties.SAML_ASSERTION_ACCESSPOINT_NAME);
    final String samlSenderName = aConfigFile.getString (StartClientProperties.SAML_ASSERTION_SENDER_NAME);
    final String samlKeyAlias = aConfigFile.getString (StartClientProperties.SAML_SIGNATURE_KEY_ALIAS);
    @SuppressWarnings ("unchecked")
    final Map <String, String> aMap = samlCallback.getRuntimeProperties ();
    aMap.put (SamlCallbackHandler.SENDER_ID, samlSenderName);
    aMap.put (SamlCallbackHandler.SAML_TOKEN_ISSUER_NAME, samlTokenIssuer);
    aMap.put (SamlCallbackHandler.KEYSTORE_FILE, keystoreName);
    aMap.put (SamlCallbackHandler.KEYSTORE_PASSWORD, keyStorePassword);
    aMap.put (SamlCallbackHandler.KEY_ALIAS, samlKeyAlias);
  }
}

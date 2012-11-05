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
/*
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
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
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
package eu.peppol.outbound.callbacks;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
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
import javax.xml.soap.MessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.phloc.commons.CGlobal;
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

import eu.peppol.start.identifier.Configuration;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * The SAMLCallbackHandler is the CallbackHandler implementation used to deal
 * with SAML authentication.
 * 
 * @author Alexander Aguirre Julcapoma(alex@alfa1lab.com) Jose Gorvenia
 *         Narvaez(jose@alfa1lab.com)
 */
public final class SAMLCallbackHandler implements CallbackHandler {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-out");

  private final String SENDER_NAME_ID_SYNTAX = "http://busdox.org/profiles/serviceMetadata/1.0/UniversalBusinessIdentifier/1.0/";
  private final String ACCESSPOINT_NAME_ID_SYNTAX = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
  private final String CONFIRMATION_METHOD = "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";
  private final String AUTHENTICATION_CONTEXT_TYPE = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
  private final String ATTRIBUTE_NAME = "urn:eu:busdox:attribute:assurance-level";
  private final String ATTRIBUTE_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";

  /**
   * Retrieve or display the information requested in the provided Callbacks.
   * 
   * @param callbacks
   *        an array of Callback objects provided by an underlying security
   *        service which contains the information requested to be retrieved or
   *        displayed.
   * @throws IOException
   *         if an input or output error occurs.
   * @throws UnsupportedCallbackException
   *         if the implementation of this method does not support one or more
   *         of the Callbacks specified in the callbacks parameter.
   */
  public void handle (final Callback [] callbacks) throws IOException, UnsupportedCallbackException {
    log.debug ("Requested SAML callback handling");

    for (final Callback callback : callbacks) {

      if (callback instanceof SAMLCallback) {
        final SAMLCallback samlCallback = (SAMLCallback) callback;

        try {
          if (samlCallback.getConfirmationMethod ().equals (SAMLCallback.SV_ASSERTION_TYPE)) {
            final Element element = createSenderVouchesSAMLAssertion (samlCallback);
            samlCallback.setAssertionElement (element);
          }
        }
        catch (final Exception e) {
          throw new RuntimeException ("Error while handling SAML callbacks", e);
        }

      }
      else {
        throw new UnsupportedCallbackException (callback);
      }
    }
  }

  /**
   * Gets an Element representing SAML Assertion.
   * 
   * @param samlCallback
   *        the SAMLCallback.
   * @return an Element.
   * @throws Exception
   *         thrown if there is a SOAP problem.
   */
  private Element createSenderVouchesSAMLAssertion (final SAMLCallback samlCallback) throws Exception {
    log.debug ("Creating and setting the SAML Sender Vouches Assertion");

    final KeystoreManager keystoreManager = new KeystoreManager ();

    final Configuration configuration = Configuration.getInstance ();
    final String senderId = configuration.getPeppolSenderId ();
    final String accesspointName = configuration.getPeppolServiceName ();

    final String assertionID = "SamlID" + String.valueOf (System.currentTimeMillis ());
    samlCallback.setAssertionId (assertionID);

    final GregorianCalendar oneHourAgo = _getNowOffsetByHours (-1);
    final GregorianCalendar now = _getNowOffsetByHours (0);
    final GregorianCalendar inOneHour = _getNowOffsetByHours (1);

    final SAMLAssertionFactory assertionFactory = SAMLAssertionFactory.newInstance (SAMLAssertionFactory.SAML2_0);

    final NameID senderNameID = assertionFactory.createNameID (senderId, null, SENDER_NAME_ID_SYNTAX);
    final NameID accessPointNameID = assertionFactory.createNameID (accesspointName, null, ACCESSPOINT_NAME_ID_SYNTAX);
    final SubjectConfirmation subjectConfirmation = assertionFactory.createSubjectConfirmation (null,
                                                                                                CONFIRMATION_METHOD);
    final Subject subject = assertionFactory.createSubject (senderNameID, subjectConfirmation);
    final AuthnContext authnContext = assertionFactory.createAuthnContext (AUTHENTICATION_CONTEXT_TYPE, null);
    final AuthnStatement authnStatement = assertionFactory.createAuthnStatement (now, null, authnContext, null, null);

    final List <Object> statements = new LinkedList <Object> ();
    statements.add (authnStatement);
    // FIXME: eu hard coding of security assurance level
    statements.add (getAssuranceLevelStatement ("2", assertionFactory));
    final Conditions conditions = assertionFactory.createConditions (oneHourAgo, inOneHour, null, null, null, null);

    final Assertion assertion = assertionFactory.createAssertion (assertionID,
                                                                  accessPointNameID,
                                                                  now,
                                                                  conditions,
                                                                  null,
                                                                  subject,
                                                                  statements);

    return sign (assertion, keystoreManager.getOurCertificate (), keystoreManager.getOurPrivateKey ());
  }

  private static GregorianCalendar _getNowOffsetByHours (final int hours) {
    final GregorianCalendar gregorianCalendar = new GregorianCalendar ();
    final long then = gregorianCalendar.getTimeInMillis () + CGlobal.MILLISECONDS_PER_HOUR * hours;
    gregorianCalendar.setTimeInMillis (then);
    return gregorianCalendar;
  }

  /**
   * Gets the Level Statement of Assurance. Assuarnace levels are defined in
   * http://csrc.nist.gov/publications/nistpubs/800-63/SP800-63V1_0_2.pdf
   * 
   * @param assuranceLevel
   *        the assurance level.
   * @param samlAssertFactory
   *        the SAMLAssertionFactory.
   * @return an AttributeStatement.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  private AttributeStatement getAssuranceLevelStatement (final String assuranceLevel,
                                                         final SAMLAssertionFactory samlAssertFactory) throws SAMLException {

    final List <String> values = new ArrayList <String> ();
    values.add (assuranceLevel);

    final Attribute attribute = samlAssertFactory.createAttribute (ATTRIBUTE_NAME, ATTRIBUTE_NAMESPACE, values);
    final List <Attribute> attributes = new ArrayList <Attribute> ();
    attributes.add (attribute);

    return samlAssertFactory.createAttributeStatement (attributes);
  }

  /**
   * Converts a SAML Assertion to an org.w3c.dom.Element object and signs it
   * with the X509Certificate and PrivateKey using the SHA1 DigestMethod and the
   * RSA_SHA1 SignatureMethod.
   * 
   * @param assertion
   *        SAML Assertion to be signed.
   * @param certificate
   *        the X509Certificate.
   * @param privateKey
   *        the certificate's private key.
   * @return a signed org.w3c.dom.Element holding the SAML Assertion.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  public final Element sign (final Assertion assertion, final X509Certificate certificate, final PrivateKey privateKey) throws SAMLException {

    try {

      final XMLSignatureFactory signatureFactory = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
      final DigestMethod digestMethod = signatureFactory.newDigestMethod (DigestMethod.SHA1, null);
      return sign (assertion, digestMethod, SignatureMethod.RSA_SHA1, certificate, privateKey);

    }
    catch (final Exception ex) {
      throw new SAMLException (ex);
    }
  }

  /**
   * Converts a SAML Assertion to an org.w3c.dom.Element object and signs it
   * with the X509Certificate and private key using the given DigestMethod and
   * the specified SignatureMethod.
   * 
   * @param assertion
   *        SAML Assertion to be signed.
   * @param digestMethod
   *        the digest method.
   * @param signatureMethod
   *        the signature method.
   * @param certificate
   *        the X509Certificate.
   * @param privateKey
   *        the certificate's private key.
   * @return a signed org.w3c.dom.Element holding the SAML Assertion.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  public final Element sign (final Assertion assertion,
                             final DigestMethod digestMethod,
                             final String signatureMethod,
                             final X509Certificate certificate,
                             final PrivateKey privateKey) throws SAMLException {

    try {
      final XMLSignatureFactory signatureFactory = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();

      final List <Transform> transformList = new ArrayList <Transform> ();
      transformList.add (signatureFactory.newTransform (Transform.ENVELOPED, (TransformParameterSpec) null));
      transformList.add (signatureFactory.newTransform (CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null));

      final Reference reference = signatureFactory.newReference ("#" + assertion.getID (),
                                                                 digestMethod,
                                                                 transformList,
                                                                 null,
                                                                 null);

      final CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod (CanonicalizationMethod.EXCLUSIVE,
                                                                                                        (C14NMethodParameterSpec) null);

      final SignedInfo signedInfo = signatureFactory.newSignedInfo (canonicalizationMethod,
                                                                    signatureFactory.newSignatureMethod (signatureMethod,
                                                                                                         null),
                                                                    Collections.singletonList (reference));

      final Document document = MessageFactory.newInstance ().createMessage ().getSOAPPart ();
      final KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory ();

      final X509Data x509Data = keyInfoFactory.newX509Data (Collections.singletonList (certificate));
      final KeyInfo keyInfo = keyInfoFactory.newKeyInfo (Collections.singletonList (x509Data));

      final Element assertionElement = assertion.toElement (document);
      final DOMSignContext domSignContext = new DOMSignContext (privateKey, assertionElement);

      final XMLSignature xmlSignature = signatureFactory.newXMLSignature (signedInfo, keyInfo);
      domSignContext.putNamespacePrefix ("http://www.w3.org/2000/09/xmldsig#", "ds");
      xmlSignature.sign (domSignContext);
      placeSignatureAfterIssuer (assertionElement);

      return assertionElement;

    }
    catch (final Exception e) {
      throw new SAMLException (e);
    }
  }

  /**
   * Places a Signature.
   * 
   * @param assertionElement
   *        an Element containing an Assertion.
   * @throws DOMException
   *         Throws a DOMException.
   */
  private void placeSignatureAfterIssuer (final Element assertionElement) throws DOMException {

    final NodeList nodes = assertionElement.getChildNodes ();
    final List <Node> movingNodes = new ArrayList <Node> ();

    for (int i = 1; i < nodes.getLength () - 1; i++) {
      movingNodes.add (nodes.item (i));
    }

    for (final Node node : movingNodes) {
      assertionElement.removeChild (node);
    }

    for (final Node node : movingNodes) {
      assertionElement.appendChild (node);
    }
  }
}

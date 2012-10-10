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

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.utils.ConfigFile;

import com.phloc.commons.collections.ContainerHelper;

/**
 * Finds and returns a key using the data contained in a {@link KeyInfo} object
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 * @see <a
 *      href="http://java.sun.com/developer/technicalArticles/xml/dig_signature_api/">Programming
 *      with the Java XML Digital Signature API</a>
 */
public final class X509KeySelector extends KeySelector {
  private static final ConfigFile s_aConfigFile = ConfigFile.getInstance ();
  private static final String TRUSTSTORE_LOCATION = s_aConfigFile.getString ("truststore.location",
                                                                             KeyStoreUtils.TRUSTSTORE_CLASSPATH);
  private static final String TRUSTSTORE_PASSWORD = s_aConfigFile.getString ("truststore.password",
                                                                             KeyStoreUtils.TRUSTSTORE_PASSWORD);

  public X509KeySelector () {}

  private static boolean _algorithmEquals (@Nonnull final String sAlgURI, @Nonnull final String sAlgName) {
    return (sAlgName.equalsIgnoreCase ("DSA") && sAlgURI.equalsIgnoreCase (SignatureMethod.DSA_SHA1)) ||
           (sAlgName.equalsIgnoreCase ("RSA") && sAlgURI.equalsIgnoreCase (SignatureMethod.RSA_SHA1));
  }

  @Override
  public KeySelectorResult select (@Nonnull final KeyInfo aKeyInfo,
                                   final KeySelector.Purpose aPurpose,
                                   final AlgorithmMethod aMethod,
                                   final XMLCryptoContext aCryptoContext) throws KeySelectorException {

    final Iterator <?> aKeyInfoIter = aKeyInfo.getContent ().iterator ();
    while (aKeyInfoIter.hasNext ()) {
      final XMLStructure aInfo = (XMLStructure) aKeyInfoIter.next ();
      if (!(aInfo instanceof X509Data))
        continue;

      final X509Data x509Data = (X509Data) aInfo;
      final Iterator <?> aX509Iter = x509Data.getContent ().iterator ();
      while (aX509Iter.hasNext ()) {
        final Object o = aX509Iter.next ();
        if (!(o instanceof X509Certificate))
          continue;

        final X509Certificate aCertificate = (X509Certificate) o;
        try {
          // Check if the certificate is expired or active.
          aCertificate.checkValidity ();

          // Checks whether the certificate is in the trusted store.
          final List <X509Certificate> aCertList = ContainerHelper.newList (aCertificate);

          final KeyStore ks = KeyStoreUtils.loadKeyStore (TRUSTSTORE_LOCATION, TRUSTSTORE_PASSWORD);
          // The PKIXParameters constructor may fail because:
          // - the trustAnchorsParameter is empty
          final PKIXParameters aPKIXParams = new PKIXParameters (ks);
          aPKIXParams.setRevocationEnabled (false);
          final CertificateFactory aCertificateFactory = CertificateFactory.getInstance ("X509");
          final CertPath aCertPath = aCertificateFactory.generateCertPath (aCertList);
          final CertPathValidator aPathValidator = CertPathValidator.getInstance ("PKIX");
          aPathValidator.validate (aCertPath, aPKIXParams);

          final PublicKey aPublicKey = aCertificate.getPublicKey ();

          // Make sure the algorithm is compatible with the method.
          if (_algorithmEquals (aMethod.getAlgorithm (), aPublicKey.getAlgorithm ())) {
            return new ConstantKeySelectorResult (aPublicKey);
          }
        }
        catch (final Exception e) {
          throw new KeySelectorException ("Failed to select public key", e);
        }
      }
    }
    throw new KeySelectorException ("No public key found!");
  }
}

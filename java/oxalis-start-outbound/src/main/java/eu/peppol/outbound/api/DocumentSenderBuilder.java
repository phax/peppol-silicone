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
package eu.peppol.outbound.api;

import java.io.File;

import javax.annotation.Nonnull;

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.process.EPredefinedProcessIdentifier;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * responsible for constructing a DocumentSender. A DocumentSender is dedicated
 * to a particular document and process type. DocumentSenders are guaranteed to
 * be thread-safe. specification of User: nigel Date: Oct 24, 2011 Time:
 * 10:38:35 AM
 */
public class DocumentSenderBuilder {
  private IReadonlyDocumentTypeIdentifier m_aDocumentTypeIdentifier = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A;
  private IReadonlyProcessIdentifier m_aPeppolProcessTypeId = EPredefinedProcessIdentifier.BIS5A;
  private File m_aKeystoreFile;
  private String m_sKeystorePassword;
  private boolean m_bSoapLogging;

  /**
   * constructs and returns a DocumentSender based on the previously specified
   * parameters.
   */
  @Nonnull
  public DocumentSender build () {
    final KeystoreManager keystoreManager = new KeystoreManager ();
    keystoreManager.initialiseKeystore (m_aKeystoreFile, m_sKeystorePassword);
    return new DocumentSender (m_aDocumentTypeIdentifier, m_aPeppolProcessTypeId, m_bSoapLogging);
  }

  /**
   * enables logging of SOAP messages. The default is eu logging.
   */
  @Nonnull
  public DocumentSenderBuilder enableSoapLogging () {
    m_bSoapLogging = true;
    return this;
  }

  /**
   * sets the document type for this DocumentSender. The default value is an
   * invoice document.
   */
  @Nonnull
  public DocumentSenderBuilder setDocumentTypeIdentifier (final IReadonlyDocumentTypeIdentifier documentTypeIdentifier) {
    m_aDocumentTypeIdentifier = documentTypeIdentifier;
    return this;
  }

  /**
   * specifies the location of the keystore containing our own certificate and
   * private key.
   */
  @Nonnull
  public DocumentSenderBuilder setKeystoreFile (final File keystore) {
    m_aKeystoreFile = keystore;
    return this;
  }

  /**
   * specifies the password for the keystore.
   */
  @Nonnull
  public DocumentSenderBuilder setKeystorePassword (final String keystorePassword) {
    m_sKeystorePassword = keystorePassword;
    return this;
  }

  /**
   * specifies the peppolProcessTypeId for the business process of which the
   * document is a part. The default value is the process containing a single
   * invoice.
   */
  @Nonnull
  public DocumentSenderBuilder setPeppolProcessTypeId (final IReadonlyProcessIdentifier peppolProcessTypeId) {
    m_aPeppolProcessTypeId = peppolProcessTypeId;
    return this;
  }
}

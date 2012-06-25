package eu.peppol.outbound.api;

import java.io.File;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;

import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;
import eu.peppol.start.identifier.KeystoreManager;

/**
 * responsible for constructing a DocumentSender. A DocumentSender is dedicated
 * to a particular document and process type. DocumentSenders are guaranteed to
 * be thread-safe. specification of User: nigel Date: Oct 24, 2011 Time:
 * 10:38:35 AM
 */
public class DocumentSenderBuilder {
  private DocumentIdentifierType m_aDocumentTypeIdentifier = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ();
  private ProcessIdentifierType m_aPeppolProcessTypeId = EPredefinedProcessIdentifier.BIS5A.getAsProcessIdentifier ();
  private File m_aKeystoreFile;
  private String m_sKeystorePassword;
  private boolean m_bSoapLogging;

  /**
   * constructs and returns a DocumentSender based on the previously specified
   * parameters.
   */
  public DocumentSender build () {
    final KeystoreManager keystoreManager = new KeystoreManager ();
    keystoreManager.initialiseKeystore (m_aKeystoreFile, m_sKeystorePassword);

    return new DocumentSender (m_aDocumentTypeIdentifier, m_aPeppolProcessTypeId, m_bSoapLogging);
  }

  /**
   * enables logging of SOAP messages. The default is eu logging.
   */
  public DocumentSenderBuilder enableSoapLogging () {
    this.m_bSoapLogging = true;
    return this;
  }

  /**
   * sets the document type for this DocumentSender. The default value is an
   * invoice document.
   */
  public DocumentSenderBuilder setDocumentTypeIdentifier (final DocumentIdentifierType documentTypeIdentifier) {
    this.m_aDocumentTypeIdentifier = documentTypeIdentifier;
    return this;
  }

  /**
   * specifies the location of the keystore containing our own certificate and
   * private key.
   */
  public DocumentSenderBuilder setKeystoreFile (final File keystore) {
    this.m_aKeystoreFile = keystore;
    return this;
  }

  /**
   * specifies the password for the keystore.
   */
  public DocumentSenderBuilder setKeystorePassword (final String keystorePassword) {
    this.m_sKeystorePassword = keystorePassword;
    return this;
  }

  /**
   * specifies the peppolProcessTypeId for the business process of which the
   * document is a part. The default value is the process containing a single
   * invoice.
   */
  public DocumentSenderBuilder setPeppolProcessTypeId (final ProcessIdentifierType peppolProcessTypeId) {
    this.m_aPeppolProcessTypeId = peppolProcessTypeId;
    return this;
  }
}

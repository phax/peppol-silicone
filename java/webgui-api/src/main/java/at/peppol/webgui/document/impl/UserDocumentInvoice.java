package at.peppol.webgui.document.impl;

import javax.annotation.Nonnull;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.webgui.document.AbstractUserDocument;
import at.peppol.webgui.document.EDocumentType;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.idfactory.GlobalIDFactory;

/**
 * Represents a single invoice document
 * 
 * @author philip
 */
public final class UserDocumentInvoice extends AbstractUserDocument {
  private final InvoiceType m_aInvoice;

  public UserDocumentInvoice (@Nonnull final InvoiceType aInvoice) {
    this (GlobalIDFactory.getNewPersistentStringID (), aInvoice);
  }

  public UserDocumentInvoice (@Nonnull @Nonempty final String sID, @Nonnull final InvoiceType aInvoice) {
    super (sID, EDocumentType.INVOICE);
    if (aInvoice == null)
      throw new NullPointerException ("invoice");
    m_aInvoice = aInvoice;
  }

  @Nonnull
  public InvoiceType getInvoice () {
    return m_aInvoice;
  }
}

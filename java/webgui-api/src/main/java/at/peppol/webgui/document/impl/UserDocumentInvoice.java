package at.peppol.webgui.document.impl;

import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.xml.transform.stream.StreamResult;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import at.peppol.webgui.document.AbstractUserDocument;
import at.peppol.webgui.document.EDocumentType;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.state.ESuccess;
import com.phloc.ubl.UBL20DocumentMarshaller;

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

  @Nonnull
  public ESuccess writeToStream (@Nonnull @WillClose final OutputStream aOS) {
    try {
      return UBL20DocumentMarshaller.writeInvoice (m_aInvoice, new StreamResult (aOS));
    }
    finally {
      StreamUtils.close (aOS);
    }
  }
}

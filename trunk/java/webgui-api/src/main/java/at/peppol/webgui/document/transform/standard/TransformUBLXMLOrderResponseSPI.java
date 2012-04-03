package at.peppol.webgui.document.transform.standard;

import javax.annotation.Nonnull;

import at.peppol.webgui.document.EDocumentMetaType;
import at.peppol.webgui.document.transform.ITransformOrderResponseToUBLSPI;
import at.peppol.webgui.document.transform.TransformationResult;
import at.peppol.webgui.document.transform.TransformationSource;

import com.phloc.commons.typeconvert.TypeConverterException;
import com.phloc.commons.xml.XMLHelper;
import com.phloc.ubl.EUBL20DocumentType;

/**
 * "Transform" UBL order response available as XML
 * 
 * @author philip
 */
public final class TransformUBLXMLOrderResponseSPI implements ITransformOrderResponseToUBLSPI {
  public boolean canConvertOrderResponse (@Nonnull final TransformationSource aSource) {
    // We can only handle XML documents
    if (!EDocumentMetaType.XML.equals (aSource.getDocumentMetaType ()))
      return false;

    // Check if the namespace matches
    final String sNamespaceURI = XMLHelper.getNamespaceURI (aSource.getXMLDocument ());
    return EUBL20DocumentType.ORDER_RESPONSE_SIMPLE.getNamespaceURI ().equals (sNamespaceURI);
  }

  @Nonnull
  public TransformationResult convertOrderResponseToUBL (@Nonnull final TransformationSource aSource) throws TypeConverterException {
    // Use input document as is
    return TransformationResult.createResult (aSource.getXMLDocument ());
  }
}

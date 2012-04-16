package at.peppol.webgui.document.transform.standard;

import javax.annotation.Nonnull;

import at.peppol.webgui.document.transform.ITransformOrderToUBLSPI;
import at.peppol.webgui.document.transform.TransformationResult;
import at.peppol.webgui.document.transform.TransformationSource;

import com.phloc.commons.typeconvert.TypeConverterException;
import com.phloc.commons.xml.XMLHelper;
import com.phloc.ubl.EUBL20DocumentType;

/**
 * "Transform" UBL orders available as XML
 * 
 * @author philip
 */
public final class TransformUBLXMLOrderSPI implements ITransformOrderToUBLSPI {
  public boolean canConvertOrder (@Nonnull final TransformationSource aSource) {
    // We can only handle XML documents
    if (!aSource.isXMLSource ())
      return false;

    // Check if the namespace matches
    final String sNamespaceURI = XMLHelper.getNamespaceURI (aSource.getXMLDocument ());
    return EUBL20DocumentType.ORDER.getNamespaceURI ().equals (sNamespaceURI);
  }

  @Nonnull
  public TransformationResult convertOrderToUBL (@Nonnull final TransformationSource aSource) throws TypeConverterException {
    // Use input document as is
    return TransformationResult.createResult (aSource.getXMLDocument ());
  }
}

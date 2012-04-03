package at.peppol.webgui.document.transform;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.document.EDocumentType;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.lang.ServiceLoaderBackport;
import com.phloc.commons.typeconvert.TypeConverterException;

/**
 * This is the central class for handling the transformation of resources.
 * 
 * @author philip
 */
@Immutable
public final class TransformationManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (TransformationManager.class);
  private static final List <ITransformCatalogueToUBLSPI> s_aCatalogueTransformers;
  private static final List <ITransformOrderToUBLSPI> s_aOrderTransformers;
  private static final List <ITransformOrderResponseToUBLSPI> s_aOrderResponseTransformers;
  private static final List <ITransformInvoiceToUBLSPI> s_aInvoiceTransformers;

  static {
    // Resolve all SPI implementations
    s_aCatalogueTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformCatalogueToUBLSPI.class));
    s_aLogger.info ("Found " + s_aCatalogueTransformers.size () + " catalogue transformer(s)");

    s_aOrderTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformOrderToUBLSPI.class));
    s_aLogger.info ("Found " + s_aOrderTransformers.size () + " order transformer(s)");

    s_aOrderResponseTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformOrderResponseToUBLSPI.class));
    s_aLogger.info ("Found " + s_aOrderResponseTransformers.size () + " order response transformer(s)");

    s_aInvoiceTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformInvoiceToUBLSPI.class));
    s_aLogger.info ("Found " + s_aInvoiceTransformers.size () + " invoice transformer(s)");
  }

  private TransformationManager () {}

  /**
   * Convert the passed resource to a UBL document.
   * 
   * @param eDocType
   *        The desired document type. May not be <code>null</code>.
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformDocumentToUBL (@Nonnull final EDocumentType eDocType,
                                                             @Nonnull final TransformationSource aSource) {
    if (eDocType == null)
      throw new NullPointerException ("docType");

    switch (eDocType) {
      case CATALOGUE:
        return transformCatalogueToUBL (aSource);
      case ORDER:
        return transformOrderToUBL (aSource);
      case ORDER_RESPONSE:
        return transformOrderResponseToUBL (aSource);
      case INVOICE:
        return transformInvoiceToUBL (aSource);
      default:
        throw new IllegalArgumentException ("Unsupported document type " + eDocType);
    }
  }

  /**
   * Convert the passed resource to a UBL catalogue.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformCatalogueToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformCatalogueToUBLSPI aTransformer : s_aCatalogueTransformers)
      if (aTransformer.canConvertCatalogue (aSource)) {
        s_aLogger.info ("Found matching catalogue transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertCatalogueToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert catalogue - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL order.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformOrderToUBLSPI aTransformer : s_aOrderTransformers)
      if (aTransformer.canConvertOrder (aSource)) {
        s_aLogger.info ("Found matching order transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert order - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL order response.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderResponseToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformOrderResponseToUBLSPI aTransformer : s_aOrderResponseTransformers)
      if (aTransformer.canConvertOrderResponse (aSource)) {
        s_aLogger.info ("Found matching order response transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderResponseToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert order response - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL invoice.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformInvoiceToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformInvoiceToUBLSPI aTransformer : s_aInvoiceTransformers)
      if (aTransformer.canConvertInvoice (aSource)) {
        s_aLogger.info ("Found matching invoice transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertInvoiceToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert invoice - ignoring");
        }
      }
    return null;
  }
}

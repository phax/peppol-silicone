package at.peppol.webgui.document.transform;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.document.EDocumentType;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
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
    s_aLogger.info ("Found " + s_aCatalogueTransformers.size () + " catalogue transformers");

    s_aOrderTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformOrderToUBLSPI.class));
    s_aLogger.info ("Found " + s_aOrderTransformers.size () + " order transformers");

    s_aOrderResponseTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformOrderResponseToUBLSPI.class));
    s_aLogger.info ("Found " + s_aOrderResponseTransformers.size () + " order response transformers");

    s_aInvoiceTransformers = ContainerHelper.newList (ServiceLoaderBackport.load (ITransformInvoiceToUBLSPI.class));
    s_aLogger.info ("Found " + s_aInvoiceTransformers.size () + " invoice transformers");
  }

  private TransformationManager () {}

  /**
   * Convert the passed resource to a UBL document.
   * 
   * @param eDocType
   *        The desired document type. May not be <code>null</code>.
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformDocumentToUBL (@Nonnull final EDocumentType eDocType,
                                                             @Nonnull final IReadableResource aRes) {
    switch (eDocType) {
      case CATALOGUE:
        return transformCatalogueToUBL (aRes);
      case ORDER:
        return transformOrderToUBL (aRes);
      case ORDER_RESPONSE:
        return transformOrderResponseToUBL (aRes);
      case INVOICE:
        return transformInvoiceToUBL (aRes);
      default:
        throw new IllegalArgumentException ("Unsupported document type " + eDocType);
    }
  }

  /**
   * Convert the passed resource to a UBL catalogue.
   * 
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformCatalogueToUBL (@Nonnull final IReadableResource aRes) {
    for (final ITransformCatalogueToUBLSPI aTransformer : s_aCatalogueTransformers)
      if (aTransformer.canConvertCatalogue (aRes)) {
        s_aLogger.info ("Found matching catalogue transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertCatalogueToUBL (aRes);
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
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderToUBL (@Nonnull final IReadableResource aRes) {
    for (final ITransformOrderToUBLSPI aTransformer : s_aOrderTransformers)
      if (aTransformer.canConvertOrder (aRes)) {
        s_aLogger.info ("Found matching order transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderToUBL (aRes);
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
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderResponseToUBL (@Nonnull final IReadableResource aRes) {
    for (final ITransformOrderResponseToUBLSPI aTransformer : s_aOrderResponseTransformers)
      if (aTransformer.canConvertOrderResponse (aRes)) {
        s_aLogger.info ("Found matching order response transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderResponseToUBL (aRes);
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
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformInvoiceToUBL (@Nonnull final IReadableResource aRes) {
    for (final ITransformInvoiceToUBLSPI aTransformer : s_aInvoiceTransformers)
      if (aTransformer.canConvertInvoice (aRes)) {
        s_aLogger.info ("Found matching invoice transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertInvoiceToUBL (aRes);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert invoice - ignoring");
        }
      }
    return null;
  }
}

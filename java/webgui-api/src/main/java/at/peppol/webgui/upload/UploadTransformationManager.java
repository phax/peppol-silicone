package at.peppol.webgui.upload;

import javax.annotation.Nonnull;

import at.peppol.webgui.document.EDocumentType;
import at.peppol.webgui.document.transform.TransformationManager;
import at.peppol.webgui.document.transform.TransformationResult;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.log.InMemoryLogger;

/**
 * This class should be first class to be called after an upload finished
 * successful.
 * 
 * @author philip
 */
public final class UploadTransformationManager {
  private UploadTransformationManager () {}

  /**
   * Perform the transformation of an uploaded document.
   * 
   * @param eDocType
   *        The document type that was uploaded
   * @param aUploadedResource
   *        The uploaded resource descriptor
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static TransformationResult tryToTransformUploadedResource (@Nonnull final EDocumentType eDocType,
                                                                     @Nonnull final IUploadedResource aUploadedResource) {
    if (eDocType == null)
      throw new NullPointerException ("docType");
    if (aUploadedResource == null)
      throw new NullPointerException ("uploadedResource");
    if (!aUploadedResource.isSuccess ())
      throw new IllegalArgumentException ("Cannot handle failed uploads!");

    // Convert to a resource
    final InMemoryLogger aErrorMsgs = new InMemoryLogger ();
    final IReadableResource aRes = new FileSystemResource (aUploadedResource.getTemporaryFile ());
    if (!aRes.exists ()) {
      aErrorMsgs.error ("Temporary file " +
                        aUploadedResource.getTemporaryFile ().getAbsolutePath () +
                        " does not exist!");
      return TransformationResult.createFailure (aErrorMsgs);
    }

    // Do the transformation
    final TransformationResult aRet = TransformationManager.transformDocumentToUBL (eDocType, aRes);
    if (aRet == null) {
      aErrorMsgs.error ("No transformer was able to transform uploaded file " +
                        aUploadedResource.getTemporaryFile ().getAbsolutePath () +
                        "!");
      return TransformationResult.createFailure (aErrorMsgs);
    }
    return aRet;
  }
}

package at.peppol.webgui.upload;

import javax.annotation.Nonnull;

import at.peppol.webgui.document.EDocumentType;
import at.peppol.webgui.document.transform.TransformationManager;
import at.peppol.webgui.document.transform.TransformationResult;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.serialize.MicroReader;

public final class UploadTransformationManager {
  private UploadTransformationManager () {}

  @Nonnull
  public static TransformationResult tryToTransformUploadedResource (@Nonnull final EDocumentType eDocType,
                                                                     @Nonnull final IUploadedResource aUploadedResource) {
    if (eDocType == null)
      throw new NullPointerException ("docType");
    if (aUploadedResource == null)
      throw new NullPointerException ("uploadedResource");

    // Convert to a resource
    final InMemoryLogger aErrorMsgs = new InMemoryLogger ();
    final IReadableResource aRes = new FileSystemResource (aUploadedResource.getTemporaryFile ());
    if (!aRes.exists ()) {
      aErrorMsgs.error ("Temporary file " +
                        aUploadedResource.getTemporaryFile ().getAbsolutePath () +
                        " does not exist!");
      return TransformationResult.createFailure (aErrorMsgs);
    }

    if (false) {
      // Check if it is XML?
      final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    }

    return TransformationManager.transformDocument (eDocType, aRes);
  }
}

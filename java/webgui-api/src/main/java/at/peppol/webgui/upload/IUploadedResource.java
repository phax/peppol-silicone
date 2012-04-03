package at.peppol.webgui.upload;

import java.io.File;

import javax.annotation.Nonnull;

import com.phloc.commons.state.ISuccessIndicator;

public interface IUploadedResource extends ISuccessIndicator {
  /**
   * @return The original filename provided on upload.
   */
  @Nonnull
  String getOriginalFilename ();

  /**
   * @return The temporary file where the uploaded content was stored.
   */
  @Nonnull
  File getTemporaryFile ();
}

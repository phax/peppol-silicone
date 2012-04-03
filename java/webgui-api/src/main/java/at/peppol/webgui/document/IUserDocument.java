package at.peppol.webgui.document;

import javax.annotation.Nonnull;

import com.phloc.commons.id.IHasID;

/**
 * Base interface for all documents
 * 
 * @author philip
 */
public interface IUserDocument extends IHasID <String> {
  /**
   * @return The type of this document. May not be <code>null</code>.
   */
  @Nonnull
  EDocumentType getType ();
}

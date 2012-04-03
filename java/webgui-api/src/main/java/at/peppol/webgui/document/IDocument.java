package at.peppol.webgui.document;

import javax.annotation.Nonnull;

import com.phloc.commons.id.IHasID;

/**
 * Base interface for all documents
 * 
 * @author philip
 */
public interface IDocument extends IHasID <String> {
  /**
   * @return The meta type of the document
   */
  @Nonnull
  EDocumentMetaType getMetaType ();
}

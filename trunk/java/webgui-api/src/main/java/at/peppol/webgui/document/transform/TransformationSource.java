package at.peppol.webgui.document.transform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;

import at.peppol.webgui.document.EDocumentMetaType;

import com.phloc.commons.io.IReadableResource;

/**
 * Encapsulate all parameters required to start a transformation
 * 
 * @author philip
 */
@Immutable
public final class TransformationSource {
  private final EDocumentMetaType m_eDocMetaType;
  private final IReadableResource m_aRes;
  private final Document m_aXMLDoc;

  /**
   * Constructor
   * 
   * @param eDocMetaType
   *        Document meta type. May not be <code>null</code>.
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @param aXMLDoc
   *        A pre-parsed XML document, in case it is of type XML. May not be
   *        <code>null</code> if the meta type is XML.
   */
  public TransformationSource (@Nonnull final EDocumentMetaType eDocMetaType,
                               @Nonnull final IReadableResource aRes,
                               @Nullable final Document aXMLDoc) {
    if (eDocMetaType == null)
      throw new NullPointerException ("docMetaType");
    if (aRes == null)
      throw new NullPointerException ("resource");
    if (eDocMetaType.equals (EDocumentMetaType.XML) && aXMLDoc == null)
      throw new IllegalArgumentException ("XML document may not be null for meta type XML");
    m_eDocMetaType = eDocMetaType;
    m_aRes = aRes;
    m_aXMLDoc = aXMLDoc;
  }

  /**
   * @return The document meta type. Never <code>null</code>.
   */
  @Nonnull
  public EDocumentMetaType getDocumentMetaType () {
    return m_eDocMetaType;
  }

  /**
   * @return The underlying resource. Never <code>null</code>.
   */
  @Nonnull
  public IReadableResource getResource () {
    return m_aRes;
  }

  /**
   * @return The pre-parsed XML document in case the meta type is XML
   */
  @Nullable
  public Document getXMLDocument () {
    return m_aXMLDoc;
  }
}

package at.peppol.webgui.document;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;

/**
 * Abstract base class for user documents
 * 
 * @author philip
 */
public abstract class AbstractUserDocument implements IUserDocument {
  private final String m_sID;
  private final EDocumentType m_eDocType;

  public AbstractUserDocument (@Nonnull @Nonempty final String sID, @Nonnull final EDocumentType eDocType) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (eDocType == null)
      throw new NullPointerException ("docType");
    m_sID = sID;
    m_eDocType = eDocType;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public EDocumentType getType () {
    return m_eDocType;
  }
}

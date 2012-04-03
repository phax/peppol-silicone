package at.peppol.webgui.document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.lang.EnumHelper;

/**
 * Defines the meta type of a document
 * 
 * @author philip
 */
public enum EDocumentMetaType implements IHasID <String> {
  XML ("xml"),
  BINARY ("binary");

  private final String m_sID;

  private EDocumentMetaType (@Nonnull @Nonempty final String sID) {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nullable
  public static EDocumentMetaType getFromIDOrNull (@Nullable final String sID) {
    return EnumHelper.getFromIDOrNull (EDocumentMetaType.class, sID);
  }
}

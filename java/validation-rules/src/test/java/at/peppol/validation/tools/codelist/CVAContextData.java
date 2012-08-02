package at.peppol.validation.tools.codelist;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;

@Immutable
final class CVAContextData {
  private final String m_sID;
  private final String m_sItem;
  private final String m_sCodeListName;
  private final String m_sSeverity;
  private final String m_sMessage;

  public CVAContextData (@Nonnull @Nonempty final String sID,
                         @Nonnull @Nonempty final String sItem,
                         @Nonnull @Nonempty final String sCodeListName,
                         @Nonnull @Nonempty final String sSeverity,
                         @Nonnull @Nonempty final String sMessage) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sItem))
      throw new IllegalArgumentException ("item");
    if (StringHelper.hasNoText (sCodeListName))
      throw new IllegalArgumentException ("codeListName");
    if (StringHelper.hasNoText (sSeverity))
      throw new IllegalArgumentException ("severity");
    if (StringHelper.hasNoText (sMessage))
      throw new IllegalArgumentException ("message");
    m_sID = sID;
    m_sItem = sItem;
    m_sCodeListName = sCodeListName;
    m_sSeverity = sSeverity;
    m_sMessage = sMessage;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getItem () {
    return m_sItem;
  }

  @Nonnull
  @Nonempty
  public String getCodeListName () {
    return m_sCodeListName;
  }

  @Nonnull
  @Nonempty
  public String getSeverity () {
    return m_sSeverity;
  }

  @Nonnull
  @Nonempty
  public String getMessage () {
    return m_sMessage;
  }
}

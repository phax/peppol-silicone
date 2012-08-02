package at.peppol.validation.tools.codelist;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CVAContext {
  private final String m_sID;
  private final String m_sItem;
  private final String m_sScope;
  private final String m_sValue;
  private final String m_sSeverity;
  private final String m_sMessage;

  public CVAContext (final String sID,
                     final String sItem,
                     final String sScope,
                     final String sValue,
                     final String sSeverity,
                     final String sMessage) {
    m_sID = sID;
    m_sItem = sItem;
    m_sScope = sScope;
    m_sValue = sValue;
    m_sSeverity = sSeverity;
    m_sMessage = sMessage;
  }

  public String getID () {
    return m_sID;
  }

  public String getItem () {
    return m_sItem;
  }

  public String getScope () {
    return m_sScope;
  }

  public String getValue () {
    return m_sValue;
  }

  public String getSeverity () {
    return m_sSeverity;
  }

  public String getMessage () {
    return m_sMessage;
  }
}

package at.peppol.validation.tools;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;

public final class RuleAssertion {
  private final String m_sRuleID;
  private final String m_sMessage;
  private final String m_sSeverity;

  public RuleAssertion (@Nonnull @Nonempty final String sRuleID,
                        @Nonnull @Nonempty final String sMessage,
                        @Nonnull @Nonempty final String sSeverity) {
    m_sRuleID = sRuleID;
    m_sMessage = sMessage;
    m_sSeverity = sSeverity;
  }

  @Nonnull
  @Nonempty
  public String getRuleID () {
    return m_sRuleID;
  }

  @Nonnull
  @Nonempty
  public String getMessage () {
    return m_sMessage;
  }

  @Nonnull
  @Nonempty
  public String getSeverity () {
    return m_sSeverity;
  }
}

package at.peppol.validation.tools.sch;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;

public final class RuleParam {
  private final String m_sRuleID;
  private final String m_sTest;

  public RuleParam (@Nonnull @Nonempty final String sRuleID, @Nonnull @Nonempty final String sTest) {
    m_sRuleID = sRuleID;
    m_sTest = sTest;
  }

  @Nonnull
  @Nonempty
  public String getRuleID () {
    return m_sRuleID;
  }

  @Nonnull
  @Nonempty
  public String getTest () {
    return m_sTest;
  }
}

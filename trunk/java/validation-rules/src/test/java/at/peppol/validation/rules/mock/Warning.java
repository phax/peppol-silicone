package at.peppol.validation.rules.mock;

import com.phloc.commons.error.EErrorLevel;

public final class Warning extends AbstractErrorDefinition {
  public Warning (final String sErrorCode) {
    super (EErrorLevel.WARN, sErrorCode);
  }
}
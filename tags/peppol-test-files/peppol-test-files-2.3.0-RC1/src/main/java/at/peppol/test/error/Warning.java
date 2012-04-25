package at.peppol.test.error;

import com.phloc.commons.error.EErrorLevel;

public final class Warning extends AbstractErrorDefinition {
  public Warning (final String sErrorCode) {
    super (EErrorLevel.WARN, sErrorCode);
  }
}
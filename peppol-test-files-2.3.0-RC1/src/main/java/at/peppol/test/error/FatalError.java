package at.peppol.test.error;

import com.phloc.commons.error.EErrorLevel;

public final class FatalError extends AbstractErrorDefinition {
  public FatalError (final String sErrorCode) {
    super (EErrorLevel.FATAL_ERROR, sErrorCode);
  }
}
package at.peppol.validation.rules.mock;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;


import com.phloc.commons.collections.ContainerHelper;

public final class TestDocument {
  private final String m_sFilename;
  private final Set <AbstractErrorDefinition> m_aExpectedErrors = new HashSet <AbstractErrorDefinition> ();

  public TestDocument (@Nonnull final String sFilename, final AbstractErrorDefinition... aExpectedErrors) {
    m_sFilename = sFilename;
    if (aExpectedErrors != null)
      for (final AbstractErrorDefinition aExpectedError : aExpectedErrors)
        if (aExpectedError != null)
          m_aExpectedErrors.add (aExpectedError);
  }

  @Nonnull
  public String getFilename () {
    return m_sFilename;
  }

  @Nonnull
  public Set <AbstractErrorDefinition> getAllExpectedErrors () {
    return ContainerHelper.makeUnmodifiable (m_aExpectedErrors);
  }
}

package at.peppol.test.error;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;

public final class TestResource {
  private final IReadableResource m_aRes;
  private final Set <AbstractErrorDefinition> m_aExpectedErrors = new HashSet <AbstractErrorDefinition> ();

  public TestResource (@Nonnull final IReadableResource aRes, final Set <AbstractErrorDefinition> aExpectedErrors) {
    m_aRes = aRes;
    if (aExpectedErrors != null)
      m_aExpectedErrors.addAll (aExpectedErrors);
  }

  /**
   * @return The XML resource path
   */
  @Nonnull
  public IReadableResource getResource () {
    return m_aRes;
  }

  /**
   * @return The filename of the underlying resources
   */
  @Nonnull
  public String getFilename () {
    return m_aRes.getPath ();
  }

  /**
   * @return The expected validation errors
   */
  @Nonnull
  public Set <AbstractErrorDefinition> getAllExpectedErrors () {
    return ContainerHelper.makeUnmodifiable (m_aExpectedErrors);
  }
}
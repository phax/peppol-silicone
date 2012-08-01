package at.peppol.validation.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.id.IHasID;

public final class RuleSourceItem implements IHasID <String> {
  private final File m_aDirectory;
  private final List <RuleSourceBusinessRule> m_aBusinessRules = new ArrayList <RuleSourceBusinessRule> ();

  public RuleSourceItem (@Nonnull final File aDirectory) {
    if (!aDirectory.isDirectory ())
      throw new IllegalArgumentException (aDirectory + " is not a directory!");
    m_aDirectory = aDirectory;
  }

  @Nonnull
  RuleSourceItem addBussinessRule (@Nonnull @Nonempty final String sSourceFilename,
                                   @Nonnull @Nonempty final String sOutputDirectory,
                                   @Nonnull @Nonempty final String sFilePrefix) {
    m_aBusinessRules.add (new RuleSourceBusinessRule (new File (m_aDirectory, sSourceFilename),
                                                      new File (m_aDirectory, sOutputDirectory),
                                                      sFilePrefix));
    return this;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_aDirectory.getName ();
  }

  @Nonnull
  public List <RuleSourceBusinessRule> getAllBusinessRuleFiles () {
    return ContainerHelper.newList (m_aBusinessRules);
  }
}

package at.peppol.validation.tools.sch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.id.IHasID;

public final class RuleSourceItem implements IHasID <String> {
  private final File m_aDirectory;
  private final String m_sID;
  private final List <RuleSourceBusinessRule> m_aBusinessRules = new ArrayList <RuleSourceBusinessRule> ();

  public RuleSourceItem (@Nonnull final File aDirectory) {
    if (!aDirectory.isDirectory ())
      throw new IllegalArgumentException (aDirectory + " is not a directory!");
    m_aDirectory = aDirectory;
    m_sID = aDirectory.getName ().toUpperCase (Locale.US);
  }

  @Nonnull
  public RuleSourceItem addBussinessRule (@Nonnull @Nonempty final String sSourceFilename) {
    m_aBusinessRules.add (new RuleSourceBusinessRule (new File (m_aDirectory, sSourceFilename),
                                                      new File (m_aDirectory, "schematron"),
                                                      m_sID,
                                                      null));
    return this;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public List <RuleSourceBusinessRule> getAllBusinessRuleFiles () {
    return ContainerHelper.newList (m_aBusinessRules);
  }
}

package at.peppol.validation.tools;

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
  private final List <RuleSourceCodeList> m_aCodeLists = new ArrayList <RuleSourceCodeList> ();
  private final List <RuleSourceBusinessRule> m_aBusinessRules = new ArrayList <RuleSourceBusinessRule> ();

  public RuleSourceItem (@Nonnull final File aDirectory) {
    if (!aDirectory.isDirectory ())
      throw new IllegalArgumentException (aDirectory + " is not a directory!");
    m_aDirectory = aDirectory;
    m_sID = aDirectory.getName ().toUpperCase (Locale.US);
  }

  @Nonnull
  public RuleSourceItem addCodeList (@Nonnull @Nonempty final String sSourceFilename) {
    m_aCodeLists.add (new RuleSourceCodeList (new File (m_aDirectory, sSourceFilename), new File (m_aDirectory,
                                                                                                  "codelist"), m_sID));
    return this;
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
  public List <RuleSourceCodeList> getAllCodeLists () {
    return ContainerHelper.newList (m_aCodeLists);
  }

  @Nonnull
  public List <RuleSourceBusinessRule> getAllBusinessRules () {
    return ContainerHelper.newList (m_aBusinessRules);
  }
}

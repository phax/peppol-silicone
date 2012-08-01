package at.peppol.validation.tools;

import java.io.File;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;

public final class RuleSourceBusinessRule {
  private final File m_aSourceFile;
  private final File m_aOutputDirectory;
  private final String m_sFilePrefix;

  public RuleSourceBusinessRule (@Nonnull final File aSourceFilename,
                                 @Nonnull final File aOutputDirectory,
                                 @Nonnull @Nonempty final String sFilePrefix) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aOutputDirectory);
    m_aSourceFile = aSourceFilename;
    m_aOutputDirectory = aOutputDirectory;
    m_sFilePrefix = sFilePrefix;
  }

  @Nonnull
  public File getSourceFile () {
    return m_aSourceFile;
  }

  @Nonnull
  public File getOutputDirectory () {
    return m_aOutputDirectory;
  }

  @Nonnull
  @Nonempty
  public String getFilePrefix () {
    return m_sFilePrefix;
  }

  @Nonnull
  public File getSchematronAbstractFile (@Nonnull @Nonempty final String sRuleID) {
    return new File (m_aOutputDirectory, m_sFilePrefix + "-" + sRuleID + "-abstract.sch");
  }

  @Nonnull
  public File getSchematronBindingFile (@Nonnull @Nonempty final String sBindingName,
                                        @Nonnull @Nonempty final String sRuleID) {
    return new File (m_aOutputDirectory, m_sFilePrefix + "-" + sBindingName + "-" + sRuleID + "-test.sch");
  }
}

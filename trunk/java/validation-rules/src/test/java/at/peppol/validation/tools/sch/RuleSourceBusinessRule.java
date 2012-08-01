package at.peppol.validation.tools.sch;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.string.StringHelper;

public final class RuleSourceBusinessRule {
  private final File m_aSourceFile;
  private final File m_aOutputDirectory;
  private final String m_sID;
  private final String m_sCodeList;

  public RuleSourceBusinessRule (@Nonnull final File aSourceFilename,
                                 @Nonnull final File aOutputDirectory,
                                 @Nonnull @Nonempty final String sID,
                                 @Nullable final String sCodeList) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aOutputDirectory);
    m_aSourceFile = aSourceFilename;
    m_aOutputDirectory = aOutputDirectory;
    m_sID = sID;
    m_sCodeList = sCodeList;
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
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public File getSchematronAbstractFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sTransaction + "-abstract.sch");
  }

  @Nonnull
  public File getSchematronBindingFile (@Nonnull @Nonempty final String sBindingName,
                                        @Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sBindingName + "-" + sTransaction + "-test.sch");
  }

  @Nonnull
  public File getSchematronCodeListFile () {
    return new File (m_aOutputDirectory, m_sID + "-" + m_sCodeList + ".sch");
  }

  @Nonnull
  public File getSchematronAssemblyFile (@Nonnull @Nonempty final String sBindingName,
                                         @Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sBindingName + "-" + sTransaction + ".sch");
  }

  public boolean hasCodeList () {
    return StringHelper.hasText (m_sCodeList);
  }

  public String getCodeList () {
    return m_sCodeList;
  }
}

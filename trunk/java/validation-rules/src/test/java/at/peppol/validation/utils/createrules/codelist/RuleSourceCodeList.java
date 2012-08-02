package at.peppol.validation.utils.createrules.codelist;

import java.io.File;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;

public final class RuleSourceCodeList {
  private final File m_aSourceFile;
  private final File m_aCodeListOutputDirectory;
  private final File m_aSchematronOutputDirectory;
  private final String m_sID;

  public RuleSourceCodeList (@Nonnull final File aSourceFilename,
                             @Nonnull final File aCodeListOutputDirectory,
                             @Nonnull final File aSchematronOutputDirectory,
                             @Nonnull @Nonempty final String sID) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aCodeListOutputDirectory);
    FileOperations.createDirIfNotExisting (aSchematronOutputDirectory);
    m_aSourceFile = aSourceFilename;
    m_aCodeListOutputDirectory = aCodeListOutputDirectory;
    m_aSchematronOutputDirectory = aSchematronOutputDirectory;
    m_sID = sID;
  }

  @Nonnull
  public File getSourceFile () {
    return m_aSourceFile;
  }

  @Nonnull
  public File getGCFile (@Nonnull @Nonempty final String sCodeListName) {
    return new File (m_aCodeListOutputDirectory, sCodeListName + ".gc");
  }

  @Nonnull
  public File getCVAFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aCodeListOutputDirectory, m_sID + "-" + sTransaction + "-codes.cva");
  }

  @Nonnull
  public File getXSLTFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aCodeListOutputDirectory, m_sID + "-" + sTransaction + "-codes.sch.xslt");
  }

  @Nonnull
  public File getSchematronFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aSchematronOutputDirectory, m_sID + "-" + sTransaction + "-codes.sch");
  }
}

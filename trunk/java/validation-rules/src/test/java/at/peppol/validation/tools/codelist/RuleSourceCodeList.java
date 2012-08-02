package at.peppol.validation.tools.codelist;

import java.io.File;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;

public final class RuleSourceCodeList {
  private final File m_aSourceFile;
  private final File m_aOutputDirectory;
  private final String m_sID;

  public RuleSourceCodeList (@Nonnull final File aSourceFilename,
                             @Nonnull final File aOutputDirectory,
                             @Nonnull @Nonempty final String sID) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aOutputDirectory);
    m_aSourceFile = aSourceFilename;
    m_aOutputDirectory = aOutputDirectory;
    m_sID = sID;
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
  public File getGCFile (@Nonnull @Nonempty final String sCodeListName) {
    return new File (m_aOutputDirectory, sCodeListName + ".gc");
  }

  @Nonnull
  public File getCVAFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sTransaction + "-codes.cva");
  }

  @Nonnull
  public static String getXSLTFilename (@Nonnull @Nonempty final String sID,
                                        @Nonnull @Nonempty final String sTransaction) {
    return sID + "-" + sTransaction + "-codes.sch.xslt";
  }

  @Nonnull
  public File getXSLTFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, getXSLTFilename (m_sID, sTransaction));
  }
}

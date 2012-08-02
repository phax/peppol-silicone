package at.peppol.validation.tools.sch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.string.StringHelper;

public final class RuleSourceBusinessRule {
  private final File m_aSourceFile;
  private final File m_aOutputDirectory;
  private final String m_sID;
  private final String m_sCodeListTransaction;
  private final List <File> m_aResultSCHFiles = new ArrayList <File> ();

  public RuleSourceBusinessRule (@Nonnull final File aSourceFilename,
                                 @Nonnull final File aOutputDirectory,
                                 @Nonnull @Nonempty final String sID,
                                 @Nullable final String sCodeListTransaction) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aOutputDirectory);
    m_aSourceFile = aSourceFilename;
    m_aOutputDirectory = aOutputDirectory;
    m_sID = sID;
    m_sCodeListTransaction = sCodeListTransaction;
  }

  @Nonnull
  public File getSourceFile () {
    return m_aSourceFile;
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
    return new File (m_aOutputDirectory, m_sID + "-" + m_sCodeListTransaction + "-codes.sch");
  }

  @Nonnull
  public File getSchematronAssemblyFile (@Nonnull @Nonempty final String sBindingName,
                                         @Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sBindingName + "-" + sTransaction + ".sch");
  }

  public boolean hasCodeList () {
    return StringHelper.hasText (m_sCodeListTransaction);
  }

  public String getCodeList () {
    return m_sCodeListTransaction;
  }

  public void addResultSchematronFile (@Nonnull final File aSCHFile) {
    m_aResultSCHFiles.add (aSCHFile);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <File> getAllResultSchematronFiles () {
    return ContainerHelper.newList (m_aResultSCHFiles);
  }
}

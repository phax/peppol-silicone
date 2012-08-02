package at.peppol.validation.tools.codelist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;

@NotThreadSafe
public final class CVA {
  private final String m_sTransaction;
  private final List <File> m_aGCFiles;
  private final List <CVAContext> m_aContexts = new ArrayList <CVAContext> ();

  public CVA (@Nonnull @Nonempty final String sTransaction, @Nonnull final List <File> aGCFiles) {
    m_sTransaction = sTransaction;
    m_aGCFiles = ContainerHelper.newList (aGCFiles);
  }

  public void addContext (final String sID,
                          final String sItem,
                          final String sScope,
                          final String sValue,
                          final String sSeverity,
                          final String sMessage) {
    m_aContexts.add (new CVAContext (sID, sItem, sScope, sValue, sSeverity, sMessage));
  }

  public String getTransaction () {
    return m_sTransaction;
  }
}

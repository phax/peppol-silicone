package at.peppol.validation.tools.codelist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;

@NotThreadSafe
public final class CVAData {
  private final String m_sTransaction;
  private final List <CVAContextData> m_aContexts = new ArrayList <CVAContextData> ();

  public CVAData (@Nonnull @Nonempty final String sTransaction) {
    m_sTransaction = sTransaction;
  }

  public void addContext (@Nonnull @Nonempty final String sID,
                          @Nonnull @Nonempty final String sItem,
                          @Nullable final String sScope,
                          @Nonnull @Nonempty final String sCodeListName,
                          @Nonnull @Nonempty final String sSeverity,
                          @Nonnull @Nonempty final String sMessage) {
    m_aContexts.add (new CVAContextData (sID, sItem, sScope, sCodeListName, sSeverity, sMessage));
  }

  public String getTransaction () {
    return m_sTransaction;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <CVAContextData> getAllContexts () {
    return ContainerHelper.newList (m_aContexts);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <String> getAllUsedCodeListNames () {
    final Set <String> ret = new TreeSet <String> ();
    for (final CVAContextData aContextData : m_aContexts)
      ret.add (aContextData.getCodeListName ());
    return ret;
  }
}

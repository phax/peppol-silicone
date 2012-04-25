package at.peppol.test.error;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

public abstract class AbstractErrorDefinition implements Comparable <AbstractErrorDefinition> {
  private final EErrorLevel m_eLevel;
  final String m_sErrorCode;

  public AbstractErrorDefinition (@Nonnull final EErrorLevel eLevel, @Nonnull @Nonempty final String sErrorCode) {
    m_eLevel = eLevel;
    m_sErrorCode = sErrorCode;
  }

  @Nonnull
  public EErrorLevel getLevel () {
    return m_eLevel;
  }

  @Nonnull
  @Nonempty
  public String getErrorCode () {
    return m_sErrorCode;
  }

  public int compareTo (@Nonnull final AbstractErrorDefinition rhs) {
    int i = m_eLevel.compareTo (rhs.m_eLevel);
    if (i == 0)
      i = m_sErrorCode.compareTo (rhs.m_sErrorCode);
    return i;
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractErrorDefinition rhs = (AbstractErrorDefinition) o;
    return m_eLevel.equals (rhs.m_eLevel) && m_sErrorCode.equals (rhs.m_sErrorCode);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_eLevel).append (m_sErrorCode).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (null).append ("level", m_eLevel).append ("errCode", m_sErrorCode).toString ();
  }
}

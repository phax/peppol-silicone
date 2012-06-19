package at.peppol.commons.identifier.validator;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.IsSPIInterface;
import com.phloc.commons.annotations.Nonempty;

/**
 * An SPI interface to validate arbitrary identifier values (independent of the
 * identifier type). This interface can e.g. be used to validate VATIN numbers
 * that are used as PEPPOL participant IDs.
 * 
 * @author philip
 */
@IsSPIInterface
public interface IParticipantIdentifierValidatorSPI {
  /**
   * Check if the passed issuing agency UD (like "9908") is supported by this
   * validator implementation.
   * 
   * @param sScheme
   *        The identifier scheme to check for support. Is neither null nor
   *        empty.
   * @return <code>true</code> if this validator can validate values of the
   *         passed scheme, <code>false</code> otherwise.
   */
  boolean isSupportedIssuingAgency (@Nonnull @Nonempty String sScheme);

  /**
   * Check if the identifier value is valid. This method is only called if the
   * check for the scheme ({@link #isSupportedIssuingAgency(String)} returned
   * <code>true</code>.
   * 
   * @param sValue
   *        The identifier value to be checked. Is neither null nor empty.
   * @return <code>true</code> if the identifier value is valid,
   *         <code>false</code> if not.
   */
  boolean isValueValid (@Nonnull @Nonempty String sValue);
}

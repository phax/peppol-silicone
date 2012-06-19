package at.peppol.commons.identifier.validator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.IExtendedParticipantIdentifier;

import com.phloc.commons.lang.ServiceLoaderBackport;

/**
 * A wrapper around the custom identifier validator implementations.
 * 
 * @author philip
 */
@Immutable
public final class IdentifierValidator {
  private static final List <IParticipantIdentifierValidatorSPI> s_aParticipantIDValidators = new ArrayList <IParticipantIdentifierValidatorSPI> ();

  static {
    for (final IParticipantIdentifierValidatorSPI aValidator : ServiceLoaderBackport.load (IParticipantIdentifierValidatorSPI.class))
      s_aParticipantIDValidators.add (aValidator);
  }

  private IdentifierValidator () {}

  public static boolean isValidParticipantIdentifier (@Nonnull final IExtendedParticipantIdentifier aParticipantID) {
    if (aParticipantID == null)
      throw new NullPointerException ("participantID");

    // Only handle our default scheme
    if (!CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME.equals (aParticipantID.getScheme ()))
      return false;

    boolean bAtLeastOneSupported = false;
    final String sIssuingAgencyID = aParticipantID.getIssuingAgencyID ();
    final String sLocal = aParticipantID.getLocalParticipantID ();
    for (final IParticipantIdentifierValidatorSPI aValidator : s_aParticipantIDValidators) {
      if (aValidator.isSupportedIssuingAgency (sIssuingAgencyID)) {
        if (aValidator.isValueValid (sLocal))
          return true;
        bAtLeastOneSupported = true;
      }
    }
    return bAtLeastOneSupported ? false : true;
  }
}

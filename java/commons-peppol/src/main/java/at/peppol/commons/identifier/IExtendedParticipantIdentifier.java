package at.peppol.commons.identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.peppol.busdox.identifier.IParticipantIdentifier;

public interface IExtendedParticipantIdentifier extends IParticipantIdentifier {
  @Nullable
  String getIssuingAgencyID ();

  @Nullable
  String getLocalParticipantID ();

  @Nonnull
  String getURIEncoded ();

  @Nonnull
  String getURIPercentEncoded ();
}

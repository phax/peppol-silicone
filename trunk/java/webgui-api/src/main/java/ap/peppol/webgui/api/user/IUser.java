package ap.peppol.webgui.api.user;

import java.util.Locale;

import javax.annotation.Nullable;

import com.phloc.commons.id.IHasID;

public interface IUser extends IHasID <String> {
  @Nullable
  String getEmailAddress ();

  @Nullable
  String getFirstName ();

  @Nullable
  String getLastName ();

  @Nullable
  Locale getDesiredLocale ();

  @Nullable
  String getPasswordHash ();
}

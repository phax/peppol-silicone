package ap.peppol.webgui.api.user;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;

public interface IUser extends IHasID <String> {
  /**
   * @return The email address of the user. Also the login name.
   */
  @Nonnull
  @Nonempty
  String getEmailAddress ();

  /**
   * @return The hashed password of the user.
   */
  @Nonnull
  @Nonempty
  String getPasswordHash ();

  /**
   * @return The first name of the user.
   */
  @Nullable
  String getFirstName ();

  /**
   * @return The last name of the user.
   */
  @Nullable
  String getLastName ();

  /**
   * @return The desired locale of the user.
   */
  @Nullable
  Locale getDesiredLocale ();
}

package ap.peppol.webgui.security.exchange;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ap.peppol.webgui.security.role.IRole;
import ap.peppol.webgui.security.role.Role;
import ap.peppol.webgui.security.user.IUser;
import ap.peppol.webgui.security.user.User;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.phloc.commons.microdom.convert.IMicroTypeConverterRegistry;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.microdom.utils.MicroUtils;

@IsSPIImplementation
public final class WebGUIMicroTypeConverterRegistrarSPI implements IMicroTypeConverterRegistrarSPI {
  private static final class UserMicroTypeConverter implements IMicroTypeConverter {
    private static final String ATTR_ID = "id";
    private static final String ATTR_DESIREDLOCALE = "desiredlocale";
    private static final String ELEMENT_EMAILADDRESS = "emailaddress";
    private static final String ELEMENT_PASSWORDHASH = "passwordhash";
    private static final String ELEMENT_FIRSTNAME = "firstname";
    private static final String ELEMENT_LASTNAME = "lastname";

    @Nonnull
    public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                                @Nullable final String sNamespaceURI,
                                                @Nonnull final String sTagName) {
      final IUser aUser = (IUser) aObject;
      final IMicroElement eUser = new MicroElement (sNamespaceURI, sTagName);
      eUser.setAttribute (ATTR_ID, aUser.getID ());
      eUser.appendElement (ELEMENT_EMAILADDRESS).appendText (aUser.getEmailAddress ());
      eUser.appendElement (ELEMENT_PASSWORDHASH).appendText (aUser.getPasswordHash ());
      if (aUser.getFirstName () != null)
        eUser.appendElement (ELEMENT_FIRSTNAME).appendText (aUser.getFirstName ());
      if (aUser.getLastName () != null)
        eUser.appendElement (ELEMENT_LASTNAME).appendText (aUser.getLastName ());
      if (aUser.getDesiredLocale () != null)
        eUser.setAttribute (ATTR_DESIREDLOCALE, aUser.getDesiredLocale ().toString ());
      return eUser;
    }

    @Nonnull
    public IUser convertToNative (@Nonnull final IMicroElement aElement) {
      final String sID = aElement.getAttribute (ATTR_ID);
      final String sEmailAddress = MicroUtils.getChildTextContent (aElement, ELEMENT_EMAILADDRESS);
      final String sPasswordHash = MicroUtils.getChildTextContent (aElement, ELEMENT_PASSWORDHASH);
      final String sFirstName = MicroUtils.getChildTextContent (aElement, ELEMENT_FIRSTNAME);
      final String sLastName = MicroUtils.getChildTextContent (aElement, ELEMENT_LASTNAME);
      final String sDesiredLocale = aElement.getAttribute (ATTR_DESIREDLOCALE);
      final Locale aDesiredLocale = sDesiredLocale == null ? null : LocaleCache.get (sDesiredLocale);
      return new User (sID, sEmailAddress, sPasswordHash, sFirstName, sLastName, aDesiredLocale);
    }
  }

  private static final class RoleMicroTypeConverter implements IMicroTypeConverter {
    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";

    @Nonnull
    public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                                @Nullable final String sNamespaceURI,
                                                @Nonnull final String sTagName) {
      final IRole aRole = (IRole) aObject;
      final IMicroElement eUser = new MicroElement (sNamespaceURI, sTagName);
      eUser.setAttribute (ATTR_ID, aRole.getID ());
      eUser.setAttribute (ATTR_NAME, aRole.getName ());
      return eUser;
    }

    @Nonnull
    public IRole convertToNative (@Nonnull final IMicroElement aElement) {
      final String sID = aElement.getAttribute (ATTR_ID);
      final String sName = aElement.getAttribute (ATTR_NAME);
      return new Role (sID, sName);
    }
  }

  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry) {
    aRegistry.registerMicroElementTypeConverter (User.class, new UserMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (Role.class, new RoleMicroTypeConverter ());
  }
}

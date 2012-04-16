package at.peppol.webgui.api;

import javax.annotation.Nonnull;

import at.peppol.webgui.folder.UserFolder;
import at.peppol.webgui.folder.UserFolderMicroTypeConverter;
import at.peppol.webgui.security.role.Role;
import at.peppol.webgui.security.role.RoleMicroTypeConverter;
import at.peppol.webgui.security.user.User;
import at.peppol.webgui.security.user.UserMicroTypeConverter;
import at.peppol.webgui.security.usergroup.UserGroup;
import at.peppol.webgui.security.usergroup.UserGroupMicroTypeConverter;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.phloc.commons.microdom.convert.IMicroTypeConverterRegistry;

@IsSPIImplementation
public final class WebGUIMicroTypeConverterRegistrarSPI implements IMicroTypeConverterRegistrarSPI {
  public void registerMicroTypeConverter (@Nonnull final IMicroTypeConverterRegistry aRegistry) {
    // Security
    aRegistry.registerMicroElementTypeConverter (User.class, new UserMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (UserGroup.class, new UserGroupMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (Role.class, new RoleMicroTypeConverter ());

    // Folders
    aRegistry.registerMicroElementTypeConverter (UserFolder.class, new UserFolderMicroTypeConverter ());
  }
}

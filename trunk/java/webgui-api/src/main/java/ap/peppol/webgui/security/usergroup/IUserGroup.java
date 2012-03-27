package ap.peppol.webgui.security.usergroup;

import ap.peppol.webgui.security.role.IRoleContainer;
import ap.peppol.webgui.security.user.IUserContainer;

import com.phloc.commons.id.IHasID;
import com.phloc.commons.name.IHasDisplayName;

/**
 * Represents a single user group encapsulating 0-n users.
 * 
 * @author philip
 */
public interface IUserGroup extends IHasID <String>, IHasDisplayName, IUserContainer, IRoleContainer {
  /* empty */
}

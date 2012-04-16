package at.peppol.webgui.folder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroElement;

public final class UserFolderTreeMicroTypeConverter implements IMicroTypeConverter {
  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName) {
    final UserFolderTree aUserFolderTree = (UserFolderTree) aObject;
    final IMicroElement eUserFolderTree = new MicroElement (sNamespaceURI, sTagName);
    eUserFolderTree.appendChild (aUserFolderTree.getAsXML ());
    return eUserFolderTree;
  }

  @Nonnull
  public UserFolderTree convertToNative (@Nonnull final IMicroElement eUserFolderTree) {
    return new UserFolderTree (eUserFolderTree.getFirstChildElement ());
  }
}

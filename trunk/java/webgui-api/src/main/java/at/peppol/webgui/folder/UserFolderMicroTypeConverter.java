package at.peppol.webgui.folder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroElement;

public final class UserFolderMicroTypeConverter implements IMicroTypeConverter {
  private static final String ATTR_ID = "id";
  private static final String ATTR_NAME = "name";
  private static final String ELEMENT_DOC = "doc";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName) {
    final IUserFolder aUserFolder = (IUserFolder) aObject;
    final IMicroElement eUserFolder = new MicroElement (sNamespaceURI, sTagName);
    eUserFolder.setAttribute (ATTR_ID, aUserFolder.getID ());
    eUserFolder.setAttribute (ATTR_NAME, aUserFolder.getDisplayName ());
    // Sort for reproducible results
    for (final String sDocID : ContainerHelper.getSorted (aUserFolder.getAllDocumentIDs ()))
      eUserFolder.appendElement (ELEMENT_DOC).setAttribute (ATTR_ID, sDocID);
    return eUserFolder;
  }

  @Nonnull
  public IUserFolder convertToNative (@Nonnull final IMicroElement eRole) {
    final String sID = eRole.getAttribute (ATTR_ID);
    final String sName = eRole.getAttribute (ATTR_NAME);
    final UserFolder aUserFolder = new UserFolder (sID, sName);
    for (final IMicroElement eDoc : eRole.getChildElements (ELEMENT_DOC))
      aUserFolder.addDocument (eDoc.getAttribute (ATTR_ID));
    return aUserFolder;
  }
}

package at.peppol.webgui.folder;

import java.util.Set;

import javax.annotation.Nullable;

import com.phloc.commons.id.IHasID;
import com.phloc.commons.name.IHasDisplayName;

public interface IUserFolder extends IHasID <String>, IHasDisplayName {
  boolean containsDocumentWithID (@Nullable String sDocumentID);

  Set <String> getAllDocumentIDs ();

  int getDocumentCount ();

  boolean hasDocuments ();
}

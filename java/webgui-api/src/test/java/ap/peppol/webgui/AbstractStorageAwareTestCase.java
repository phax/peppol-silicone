package ap.peppol.webgui;

import java.io.File;

import ap.peppol.webgui.api.io.StorageIO;

import com.phloc.scopes.web.mock.AbstractWebScopeAwareTestCase;

public class AbstractStorageAwareTestCase extends AbstractWebScopeAwareTestCase {
  @Override
  protected void beforeSingleTest () throws Exception {
    super.beforeSingleTest ();
    if (!StorageIO.isBasePathInited ())
      StorageIO.initBasePath (new File ("target/junit").getAbsolutePath ());
  }
}

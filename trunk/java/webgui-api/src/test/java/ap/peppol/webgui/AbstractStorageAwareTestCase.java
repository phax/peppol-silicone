package ap.peppol.webgui;

import java.io.File;

import ap.peppol.webgui.api.io.StorageIO;

import com.phloc.scopes.nonweb.mock.AbstractScopeAwareTestCase;

public class AbstractStorageAwareTestCase extends AbstractScopeAwareTestCase {
  @Override
  protected void beforeSingleTest () throws Exception {
    super.beforeSingleTest ();
    StorageIO.initBasePath (new File ("target/junit").getAbsolutePath ());
  }
}

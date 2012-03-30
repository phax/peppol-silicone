package at.peppol.webgui;

import java.io.File;

import at.peppol.webgui.io.StorageIO;

import com.phloc.scopes.web.mock.AbstractWebScopeAwareTestCase;

public abstract class AbstractStorageAwareTestCase extends AbstractWebScopeAwareTestCase {
  static {
    // Init the base path once
    StorageIO.initBasePath (new File ("target/junit").getAbsolutePath ());
  }
}

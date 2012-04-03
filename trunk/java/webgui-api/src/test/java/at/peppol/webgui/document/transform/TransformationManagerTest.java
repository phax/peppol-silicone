package at.peppol.webgui.document.transform;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import at.peppol.webgui.document.EDocumentMetaType;

import com.phloc.commons.io.resource.ClassPathResource;

/**
 * Test class for class {@link TransformationManager}.
 * 
 * @author philip
 */
public final class TransformationManagerTest {
  @Test
  public void testNoConverter () {
    assertNull (TransformationManager.transformInvoiceToUBL (new TransformationSource (EDocumentMetaType.BINARY,
                                                                                       new ClassPathResource ("dummy.xml"),
                                                                                       null)));
  }
}

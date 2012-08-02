package at.peppol.validation.tools.sch.odf;

import javax.annotation.Nonnull;

import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.common.TextExtractor;

/**
 * Extract all texts except annotations
 * 
 * @author philip
 */
public class SimpleTextExtractor extends TextExtractor {
  public SimpleTextExtractor (final OdfElement element) {
    super (element);
  }

  @Override
  public void visit (final OdfElement element) {
    if (!(element instanceof OfficeAnnotationElement))
      super.visit (element);
  }

  public static String getText (@Nonnull final Component aComponent) {
    return new SimpleTextExtractor (aComponent.getOdfElement ()).getText ();
  }
}

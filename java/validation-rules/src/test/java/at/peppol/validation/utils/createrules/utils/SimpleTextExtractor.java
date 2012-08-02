package at.peppol.validation.utils.createrules.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.common.TextExtractor;

/**
 * Extract all texts except annotations
 * 
 * @author philip
 */
@Immutable
public class SimpleTextExtractor extends TextExtractor {
  public SimpleTextExtractor (final OdfElement element) {
    super (element);
  }

  @Override
  public void visit (final OdfElement element) {
    // Ignore all notes
    if (!(element instanceof OfficeAnnotationElement))
      super.visit (element);
  }

  public static String getText (@Nonnull final Component aComponent) {
    return new SimpleTextExtractor (aComponent.getOdfElement ()).getText ();
  }
}

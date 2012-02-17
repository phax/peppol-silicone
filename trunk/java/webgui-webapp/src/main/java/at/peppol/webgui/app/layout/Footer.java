package at.peppol.webgui.app.layout;

import javax.annotation.Nonnull;

import at.peppol.webgui.app.CWebGui;

import com.phloc.datetime.PDTFactory;
import com.phloc.html.hc.IHCNode;
import com.phloc.html.hc.html.HCA;
import com.phloc.html.hc.html.HCA_Target;
import com.phloc.html.hc.html.HCP;
import com.phloc.html.hc.impl.HCEntityNode;
import com.phloc.html.hc.impl.HCTextNode;

/**
 * Responsible class for creating the footer
 * 
 * @author philip
 */
final class Footer {
  private Footer () {}

  @Nonnull
  public static IHCNode createFooter () {
    return new HCP ().addChildren (HCEntityNode.newCopy (),
                                   new HCTextNode (" "),
                                   new HCA (CWebGui.URL_PEPPOL).setTarget (HCA_Target.BLANK).addChild ("PEPPOL"),
                                   new HCTextNode (" " + PDTFactory.getCurrentYear ()));
  }
}

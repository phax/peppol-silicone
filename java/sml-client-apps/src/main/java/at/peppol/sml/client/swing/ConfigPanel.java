/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.sml.client.swing;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.compare.AbstractComparator;


/**
 * Configuration Panel
 *
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class ConfigPanel extends JPanel {
  /**
   * Comparator for {@link ESML} objects, where the public ones (with client
   * certificate) come first, and the others later on.
   *
   * @author PEPPOL.AT, BRZ, Philip Helger
   */
  private static final class ESMLNameComparator extends AbstractComparator <ESML> {
    public ESMLNameComparator () {}

    @Override
    protected int mainCompare (final ESML eSML1, final ESML eSML2) {
      final boolean b1 = eSML1.requiresClientCertificate ();
      final boolean b2 = eSML2.requiresClientCertificate ();
      return b1 != b2 ? (b1 ? -1 : +1) : eSML1.getManagementHostName ().compareTo (eSML2.getManagementHostName ());
    }
  }

  private final MainFrame mainFrame;
  private JComboBox cbHost;
  private JTextField tfId;

  public ConfigPanel (final MainFrame aMainFrame) {
    this.mainFrame = aMainFrame;
    init ();

    aMainFrame.displayStatus ("");
  }

  private void init () {
    setLayout (new MigLayout ("fill", "[label][left]", ""));
    // setPreferredSize (new Dimension (450, 100));
    setBorder (BorderFactory.createTitledBorder ("Client Configuration"));

    final Vector <WrappedSMLInfo> vHost = new Vector <WrappedSMLInfo> ();
    for (final ESML eSml : ContainerHelper.getSorted (ESML.values (), new ESMLNameComparator ()))
      vHost.add (new WrappedSMLInfo (eSml));

    final JLabel lHost = new JLabel ("SML Hostname: ");
    cbHost = new JComboBox (vHost);

    final JLabel lId = new JLabel ("SMP ID: ");
    tfId = new JTextField (15);

    add (lHost);
    add (cbHost, "width 100%,wrap");
    add (lId);
    add (tfId, "width 100%,wrap");

    load ();
  }

  public void load () {
    cbHost.setSelectedItem (mainFrame.getSmlHost ());
    tfId.setText (mainFrame.getSmpId ());
  }

  public void save () {
    mainFrame.setSmlHost ((ISMLInfo) cbHost.getSelectedItem ());
    mainFrame.setSmpId (tfId.getText ());
  }
}

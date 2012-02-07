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
package eu.peppol.sml.client.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.phloc.commons.string.StringHelper;

import eu.peppol.sml.client.ESMLAction;

/**
 * Action Panel
 *
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class ActionPanel extends JPanel implements ActionListener {
  private final MainFrame mainFrame;
  private JComboBox cbAction;
  private JTextField tfParams;
  private JTextArea taOut;
  private JScrollPane spOut;

  public ActionPanel (final MainFrame aMainFrame) {
    this.mainFrame = aMainFrame;
    init ();

    aMainFrame.displayStatus ("Ready.");
  }

  private void init () {
    setLayout (new MigLayout ("", "[label][left]", ""));
    setPreferredSize (new Dimension (450, 100));
    setBorder (BorderFactory.createTitledBorder ("Command Parameters"));

    final Vector <ESMLAction> vAction = new Vector <ESMLAction> ();
    for (final ESMLAction eA : ESMLAction.values ())
      vAction.add (eA);

    cbAction = new JComboBox (vAction);
    cbAction.addActionListener (this);

    tfParams = new JTextField ();

    taOut = new JTextArea ();
    taOut.setLineWrap (true);
    taOut.setWrapStyleWord (true);
    // The height is relevant
    taOut.setSize (new Dimension (0, 100));
    spOut = new JScrollPane ();
    spOut.setMinimumSize (taOut.getSize ());
    spOut.setAutoscrolls (true);
    spOut.getViewport ().add (taOut);

    add (new JLabel ("Action: "), "aligny top");
    add (cbAction, "width 100%,wrap");

    add (new JLabel ("Params: "), "aligny top");
    add (tfParams, "width 100%,wrap");

    add (new JLabel ("Response: "), "aligny top");
    add (spOut, "width 100%,wrap");
  }

  public void executeAction () {
    final String sResult = mainFrame.performAction ((ESMLAction) cbAction.getSelectedItem (), tfParams.getText ());
    final String out = "[" + getDate () + "] " + sResult + "\n";

    taOut.append (out);
  }

  private static String getDate () {
    final Calendar cal = Calendar.getInstance ();
    return StringHelper.leadingZero (cal.get (Calendar.HOUR_OF_DAY), 2) +
           ":" +
           StringHelper.leadingZero (cal.get (Calendar.MINUTE), 2) +
           ":" +
           StringHelper.leadingZero (cal.get (Calendar.SECOND), 2);
  }

  @Override
  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().toString ().equals (cbAction.getActionCommand ())) {
      final ESMLAction eAction = (ESMLAction) cbAction.getSelectedItem ();
      final int params = eAction.getRequiredParameters ();

      if (params == 0) {
        mainFrame.displayStatus ("No parameters required.");
        tfParams.setEditable (false);
      }
      else {
        final StringBuilder aMsg = new StringBuilder (params + " paramters are required: ");
        int nIndex = 0;
        for (final String sDescription : eAction.getRequiredParameterDescriptions ()) {
          if (++nIndex > 1)
            aMsg.append (", ");
          aMsg.append (sDescription);
        }

        mainFrame.displayStatus (aMsg.toString ());
        tfParams.setEditable (true);
      }
    }
  }
}

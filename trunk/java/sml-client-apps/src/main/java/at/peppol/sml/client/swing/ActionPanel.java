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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Vector;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import at.peppol.sml.client.ESMLAction;

import com.phloc.datetime.PDTFactory;
import com.phloc.datetime.format.PDTToString;

/**
 * Action Panel
 * 
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class ActionPanel extends JPanel implements ActionListener {
  private final MainFrame m_aMainFrame;
  private JComboBox m_aCBAction;
  private JTextField m_aTFParams;
  private JTextArea m_aTAOut;
  private JScrollPane m_aSPOut;

  public ActionPanel (final MainFrame aMainFrame) {
    m_aMainFrame = aMainFrame;
    _init ();

    aMainFrame.displayStatus ("Ready.");
  }

  private void _init () {
    setLayout (new MigLayout ("", "[label][left]", ""));
    setPreferredSize (new Dimension (450, 100));
    setBorder (BorderFactory.createTitledBorder ("Command Parameters"));

    final Vector <ESMLAction> vAction = new Vector <ESMLAction> ();
    for (final ESMLAction eA : ESMLAction.values ())
      vAction.add (eA);

    m_aCBAction = new JComboBox (vAction);
    m_aCBAction.addActionListener (this);

    m_aTFParams = new JTextField ();

    m_aTAOut = new JTextArea ();
    m_aTAOut.setLineWrap (true);
    m_aTAOut.setWrapStyleWord (true);
    // The height is relevant
    m_aTAOut.setSize (new Dimension (0, 100));
    m_aSPOut = new JScrollPane ();
    m_aSPOut.setMinimumSize (m_aTAOut.getSize ());
    m_aSPOut.setAutoscrolls (true);
    m_aSPOut.getViewport ().add (m_aTAOut);

    add (new JLabel ("Action: "), "aligny top");
    add (m_aCBAction, "width 100%,wrap");

    add (new JLabel ("Params: "), "aligny top");
    add (m_aTFParams, "width 100%,wrap");

    add (new JLabel ("Response: "), "aligny top");
    add (m_aSPOut, "width 100%,wrap");
  }

  public void executeAction () {
    final String sResult = m_aMainFrame.performAction ((ESMLAction) m_aCBAction.getSelectedItem (),
                                                       m_aTFParams.getText ());
    final String out = "[" + _getCurrentDate () + "] " + sResult + "\n";

    m_aTAOut.append (out);
  }

  @Nonnull
  private static String _getCurrentDate () {
    return PDTToString.getAsString (PDTFactory.getCurrentLocalTime (), Locale.US);
  }

  @Override
  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().toString ().equals (m_aCBAction.getActionCommand ())) {
      final ESMLAction eAction = (ESMLAction) m_aCBAction.getSelectedItem ();
      final int nParams = eAction.getRequiredParameters ();

      if (nParams == 0) {
        m_aMainFrame.displayStatus ("No parameters required.");
        m_aTFParams.setEditable (false);
      }
      else {
        final StringBuilder aMsg = new StringBuilder (nParams + " paramters are required: ");
        int nIndex = 0;
        for (final String sDescription : eAction.getRequiredParameterDescriptions ()) {
          if (++nIndex > 1)
            aMsg.append (", ");
          aMsg.append (sDescription);
        }

        m_aMainFrame.displayStatus (aMsg.toString ());
        m_aTFParams.setEditable (true);
      }
    }
  }
}

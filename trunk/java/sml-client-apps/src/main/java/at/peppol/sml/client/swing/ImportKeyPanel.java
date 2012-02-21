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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class ImportKeyPanel extends JPanel implements ActionListener {
  private final MainFrame mainFrame;
  private JTextField tfPath, tfPassword;
  private JButton bBrowse;

  public ImportKeyPanel (final MainFrame aMainFrame) {
    this.mainFrame = aMainFrame;
    init ();

    aMainFrame.displayStatus ("");
  }

  private void init () {
    setLayout (new MigLayout ("fill", "[label][left]", ""));
    // setPreferredSize (mainFrame.getContentPanelDimension ());
    setBorder (BorderFactory.createTitledBorder ("Import Key"));

    final JLabel lPath = new JLabel ("Path to key store: ");
    lPath.setAlignmentX (RIGHT_ALIGNMENT);
    tfPath = new JTextField (30);
    final JLabel lPassword = new JLabel ("Key store password: ");
    tfPassword = new JPasswordField (15);
    bBrowse = new JButton ("Browse");
    bBrowse.addActionListener (this);

    add (lPath);
    add (tfPath, "width 100%");
    add (bBrowse, "right,wrap");
    add (lPassword);
    add (tfPassword, "span 2,width 100%,wrap");

    load ();
  }

  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().equals (bBrowse.getText ())) {
      getPath ();
    }
  }

  public void load () {
    tfPath.setText (AppProperties.getInstance ().getKeyStorePath ());
    tfPassword.setText (AppProperties.getInstance ().getKeyStorePassword ());
  }

  public void save () {
    mainFrame.setKeyStore (tfPath.getText (), tfPassword.getText ());
  }

  private void getPath () {
    final JFileChooser fileChooser = new JFileChooser ();
    fileChooser.setAcceptAllFileFilterUsed (false);
    fileChooser.setFileFilter (new FileFilter () {
      @Override
      public boolean accept (final File f) {
        return f.isDirectory () || f.getName ().matches (".*\\.(jks)");
      }

      @Override
      public String getDescription () {
        return "Java Keystores (*.jks)";
      }
    });

    fileChooser.showOpenDialog (null);

    final File file = fileChooser.getSelectedFile ();
    if (file != null)
      tfPath.setText (file.getAbsolutePath ());
  }
}

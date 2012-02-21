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

import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;
import at.peppol.sml.client.swing.MainFrame.EPanel;

/**
 * Content Panel
 * 
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class MainContentPanel extends JPanel implements ActionListener, ItemListener {
  private final MainFrame mainFrame;
  private JButton bNext, bSettings, bAction, bBrowse;
  private ConfigPanel configPanel;
  private ImportKeyPanel importKeyPanel;
  private ActionPanel actionPanel;
  private JTextField tfPath;
  private Checkbox cbEnable;

  public MainContentPanel (final MainFrame aMainFrame) {
    this.mainFrame = aMainFrame;
    setLayout (new MigLayout ("debug,insets 0,fill"));
  }

  public void init () {
    // insets 0 important to avoid control resizing
    bNext = new JButton ("Next  >");
    bNext.addActionListener (this);
    bAction = new JButton ("Execute");
    bAction.addActionListener (this);
    bSettings = new JButton ("<  Settings");
    bSettings.addActionListener (this);

    cbEnable = new Checkbox ("Use properties: ", AppProperties.getInstance ().isPropertiesEnabled ());
    cbEnable.addItemListener (this);

    tfPath = new JTextField (30);
    bBrowse = new JButton ("Browse");
    bBrowse.addActionListener (this);

    if (configPanel != null || importKeyPanel != null) {
      if (configPanel != null)
        add (configPanel, "width 100%, wrap");

      if (importKeyPanel != null)
        add (importKeyPanel, "width 100%,wrap");

      add (cbEnable, "gaptop 15, split 3");
      add (tfPath, "gaptop 15, width 100%, split 2");
      add (bBrowse, "gaptop 15, right, wrap");
      add (bNext, "gaptop 20, align right, wrap");

      setPropertiesEnabled (AppProperties.getInstance ().isPropertiesEnabled ());
    }

    if (actionPanel != null) {
      add (actionPanel, "dock north");
      add (bSettings, "gaptop 20, split 2");
      add (bAction, "gaptop 20, dock east");
    }

    if (configPanel != null)
      configPanel.load ();
    if (importKeyPanel != null)
      importKeyPanel.load ();

    tfPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  @Override
  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().equals (bNext.getText ()))
      save ();
    if (e.getActionCommand ().equals (bAction.getText ()))
      executeAction ();
    if (e.getActionCommand ().equals (bSettings.getText ()))
      openSettings ();
    if (e.getActionCommand ().equals (bBrowse.getText ())) {
      getPath ();
    }
  }

  @Override
  public void itemStateChanged (final ItemEvent e) {
    if (cbEnable.getState ()) {
      setPropertiesEnabled (true);
    }
    else {
      setPropertiesEnabled (false);
    }
  }

  private void load () {
    AppProperties.getInstance ().readProperties ();

    if (configPanel != null)
      configPanel.load ();
    if (importKeyPanel != null)
      importKeyPanel.load ();

    tfPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  private void save () {
    if (configPanel != null)
      configPanel.save ();
    if (importKeyPanel != null)
      importKeyPanel.save ();

    AppProperties.getInstance ().setPropertiesPath (tfPath.getText ());

    if (cbEnable.getState ())
      AppProperties.getInstance ().writeProperties ();

    mainFrame.setContent (EPanel.ACTION_PANEL);
    mainFrame.displayStatus ("Data saved");
  }

  private void executeAction () {
    mainFrame.displayStatus ("Executed action.");

    try {
      actionPanel.executeAction ();
      mainFrame.displayStatus ("Ready.");
    }
    catch (final Exception e) {
      mainFrame.displayStatus ("Error.");
      throw new RuntimeException ("Error executing action: " + e);
    }
  }

  private void openSettings () {
    mainFrame.setContent (EPanel.CONFIG_PANELS);
    mainFrame.displayStatus ("Ready.");
  }

  public void setConfigPanels () {
    configPanel = new ConfigPanel (mainFrame);
    importKeyPanel = new ImportKeyPanel (mainFrame);
  }

  public void setActionPanel () {
    actionPanel = new ActionPanel (mainFrame);
  }

  private void getPath () {
    final JFileChooser fileChooser = new JFileChooser ();
    fileChooser.setAcceptAllFileFilterUsed (false);
    fileChooser.setFileFilter (new FileFilter () {
      @Override
      public boolean accept (final File f) {
        return f.isDirectory () || f.getName ().matches (".*\\.(properties)");
      }

      @Override
      public String getDescription () {
        return "Properties files (*.properties)";
      }
    });

    fileChooser.showOpenDialog (null);

    final File file = fileChooser.getSelectedFile ();
    if (file != null) {
      tfPath.setText (file.getAbsolutePath ());

      AppProperties.getInstance ().setPropertiesPath (file.getAbsolutePath ());
      load ();
    }
  }

  private void setPropertiesEnabled (final boolean enabled) {
    tfPath.setEditable (enabled);
    bBrowse.setEnabled (enabled);

    AppProperties.getInstance ().setPropertiesEnabled (enabled);

    if (enabled)
      load ();
    else
      clean ();
  }

  private void clean () {
    AppProperties.getInstance ().clear ();

    if (configPanel != null)
      configPanel.load ();
    if (importKeyPanel != null)
      importKeyPanel.load ();
  }
}

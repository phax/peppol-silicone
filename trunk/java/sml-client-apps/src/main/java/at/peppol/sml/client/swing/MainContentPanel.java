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

import net.miginfocom.swing.MigLayout;
import at.peppol.sml.client.swing.MainFrame.EPanel;
import at.peppol.sml.client.swing.utils.FileFilterProperties;

/**
 * Content Panel
 * 
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class MainContentPanel extends JPanel implements ActionListener, ItemListener {
  private final MainFrame m_aMainFrame;
  private JButton m_aBtnNext, m_aBtnSettings, m_aBtnAction, m_aBtnBrowse;
  private ConfigPanel m_aConfigPanel;
  private ImportKeyPanel m_aImportKeyPanel;
  private ActionPanel m_aActionPanel;
  private JTextField m_aTFPath;
  private Checkbox m_aCBEnable;

  public MainContentPanel (final MainFrame aMainFrame) {
    m_aMainFrame = aMainFrame;
    setLayout (new MigLayout ("debug,insets 0,fill"));
  }

  public void init () {
    // insets 0 important to avoid control resizing
    m_aBtnNext = new JButton ("Next  >");
    m_aBtnNext.addActionListener (this);
    m_aBtnAction = new JButton ("Execute");
    m_aBtnAction.addActionListener (this);
    m_aBtnSettings = new JButton ("<  Settings");
    m_aBtnSettings.addActionListener (this);

    m_aCBEnable = new Checkbox ("Use properties: ", AppProperties.getInstance ().isPropertiesEnabled ());
    m_aCBEnable.addItemListener (this);

    m_aTFPath = new JTextField (30);
    m_aBtnBrowse = new JButton ("Browse");
    m_aBtnBrowse.addActionListener (this);

    if (m_aConfigPanel != null || m_aImportKeyPanel != null) {
      if (m_aConfigPanel != null)
        add (m_aConfigPanel, "width 100%, wrap");

      if (m_aImportKeyPanel != null)
        add (m_aImportKeyPanel, "width 100%,wrap");

      add (m_aCBEnable, "gaptop 15, split 3");
      add (m_aTFPath, "gaptop 15, width 100%, split 2");
      add (m_aBtnBrowse, "gaptop 15, right, wrap");
      add (m_aBtnNext, "gaptop 20, align right, wrap");

      _setPropertiesEnabled (AppProperties.getInstance ().isPropertiesEnabled ());
    }

    if (m_aActionPanel != null) {
      add (m_aActionPanel, "dock north");
      add (m_aBtnSettings, "gaptop 20, split 2");
      add (m_aBtnAction, "gaptop 20, dock east");
    }

    if (m_aConfigPanel != null)
      m_aConfigPanel.load ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.load ();

    m_aTFPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  @Override
  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().equals (m_aBtnNext.getText ()))
      _save ();
    if (e.getActionCommand ().equals (m_aBtnAction.getText ()))
      _executeAction ();
    if (e.getActionCommand ().equals (m_aBtnSettings.getText ()))
      _openSettings ();
    if (e.getActionCommand ().equals (m_aBtnBrowse.getText ()))
      _getPath ();
  }

  @Override
  public void itemStateChanged (final ItemEvent e) {
    if (m_aCBEnable.getState ()) {
      _setPropertiesEnabled (true);
    }
    else {
      _setPropertiesEnabled (false);
    }
  }

  private void _load () {
    AppProperties.getInstance ().readProperties ();

    if (m_aConfigPanel != null)
      m_aConfigPanel.load ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.load ();

    m_aTFPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  private void _save () {
    if (m_aConfigPanel != null)
      m_aConfigPanel.save ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.save ();

    AppProperties.getInstance ().setPropertiesPath (m_aTFPath.getText ());

    if (m_aCBEnable.getState ())
      AppProperties.getInstance ().writeProperties ();

    m_aMainFrame.setContent (EPanel.ACTION_PANEL);
    m_aMainFrame.displayStatus ("Data saved");
  }

  private void _executeAction () {
    m_aMainFrame.displayStatus ("Executed action.");

    try {
      m_aActionPanel.executeAction ();
      m_aMainFrame.displayStatus ("Ready.");
    }
    catch (final Exception e) {
      m_aMainFrame.displayStatus ("Error.");
      throw new RuntimeException ("Error executing action: " + e);
    }
  }

  private void _openSettings () {
    m_aMainFrame.setContent (EPanel.CONFIG_PANELS);
    m_aMainFrame.displayStatus ("Ready.");
  }

  public void setConfigPanels () {
    m_aConfigPanel = new ConfigPanel (m_aMainFrame);
    m_aImportKeyPanel = new ImportKeyPanel (m_aMainFrame);
  }

  public void setActionPanel () {
    m_aActionPanel = new ActionPanel (m_aMainFrame);
  }

  private void _getPath () {
    final JFileChooser fileChooser = new JFileChooser ();
    fileChooser.setAcceptAllFileFilterUsed (false);
    fileChooser.setFileFilter (new FileFilterProperties ());

    fileChooser.showOpenDialog (null);

    final File file = fileChooser.getSelectedFile ();
    if (file != null) {
      m_aTFPath.setText (file.getAbsolutePath ());

      AppProperties.getInstance ().setPropertiesPath (file.getAbsolutePath ());
      _load ();
    }
  }

  private void _setPropertiesEnabled (final boolean enabled) {
    m_aTFPath.setEditable (enabled);
    m_aBtnBrowse.setEnabled (enabled);

    AppProperties.getInstance ().setPropertiesEnabled (enabled);

    if (enabled)
      _load ();
    else
      _clean ();
  }

  private void _clean () {
    AppProperties.getInstance ().clear ();

    if (m_aConfigPanel != null)
      m_aConfigPanel.load ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.load ();
  }
}

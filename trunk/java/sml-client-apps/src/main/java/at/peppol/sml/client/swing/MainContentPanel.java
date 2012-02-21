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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import at.peppol.sml.client.ESMLAction;
import at.peppol.sml.client.swing.MainFrame.EPanel;
import at.peppol.sml.client.swing.utils.FileFilterProperties;

/**
 * Content Panel
 * 
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
final class MainContentPanel extends JPanel {
  private final MainFrame m_aMainFrame;
  private JButton m_aBtnNext, m_aBtnSettings, m_aBtnAction, m_aBtnBrowse;
  private ConfigPanel m_aConfigPanel;
  private ImportKeyPanel m_aImportKeyPanel;
  private ActionPanel m_aActionPanel;
  private JTextField m_aTFPropertiesPath;
  private Checkbox m_aCBEnable;

  public MainContentPanel (final MainFrame aMainFrame) {
    m_aMainFrame = aMainFrame;
    setLayout (new MigLayout ("debug,insets 0,fill"));
  }

  private void _init () {
    // insets 0 important to avoid control resizing
    m_aBtnNext = new JButton ("Next  >");
    m_aBtnNext.addActionListener (new ActionListener () {
      public void actionPerformed (final ActionEvent e) {
        if (m_aConfigPanel != null)
          m_aConfigPanel.saveData ();
        if (m_aImportKeyPanel != null)
          m_aImportKeyPanel.save ();

        AppProperties.getInstance ().setPropertiesPath (m_aTFPropertiesPath.getText ());

        if (m_aCBEnable.getState ())
          AppProperties.getInstance ().writeProperties ();

        m_aMainFrame.setContent (EPanel.ACTION_PANEL);
        MainStatusBar.setStatus ("Configuration saved");
      }
    });
    m_aBtnAction = new JButton ("Execute");
    m_aBtnAction.addActionListener (new ActionListener () {
      public void actionPerformed (final ActionEvent e) {
        final ESMLAction eAction = m_aActionPanel.getSelectedAction ();
        MainStatusBar.setStatus ("Executing action " + eAction + ".");

        try {
          m_aActionPanel.executeAction ();
          MainStatusBar.setStatus ("Executed action " + eAction + ".");
        }
        catch (final Exception ex) {
          MainStatusBar.setStatus ("Error executing action" + eAction + ".");
          throw new RuntimeException ("Error executing action " + eAction + ": " + ex);
        }
      }
    });
    m_aBtnSettings = new JButton ("<  Settings");
    m_aBtnSettings.addActionListener (new ActionListener () {
      public void actionPerformed (final ActionEvent e) {
        m_aMainFrame.setContent (EPanel.CONFIG_PANELS);
        MainStatusBar.setStatus ("");
      }
    });

    m_aCBEnable = new Checkbox ("Use properties: ", AppProperties.getInstance ().isPropertiesEnabled ());
    m_aCBEnable.addItemListener (new ItemListener () {
      public void itemStateChanged (final ItemEvent e) {
        _setPropertiesEnabled (m_aCBEnable.getState ());
      }
    });
    m_aTFPropertiesPath = new JTextField (30);
    m_aBtnBrowse = new JButton ("Browse");
    m_aBtnBrowse.addActionListener (new ActionListener () {
      public void actionPerformed (final ActionEvent e) {
        final JFileChooser fileChooser = new JFileChooser ();
        fileChooser.setAcceptAllFileFilterUsed (false);
        fileChooser.setFileFilter (new FileFilterProperties ());
        fileChooser.showOpenDialog (null);
        final File aSelectedFile = fileChooser.getSelectedFile ();
        if (aSelectedFile != null) {
          m_aTFPropertiesPath.setText (aSelectedFile.getAbsolutePath ());
          AppProperties.getInstance ().setPropertiesPath (aSelectedFile.getAbsolutePath ());
          _loadFileData ();
        }
      }
    });

    if (m_aConfigPanel != null || m_aImportKeyPanel != null) {
      if (m_aConfigPanel != null)
        add (m_aConfigPanel, "width 100%, wrap");

      if (m_aImportKeyPanel != null)
        add (m_aImportKeyPanel, "width 100%,wrap");

      final JPanel aPropsPanel = new JPanel ();
      aPropsPanel.setLayout (new MigLayout (new LC ().fill (),
                                            new AC ().size ("label").gap ().align ("left"),
                                            new AC ()));
      aPropsPanel.setBorder (BorderFactory.createTitledBorder ("Client properties"));
      aPropsPanel.add (m_aCBEnable);
      aPropsPanel.add (m_aTFPropertiesPath, "width 100%");
      aPropsPanel.add (m_aBtnBrowse, "right, wrap");
      add (aPropsPanel, "width 100%,wrap");

      add (m_aBtnNext, "gapright 20,align right,wrap");

      _setPropertiesEnabled (AppProperties.getInstance ().isPropertiesEnabled ());
    }

    if (m_aActionPanel != null) {
      add (m_aActionPanel, "dock north");
      add (m_aBtnSettings, "gaptop 20, split 2");
      add (m_aBtnAction, "gaptop 20, dock east");
    }

    if (m_aConfigPanel != null)
      m_aConfigPanel.loadData ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.loadData ();

    m_aTFPropertiesPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  private void _clearData () {
    AppProperties.getInstance ().clear ();

    if (m_aConfigPanel != null)
      m_aConfigPanel.loadData ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.loadData ();
  }

  private void _loadFileData () {
    AppProperties.getInstance ().readProperties ();

    if (m_aConfigPanel != null)
      m_aConfigPanel.loadData ();
    if (m_aImportKeyPanel != null)
      m_aImportKeyPanel.loadData ();

    m_aTFPropertiesPath.setText (AppProperties.getInstance ().getPropertiesPath ().getPath ());
  }

  public void setConfigPanels () {
    m_aConfigPanel = new ConfigPanel ();
    m_aImportKeyPanel = new ImportKeyPanel ();
    _init ();
  }

  public void setActionPanel () {
    m_aActionPanel = new ActionPanel ();
    _init ();
  }

  private void _setPropertiesEnabled (final boolean bEnabled) {
    m_aTFPropertiesPath.setEditable (bEnabled);
    m_aBtnBrowse.setEnabled (bEnabled);

    AppProperties.getInstance ().setPropertiesEnabled (bEnabled);

    if (bEnabled)
      _loadFileData ();
    else
      _clearData ();
  }
}

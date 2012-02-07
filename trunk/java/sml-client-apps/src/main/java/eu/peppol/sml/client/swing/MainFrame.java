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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.string.StringHelper;

import eu.peppol.busdox.security.DoNothingTrustManager;
import eu.peppol.busdox.security.KeyStoreUtils;
import eu.peppol.busdox.sml.ESML;
import eu.peppol.busdox.sml.ISMLInfo;
import eu.peppol.sml.client.ESMLAction;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class MainFrame extends JFrame implements ActionListener {
  public static enum EPanel {
    CONFIG_PANELS,
    ACTION_PANEL;
  }

  private static final int FRAME_WIDTH = 530;
  private static final int FRAME_HEIGHT = 330;
  private static final String DEFAULT_PROPERTIES_PATH = ".";
  private static final String DEFAULT_PROPERTIES_NAME = "config.properties";
  private static final boolean DEFAULT_PROPERTIES_ENABLED = true;

  private JMenuItem configMenuItem, actionMenuItem;
  private StatusBar statusBar;
  private ISMLInfo smlHost;
  private String smpId;
  private String keyStorePath;
  private String keyStorePwd;
  private File propertiesPath = new File (DEFAULT_PROPERTIES_PATH, DEFAULT_PROPERTIES_NAME);
  private JPanel contentPanel;
  private final PropertiesReader pReader;
  private boolean propertiesEnabled = DEFAULT_PROPERTIES_ENABLED;

  public MainFrame () {
    pReader = new PropertiesReader ();
    init ();
  }

  private void init () {
    setTitle ("PEPPOL SML Client");

    final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
    setLocation ((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
    setPreferredSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));
    setMinimumSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));

    setDefaultCloseOperation (EXIT_ON_CLOSE);
    setLayout (new MigLayout ("fillx,wrap", "[fill,left]", "[][fill][grow, fill][]"));

    statusBar = new StatusBar ();

    contentPanel = new JPanel (new MigLayout ("fill,insets 0"));
    setContent (EPanel.CONFIG_PANELS);

    add (contentPanel, "width 100%,wrap");
    add (statusBar, "dock south");

    pack ();
  }

  public void displayStatus (final String message) {
    statusBar.showMessage (message);
  }

  @Override
  public void actionPerformed (final ActionEvent e) {
    if (e.getActionCommand ().equals (configMenuItem.getText ())) {
      setContent (EPanel.CONFIG_PANELS);
    }
    if (e.getActionCommand ().equals (actionMenuItem.getText ())) {
      setContent (EPanel.ACTION_PANEL);
    }
  }

  public void setContent (final EPanel panel) {
    contentPanel.removeAll ();
    final ContentPanel cp = new ContentPanel (this);
    cp.setLayout (new MigLayout ("fill,insets 0"));

    switch (panel) {
      case CONFIG_PANELS:
        cp.setConfigPanels ();
        break;
      case ACTION_PANEL:
        cp.setActionPanel ();
        break;
    }

    cp.init ();
    contentPanel.add (cp, "width 100%");
    contentPanel.updateUI ();
  }

  public void setSmlHost (final ISMLInfo host) {
    this.smlHost = host;
  }

  public ISMLInfo getSmlHost () {
    return smlHost;
  }

  public void setSmpId (final String id) {
    this.smpId = id;
  }

  public String getSmpId () {
    return smpId;
  }

  public void setKeyStorePath (final String sKeyStorePath) {
    this.keyStorePath = sKeyStorePath;
  }

  public void setKeyStorePwd (final String sKeyStorePwd) {
    this.keyStorePwd = sKeyStorePwd;
  }

  public String getKeyStorePath () {
    return keyStorePath;
  }

  public String getKeyStorePwd () {
    return keyStorePwd;
  }

  public File getPropertiesPath () {
    return propertiesPath;
  }

  public void setPropertiesPath (final String sPropertiesPath) {
    this.propertiesPath = new File (sPropertiesPath);
  }

  public boolean isPropertiesEnabled () {
    return propertiesEnabled;
  }

  public void setPropertiesEnabled (final boolean bPropertiesEnabled) {
    this.propertiesEnabled = bPropertiesEnabled;
  }

  public void setKeyStore (final String path, final String password) {
    keyStorePath = path;
    keyStorePwd = password;

    try {
      // Main key storage
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStoreFromFile (path, password);

      // Key manager
      final KeyManagerFactory aKeyManagerFactory = KeyManagerFactory.getInstance ("SunX509");
      aKeyManagerFactory.init (aKeyStore, password.toCharArray ());

      // Assign key manager and empty trust manager to SSL context
      final SSLContext aSSLCtx = SSLContext.getInstance ("TLS");
      aSSLCtx.init (aKeyManagerFactory.getKeyManagers (),
                    new TrustManager [] { new DoNothingTrustManager () },
                    VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLCtx.getSocketFactory ());
    }
    catch (final Exception ex) {
      displayStatus (ex.getMessage ());
    }
  }

  public String performAction (final ESMLAction action, final String parameter) {
    final String [] params = parameter.split (" ");

    GuiSMLController ctrl;
    ctrl = new GuiSMLController ();

    if (smlHost == null) {
      displayStatus ("Error.");
      return "No SML Hostname set.";
    }
    ctrl.setManageServiceMetadataEndpointAddress (smlHost);

    if (StringHelper.hasNoText (smpId)) {
      displayStatus ("Error.");
      return "No SMP ID set.";
    }
    ctrl.setSmpID (smpId);

    return ctrl.handleCommand (action, params);
  }

  public void readProperties () {
    if (pReader.readProperties (propertiesPath)) {
      smlHost = null;
      final String sHostName = pReader.getHostname ();
      for (final ESML eSML : ESML.values ())
        if (eSML.getManagementHostName ().equals (sHostName)) {
          smlHost = new WrappedSMLInfo (eSML);
          break;
        }
      smpId = pReader.getSmpId ();
      keyStorePath = pReader.getKeyStorePath ();
      keyStorePwd = pReader.getKeyStorePwd ();

      displayStatus ("Loaded properties file.");
    }
    else {
      displayStatus ("Cannot load properties file.");
    }
  }

  public void writeProperties () {
    if (smlHost == null) {
      displayStatus ("No hostname set.");
      throw new NullPointerException ("No hostname set.");
    }
    pReader.setHostname (smlHost.getManagementHostName ());
    pReader.setSmpId (smpId);
    pReader.setKeyStorePath (keyStorePath);
    pReader.setKeyStorePwd (keyStorePwd);

    if (pReader.writeProperties (propertiesPath)) {
      displayStatus ("Properties wrote successfully.");
    }
    else {
      displayStatus ("Cannot write properties file.");
    }
  }
}

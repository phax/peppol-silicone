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
import java.awt.Toolkit;
import java.security.KeyStore;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import at.peppol.commons.security.DoNothingTrustManager;
import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.sml.client.ESMLAction;

import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.string.StringHelper;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class MainFrame extends JFrame {
  public static enum EPanel {
    CONFIG_PANELS,
    ACTION_PANEL;
  }

  private static final int FRAME_WIDTH = 530;
  private static final int FRAME_HEIGHT = 360;

  private final JPanel m_aContentPanel;
  private final StatusBar m_aStatusBar;

  public MainFrame () {
    setTitle ("PEPPOL SML Client");

    final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
    setLocation ((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
    setPreferredSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));
    setMinimumSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));
    setDefaultCloseOperation (EXIT_ON_CLOSE);
    setLayout (new MigLayout (new LC ().fillX ().wrap (), new AC ().fill ().align ("left"), new AC ().gap ()
                                                                                                     .fill ()
                                                                                                     .gap ()
                                                                                                     .grow ()
                                                                                                     .fill ()
                                                                                                     .gap ()));

    m_aStatusBar = new StatusBar ();

    m_aContentPanel = new JPanel (new MigLayout ("fill,insets 0"));
    setContent (EPanel.CONFIG_PANELS);

    add (m_aContentPanel, "width 100%,wrap");
    add (m_aStatusBar, "dock south");

    pack ();
  }

  public void displayStatus (final String message) {
    m_aStatusBar.showMessage (message);
  }

  public void setContent (@Nonnull final EPanel ePanel) {
    m_aContentPanel.removeAll ();
    final MainContentPanel cp = new MainContentPanel (this);
    cp.setLayout (new MigLayout ("fill,insets 0"));

    switch (ePanel) {
      case CONFIG_PANELS:
        cp.setConfigPanels ();
        break;
      case ACTION_PANEL:
        cp.setActionPanel ();
        break;
    }

    cp.init ();
    m_aContentPanel.add (cp, "width 100%");
    m_aContentPanel.updateUI ();
  }

  public void setKeyStore (final String path, final String password) {
    AppProperties.getInstance ().setKeyStorePath (path);
    AppProperties.getInstance ().setKeyStorePassword (password);

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
    final String [] aParams = parameter.split (" ");

    final GuiSMLController ctrl = new GuiSMLController ();

    final AppProperties aAP = AppProperties.getInstance ();
    if (aAP.getSMLInfo () == null) {
      displayStatus ("Error.");
      return "No SML Hostname set.";
    }
    GuiSMLController.setSMLInfo (aAP.getSMLInfo ());

    if (StringHelper.hasNoText (aAP.getSMPID ())) {
      displayStatus ("Error.");
      return "No SMP ID set.";
    }
    GuiSMLController.setSMPID (aAP.getSMPID ());

    return ctrl.handleCommand (action, aParams);
  }
}

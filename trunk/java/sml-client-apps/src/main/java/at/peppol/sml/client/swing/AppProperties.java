package at.peppol.sml.client.swing;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;

import com.phloc.commons.state.ESuccess;

public final class AppProperties {
  private static final class SingletonHolder {
    static final AppProperties s_aInstance = new AppProperties ();
  }

  private static final String DEFAULT_PROPERTIES_PATH = ".";
  private static final String DEFAULT_PROPERTIES_NAME = "config.properties";
  private static final boolean DEFAULT_PROPERTIES_ENABLED = true;

  private ISMLInfo m_aSMLInfo;
  private String m_sSMPID;
  private String m_sKeyStorePath;
  private String m_sKeyStorePassword;
  private File m_aPropertiesPath = new File (DEFAULT_PROPERTIES_PATH, DEFAULT_PROPERTIES_NAME);
  private final PropertiesReader m_aPropsReader = new PropertiesReader ();
  private boolean m_bPropertiesEnabled = DEFAULT_PROPERTIES_ENABLED;

  private AppProperties () {}

  @Nonnull
  public static AppProperties getInstance () {
    return SingletonHolder.s_aInstance;
  }

  @Nonnull
  public ESuccess readProperties () {
    if (!m_aPropsReader.readProperties (m_aPropertiesPath))
      return ESuccess.FAILURE;
    m_aSMLInfo = null;
    final String sHostName = m_aPropsReader.getHostname ();
    for (final ESML eSML : ESML.values ())
      if (eSML.getManagementHostName ().equals (sHostName)) {
        m_aSMLInfo = new WrappedSMLInfo (eSML);
        break;
      }
    m_sSMPID = m_aPropsReader.getSmpId ();
    m_sKeyStorePath = m_aPropsReader.getKeyStorePath ();
    m_sKeyStorePassword = m_aPropsReader.getKeyStorePwd ();
    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess writeProperties () {
    if (m_aSMLInfo == null)
      throw new IllegalStateException ("No SML hostname set.");
    m_aPropsReader.setHostname (m_aSMLInfo.getManagementHostName ());
    m_aPropsReader.setSmpId (m_sSMPID);
    m_aPropsReader.setKeyStorePath (m_sKeyStorePath);
    m_aPropsReader.setKeyStorePwd (m_sKeyStorePassword);
    return ESuccess.valueOf (m_aPropsReader.writeProperties (m_aPropertiesPath));
  }

  public void setSMLInfo (final ISMLInfo aSMLInfo) {
    m_aSMLInfo = aSMLInfo;
  }

  @Nullable
  public ISMLInfo getSMLInfo () {
    return m_aSMLInfo;
  }

  public void setSMPID (final String sSMPID) {
    m_sSMPID = sSMPID;
  }

  public String getSMPID () {
    return m_sSMPID;
  }

  public void setKeyStorePath (final String sKeyStorePath) {
    m_sKeyStorePath = sKeyStorePath;
  }

  public void setKeyStorePassword (final String sKeyStorePassword) {
    m_sKeyStorePassword = sKeyStorePassword;
  }

  public String getKeyStorePath () {
    return m_sKeyStorePath;
  }

  public String getKeyStorePassword () {
    return m_sKeyStorePassword;
  }

  public File getPropertiesPath () {
    return m_aPropertiesPath;
  }

  public void setPropertiesPath (final String sPropertiesPath) {
    m_aPropertiesPath = new File (sPropertiesPath);
  }

  public boolean isPropertiesEnabled () {
    return m_bPropertiesEnabled;
  }

  public void setPropertiesEnabled (final boolean bPropertiesEnabled) {
    m_bPropertiesEnabled = bPropertiesEnabled;
  }

  public void clear () {
    setSMLInfo (null);
    setSMPID ("");
    setKeyStorePath (null);
    setKeyStorePassword (null);
  }
}

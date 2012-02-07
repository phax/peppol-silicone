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
package eu.peppol.busdox.ipmapper;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.peppol.common.ConfigFile;

/**
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 * @see IDNSInternalMapper
 */
public class ConfiguredDNSMapper implements IDNSInternalMapper {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ConfiguredDNSMapper.class);

  /**
   * string representing a mapping of external IPs that should be translated
   * into internal ones. the string must conform to the following pattern
   * externalip1,externalip2=internalip1:port1 externalip3=internalip2:port2
   * etc.
   */
  private static final String HOSTNAME_MAPPING = "busdox.net.dns.map.hostname_mapping";

  private final Map <String, String> m_aNameMapping = new HashMap <String, String> ();

  public ConfiguredDNSMapper () {
    // Init mapping
    final String sIPMapping = ConfigFile.getInstance ().getString (HOSTNAME_MAPPING);
    _verifyAndSetMapping (sIPMapping);
  }

  private void _verifyAndSetMapping (@Nonnull final String sIPMapping) {
    if (sIPMapping == null) {
      s_aLogger.warn ("no external to internal IP translation configuration property found. see property '" +
                      HOSTNAME_MAPPING +
                      "'...");
      return;
    }
    // cleanup property string
    // should look like 1.1.1.1,2.2.2.2=4.4.4.4:8080 5.5.5.5=6.6.6.6 etc.
    // 1.1.1.1,2.2.2.2 := list of external IPs, comma separated
    // 1.1.1.1 and 2.2.2.2 are mapped to the internal IP 4.4.4.4
    // 4.4.4.4 is assumed to listen on a socket on port 8080
    final String sCleared = sIPMapping.replaceAll (" +", " ");
    if (s_aLogger.isInfoEnabled ()) {
      s_aLogger.info ("found configuration string '" + sCleared + "' for property '" + HOSTNAME_MAPPING + "'...");
    }
    // get all external to internal mappings should look like
    // 1.1.1.1,2.2.2.2=4.4.4.4:8080 etc.
    final StringTokenizer aMappings = new StringTokenizer (sCleared, " ");
    if (aMappings.countTokens () <= 0) {
      s_aLogger.warn ("empty external to internal IP translation configuration property found. see property '" +
                      HOSTNAME_MAPPING +
                      "'...");
      return;
    }

    while (aMappings.hasMoreElements ()) {
      // should look like 1.1.1.1,2.2.2.2 (a comma separated list of external
      // IPs)
      final StringTokenizer aOneMapping = new StringTokenizer (aMappings.nextToken (), "=");
      if (aOneMapping.countTokens () < 2) {
        s_aLogger.warn (String.format ("invalid external to internal IP translation configuration '%s' found.",
                                       aOneMapping.nextToken ()));
        continue;
      }
      final StringTokenizer aExternalIPs = new StringTokenizer (aOneMapping.nextToken (), ",");
      if (aExternalIPs.countTokens () < 1) {
        s_aLogger.warn ("invalid external to internal IP translation configuration property found. at least one external IP must be defined.");
        continue;
      }
      // should look like 2.2.2.2
      String sInternalIP;
      if (!aOneMapping.hasMoreTokens () ||
          (sInternalIP = aOneMapping.nextToken ()) == null ||
          sInternalIP.length () < 3) {
        s_aLogger.warn ("invalid external to internal IP translation configuration found. at least one internal per IP mapping must be defined.");
        continue;
      }
      while (aExternalIPs.hasMoreElements ()) {
        final String sExt = aExternalIPs.nextToken ();
        m_aNameMapping.put (sExt, sInternalIP);
      }
    }
  }

  @Override
  @Nonnull
  public ISocketType mapInternal (@Nonnull final InetAddress aExternalAdr) {
    if (aExternalAdr == null)
      throw new NullPointerException ("externalAdr must not be null...");

    final String sHostAddr = aExternalAdr.getHostAddress ();

    final String sInternalMapping = m_aNameMapping.get (sHostAddr);
    if (sInternalMapping != null) {
      s_aLogger.info ("mapping of external IP '" + sHostAddr + "' to internal IP '" + sInternalMapping + "' found...");
      return SocketType.createSocketType (sInternalMapping);
    }

    s_aLogger.info ("external IP '" + sHostAddr + "' has no mapping to an internal IP...");
    return SocketType.createSocketType (aExternalAdr.getHostName ());
  }
}

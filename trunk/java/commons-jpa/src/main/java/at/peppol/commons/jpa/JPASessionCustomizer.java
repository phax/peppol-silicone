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
package at.peppol.commons.jpa;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import com.phloc.commons.CGlobal;
import com.phloc.commons.state.EChange;

/**
 * Class for customizing JPA sessions.<br>
 * Set the class name in the property
 * <code>eclipselink.session.customizer</code><br>
 * Should have a public no-argument ctor
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class JPASessionCustomizer implements SessionCustomizer {
  private static final AtomicInteger s_aLogLevel = new AtomicInteger (CGlobal.ILLEGAL_UINT);

  public JPASessionCustomizer () {}

  @Nonnull
  public static EChange setGlobalLogLevel (final int nLogLevel) {
    if (nLogLevel >= SessionLog.ALL && nLogLevel <= SessionLog.OFF) {
      if (s_aLogLevel.getAndSet (nLogLevel) != nLogLevel)
        return EChange.CHANGED;
    }
    return EChange.UNCHANGED;
  }

  public static int getGlobalLogLevel () {
    return s_aLogLevel.get ();
  }

  public void customize (final Session aSession) throws Exception {
    final int nLogLevel = getGlobalLogLevel ();
    if (nLogLevel != CGlobal.ILLEGAL_UINT) {
      // create a custom logger and assign it to the session
      final SessionLog aCustomLogger = new JPALogger ();
      aCustomLogger.setLevel (nLogLevel);
      aSession.setSessionLog (aCustomLogger);
    }
  }
}

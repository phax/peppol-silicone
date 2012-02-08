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
package at.peppol.transport.lime.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import at.peppol.transport.IMessageMetadata;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class ResourceMemoryStore {
  private static final class SingletonHolder {
    static final ResourceMemoryStore s_aInstance = new ResourceMemoryStore ();
  }

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, IMessageMetadata> resourceMap = new HashMap <String, IMessageMetadata> ();

  private ResourceMemoryStore () {}

  @Nonnull
  public static ResourceMemoryStore getInstance () {
    return SingletonHolder.s_aInstance;
  }

  public boolean isStored (final String messageID, final String urlStr) {
    m_aRWLock.readLock ().lock ();
    try {
      final String key = getKey (messageID, urlStr);
      return resourceMap.containsKey (key);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean createResource (final String messageID, final String urlStr, final IMessageMetadata soapHeader) {
    m_aRWLock.writeLock ().lock ();
    try {
      final String key = getKey (messageID, urlStr);
      if (resourceMap.containsKey (key))
        return false;
      resourceMap.put (key, soapHeader);
      return true;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public IMessageMetadata getMessage (final String messageID, final String urlStr) {
    m_aRWLock.readLock ().lock ();
    try {
      final String key = getKey (messageID, urlStr);
      return resourceMap.get (key);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  private static String getKey (final String messageID, final String urlStr) {
    return messageID + urlStr;
  }
}

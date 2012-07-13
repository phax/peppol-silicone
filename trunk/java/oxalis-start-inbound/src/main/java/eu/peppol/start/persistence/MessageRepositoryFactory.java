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
package eu.peppol.start.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 28.11.11
 *         Time: 21:00
 */
public class MessageRepositoryFactory {

  public static ServiceLoader <MessageRepository> messageRepositoryServiceLoader = ServiceLoader.load (MessageRepository.class);

  private static final Logger log = LoggerFactory.getLogger (MessageRepositoryFactory.class);

  /** Prevents any attempts to create instances of this class */
  private MessageRepositoryFactory () {}

  private enum MessageRepositorySingleton {
    INSTANCE;

    MessageRepository messageRepository;

    private MessageRepositorySingleton () {
      messageRepository = getInstanceWithDefault ();
    }
  }

  /** Provides a singleton instance of MessageRepository */
  public static MessageRepository getInstance () {
    return MessageRepositorySingleton.INSTANCE.messageRepository;
  }

  /**
   * Attempts to get an instance of the message persistence, throwing an
   * exception if an implementation could not be found in any
   * META-INF/service/....MessageRepository
   * 
   * @return instance of MessageRepository
   */
  public static MessageRepository getInstanceNoDefault () {
    final MessageRepository messageRepository = getInstance ();
    if (getInstance () == null) {
      throw new IllegalStateException ("No implementation of " +
                                       MessageRepository.class.getCanonicalName () +
                                       " found in class path. Searched for files matching /META-INF/services/" +
                                       MessageRepository.class.getCanonicalName () +
                                       " in class path");
    }

    return messageRepository;
  }

  static MessageRepository getInstanceWithDefault () {
    final List <MessageRepository> messageRepositoryImplementations = new ArrayList <MessageRepository> ();
    for (final MessageRepository messageRepository : messageRepositoryServiceLoader) {
      messageRepositoryImplementations.add (messageRepository);
    }

    if (messageRepositoryImplementations.isEmpty ()) {
      return new SimpleMessageRepository ();
    }

    if (messageRepositoryImplementations.size () > 1) {
      log.warn ("Found " + messageRepositoryImplementations.size () + " implementations of " + MessageRepository.class);
    }

    // Provides the first available implementation
    return messageRepositoryImplementations.get (0);

  }
}

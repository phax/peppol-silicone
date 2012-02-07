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
package eu.peppol.busdox.identifier;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.exceptions.InitializationException;

/**
 * Constants on BUSDOX identifiers used in PEPPOL.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CIdentifier {
  /**
   * The identifier prefix for DNS name creation.
   */
  public static final String DNS_HASHED_IDENTIFIER_PREFIX = "B-";

  /**
   * The maximum length of a participant identifier scheme.
   */
  @Nonnegative
  public static final int MAX_PARTICIPANT_IDENTIFIER_SCHEME_LENGTH = 25;

  /**
   * The regular expression to be used for validating participant identifier
   * schemes (not values!).
   */
  public static final String PARTICIPANT_IDENTIFIER_SCHEME_REGEX = "[a-z0-9]+-actorid-[a-z0-9]+";

  /**
   * The default identifier scheme ID to be used for participants/businesses.<br>
   * The matching values have the format "agency:id" whereas agency should be
   * within the code-list.<br>
   * Please note that this is a change to the PEPPOL Common definitions chapter
   * 3.4!
   *
   * @see eu.peppol.busdox.identifier.actorid.IdentifierIssuingAgencyManager
   */
  public static final String DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME = "iso6523-actorid-upis";

  /**
   * The default document identifier scheme.<br>
   * See PEPPOL Common definitions chapter 3.5
   */
  public static final String DEFAULT_DOCUMENT_IDENTIFIER_SCHEME = "busdox-docid-qns";

  /**
   * The default process identifier scheme.<br>
   * Overrides PEPPOL Common definitions chapter 3.6!
   */
  public static final String DEFAULT_PROCESS_IDENTIFIER_SCHEME = "cenbii-procid-ubl";

  /**
   * The default process identifier to indicate that no default process belongs
   * to it.<br>
   * See PEPPOL Common definitions chapter 3.6
   */
  public static final String DEFAULT_PROCESS_IDENTIFIER_NOPROCESS = "busdox:noprocess";

  /**
   * The delimiter used for the service group identifier.
   */
  public static final String URL_SCHEME_VALUE_SEPARATOR = "::";

  static {
    // Some consistency assertions
    if (!IdentifierUtils.isValidParticipantIdentifierScheme (DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME))
      throw new InitializationException ("The default participant scheme '" +
                                         DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME +
                                         "' is not valid!");
  }

  private CIdentifier () {}
}

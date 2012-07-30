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
package at.peppol.smp.client.functest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.concurrent.Immutable;

import at.peppol.commons.identifier.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.identifier.actorid.EPredefinedIdentifierIssuingAgency;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.ReadonlyUsernamePWCredentials;
import at.peppol.smp.client.UserId;

import com.phloc.commons.exceptions.InitializationException;

/**
 * Constants for the SMP
 * 
 * @author philip
 */
@Immutable
public final class CSMP {
  // The username to be found in the SMP DB
  private static final String SMP_USERNAME = "smp_admin";
  private static final String SMP_PASSWORD = "peppol.at.smp";
  public static final IReadonlyUsernamePWCredentials SMP_CREDENTIALS = new ReadonlyUsernamePWCredentials (SMP_USERNAME,
                                                                                                          SMP_PASSWORD);
  public static final UserId SMP_USERID = new UserId (SMP_USERNAME);
  private static final String SMP_URI_STR = "http://smp.peppol.at/";
  public static final URI SMP_URI;

  // 9915:B
  public static final SimpleParticipantIdentifier PARTICIPANT_ID = EPredefinedIdentifierIssuingAgency.AT_GOV.createParticipantIdentifier ("B");

  // Invoice T10
  public static final SimpleDocumentTypeIdentifier DOCUMENT_ID = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();

  // BIS 4a
  public static final SimpleProcessIdentifier PROCESS_ID = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();

  // init
  static {
    try {
      // Use localhost for local testing only
      SMP_URI = new URI (SMP_URI_STR);
    }
    catch (final URISyntaxException ex) {
      throw new InitializationException (ex);
    }
  }

  private CSMP () {}
}

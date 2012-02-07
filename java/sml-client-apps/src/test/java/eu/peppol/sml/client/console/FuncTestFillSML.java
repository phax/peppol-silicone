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
package eu.peppol.sml.client.console;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;

import com.phloc.commons.annotations.DevelopersNote;

import eu.peppol.registry.sml.management.client.ManageParticipantIdentifierServiceCaller;
import eu.peppol.registry.sml.management.client.ManageServiceMetadataServiceCaller;

/**
 * This class if for BRZ internal use only!
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
@DevelopersNote ("You need a running test SML for this test")
public final class FuncTestFillSML {
  private static final Logger log = LoggerFactory.getLogger (FuncTestFillSML.class);
  private static final ISMLInfo SML_INFO = ESML.DEVELOPMENT_LOCAL;
  private static final String SMP_ID = "SMP-CRUD";

  @Test
  public void testFillSML () throws Exception {
    final ManageServiceMetadataServiceCaller aManageSMP = new ManageServiceMetadataServiceCaller (SML_INFO);
    final ManageParticipantIdentifierServiceCaller aManagePI = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:1234567890aa");

    try {
      aManageSMP.create (SMP_ID, "127.0.0.1", "http://mySMP.example.org");
    }
    catch (final Exception e) {
      log.warn (e.getMessage ());
    }
    try {
      aManagePI.create (SMP_ID, aPI);
    }
    catch (final Exception e) {
      log.warn (e.getMessage ());
    }
    final ParticipantIdentifierPageType page = aManagePI.list ("", SMP_ID);
    ManageParticipantsClient.print (page);
  }
}

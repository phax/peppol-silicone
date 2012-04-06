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
package at.peppol.sml.client.swing.ctrl;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.sml.client.ManageParticipantIdentifierServiceCaller;

public final class ManageParticipantsClient {
  private final ManageParticipantIdentifierServiceCaller m_aCaller;

  public ManageParticipantsClient (final URL aEndpointAddress) {
    m_aCaller = new ManageParticipantIdentifierServiceCaller (aEndpointAddress);
  }

  public void create (final String sSMPID, final String [] args) throws BadRequestFault,
                                                                InternalErrorFault,
                                                                UnauthorizedFault,
                                                                NotFoundFault {
    if (args.length < 2) {
      System.err.println ("Invalid number of args to create a new identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return;
    }
    m_aCaller.create (sSMPID, new SimpleParticipantIdentifier (args[1], args[0]));
  }

  public void delete (final String [] args) throws BadRequestFault,
                                           InternalErrorFault,
                                           NotFoundFault,
                                           UnauthorizedFault {
    if (args.length < 2) {
      System.err.println ("Invalid number of args to delete an identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return;
    }
    m_aCaller.delete (new SimpleParticipantIdentifier (args[1], args[0]));
  }

  public void list (final String sSMPID, final String [] args) throws BadRequestFault,
                                                              InternalErrorFault,
                                                              NotFoundFault,
                                                              UnauthorizedFault {
    switch (args.length) {
      case 0:
        print (m_aCaller.list ("", sSMPID));
        break;
      default:
        print (m_aCaller.list (args[0], sSMPID));
        break;
    }
  }

  public UUID prepareToMigrate (final String sSMPID, final String [] args) throws BadRequestFault,
                                                                          InternalErrorFault,
                                                                          NotFoundFault,
                                                                          UnauthorizedFault {
    if (args.length != 2) {
      System.err.println ("Invalid number of args to prepare migrate of identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return null;
    }
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (args[1], args[0]);
    final UUID migrationCode = m_aCaller.prepareToMigrate (aPI, sSMPID);

    System.out.println ("Migration code: " + migrationCode);
    return migrationCode;
  }

  public void migrate (final String sSMPID, final String [] args) throws BadRequestFault,
                                                                 InternalErrorFault,
                                                                 NotFoundFault,
                                                                 UnauthorizedFault {
    if (args.length != 3) {
      System.err.println ("Invalid number of args to migrate an identifier.");
      System.out.println ("Use the following three parameters: identifier indentifierType migrationCode");
      return;
    }
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (args[1], args[0]);
    final UUID aCode = UUID.fromString (args[2]);
    m_aCaller.migrate (aPI, aCode, sSMPID);
  }

  public static void print (final ParticipantIdentifierPageType page) {
    if (page == null) {
      System.out.println ("Returned page is null");
      return;
    }

    final List <ParticipantIdentifierType> identifierList = page.getParticipantIdentifier ();
    System.out.println ("Found " + identifierList.size () + " participant identifiers:");
    for (final ParticipantIdentifierType identifier : identifierList)
      System.out.println ("  " + identifier.getScheme () + " : " + identifier.getValue ());

    final String sNextPage = page.getNextPageIdentifier ();
    if (sNextPage != null) {
      System.out.println ();
      System.out.println ("Next page: " + sNextPage);
    }
  }
}

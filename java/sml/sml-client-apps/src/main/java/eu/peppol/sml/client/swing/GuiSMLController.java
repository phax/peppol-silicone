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
package eu.peppol.sml.client.swing;

import java.net.URL;
import java.util.UUID;

import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;

import eu.peppol.busdox.sml.ISMLInfo;
import eu.peppol.sml.client.ESMLAction;
import eu.peppol.sml.client.ESMLObjectType;
import eu.peppol.sml.client.console.ManageParticipantsClient;
import eu.peppol.sml.client.console.ManageSMPClient;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
public class GuiSMLController {
  private static ManageSMPClient manageServiceMetadataClient;
  private static ManageParticipantsClient manageParticipantIdentifierClient;
  private static URL manageParticipantIdentifierEndpointAddress;
  private static URL manageServiceMetadataEndpointAddress;
  private static String smpID;

  public GuiSMLController () {}

  public String handleCommand (final ESMLAction action, final String [] args) {
    try {
      switch (action.getCommand ()) {
        case CREATE:
          return create (action.getObjectType (), args);
        case UPDATE:
          return update (action.getObjectType (), args);
        case DELETE:
          return delete (action.getObjectType (), args);
        case READ:
          return read (action.getObjectType (), args);
        case LIST:
          return list (action.getObjectType (), args);
        case PREPARETOMIGRATE:
          return prepareToMigrate (action.getObjectType (), args);
        case MIGRATE:
          return migrate (action.getObjectType (), args);
      }
    }
    catch (final Exception e) {
      e.printStackTrace ();

      return "AN ERROR OCCURED:\n  " + e.getMessage ();
    }

    return "UNKOWN COMMAND GIVEN";
  }

  private static String create (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2) {
      return "Invalid number of args to create a new object.";
    }

    switch (eObject) {
      case PARTICIPANT:
        getMPClient ().create (smpID, args, 0);
        return "PARTICIPANT FOR SMP: " + smpID + " CREATED";
      case METADATA:
        getSMClient ().create (smpID, args, 0);
        return "METADATA FOR SMP: " + smpID + " CREATED";
    }

    return "CANNOT DO CREATE ON " + eObject;
  }

  private static String update (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2) {
      return "Invalid number of args to update an object.";
    }

    switch (eObject) {
      case METADATA:
        getSMClient ().update (smpID, args, 0);
        return "METADATA FOR SMP " + smpID + " UPDATED";
    }
    return "CANNOT DO UPDATE ON " + eObject;
  }

  private static String delete (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2 && eObject.getName ().equals ("participant")) {
      return "Invalid number of args to delete.";
    }
    else
      if (args.length < 0 && eObject.getName ().equals ("metadata")) {
        return "Invalid number of args to delete.";
      }

    switch (eObject) {
      case PARTICIPANT:
        getMPClient ().delete (args, 0);
        return "PARTICIPANT DELETED";
      case METADATA:
        getSMClient ().delete (smpID, args, 0);
        return "METADATA FOR SMP " + smpID + " DELETED";
    }
    return "CANNOT DO DELETE ON " + eObject;
  }

  private static String read (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 0) {
      return "Invalid number of args to list.";
    }
    switch (eObject) {
      case METADATA:
        final ServiceMetadataPublisherServiceType aSMP = getSMClient ().read (smpID, args, 0);
        return "METADATA FOR SMP " +
               smpID +
               " READ: physical address=" +
               aSMP.getPublisherEndpoint ().getPhysicalAddress () +
               "; logical address=" +
               aSMP.getPublisherEndpoint ().getLogicalAddress ();
    }
    return "CANNOT DO READ ON " + eObject;
  }

  private static String list (final ESMLObjectType eObject, final String [] args) throws Exception {
    switch (eObject) {
      case PARTICIPANT:
        getMPClient ().list (smpID, args, 0);
        return "PARTICIPANT FOR SMP: " + smpID + " LISTED";
    }
    return "CANNOT DO LIST ON " + eObject;
  }

  private static String prepareToMigrate (final ESMLObjectType eObject, final String [] args) throws BadRequestFault,
                                                                                             InternalErrorFault,
                                                                                             NotFoundFault,
                                                                                             UnauthorizedFault {
    if (args.length < 2) {
      return "Invalid number of args to prepare to migrate.";
    }

    switch (eObject) {
      case PARTICIPANT:
        final UUID aUUID = getMPClient ().prepareToMigrate (smpID, args, 0);
        return "PARTICIPANT FOR SMP: " + smpID + " PREPARED TO MIGRATE. Migration code = " + aUUID;
    }
    return "CANNOT DO PREPARETOMIGRATE ON " + eObject;
  }

  private static String migrate (final ESMLObjectType eObject, final String [] args) throws BadRequestFault,
                                                                                    InternalErrorFault,
                                                                                    NotFoundFault,
                                                                                    UnauthorizedFault {
    if (args.length < 3) {
      return "Invalid number of args to prepare to migrate.";
    }

    switch (eObject) {
      case PARTICIPANT:
        getMPClient ().migrate (smpID, args, 0);
        return "PARTICIPANT FOR SMP: " + smpID + " MIGRATED";
    }
    return "CANNOT DO MIGRATE ON " + eObject;
  }

  private static ManageSMPClient getSMClient () {
    if (manageServiceMetadataClient == null)
      manageServiceMetadataClient = new ManageSMPClient (manageServiceMetadataEndpointAddress);
    return manageServiceMetadataClient;
  }

  private static ManageParticipantsClient getMPClient () {
    if (manageParticipantIdentifierClient == null)
      manageParticipantIdentifierClient = new ManageParticipantsClient (manageParticipantIdentifierEndpointAddress);
    return manageParticipantIdentifierClient;
  }

  /**
   * @return the m_aManageParticipantIdentifierEndpointAddress
   */
  public URL getManageParticipantIdentifierEndpointAddress () {
    return manageParticipantIdentifierEndpointAddress;
  }

  /**
   * @return the m_aManageServiceMetadataEndpointAddress
   */
  public URL getManageServiceMetadataEndpointAddress () {
    return manageServiceMetadataEndpointAddress;
  }

  /**
   * @param aSML
   *        the sHost to set
   */
  public void setManageServiceMetadataEndpointAddress (final ISMLInfo aSML) {
    manageParticipantIdentifierEndpointAddress = aSML.getManageParticipantIdentifierEndpointAddress ();
    manageServiceMetadataEndpointAddress = aSML.getManageServiceMetaDataEndpointAddress ();
  }

  /**
   * @param sSmpID
   *        the smpID to set
   */
  public void setSmpID (final String sSmpID) {
    smpID = sSmpID;
  }
}

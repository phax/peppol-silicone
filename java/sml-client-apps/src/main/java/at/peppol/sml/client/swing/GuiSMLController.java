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
package at.peppol.sml.client.swing;

import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;

import at.peppol.commons.sml.ISMLInfo;
import at.peppol.sml.client.ESMLAction;
import at.peppol.sml.client.ESMLObjectType;
import at.peppol.sml.client.console.ManageParticipantsClient;
import at.peppol.sml.client.console.ManageSMPClient;

/**
 * @author PEPPOL.AT, BRZ, Jakob Frohnwieser
 */
@NotThreadSafe
final class GuiSMLController {
  private static ManageSMPClient s_aSMPClient;
  private static ManageParticipantsClient s_aParticipantClient;
  private static URL s_aParticipantEndpointAddress;
  private static URL s_aSMPClientEndpointAddress;
  private static String s_sSMPID;

  private GuiSMLController () {}

  public static String handleCommand (@Nonnull final ESMLAction eAction, final String [] aArgs) {
    try {
      switch (eAction.getCommand ()) {
        case CREATE:
          return _create (eAction.getObjectType (), aArgs);
        case UPDATE:
          return _update (eAction.getObjectType (), aArgs);
        case DELETE:
          return _delete (eAction.getObjectType (), aArgs);
        case READ:
          return _read (eAction.getObjectType (), aArgs);
        case LIST:
          return _list (eAction.getObjectType (), aArgs);
        case PREPARETOMIGRATE:
          return _prepareToMigrate (eAction.getObjectType (), aArgs);
        case MIGRATE:
          return _migrate (eAction.getObjectType (), aArgs);
      }
    }
    catch (final Exception e) {
      e.printStackTrace ();

      return "AN ERROR OCCURED:\n  " + e.getMessage ();
    }

    return "UNKOWN COMMAND GIVEN";
  }

  private static String _create (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2) {
      return "Invalid number of args to create a new object.";
    }

    switch (eObject) {
      case PARTICIPANT:
        _getParticipantClient ().create (s_sSMPID, args, 0);
        return "PARTICIPANT FOR SMP: " + s_sSMPID + " CREATED";
      case METADATA:
        _getSMPClient ().create (s_sSMPID, args, 0);
        return "METADATA FOR SMP: " + s_sSMPID + " CREATED";
    }

    return "CANNOT DO CREATE ON " + eObject;
  }

  private static String _update (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2) {
      return "Invalid number of args to update an object.";
    }

    switch (eObject) {
      case METADATA:
        _getSMPClient ().update (s_sSMPID, args, 0);
        return "METADATA FOR SMP " + s_sSMPID + " UPDATED";
    }
    return "CANNOT DO UPDATE ON " + eObject;
  }

  private static String _delete (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 2 && eObject.getName ().equals ("participant")) {
      return "Invalid number of args to delete.";
    }
    else
      if (args.length < 0 && eObject.getName ().equals ("metadata")) {
        return "Invalid number of args to delete.";
      }

    switch (eObject) {
      case PARTICIPANT:
        _getParticipantClient ().delete (args, 0);
        return "PARTICIPANT DELETED";
      case METADATA:
        _getSMPClient ().delete (s_sSMPID, args, 0);
        return "METADATA FOR SMP " + s_sSMPID + " DELETED";
    }
    return "CANNOT DO DELETE ON " + eObject;
  }

  private static String _read (final ESMLObjectType eObject, final String [] args) throws Exception {
    if (args.length < 0) {
      return "Invalid number of args to list.";
    }
    switch (eObject) {
      case METADATA:
        final ServiceMetadataPublisherServiceType aSMP = _getSMPClient ().read (s_sSMPID, args, 0);
        return "METADATA FOR SMP " +
               s_sSMPID +
               " READ: physical address=" +
               aSMP.getPublisherEndpoint ().getPhysicalAddress () +
               "; logical address=" +
               aSMP.getPublisherEndpoint ().getLogicalAddress ();
    }
    return "CANNOT DO READ ON " + eObject;
  }

  private static String _list (final ESMLObjectType eObject, final String [] args) throws Exception {
    switch (eObject) {
      case PARTICIPANT:
        _getParticipantClient ().list (s_sSMPID, args, 0);
        return "PARTICIPANT FOR SMP: " + s_sSMPID + " LISTED";
    }
    return "CANNOT DO LIST ON " + eObject;
  }

  private static String _prepareToMigrate (final ESMLObjectType eObject, final String [] args) throws BadRequestFault,
                                                                                              InternalErrorFault,
                                                                                              NotFoundFault,
                                                                                              UnauthorizedFault {
    if (args.length < 2) {
      return "Invalid number of args to prepare to migrate.";
    }

    switch (eObject) {
      case PARTICIPANT:
        final UUID aUUID = _getParticipantClient ().prepareToMigrate (s_sSMPID, args, 0);
        return "PARTICIPANT FOR SMP: " + s_sSMPID + " PREPARED TO MIGRATE. Migration code = " + aUUID;
    }
    return "CANNOT DO PREPARETOMIGRATE ON " + eObject;
  }

  private static String _migrate (final ESMLObjectType eObject, final String [] args) throws BadRequestFault,
                                                                                     InternalErrorFault,
                                                                                     NotFoundFault,
                                                                                     UnauthorizedFault {
    if (args.length < 3) {
      return "Invalid number of args to prepare to migrate.";
    }

    switch (eObject) {
      case PARTICIPANT:
        _getParticipantClient ().migrate (s_sSMPID, args, 0);
        return "PARTICIPANT FOR SMP: " + s_sSMPID + " MIGRATED";
    }
    return "CANNOT DO MIGRATE ON " + eObject;
  }

  private static ManageSMPClient _getSMPClient () {
    if (s_aSMPClient == null)
      s_aSMPClient = new ManageSMPClient (s_aSMPClientEndpointAddress);
    return s_aSMPClient;
  }

  private static ManageParticipantsClient _getParticipantClient () {
    if (s_aParticipantClient == null)
      s_aParticipantClient = new ManageParticipantsClient (s_aParticipantEndpointAddress);
    return s_aParticipantClient;
  }

  /**
   * @return the manageParticipantIdentifierEndpointAddress
   */
  public static URL getManageParticipantIdentifierEndpointAddress () {
    return s_aParticipantEndpointAddress;
  }

  /**
   * @return the manageServiceMetadataEndpointAddress
   */
  public static URL getManageServiceMetadataEndpointAddress () {
    return s_aSMPClientEndpointAddress;
  }

  /**
   * @param aSML
   *        the sHost to set
   */
  public static void setSMLInfo (final ISMLInfo aSML) {
    s_aParticipantEndpointAddress = aSML.getManageParticipantIdentifierEndpointAddress ();
    s_aSMPClientEndpointAddress = aSML.getManageServiceMetaDataEndpointAddress ();
  }

  /**
   * @param sSMPID
   *        the smpID to set
   */
  public static void setSMPID (final String sSMPID) {
    s_sSMPID = sSMPID;
  }
}

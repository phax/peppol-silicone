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
package at.peppol.sml.server.management.bi;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;

import org.busdox.servicemetadata.locator._1.FaultType;
import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.ManageBusinessIdentifierServiceSoap;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.sml.server.IGenericDataHandler;
import at.peppol.sml.server.IParticipantDataHandler;
import at.peppol.sml.server.exceptions.BadRequestException;
import at.peppol.sml.server.exceptions.InternalErrorException;
import at.peppol.sml.server.exceptions.NotFoundException;
import at.peppol.sml.server.exceptions.UnauthorizedException;
import at.peppol.sml.server.exceptions.UnknownUserException;
import at.peppol.sml.server.management.DataHandlerFactory;
import at.peppol.sml.server.management.DataValidator;
import at.peppol.sml.server.web.WebRequestClientIdentifier;

import com.phloc.commons.collections.ContainerHelper;
import com.sun.xml.ws.developer.SchemaValidation;


/**
 * An implementation of the Manage Participant Identifier web service.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@SchemaValidation
@WebService (serviceName = "ManageBusinessIdentifierService",
             portName = "ManageBusinessIdentifierServicePort",
             endpointInterface = "org.busdox.servicemetadata.managebusinessidentifierservice._1.ManageBusinessIdentifierServiceSoap",
             targetNamespace = "http://busdox.org/serviceMetadata/ManageBusinessIdentifierService/1.0/",
             wsdlLocation = "WEB-INF/wsdl/ManageBusinessIdentifierService-1.0.wsdl")
@BindingType (value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
public class ManageParticipantIdentifierImpl implements ManageBusinessIdentifierServiceSoap {
  private static final Logger log = LoggerFactory.getLogger (ManageParticipantIdentifierImpl.class);

  @Resource
  public WebServiceContext wsContext;

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private final IParticipantDataHandler m_aDataHandler;
  private final IGenericDataHandler m_aReqAuthHdl;

  public ManageParticipantIdentifierImpl () {
    m_aDataHandler = DataHandlerFactory.getParticipantDataHandler ();
    m_aReqAuthHdl = DataHandlerFactory.getGenericDataHandler ();
  }

  public void createList (final ParticipantIdentifierPageType createListIn) throws BadRequestFault,
                                                                           InternalErrorFault,
                                                                           NotFoundFault,
                                                                           UnauthorizedFault {
    try {
      // Validate input
      DataValidator.validate (createListIn);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.createParticipantIdentifiers (createListIn, sClientUniqueID);

      log.info ("Created " +
                createListIn.getParticipantIdentifier ().size () +
                " participants in " +
                createListIn.getServiceMetadataPublisherID ());
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on create", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on create", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
  }

  public void deleteList (final ParticipantIdentifierPageType deleteListIn) throws BadRequestFault,
                                                                           InternalErrorFault,
                                                                           NotFoundFault,
                                                                           UnauthorizedFault {
    try {
      // Validate input
      for (final ParticipantIdentifierType participantIdentifier : deleteListIn.getParticipantIdentifier ())
        DataValidator.validate (participantIdentifier);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.deleteParticipantIdentifiers (deleteListIn.getParticipantIdentifier (), sClientUniqueID);

      log.info ("Deleted " +
                deleteListIn.getParticipantIdentifier ().size () +
                " participants of " +
                deleteListIn.getServiceMetadataPublisherID ());
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on delete", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on delete", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
  }

  public void migrate (final MigrationRecordType aMigrationRecord) throws NotFoundFault,
                                                                  UnauthorizedFault,
                                                                  InternalErrorFault,
                                                                  BadRequestFault {
    try {
      // Validate input
      DataValidator.validate (aMigrationRecord);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.migrate (aMigrationRecord, sClientUniqueID);

      log.info ("Migrated participant " +
                IdentifierUtils.getIdentifierURIEncoded (aMigrationRecord.getParticipantIdentifier ()) +
                " to " +
                aMigrationRecord.getServiceMetadataPublisherID ());
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on migrate", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on migrate", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
  }

  public void prepareToMigrate (final MigrationRecordType aMigrationRecord) throws NotFoundFault,
                                                                           UnauthorizedFault,
                                                                           InternalErrorFault,
                                                                           BadRequestFault {

    try {
      // Validate input
      DataValidator.validate (aMigrationRecord);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.prepareToMigrate (aMigrationRecord, sClientUniqueID);

      log.info ("Prepared to migrate participant " +
                IdentifierUtils.getIdentifierURIEncoded (aMigrationRecord.getParticipantIdentifier ()) +
                " from " +
                aMigrationRecord.getServiceMetadataPublisherID ());
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on prepareToMigrate", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on prepareToMigrate", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
  }

  public void create (final ServiceMetadataPublisherServiceForParticipantType aParticipantToSMP) throws BadRequestFault,
                                                                                                InternalErrorFault,
                                                                                                UnauthorizedFault {
    final ParticipantIdentifierType aParticipantIdentifier = aParticipantToSMP.getParticipantIdentifier ();
    if (log.isDebugEnabled ())
      log.debug ("Called with identifier: " +
                 aParticipantIdentifier.getScheme () +
                 "::" +
                 aParticipantIdentifier.getValue ());

    try {
      // Validate input
      DataValidator.validate (aParticipantToSMP);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType ();
      aJAXBPage.getParticipantIdentifier ().add (aParticipantIdentifier);
      aJAXBPage.setServiceMetadataPublisherID (aParticipantToSMP.getServiceMetadataPublisherID ());
      m_aDataHandler.createParticipantIdentifiers (aJAXBPage, sClientUniqueID);

      log.info ("Assigned participant " +
                IdentifierUtils.getIdentifierURIEncoded (aParticipantIdentifier) +
                " to " +
                aParticipantToSMP.getServiceMetadataPublisherID ());
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on create", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on create", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
  }

  public void delete (final ServiceMetadataPublisherServiceForParticipantType aParticipantToSMP) throws BadRequestFault,
                                                                                                InternalErrorFault,
                                                                                                NotFoundFault,
                                                                                                UnauthorizedFault {
    final ParticipantIdentifierType aParticipantIdentifier = aParticipantToSMP.getParticipantIdentifier ();
    if (log.isDebugEnabled ())
      log.debug ("Called with identifier: " +
                 aParticipantIdentifier.getScheme () +
                 "::" +
                 aParticipantIdentifier.getValue ());

    try {
      // Validate input
      DataValidator.validate (aParticipantIdentifier);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      m_aDataHandler.deleteParticipantIdentifiers (ContainerHelper.newList (aParticipantIdentifier), sClientUniqueID);

      log.info ("Deleted participant " + IdentifierUtils.getIdentifierURIEncoded (aParticipantIdentifier));
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on delete", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on delete", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
  }

  public ParticipantIdentifierPageType list (final PageRequestType messagePart) throws BadRequestFault,
                                                                               InternalErrorFault,
                                                                               NotFoundFault,
                                                                               UnauthorizedFault {

    try {
      // Validate input
      DataValidator.validate (messagePart);

      // Validate client unique ID
      final String sClientUniqueID = WebRequestClientIdentifier.getClientUniqueID (wsContext);
      m_aReqAuthHdl.verifyExistingUser (sClientUniqueID);

      // Perform action
      final ParticipantIdentifierPageType ret = m_aDataHandler.listParticipantIdentifiers (messagePart, sClientUniqueID);
      log.info ("Retrieved " +
                ret.getParticipantIdentifier ().size () +
                " participants for " +
                messagePart.getServiceMetadataPublisherID ());
      return ret;
    }
    catch (final NotFoundException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new NotFoundFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnauthorizedException e) {
      log.warn ("Unauthorized request on list", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final UnknownUserException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new UnauthorizedFault (e.getMessage (), faultInfo, e);
    }
    catch (final InternalErrorException e) {
      log.error ("Internal error on list", e);
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new InternalErrorFault (e.getMessage (), faultInfo, e);
    }
    catch (final BadRequestException e) {
      final FaultType faultInfo = m_aObjFactory.createFaultType ();
      faultInfo.setFaultMessage (e.getMessage ());
      throw new BadRequestFault (e.getMessage (), faultInfo, e);
    }
  }
}

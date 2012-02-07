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
package eu.peppol.registry.smp;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataReferenceCollectionType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataReferenceType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;

import com.sun.jersey.api.NotFoundException;

import eu.peppol.registry.smp.data.DataManagerFactory;
import eu.peppol.registry.smp.data.IDataManager;
import eu.peppol.registry.smp.util.RequestHelper;

/**
 * This class implements the REST interface for getting ServiceGroup's. PUT and
 * DELETE are also implemented.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Path ("/{ServiceGroupId}")
public final class ServiceGroupInterface {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServiceGroupInterface.class);

  @Context
  private HttpHeaders headers;
  @Context
  private UriInfo uriInfo;

  public ServiceGroupInterface () {}

  @GET
  @Produces (MediaType.APPLICATION_XML)
  public JAXBElement <ServiceGroupType> getServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupId) {
    s_aLogger.info ("GET /" + sServiceGroupId);
    ParticipantIdentifierType aServiceGroupID = null;
    try {
      aServiceGroupID = SimpleParticipantIdentifier.createFromURIPart (sServiceGroupId);
    }
    catch (final IllegalArgumentException ex) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupId + "'");
      return null;
    }

    try {
      final ObjectFactory aObjFactory = new ObjectFactory ();

      // Retrieve the service group
      final IDataManager aDataManager = DataManagerFactory.getInstance ();
      final ServiceGroupType aServiceGroup = aDataManager.getServiceGroup (aServiceGroupID);
      if (aServiceGroup == null) {
        // No such service group
        throw new NotFoundException ("serviceGroup", uriInfo.getAbsolutePath ());
      }

      /*
       * Then add the service metadata references
       */
      final ServiceMetadataReferenceCollectionType aCollectionType = aObjFactory.createServiceMetadataReferenceCollectionType ();
      final List <ServiceMetadataReferenceType> aMetadataReferences = aCollectionType.getServiceMetadataReference ();

      final List <DocumentIdentifierType> aDocTypeIds = aDataManager.getDocumentTypes (aServiceGroupID);
      for (final DocumentIdentifierType aDocTypeId : aDocTypeIds) {
        final ServiceMetadataReferenceType aMetadataReference = aObjFactory.createServiceMetadataReferenceType ();
        aMetadataReference.setHref (uriInfo.getBaseUriBuilder ()
                                           .path (ServiceMetadataInterface.class)
                                           .buildFromEncoded (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID),
                                                              IdentifierUtils.getIdentifierURIPercentEncoded (aDocTypeId))
                                           .toString ());
        aMetadataReferences.add (aMetadataReference);
      }
      aServiceGroup.setServiceMetadataReferenceCollection (aCollectionType);

      s_aLogger.info ("Finished getServiceGroup(" + sServiceGroupId + ")");

      /*
       * Finally return it
       */
      return aObjFactory.createServiceGroup (aServiceGroup);
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("Error getting service group " + aServiceGroupID, ex);
      throw ex;
    }
  }

  @PUT
  public Response saveServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupId,
                                    final ServiceGroupType aServiceGroup) {
    s_aLogger.info ("PUT /" + sServiceGroupId + " ==> " + aServiceGroup);
    ParticipantIdentifierType aServiceGroupID = null;
    try {
      aServiceGroupID = SimpleParticipantIdentifier.createFromURIPart (sServiceGroupId);
    }
    catch (final IllegalArgumentException ex) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupId + "'");
      return Response.status (Status.BAD_REQUEST).build ();
    }

    try {
      if (!IdentifierUtils.areIdentifiersEqual (aServiceGroupID, aServiceGroup.getParticipantIdentifier ())) {
        // Business identifier must equal path
        return Response.status (Status.BAD_REQUEST).build ();
      }

      final IDataManager aDataManager = DataManagerFactory.getInstance ();
      aDataManager.saveServiceGroup (aServiceGroup, RequestHelper.getAuth (headers));

      s_aLogger.info ("Finished saveServiceGroup(" + sServiceGroupId + "," + aServiceGroup + ")");

      return Response.ok ().build ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("Error saving service " + aServiceGroupID, ex);
      throw ex;
    }
  }

  @DELETE
  public Response deleteServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupId) {
    s_aLogger.info ("DELETE /" + sServiceGroupId);
    ParticipantIdentifierType aServiceGroupID = null;
    try {
      aServiceGroupID = SimpleParticipantIdentifier.createFromURIPart (sServiceGroupId);
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupId + "'");
    }
    catch (final IllegalArgumentException ex) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupId + "'");
      return Response.status (Status.BAD_REQUEST).build ();
    }

    try {
      final IDataManager aDataManager = DataManagerFactory.getInstance ();
      aDataManager.deleteServiceGroup (aServiceGroupID, RequestHelper.getAuth (headers));

      s_aLogger.info ("Finished deleteServiceGroup(" + sServiceGroupId + ")");

      return Response.ok ().build ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("Error deleting service " + aServiceGroupID, ex);
      throw ex;
    }
  }
}

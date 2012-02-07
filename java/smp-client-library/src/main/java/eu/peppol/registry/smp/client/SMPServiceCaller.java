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
package eu.peppol.registry.smp.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBElement;

import org.busdox.servicemetadata.publishing._1.CompleteServiceGroupType;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2000._09.xmldsig.X509DataType;

import at.peppol.busdox.identifier.IReadonlyDocumentIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.sml.ISMLInfo;
import at.peppol.commons.uri.BusdoxURLUtils;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.streams.NonBlockingByteArrayInputStream;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import eu.peppol.registry.smp.client.exception.BadRequestException;
import eu.peppol.registry.smp.client.exception.NotFoundException;
import eu.peppol.registry.smp.client.exception.UnauthorizedException;
import eu.peppol.registry.smp.client.exception.UnknownException;

/**
 * This class is used for calling the SMP REST interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMPServiceCaller {
  public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";
  public static final String END_CERTIFICATE = "\n-----END CERTIFICATE-----";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceCaller.class);

  // Don't think of changing anything here! This is very sensitive. Don't
  // extract a base class or some else. DON'T TOUCH!
  // See http://sites.google.com/site/richchihlee/portal/j2ee/rs/rs-client
  private static final GenericType <JAXBElement <ServiceGroupType>> TYPE_SERVICEGROUP = new GenericType <JAXBElement <ServiceGroupType>> () {};
  private static final GenericType <JAXBElement <CompleteServiceGroupType>> TYPE_COMPLETESERVICEGROUP = new GenericType <JAXBElement <CompleteServiceGroupType>> () {};
  private static final GenericType <JAXBElement <ServiceGroupReferenceListType>> TYPE_SERVICEGROUPREFERENCELIST = new GenericType <JAXBElement <ServiceGroupReferenceListType>> () {};
  private static final GenericType <JAXBElement <SignedServiceMetadataType>> TYPE_SIGNEDSERVICEMETADATA = new GenericType <JAXBElement <SignedServiceMetadataType>> () {};

  // Members - free to change from here on
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private final WebResource m_aWebResource;
  private final WebResource m_aWebResourceWithSignatureCheck;

  @Nonnull
  private static WebResource _getResourceWithSignatureCheck (@Nonnull final URI aURI) {
    final Client aClient = Client.create ();
    aClient.addFilter (new CheckSignatureFilter ());
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  @Nonnull
  private static WebResource _getResource (@Nonnull final URI aURI) {
    final Client aClient = Client.create ();
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull final ISMLInfo aSMLInfo) {
    this (aParticipantIdentifier, aSMLInfo.getDNSZone ());
  }

  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull final String sSMLZoneName) {
    this (BusdoxURLUtils.getSMPURIOfParticipant (aParticipantIdentifier, sSMLZoneName));
  }

  /**
   * Constructs a new service caller for the specified host. Remember: must be
   * HTTP and using port 80 only!
   * 
   * @param aSMPHost
   *        The address of the SMP service. Example:
   *        http://smpcompany.company.org
   */
  public SMPServiceCaller (@Nonnull final URI aSMPHost) {
    if (aSMPHost == null)
      throw new NullPointerException ("smpHost");
    m_aWebResource = _getResource (aSMPHost);
    m_aWebResourceWithSignatureCheck = _getResourceWithSignatureCheck (aSMPHost);
  }

  /**
   * Gets a list of references to the CompleteServiceGroup's owned by the
   * specified userId.
   * 
   * @param aUserID
   *        The username for which to retrieve service groups.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @return A list of references to complete service groups.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The userId did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public ServiceGroupReferenceListType getServiceGroupReferenceList (final UserId aUserID,
                                                                     final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final WebResource aFullResource = m_aWebResource.path ("/list/" + aUserID.getUserIdPercentEncoded ());
    return _getServiceGroupReferenceListResource (aFullResource, aCredentials);
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   * 
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The complete service group containing service group and service
   *         metadata
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public CompleteServiceGroupType getCompleteServiceGroup (final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final WebResource aFullResource = m_aWebResource.path ("/complete/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));

    return _getCompleteServiceGroupResource (aFullResource);
  }

  /**
   * Returns a service group. A service group references to the service
   * metadata.
   * 
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The service group
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public ServiceGroupType getServiceGroup (final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));

    return _getServiceGroupResource (aFullResource);
  }

  /**
   * Saves a service group. The metadata references should not be set and are
   * not used.
   * 
   * @param aServiceGroup
   *        The service group to save.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void saveServiceGroup (final ServiceGroupType aServiceGroup, final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final IReadonlyParticipantIdentifier aPI = aServiceGroup.getParticipantIdentifier ();
    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
    _saveServiceGroupResource (aFullResource, aServiceGroup, aCredentials);
  }

  /**
   * Deletes a service group given by its service group id.
   * 
   * @param aServiceGroupID
   *        The service group id of the service group to delete.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void deleteServiceGroup (final IReadonlyParticipantIdentifier aServiceGroupID,
                                  final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
    _deleteServiceGroupResource (aFullResource, aCredentials);
  }

  /**
   * Gets a signed service metadata object given by its service group id and its
   * document type.
   * 
   * @param aServiceGroupID
   *        The service group id of the service metadata to get.
   * @param aDocumentTypeID
   *        The document type of the service metadata to get.
   * @return A signed service metadata object.
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id or document type did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public SignedServiceMetadataType getServiceRegistration (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                           @Nonnull final IReadonlyDocumentIdentifier aDocumentTypeID) throws Exception {
    final String path = IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                        "/services/" +
                        IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID);
    final WebResource aFullResource = m_aWebResourceWithSignatureCheck.path (path);

    return _getSignedServiceMetadataResource (aFullResource);
  }

  @Nullable
  public EndpointType getEndpoint (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                   @Nonnull final IReadonlyDocumentIdentifier aDocumentTypeID,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Get meta data for participant/documentType
    final SignedServiceMetadataType aSignedServiceMetadata = getServiceRegistration (aServiceGroupID, aDocumentTypeID);
    if (aSignedServiceMetadata != null) {
      // Iterate all processes
      final List <ProcessType> aAllProcesses = aSignedServiceMetadata.getServiceMetadata ()
                                                                     .getServiceInformation ()
                                                                     .getProcessList ()
                                                                     .getProcess ();
      for (final ProcessType aProcessType : aAllProcesses) {
        // Matches the requested one?
        if (IdentifierUtils.areIdentifiersEqual (aProcessType.getProcessIdentifier (), aProcessID)) {
          // Get all endpoints
          final List <EndpointType> aEndpoints = aProcessType.getServiceEndpointList ().getEndpoint ();
          if (aEndpoints.size () != 1)
            s_aLogger.warn ("Found " + aEndpoints.size () + " endpoints for process " + aProcessID);

          // Extract the address
          return ContainerHelper.getFirstElement (aEndpoints);
        }
      }
    }
    return null;
  }

  @Nullable
  public String getEndpointAddress (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                    @Nonnull final IReadonlyDocumentIdentifier aDocumentTypeID,
                                    @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID);
    return aEndpoint == null ? null : W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ());
  }

  @Nullable
  public String getEndpointCertificateString (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                              @Nonnull final IReadonlyDocumentIdentifier aDocumentTypeID,
                                              @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {

    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID);
    return aEndpoint == null ? null : aEndpoint.getCertificate ();
  }

  @Nullable
  public static X509Certificate convertStringToCertficate (@Nullable final String sCertString) throws CertificateException {
    if (sCertString == null)
      return null;

    // Convert certificate string to an object
    String sRealCertString = sCertString;
    if (!sRealCertString.startsWith (BEGIN_CERTIFICATE))
      sRealCertString = BEGIN_CERTIFICATE + sRealCertString;
    if (!sRealCertString.endsWith (END_CERTIFICATE))
      sRealCertString += END_CERTIFICATE;
    final CertificateFactory certificateFactory = CertificateFactory.getInstance ("X.509");
    return (X509Certificate) certificateFactory.generateCertificate (new NonBlockingByteArrayInputStream (sRealCertString.getBytes ()));
  }

  @Nullable
  public X509Certificate getEndpointCertificate (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IReadonlyDocumentIdentifier aDocumentTypeID,
                                                 @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {

    final String sCertString = getEndpointCertificateString (aServiceGroupID, aDocumentTypeID, aProcessID);
    return convertStringToCertficate (sCertString);
  }

  /**
   * Saves a service metadata object. The ServiceGroupReference value is
   * ignored.
   * 
   * @param aServiceMetadata
   *        The service metadata object to save.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void saveServiceRegistration (final ServiceMetadataType aServiceMetadata,
                                       final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final ServiceInformationType serviceInformation = aServiceMetadata.getServiceInformation ();
    final IReadonlyParticipantIdentifier aServiceGroupID = serviceInformation.getParticipantIdentifier ();
    final IReadonlyDocumentIdentifier aDocumentTypeID = serviceInformation.getDocumentIdentifier ();

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));

    _saveServiceRegistrationResource (aFullResource, aServiceMetadata, aCredentials);
  }

  /**
   * Deletes a service metadata object given by its service group id and its
   * document type.
   * 
   * @param aServiceGroupID
   *        The service group id of the service metadata to delete.
   * @param aDocumentTypeID
   *        The document type of the service metadata to delete.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The service metadata object did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void deleteServiceRegistration (final IReadonlyParticipantIdentifier aServiceGroupID,
                                         final IReadonlyDocumentIdentifier aDocumentTypeID,
                                         final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));

    _deleteServiceRegistrationResource (aFullResource, aCredentials);
  }

  /**
   * Gets a list of references to the CompleteServiceGroup's owned by the
   * specified userId.
   * 
   * @param aURI
   *        The URI containing the reference list.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @return A list of references to complete service groups.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The userId did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static ServiceGroupReferenceListType getServiceGroupReferenceList (final URI aURI,
                                                                            final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    return _getServiceGroupReferenceListResource (_getResource (aURI), aCredentials);
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   * 
   * @param aURI
   *        The URI containing the complete service group
   * @return The complete service group containing service group and service
   *         metadata
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static CompleteServiceGroupType getCompleteServiceGroup (final URI aURI) throws Exception {
    return _getCompleteServiceGroupResource (_getResource (aURI));
  }

  /**
   * Returns a service group. A service group references to the service
   * metadata.
   * 
   * @param aURI
   *        The URI to the service group resource.
   * @return The service group
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static ServiceGroupType getServiceGroup (final URI aURI) throws Exception {
    return _getServiceGroupResource (_getResource (aURI));
  }

  /**
   * Saves a service group. The metadata references should not be set and are
   * not used.
   * 
   * @param aURI
   *        The URI to the service group resource.
   * @param aServiceGroup
   *        The service group to save.
   * @param creds
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed. This could be caused by the URI
   *         not corresponding to the service group id in the service group
   *         object.
   */
  public void saveServiceGroup (final URI aURI,
                                final ServiceGroupType aServiceGroup,
                                final IReadonlyUsernamePWCredentials creds) throws Exception {
    _saveServiceGroupResource (_getResource (aURI), aServiceGroup, creds);
  }

  /**
   * Deletes a service metadata object given by its service group id and its
   * document type.
   * 
   * @param aURI
   *        The URI to the service group resource.
   * @param creds
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The service metadata object did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static void deleteServiceGroup (final URI aURI, final IReadonlyUsernamePWCredentials creds) throws Exception {
    _deleteServiceGroupResource (_getResource (aURI), creds);
  }

  /**
   * Gets a signed service metadata object given by its URI.
   * 
   * @param aURI
   *        The URI to the service metadata resource.
   * @return A signed service metadata object.
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id or document type did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static SignedServiceMetadataType getServiceRegistration (final URI aURI) throws Exception {
    return _getSignedServiceMetadataResource (_getResourceWithSignatureCheck (aURI));
  }

  /**
   * Saves a service metadata object. The ServiceGroupReference value is
   * ignored.
   * 
   * @param aURI
   *        The URI to the service metadata resource.
   * @param aServiceMetadata
   *        The service metadata object to save.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed. This could be caused by the URI
   *         not corresponding to the service group id or document type id in
   *         the service metadata object.
   */
  public void saveServiceRegistration (final URI aURI,
                                       final ServiceMetadataType aServiceMetadata,
                                       final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _saveServiceRegistrationResource (_getResource (aURI), aServiceMetadata, aCredentials);
  }

  /**
   * Deletes a service metadata object given by the URI.
   * 
   * @param aURI
   *        The URI to the service metadata resource.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The service metadata object did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static void deleteServiceRegistration (final URI aURI, final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _deleteServiceRegistrationResource (_getResource (aURI), aCredentials);
  }

  /*
   * Methods that make use of DNS lookup
   */
  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   * 
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The complete service group containing service group and service
   *         metadata
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static CompleteServiceGroupType getCompleteServiceGroupByDNS (final ISMLInfo aSMLInfo,
                                                                       final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final String sFullPath = _convertServiceGroupToURI (aSMLInfo, aServiceGroupID) +
                             "/complete/" +
                             IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID);
    final WebResource aFullResource = _getResource (URI.create (sFullPath));
    return _getCompleteServiceGroupResource (aFullResource);
  }

  /**
   * Returns a service group. A service group references to the service
   * metadata.
   * 
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The service group
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static ServiceGroupType getServiceGroupByDNS (final ISMLInfo aSMLInfo,
                                                       final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final String sFullPath = _convertServiceGroupToURI (aSMLInfo, aServiceGroupID) +
                             "/" +
                             IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID);
    final WebResource aFullResource = _getResource (URI.create (sFullPath));

    return _getServiceGroupResource (aFullResource);
  }

  /**
   * Gets a signed service metadata object given by its service group id and its
   * document type.
   * 
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id of the service metadata to get.
   * @param aDocumentTypeID
   *        The document type of the service metadata to get.
   * @return A signed service metadata object.
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id or document type did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public static SignedServiceMetadataType getServiceRegistrationByDNS (final ISMLInfo aSMLInfo,
                                                                       final IReadonlyParticipantIdentifier aServiceGroupID,
                                                                       final IReadonlyDocumentIdentifier aDocumentTypeID) throws Exception {
    final String sFullPath = _convertServiceGroupToURI (aSMLInfo, aServiceGroupID) +
                             "/" +
                             IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                             "/services/" +
                             IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID);
    final WebResource aFullResource = _getResourceWithSignatureCheck (URI.create (sFullPath));

    return _getSignedServiceMetadataResource (aFullResource);
  }

  private static ServiceGroupReferenceListType _getServiceGroupReferenceListResource (final WebResource aFullResource,
                                                                                      final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());
      return aBuilderWithAuth.get (TYPE_SERVICEGROUPREFERENCELIST).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private static CompleteServiceGroupType _getCompleteServiceGroupResource (final WebResource aFullResource) throws Exception {
    try {
      return aFullResource.get (TYPE_COMPLETESERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private static ServiceGroupType _getServiceGroupResource (final WebResource aFullResource) throws Exception {
    try {
      return aFullResource.get (TYPE_SERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private void _saveServiceGroupResource (final WebResource aFullResource,
                                          final ServiceGroupType aServiceGroup,
                                          final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());

      // Important to build a JAXBElement around the service group
      aBuilderWithAuth.put (m_aObjFactory.createServiceGroup (aServiceGroup));
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private static void _deleteServiceGroupResource (final WebResource aFullResource,
                                                   final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());
      aBuilderWithAuth.delete ();
    }
    catch (final UniformInterfaceException ex) {
      throw _getConvertedException (ex);
    }
  }

  private static SignedServiceMetadataType _getSignedServiceMetadataResource (final WebResource aFullResource) throws Exception {
    try {
      SignedServiceMetadataType aMetadata = aFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

      // If the Redirect element is present, then follow 1 redirect.
      if (aMetadata.getServiceMetadata () != null && aMetadata.getServiceMetadata ().getRedirect () != null) {
        final RedirectType aRedirect = aMetadata.getServiceMetadata ().getRedirect ();
        WebResource aRedirectFullResource;
        try {
          aRedirectFullResource = _getResourceWithSignatureCheck (new URI (aRedirect.getHref ()));
        }
        catch (final URISyntaxException e) {
          throw new UnknownException ("The URI of the received redirect was not well-formed: " + aRedirect.getHref ());
        }

        aMetadata = aRedirectFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

        // Check that the certificateUID is correct.
        boolean bCertificateSubjectFound = false;
        final Iterator <Object> ki = aMetadata.getSignature ().getKeyInfo ().getContent ().iterator ();
        OUTER: while (ki.hasNext ()) {
          final JAXBElement <?> aInfo = (JAXBElement <?>) ki.next ();
          final Object aInfoValue = aInfo.getValue ();
          if (aInfoValue instanceof X509DataType) {
            final X509DataType aX509Data = (X509DataType) aInfoValue;
            final List <Object> aX509Objects = aX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName ();
            for (final Object aX509Obj : aX509Objects) {
              final JAXBElement <?> aX509element = (JAXBElement <?>) aX509Obj;
              // Find the first subject (of type string)
              if (aX509element.getValue () instanceof String) {
                final String sSubject = (String) aX509element.getValue ();

                if (!aRedirect.getCertificateUID ().equals (sSubject)) {
                  throw new UnknownException ("The certificate UID of the redirect did not match the certificate subject. Subject: " +
                                              sSubject +
                                              ". CertificateUID: " +
                                              aRedirect.getCertificateUID ());
                }
                bCertificateSubjectFound = true;
                break OUTER;
              }
            }
          }
        }

        if (!bCertificateSubjectFound)
          throw new UnknownException ("The X509 certificate did not contain a certificate subject.");
      }

      return aMetadata;
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private void _saveServiceRegistrationResource (final WebResource aFullResource,
                                                 final ServiceMetadataType aServiceMetadata,
                                                 final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());

      // Create JAXBElement!
      aBuilderWithAuth.put (m_aObjFactory.createServiceMetadata (aServiceMetadata));
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  private static void _deleteServiceRegistrationResource (final WebResource aFullResource,
                                                          final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());
      aBuilderWithAuth.delete ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  /**
   * Get the URL of the passed service group's SMP using the specified SML zone.
   * 
   * @param aSMLInfo
   *        The SML zone to use.
   * @param aServiceGroupID
   *        The service group to get the SMP URL from.
   * @return The non-<code>null</code> URL of the passed service group's SMP.
   */
  @Nonnull
  private static String _convertServiceGroupToURI (@Nonnull final ISMLInfo aSMLInfo,
                                                   @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) {
    final String sHost = BusdoxURLUtils.getDNSNameOfParticipant (aServiceGroupID, aSMLInfo);
    return "http://" + sHost;
  }

  /**
   * Convert the passed generic exception into a more specific exception.
   * 
   * @param ex
   *        The generic exception
   * @return A new SMP specific exception, using the passed exception as the
   *         cause.
   */
  @Nonnull
  private static Exception _getConvertedException (@Nonnull final UniformInterfaceException ex) {
    final Status eHttpStatus = ex.getResponse ().getClientResponseStatus ();
    switch (eHttpStatus) {
      case FORBIDDEN:
        return new UnauthorizedException (ex);
      case NOT_FOUND:
        return new NotFoundException (ex);
      case BAD_REQUEST:
        return new BadRequestException (ex);
      default:
        return new UnknownException ("Error thrown with status code: '" +
                                     eHttpStatus +
                                     "' (" +
                                     eHttpStatus.getStatusCode () +
                                     "), and message: " +
                                     ex.getResponse ().getEntity (String.class));
    }
  }
}

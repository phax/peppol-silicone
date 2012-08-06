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
package at.peppol.smp.client;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
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

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.commons.ipmapper.ConfiguredDNSMapper;
import at.peppol.commons.ipmapper.ISocketType;
import at.peppol.commons.sml.ISMLInfo;
import at.peppol.commons.uri.BusdoxURLUtils;
import at.peppol.commons.utils.ConfigFile;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.exception.BadRequestException;
import at.peppol.smp.client.exception.NotFoundException;
import at.peppol.smp.client.exception.UnauthorizedException;
import at.peppol.smp.client.exception.UnknownException;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * This class is used for calling the SMP REST interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class SMPServiceCaller {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceCaller.class);

  // Don't think of changing anything here! This is very sensitive. Don't
  // extract a base class or some else. DON'T TOUCH!
  // See http://sites.google.com/site/richchihlee/portal/j2ee/rs/rs-client
  private static final GenericType <JAXBElement <ServiceGroupType>> TYPE_SERVICEGROUP = new GenericType <JAXBElement <ServiceGroupType>> () {};
  private static final GenericType <JAXBElement <CompleteServiceGroupType>> TYPE_COMPLETESERVICEGROUP = new GenericType <JAXBElement <CompleteServiceGroupType>> () {};
  private static final GenericType <JAXBElement <ServiceGroupReferenceListType>> TYPE_SERVICEGROUPREFERENCELIST = new GenericType <JAXBElement <ServiceGroupReferenceListType>> () {};
  private static final GenericType <JAXBElement <SignedServiceMetadataType>> TYPE_SIGNEDSERVICEMETADATA = new GenericType <JAXBElement <SignedServiceMetadataType>> () {};

  // Members - free to change from here on
  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();
  private static final ConfiguredDNSMapper s_aDNSMapper = new ConfiguredDNSMapper (ConfigFile.getInstance ());

  private final URI m_aSMPHost;
  private final WebResource m_aWebResource;
  private final WebResource m_aWebResourceWithSignatureCheck;

  @Nonnull
  private static WebResource _getResourceWithSignatureCheck (@Nonnull final URI aURI) {
    if (aURI == null)
      throw new NullPointerException ("URI");

    final Client aClient = Client.create ();
    aClient.addFilter (new CheckSignatureFilter ());
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  @Nonnull
  private static WebResource _getResource (@Nonnull final URI aURI) {
    if (aURI == null)
      throw new NullPointerException ("URI");

    final Client aClient = Client.create ();
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  /**
   * Constructor with SML lookup
   * 
   * @param aParticipantIdentifier
   *        The participant identifier to be used. Required to build the SMP
   *        access URI.
   * @param aSMLInfo
   *        The SML to be used. Required to build the SMP access URI.
   * @see BusdoxURLUtils#getSMPURIOfParticipant(IReadonlyParticipantIdentifier,
   *      ISMLInfo)
   */
  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull final ISMLInfo aSMLInfo) {
    this (BusdoxURLUtils.getSMPURIOfParticipant (aParticipantIdentifier, aSMLInfo));
  }

  /**
   * Constructor with SML lookup
   * 
   * @param aParticipantIdentifier
   *        The participant identifier to be used. Required to build the SMP
   *        access URI.
   * @param sSMLZoneName
   *        The SML DNS zone name to be used. Required to build the SMP access
   *        URI. Must end with a trailing dot (".") and may neither be
   *        <code>null</code> nor empty to build a correct URL. May not start
   *        with "http://". Example: <code>sml.peppolcentral.org.</code>
   * @see BusdoxURLUtils#getSMPURIOfParticipant(IReadonlyParticipantIdentifier,
   *      String)
   */
  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull @Nonempty final String sSMLZoneName) {
    this (BusdoxURLUtils.getSMPURIOfParticipant (aParticipantIdentifier, sSMLZoneName));
  }

  /**
   * Constructor with a direct SMP URL.<br>
   * Remember: must be HTTP and using port 80 only!
   * 
   * @param aSMPHost
   *        The address of the SMP service. Must be port 80 and basic http only
   *        (no https!). Example: http://smpcompany.company.org
   */
  public SMPServiceCaller (@Nonnull final URI aSMPHost) {
    if (aSMPHost == null)
      throw new NullPointerException ("smpHost");
    if (!"http".equals (aSMPHost.getScheme ()))
      s_aLogger.warn ("SMP URI " + aSMPHost + " does not use the expected http scheme!");
    // getPort () returns -1 if none was explicitly specified
    if (aSMPHost.getPort () != 80 && aSMPHost.getPort () != -1)
      s_aLogger.warn ("SMP URI " + aSMPHost + " is not running on port 80!");

    try {
      final String sOriginalHost = aSMPHost.getHost ();
      final InetAddress aInetAddr = InetAddress.getByName (sOriginalHost);
      final ISocketType aRealHostToUse = s_aDNSMapper.mapInternal (aInetAddr);
      if (!sOriginalHost.equals (aRealHostToUse.getHost ())) {
        final int nPortToUse = aRealHostToUse.getPort () != null ? aRealHostToUse.getPort ().intValue ()
                                                                : aSMPHost.getPort ();
        m_aSMPHost = URI.create (aSMPHost.getScheme () +
                                 "://" +
                                 aRealHostToUse.getHost () +
                                 (nPortToUse <= 0 ? "" : ":" + nPortToUse));
        s_aLogger.info ("Changed the SMP host from " + aSMPHost + " to " + m_aSMPHost);
      }
      else
        m_aSMPHost = aSMPHost;
    }
    catch (final UnknownHostException ex) {
      // Should never occur, as the SML hosts
      throw new IllegalStateException ("Failed to resolve host from " + aSMPHost, ex);
    }

    m_aWebResource = _getResource (m_aSMPHost);
    m_aWebResourceWithSignatureCheck = _getResourceWithSignatureCheck (m_aSMPHost);
  }

  /**
   * @return The SMP host URI we're operating on
   */
  @Nonnull
  public URI getSMPHost () {
    return m_aSMPHost;
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

  @Nonnull
  private static ServiceGroupReferenceListType _getServiceGroupReferenceList (@Nonnull final WebResource aFullResource,
                                                                              @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_getServiceGroupReferenceList from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());
      return aBuilderWithAuth.get (TYPE_SERVICEGROUPREFERENCELIST).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
  }

  /**
   * Gets a list of references to the CompleteServiceGroup's owned by the
   * specified userId.
   * 
   * @param sUserID
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
  @Nonnull
  public ServiceGroupReferenceListType getServiceGroupReferenceList (@Nonnull final String sUserID,
                                                                     @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (StringHelper.hasNoText (sUserID))
      throw new IllegalArgumentException ("The user ID for which the listing should be created, must be supplied!");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final WebResource aFullResource = m_aWebResource.path ("/list/" + BusdoxURLUtils.createPercentEncodedURL (sUserID));
    return _getServiceGroupReferenceList (aFullResource, aCredentials);
  }

  @Nullable
  public ServiceGroupReferenceListType getServiceGroupReferenceListOrNull (@Nonnull final String sUserID,
                                                                           @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    try {
      return getServiceGroupReferenceList (sUserID, aCredentials);
    }
    catch (final NotFoundException ex) {
      return null;
    }
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
  @Deprecated
  public static ServiceGroupReferenceListType getServiceGroupReferenceList (@Nonnull final URI aURI,
                                                                            @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    return _getServiceGroupReferenceList (_getResource (aURI), aCredentials);
  }

  private static CompleteServiceGroupType _getCompleteServiceGroup (@Nonnull final WebResource aFullResource) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_getCompleteServiceGroup from " + aFullResource.getURI ());

    try {
      return aFullResource.get (TYPE_COMPLETESERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
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
  @Nonnull
  public CompleteServiceGroupType getCompleteServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");

    final WebResource aFullResource = m_aWebResource.path ("/complete/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
    return _getCompleteServiceGroup (aFullResource);
  }

  @Nullable
  public CompleteServiceGroupType getCompleteServiceGroupOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    try {
      return getCompleteServiceGroup (aServiceGroupID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata. This method is handy when using
   * the results from
   * {@link #getServiceGroupReferenceList(String, IReadonlyUsernamePWCredentials)}
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
  public static CompleteServiceGroupType getCompleteServiceGroup (@Nonnull final URI aURI) throws Exception {
    return _getCompleteServiceGroup (_getResource (aURI));
  }

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
  @Nonnull
  public static CompleteServiceGroupType getCompleteServiceGroupByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    return new SMPServiceCaller (aServiceGroupID, aSMLInfo).getCompleteServiceGroup (aServiceGroupID);
  }

  @Nonnull
  private static ServiceGroupType _getServiceGroup (@Nonnull final WebResource aFullResource) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_getServiceGroup from " + aFullResource.getURI ());

    try {
      return aFullResource.get (TYPE_SERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
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
  @Nonnull
  public ServiceGroupType getServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
    return _getServiceGroup (aFullResource);
  }

  @Nullable
  public ServiceGroupType getServiceGroupOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    try {
      return getServiceGroup (aServiceGroupID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
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
  @Deprecated
  public static ServiceGroupType getServiceGroup (@Nonnull final URI aURI) throws Exception {
    return _getServiceGroup (_getResource (aURI));
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
  @Nonnull
  public static ServiceGroupType getServiceGroupByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    return new SMPServiceCaller (aServiceGroupID, aSMLInfo).getServiceGroup (aServiceGroupID);
  }

  private static void _saveServiceGroup (@Nonnull final WebResource aFullResource,
                                         @Nonnull final ServiceGroupType aServiceGroup,
                                         @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");
    if (aServiceGroup == null)
      throw new NullPointerException ("serviceGroup");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_saveServiceGroup from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());

      // Important to build a JAXBElement around the service group
      aBuilderWithAuth.put (s_aObjFactory.createServiceGroup (aServiceGroup));
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
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
  public void saveServiceGroup (@Nonnull final ServiceGroupType aServiceGroup,
                                @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aServiceGroup == null)
      throw new NullPointerException ("serviceGroup");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroup.getParticipantIdentifier ()));
    _saveServiceGroup (aFullResource, aServiceGroup, aCredentials);
  }

  /**
   * Saves a service group. The metadata references should not be set and are
   * not used.
   * 
   * @param aParticipantID
   *        The participant identifier for which the service group is to save.
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
  public void saveServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aParticipantID,
                                @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aParticipantID == null)
      throw new NullPointerException ("participantID");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final ServiceGroupType aServiceGroup = s_aObjFactory.createServiceGroupType ();
    aServiceGroup.setParticipantIdentifier (new SimpleParticipantIdentifier (aParticipantID));
    saveServiceGroup (aServiceGroup, aCredentials);
  }

  /**
   * Saves a service group. The metadata references should not be set and are
   * not used.
   * 
   * @param aURI
   *        The URI to the service group resource.
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
   *         The request was not well formed. This could be caused by the URI
   *         not corresponding to the service group id in the service group
   *         object.
   */
  @Deprecated
  public static void saveServiceGroup (@Nonnull final URI aURI,
                                       @Nonnull final ServiceGroupType aServiceGroup,
                                       @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _saveServiceGroup (_getResource (aURI), aServiceGroup, aCredentials);
  }

  private static void _deleteServiceGroup (@Nonnull final WebResource aFullResource,
                                           @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_deleteServiceGroup from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());
      aBuilderWithAuth.delete ();
    }
    catch (final UniformInterfaceException ex) {
      throw _getConvertedException (ex);
    }
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
  public void deleteServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                  @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
    _deleteServiceGroup (aFullResource, aCredentials);
  }

  /**
   * Deletes a service metadata object given by its service group id and its
   * document type.
   * 
   * @param aURI
   *        The URI to the service group resource.
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
  @Deprecated
  public static void deleteServiceGroup (@Nonnull final URI aURI,
                                         @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _deleteServiceGroup (_getResource (aURI), aCredentials);
  }

  private static SignedServiceMetadataType _getSignedServiceMetadata (@Nonnull final WebResource aFullResource) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_getSignedServiceMetadata from " + aFullResource.getURI ());

    try {
      SignedServiceMetadataType aMetadata = aFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

      // If the Redirect element is present, then follow 1 redirect.
      if (aMetadata.getServiceMetadata () != null && aMetadata.getServiceMetadata ().getRedirect () != null) {
        final RedirectType aRedirect = aMetadata.getServiceMetadata ().getRedirect ();

        // Follow the redirect
        final WebResource aRedirectFullResource = _getResourceWithSignatureCheck (URI.create (aRedirect.getHref ()));
        aMetadata = aRedirectFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

        // Check that the certificateUID is correct.
        boolean bCertificateSubjectFound = false;
        final Iterator <Object> aKeyInfoIter = aMetadata.getSignature ().getKeyInfo ().getContent ().iterator ();
        outer: while (aKeyInfoIter.hasNext ()) {
          final JAXBElement <?> aInfo = (JAXBElement <?>) aKeyInfoIter.next ();
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
                break outer;
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
  @Nonnull
  public SignedServiceMetadataType getServiceRegistration (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                           @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");
    if (aDocumentTypeID == null)
      throw new NullPointerException ("documentType");

    final String sPath = IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                         "/services/" +
                         IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID);
    final WebResource aFullResource = m_aWebResourceWithSignatureCheck.path (sPath);
    return _getSignedServiceMetadata (aFullResource);
  }

  @Nullable
  public SignedServiceMetadataType getServiceRegistrationOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    try {
      return getServiceRegistration (aServiceGroupID, aDocumentTypeID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
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
  @Deprecated
  public static SignedServiceMetadataType getServiceRegistration (@Nonnull final URI aURI) throws Exception {
    return _getSignedServiceMetadata (_getResourceWithSignatureCheck (aURI));
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
  @Nonnull
  public static SignedServiceMetadataType getServiceRegistrationByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                                       @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    return new SMPServiceCaller (aServiceGroupID, aSMLInfo).getServiceRegistration (aServiceGroupID, aDocumentTypeID);
  }

  @Nullable
  public EndpointType getEndpoint (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                   @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");
    if (aDocumentTypeID == null)
      throw new NullPointerException ("documentType");
    if (aProcessID == null)
      throw new NullPointerException ("processID");

    // Get meta data for participant/documentType
    final SignedServiceMetadataType aSignedServiceMetadata = getServiceRegistrationOrNull (aServiceGroupID,
                                                                                           aDocumentTypeID);
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
            s_aLogger.warn ("Found " +
                            aEndpoints.size () +
                            " endpoints for process " +
                            aProcessID +
                            ": " +
                            aEndpoints.toString ());

          // Extract the address
          return ContainerHelper.getFirstElement (aEndpoints);
        }
      }
    }
    return null;
  }

  @Nullable
  public String getEndpointAddress (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                    @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                    @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID);
    return aEndpoint == null ? null : W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ());
  }

  @Nullable
  public String getEndpointCertificateString (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                              @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                              @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID);
    return aEndpoint == null ? null : aEndpoint.getCertificate ();
  }

  @Nullable
  public X509Certificate getEndpointCertificate (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                                 @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    final String sCertString = getEndpointCertificateString (aServiceGroupID, aDocumentTypeID, aProcessID);
    return CertificateUtils.convertStringToCertficate (sCertString);
  }

  private static void _saveServiceRegistration (@Nonnull final WebResource aFullResource,
                                                @Nonnull final ServiceMetadataType aServiceMetadata,
                                                @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");
    if (aServiceMetadata == null)
      throw new NullPointerException ("serviceMetadata");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_saveServiceRegistration from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION,
                                                             aCredentials.getAsHTTPHeaderValue ());

      // Create JAXBElement!
      aBuilderWithAuth.put (s_aObjFactory.createServiceMetadata (aServiceMetadata));
    }
    catch (final UniformInterfaceException e) {
      throw _getConvertedException (e);
    }
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
  public void saveServiceRegistration (@Nonnull final ServiceMetadataType aServiceMetadata,
                                       @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aServiceMetadata == null)
      throw new NullPointerException ("serviceMetadata");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation ();
    if (aServiceInformation == null)
      throw new IllegalArgumentException ("ServiceMetadata does not contain serviceInformation");
    final IReadonlyParticipantIdentifier aServiceGroupID = aServiceInformation.getParticipantIdentifier ();
    if (aServiceGroupID == null)
      throw new IllegalArgumentException ("ServiceInformation does not contain serviceGroupID");
    final IReadonlyDocumentTypeIdentifier aDocumentTypeID = aServiceInformation.getDocumentIdentifier ();
    if (aDocumentTypeID == null)
      throw new IllegalArgumentException ("ServiceInformation does not contain documentTypeID");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));
    _saveServiceRegistration (aFullResource, aServiceMetadata, aCredentials);
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
  @Deprecated
  public static void saveServiceRegistration (@Nonnull final URI aURI,
                                              @Nonnull final ServiceMetadataType aServiceMetadata,
                                              @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _saveServiceRegistration (_getResource (aURI), aServiceMetadata, aCredentials);
  }

  private static void _deleteServiceRegistration (@Nonnull final WebResource aFullResource,
                                                  @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aFullResource == null)
      throw new NullPointerException ("fullResource");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_deleteServiceRegistration from " + aFullResource.getURI ());

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
  public void deleteServiceRegistration (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                         @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                         @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    if (aServiceGroupID == null)
      throw new NullPointerException ("serviceGroupID");
    if (aDocumentTypeID == null)
      throw new NullPointerException ("documentTypeID");
    if (aCredentials == null)
      throw new NullPointerException ("credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));
    _deleteServiceRegistration (aFullResource, aCredentials);
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
  @Deprecated
  public static void deleteServiceRegistration (@Nonnull final URI aURI,
                                                @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    _deleteServiceRegistration (_getResource (aURI), aCredentials);
  }
}

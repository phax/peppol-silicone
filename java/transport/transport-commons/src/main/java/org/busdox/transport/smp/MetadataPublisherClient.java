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
package org.busdox.transport.smp;

import java.net.InetAddress;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;

import eu.peppol.busdox.identifier.IdentifierUtils;
import eu.peppol.busdox.ipmapper.IDNSInternalMapper;
import eu.peppol.busdox.uri.BusdoxURLUtils;
import eu.peppol.busdox.wsaddr.W3CEndpointReferenceUtils;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
// TODO why not using the SMP client library here?
public final class MetadataPublisherClient {
  private static final Logger log = LoggerFactory.getLogger (MetadataPublisherClient.class);
  private final SignedServiceMetadataType signedServiceMetadataType;

  public MetadataPublisherClient (final String sSMLDNSZoneName,
                                  final ParticipantIdentifierType aBusinessId,
                                  final DocumentIdentifierType aDocumentId) throws MetadataLookupException {
    this (sSMLDNSZoneName, aBusinessId, aDocumentId, null);
  }

  public MetadataPublisherClient (final String smlDNSZoneName,
                                  final ParticipantIdentifierType aBusinessId,
                                  final DocumentIdentifierType aDocumentId,
                                  @Nullable final IDNSInternalMapper aIPMapper) throws MetadataLookupException {
    // TODO validate signature
    signedServiceMetadataType = _getSignedServiceMetadata (smlDNSZoneName, aBusinessId, aDocumentId, aIPMapper);
  }

  @Nonnull
  private static Document _getMetadataDocument (final String sSMLDNSZoneName,
                                                final IReadonlyParticipantIdentifier aBusinessID,
                                                final IReadonlyDocumentIdentifier aDocumentID,
                                                @CheckForNull final IDNSInternalMapper aIPMapper) throws MetadataLookupException {
    final String businessIdValue = aBusinessID.getValue ();
    if (!businessIdValue.contains (":")) {
      log.info ("Business identifier " + businessIdValue + " is not formatted correct(type:id)");
      throw new MetadataLookupException ("Business identifier " + businessIdValue + "is not formatted correct(type:id)");
    }

    String sRESTURL = null;
    try {
      String sDNSName = BusdoxURLUtils.getDNSNameOfParticipant (aBusinessID, sSMLDNSZoneName);

      if (aIPMapper != null) {
        // Perform IP conversion
        log.info (String.format ("original dns = '%s'", sDNSName));
        sDNSName = aIPMapper.mapInternal (InetAddress.getByName (sDNSName)).getSocketString ();
        log.info (String.format ("translated dns = '%s'", sDNSName));
      }
      else {
        log.info (String.format ("no mapping for dns-translation of '%s' found", sDNSName));
      }
      sRESTURL = "http://" +
                 sDNSName +
                 "/" +
                 IdentifierUtils.getIdentifierURIPercentEncoded (aBusinessID) +
                 "/services/" +
                 IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentID);
      log.info ("Metadata lookup: " + sRESTURL);
      final String content = BusdoxURLUtils.getURLContent (sRESTURL);
      return XMLReader.readXMLDOM (content);
    }
    catch (final Exception e) {
      log.warn ("No metadata found for business identifer at " + sRESTURL, e);
      throw new MetadataLookupException ("No metadata found for business identifer at " + sRESTURL);
    }
  }

  @Nonnull
  private static SignedServiceMetadataType _getUnmarshalledSignedServiceMetadataType (@Nonnull final Document doc) throws MetadataLookupException {
    try {
      final JAXBContext jc = JAXBContextCache.getInstance ().getFromCache (SignedServiceMetadataType.class);
      final Unmarshaller u = jc.createUnmarshaller ();
      final JAXBElement <SignedServiceMetadataType> root = u.unmarshal (doc, SignedServiceMetadataType.class);
      return root.getValue ();
    }
    catch (final Exception ex) {
      log.error ("Unmarshalling metadata from SMP failed", ex);
      final String xml = XMLWriter.getXMLString (doc);
      log.error ("XML Code:\n" + xml);
      throw new MetadataLookupException ("Invalid response from SMP server. " + xml);
    }
  }

  @Nonnull
  private static SignedServiceMetadataType _getSignedServiceMetadata (final String smlDNSZoneName,
                                                                      final ParticipantIdentifierType businessId,
                                                                      final DocumentIdentifierType documentId,
                                                                      @Nullable final IDNSInternalMapper ipMapper) throws MetadataLookupException {
    final Document doc = _getMetadataDocument (smlDNSZoneName, businessId, documentId, ipMapper);
    return _getUnmarshalledSignedServiceMetadataType (doc);
  }

  @Nullable
  public String getEndpointAddress (@Nonnull final IReadonlyProcessIdentifier aProcessID) {
    final List <ProcessType> aAllProcesses = signedServiceMetadataType.getServiceMetadata ()
                                                                      .getServiceInformation ()
                                                                      .getProcessList ()
                                                                      .getProcess ();
    for (final ProcessType aProcessType : aAllProcesses) {
      if (IdentifierUtils.areIdentifiersEqual (aProcessType.getProcessIdentifier (), aProcessID)) {
        // TODO add validation of authentication level etc.
        final List <EndpointType> aEndpoints = aProcessType.getServiceEndpointList ().getEndpoint ();
        if (aEndpoints.size () != 1)
          log.warn ("Found " + aEndpoints.size () + " endpoints for process " + aProcessID);
        return W3CEndpointReferenceUtils.getAddress (aEndpoints.get (0).getEndpointReference ());
      }
    }
    return null;
  }
}

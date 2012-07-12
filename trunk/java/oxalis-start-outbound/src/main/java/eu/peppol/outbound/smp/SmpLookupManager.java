package eu.peppol.outbound.smp;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.uri.BusdoxURLUtils;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;

import com.phloc.commons.jaxb.JAXBContextCache;

import eu.peppol.outbound.util.Log;
import eu.peppol.security.SmpResponseValidator;
import eu.peppol.util.Util;

/**
 * User: nigel Date: Oct 25, 2011 Time: 9:01:53 AM
 * 
 * @author Nigel Parker
 * @author Steinar O. Cook
 */
public final class SmpLookupManager {
  protected static final String SML_PEPPOLCENTRAL_ORG = "sml.peppolcentral.org";

  private SmpLookupManager () {}

  /**
   * @param participant
   * @param documentTypeIdentifier
   * @return The endpoint address for the participant and DocumentId
   * @throws RuntimeException
   *         If the end point address cannot be resolved for the participant.
   *         This is caused by a {@link java.net.UnknownHostException}
   */
  public static URL getEndpointAddress (final ParticipantIdentifierType participant,
                                        final IDocumentTypeIdentifier documentTypeIdentifier) {

    final EndpointType endpointType = _getEndpointType (participant, documentTypeIdentifier);
    final String address = W3CEndpointReferenceUtils.getAddress (endpointType.getEndpointReference ());
    Log.info ("Found endpoint address for " + participant.getValue () + " from SMP: " + address);

    try {
      return new URL (address);
    }
    catch (final Exception e) {
      throw new RuntimeException ("SMP returned invalid URL", e);
    }
  }

  /**
   * Retrieves the end point certificate for the given combination of receiving
   * participant id and document type identifer.
   * 
   * @param participant
   *        receiving participant
   * @param documentTypeIdentifier
   *        document type to be sent
   * @return The X509Certificate for the given ParticipantIdentifierType and
   *         DocumentId
   * @throws RuntimeException
   *         If the end point address cannot be resolved for the participant.
   *         This is caused by a {@link java.net.UnknownHostException}
   */
  public static X509Certificate getEndpointCertificate (final ParticipantIdentifierType participant,
                                                        final IDocumentTypeIdentifier documentTypeIdentifier) {

    try {
      final String body = _getEndpointType (participant, documentTypeIdentifier).getCertificate ();
      final String endpointCertificate = "-----BEGIN CERTIFICATE-----\n" + body + "\n-----END CERTIFICATE-----";
      final CertificateFactory certificateFactory = CertificateFactory.getInstance ("X.509");
      return (X509Certificate) certificateFactory.generateCertificate (new ByteArrayInputStream (endpointCertificate.getBytes ()));

    }
    catch (final CertificateException e) {
      throw new RuntimeException ("Failed to get certificate from SMP for " +
                                  participant.getScheme () +
                                  ":" +
                                  participant.getValue ());
    }
  }

  private static EndpointType _getEndpointType (final ParticipantIdentifierType participant,
                                                final IDocumentTypeIdentifier documentTypeIdentifier) {
    try {
      final SignedServiceMetadataType serviceMetadata = _getServiceMetaData (participant, documentTypeIdentifier);

      return serviceMetadata.getServiceMetadata ()
                            .getServiceInformation ()
                            .getProcessList ()
                            .getProcess ()
                            .get (0)
                            .getServiceEndpointList ()
                            .getEndpoint ()
                            .get (0);

    }
    catch (final Exception e) {
      throw new RuntimeException ("Problem with SMP lookup", e);
    }
  }

  private static SignedServiceMetadataType _getServiceMetaData (final ParticipantIdentifierType participant,
                                                                final IDocumentTypeIdentifier documentTypeIdentifier) throws SmpSignedServiceMetaDataException {

    URL smpUrl = null;
    try {
      smpUrl = _getSmpUrl (participant, documentTypeIdentifier);
    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to construct URL for " +
                                       participant +
                                       ", documentType" +
                                       documentTypeIdentifier +
                                       "; " +
                                       e.getMessage (), e);
    }

    InputSource smpContents = null;
    try {
      Log.debug ("Constructed SMP url: " + smpUrl.toExternalForm ());
      smpContents = Util.getUrlContent (smpUrl);
    }
    catch (final Exception e) {
      throw new SmpSignedServiceMetaDataException (participant, documentTypeIdentifier, smpUrl, e);
    }

    try {

      // Parses the XML response from the SMP
      final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
      documentBuilderFactory.setNamespaceAware (true);
      final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
      final Document document = documentBuilder.parse (smpContents);

      // Validates the signature
      final SmpResponseValidator smpResponseValidator = new SmpResponseValidator (document);
      if (!smpResponseValidator.isSmpSignatureValid ()) {
        throw new IllegalStateException ("SMP response contained invalid signature");
      }

      /**
       * TODO: Ucomment this once PEPPOL has decided how we can follow the chain
       * of trust for the SMP certificate. // Validates the certificate supplied
       * with the signature if
       * (!keystoreManager.validate(smpResponseValidator.getCertificate())) {
       * throw new IllegalStateException("SMP Certificate not valid for " +
       * smpUrl); }
       */
      final Unmarshaller unmarshaller = JAXBContextCache.getInstance ()
                                                        .getFromCache (SignedServiceMetadataType.class)
                                                        .createUnmarshaller ();

      return unmarshaller.unmarshal (document, SignedServiceMetadataType.class).getValue ();
    }
    catch (final Exception e) {
      throw new SmpSignedServiceMetaDataException (participant, documentTypeIdentifier, smpUrl, e);
    }
  }

  private static URL _getSmpUrl (final ParticipantIdentifierType participantId,
                                 final IDocumentTypeIdentifier documentTypeIdentifier) throws Exception {
    final String scheme = participantId.getScheme ();
    final String value = participantId.getValue ();
    final String hostname = "B-" +
                            BusdoxURLUtils.getHashValueStringRepresentation (value.toLowerCase ()) +
                            "." +
                            scheme +
                            "." +
                            SML_PEPPOLCENTRAL_ORG;
    final String encodedParticipant = IdentifierUtils.getIdentifierURIPercentEncoded (participantId);
    final String encodedDocumentId = IdentifierUtils.getIdentifierURIPercentEncoded (documentTypeIdentifier);

    return new URL ("http://" + hostname + "/" + encodedParticipant + "/services/" + encodedDocumentId);
  }

  public static SimpleProcessIdentifier getProcessIdentifierForDocumentType (final ParticipantIdentifierType participantId,
                                                                             final IDocumentTypeIdentifier documentTypeIdentifier) throws SmpSignedServiceMetaDataException {
    final SignedServiceMetadataType serviceMetaData = _getServiceMetaData (participantId, documentTypeIdentifier);
    // SOAP generated type...
    final ProcessIdentifierType processIdentifier = serviceMetaData.getServiceMetadata ()
                                                                   .getServiceInformation ()
                                                                   .getProcessList ()
                                                                   .getProcess ()
                                                                   .get (0)
                                                                   .getProcessIdentifier ();

    // Converts SOAP generated type into something nicer
    return new SimpleProcessIdentifier (processIdentifier);
  }
}

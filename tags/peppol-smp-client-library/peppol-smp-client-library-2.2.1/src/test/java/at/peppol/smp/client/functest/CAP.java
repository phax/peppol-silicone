package at.peppol.smp.client.functest;

import java.security.cert.CertificateException;

import javax.annotation.concurrent.Immutable;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.SMPServiceCaller;

import com.phloc.commons.exceptions.InitializationException;

@Immutable
public final class CAP {
  public static final String START_AP_ADDRESS = "https://peppol-ap.example.org/accessPointService/";
  public static final W3CEndpointReference START_AP_ENDPOINTREF = W3CEndpointReferenceUtils.createEndpointReference (START_AP_ADDRESS);

  /**
   * The Base64 encoded, DER encoded AP certificate. E.g. to be created by
   * extracting the head certificate from the ap_keystore.jks using Portecle.
   * This certificate only contains the public key!
   */
  public static final String AP_CERT_STRING = "TODO!!!!!";
  public static final String AP_SERVICE_DESCRIPTION = "My Accesspoint Service";
  public static final String AP_CONTACT_URL = "support@example.org";
  public static final String AP_INFO_URL = "http://www.example.org";

  // init
  static {
    try {
      if (SMPServiceCaller.convertStringToCertficate (AP_CERT_STRING) == null)
        throw new InitializationException ("Failed to convert certificate string to a certificate!");
    }
    catch (final CertificateException ex) {
      throw new InitializationException ("Failed to convert certificate string to a certificate!", ex);
    }
  }

  private CAP () {}
}

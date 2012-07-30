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
package at.peppol.smp.client.functest;

import java.io.File;
import java.security.cert.CertificateException;

import javax.annotation.concurrent.Immutable;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.SMPServiceCaller;

import com.phloc.commons.base64.Base64;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.io.file.SimpleFileIO;

@Immutable
public final class CAP {
  public static final String START_AP_ADDRESS = "http://infra.peppol.at/transport-start-server-1.0.1/accessPointService/";
  public static final W3CEndpointReference START_AP_ENDPOINTREF = W3CEndpointReferenceUtils.createEndpointReference (START_AP_ADDRESS);

  /**
   * The Base64 encoded, DER encoded AP head certificate. E.g. to be created by
   * extracting the head certificate from the ap_keystore.jks using Portecle.
   * This certificate only contains the public key!
   */
  public static final String AP_CERT_STRING = "MIIEfTCCA2WgAwIBAgIQe0Rpx6UccgXzhYxrhM4KSTANBgkqhkiG9w0BAQUFADB9MQswCQYDVQQGEwJESzEnMCUGA1UEChMeTkFUSU9OQUwgSVQgQU5EIFRFTEVDT00gQUdFTkNZMR8wHQYDVQQLExZGT1IgVEVTVCBQVVJQT1NFUyBPTkxZMSQwIgYDVQQDExtQRVBQT0wgQUNDRVNTIFBPSU5UIFRFU1QgQ0EwHhcNMTAxMjIwMDAwMDAwWhcNMTIxMjE5MjM1OTU5WjBaMQswCQYDVQQGEwJBVDEyMDAGA1UECgwpQlJaIC0gRmVkZXJhbCBDb21wdXRpbmcgQ2VudGVyIG9mIEF1c3RyaWExFzAVBgNVBAMMDkFQUF8xMDAwMDAwMDAxMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Xk1bDdathIYWW6xBgLTg0rfrm+kk/E3/QoX+4qzlKfL5LcY85LOuikkZuiPTfX9DksVCI0m4bwzeBg+9HuIP2gcFvu2Sdux32Dgjn8gnwGtOPh7VEykwFUumBoj+45NE1zXMfW4Z5Jp3M/L46M5Wp2MT63PKjyZqXsfupTd4QFcaPqlofNBHDTbkAARNPXQUx2Qmmrfyt8MoPUKhi3+iZozbZYfxswUq+H0VSM8wSaAukDIKhDNydhbTdx8pFqJICKGi5uW34W8DL64nacFT4MO5Sn+wcIZSz0QOiLhpTSudaEsPuA1hvFVcG83bzVEEDy8phNc+BvB2I7JNOfGjwIDAQABo4IBGjCCARYwCQYDVR0TBAIwADALBgNVHQ8EBAMCA7gwawYDVR0fBGQwYjBgoF6gXIZaaHR0cDovL3BpbG90b25zaXRlY3JsLnZlcmlzaWduLmNvbS9JVG9nVGVsZXN0eXJlbHNlblBpbG90UEVQUE9MQUNDRVNTUE9JTlRDQS9MYXRlc3RDUkwuY3JsMB8GA1UdIwQYMBaAFPeWixlMruJWIQC+hv16R6ydygV1MB0GA1UdDgQWBBTXpLss6GgZvDIF/I4yxyKk8KdyYzA6BggrBgEFBQcBAQQuMCwwKgYIKwYBBQUHMAGGHmh0dHA6Ly9waWxvdC1vY3NwLnZlcmlzaWduLmNvbTATBgNVHSUEDDAKBggrBgEFBQcDAjANBgkqhkiG9w0BAQUFAAOCAQEAUn4AoUnR/LtaTUQWhyTTPavTCIoUYQvCMT61KBJu0HACg01s6Yf+T2rtzrNZA6bnh9OyJM+gcvqwkxxG0hvvd+kEcFFKC8++HvSo5e6a5pLnDUJk7haKk52upevaK/NXLFn9X8wU/53nzZQbw+g9KfQANbe7MM+75DD7FkWcFCX/aMDDrnog8dKTGpMYsYpAXrntDRGA3fh4DYCdtWunqLiNKNdakUAVRymr0yjaz6kofnLjczGVsZl75ems6i9siJL7vpSz0XCyu6IXLn+5mWnrO35tmfOcL2ttapDqubZulRLmZPG79te902IFzshsPEbWNRuI7NL53W6Ow7z/4w==";
  public static final String AP_SERVICE_DESCRIPTION = "Austrian Government PEPPOL AP";
  public static final String AP_CONTACT_URL = "support@peppol.at";
  public static final String AP_INFO_URL = "http://www.peppol.at";

  // init
  static {
    // How to get the Cert String:
    if (false)
      System.out.println (Base64.encodeBytes (SimpleFileIO.readFileBytes (new File ("AP Public Key.cer"))));

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

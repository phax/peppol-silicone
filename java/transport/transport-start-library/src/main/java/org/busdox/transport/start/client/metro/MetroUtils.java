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
package org.busdox.transport.start.client.metro;

import javax.annotation.concurrent.Immutable;
import javax.xml.soap.SOAPMessage;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.sun.xml.ws.api.message.Packet;

/**
 * utility class for specific processing of metro stuff bound to the metro
 * framework version 2.0.1. this utility uses non-public api of the metro
 * framework and may (partially) fail when updating to newer metro versions.
 *
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
@Immutable
public final class MetroUtils {
  private MetroUtils () {}

  @SuppressWarnings ("deprecation")
  public static String getLogDetails (final Packet packet) {
    final StringBuilder sb = new StringBuilder ("packet details:");
    sb.append ("\n")
      .append ("acceptable mimetypes: '" + packet.acceptableMimeTypes + "'")
      .append ("\n")
      .append ("soapAction: '" + packet.soapAction + "'")
      .append ("\n")
      .append ("using ssl: '" + packet.wasTransportSecure + "'")
      .append ("\n")
      .append ("content negotiation string: '" + packet.getContentNegotiationString () + "'")
      .append ("\n")
      .append ("endpoint address string: '" + packet.getEndPointAddressString () + "'")
      .append ("\n")
      .append ("expect reply: '" + packet.expectReply + "'")
      .append ("\n");

    try {
      if (true)
        return sb.toString ();
      sb.append ("message: '" + getMessageContent (packet.getMessage ().copy ().readAsSOAPMessage ()));
    }
    catch (final Exception e) {
      throw new RuntimeException ("could not parse message payload...", e);
    }
    return sb.toString ();
  }

  private static String getMessageContent (final SOAPMessage soapMessage) {
    final NonBlockingByteArrayOutputStream bout = new NonBlockingByteArrayOutputStream ();
    try {
      soapMessage.writeTo (bout);
      return bout.getAsString (CCharset.CHARSET_UTF_8);
    }
    catch (final Exception e) {
      throw new RuntimeException ("unable to get content from the soap message...", e);
    }
  }
}

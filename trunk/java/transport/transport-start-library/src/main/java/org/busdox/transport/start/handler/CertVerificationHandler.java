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
package org.busdox.transport.start.handler;

import java.util.Set;

import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.busdox.transport.soapheader.SOAPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;

/**
 * class is used by an accesspoint client to compare the certificate extracted
 * from SMP metadata with the certificates used by the foreign access point to
 * create signatures this is required to decide whether the foreign access point
 * uses the same certificate for signing as was registered on SMP during
 * registration process
 *
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
public class CertVerificationHandler implements SOAPHandler <SOAPMessageContext> {
  /**
   * the name of the property to retrieve the certificate from the
   * MessageContext
   */
  public static final String SMP_CERT = "peppol.start.smp.cert";

  private static final Logger logger = LoggerFactory.getLogger (CertVerificationHandler.class);

  @Override
  public void close (final MessageContext msgCtx) {
    logger.info ("close...");
  }

  @Override
  public boolean handleFault (final SOAPMessageContext msgCtx) {
    logger.info ("handle fault...");
    if (!SOAPHelper.isOutboundMessage (msgCtx)) {
      logger.error ("error occured during verification of the foreign accesspoints signature certificate...");
    }
    else {
      logger.error ("error occured during processing of outbound request...");
    }
    return true;
  }

  /**
   * this method is not fully implemented yet!!! there is a problem retrieving
   * the X509 Cert data from the SOAPMessageContext. Indeed there is no X509
   * data available at all!!!
   */
  @Override
  public boolean handleMessage (final SOAPMessageContext msgCtx) {
    if (SOAPHelper.isOutboundMessage (msgCtx)) {
      logger.info ("this is an outbound request...");
    }
    else {
      logger.info ("this is an inbound request...");
    }
    logger.info ("handle message...");
    try {
      logger.info ("message Property '" + SMP_CERT + "' is '" + msgCtx.getMessage ().getProperty (SMP_CERT) + "'");
      logger.info ("context Property '" + SMP_CERT + "' is '" + msgCtx.get (SMP_CERT) + "'");
    }
    catch (final SOAPException e) {
      logger.error ("error getting Property '" + SMP_CERT + "'", e);
    }
    final NonBlockingByteArrayOutputStream bout = new NonBlockingByteArrayOutputStream ();

    try {
      msgCtx.getMessage ().writeTo (bout);
      logger.info (bout.getAsString (CCharset.CHARSET_UTF_8));
    }
    catch (final Exception e) {
      logger.error ("error logging the soap message content ...", e);
    }

    return true;
  }

  @Override
  @SuppressWarnings ({ "rawtypes", "unchecked" })
  public Set getHeaders () {
    logger.info ("get headers...");
    return null;
  }
}

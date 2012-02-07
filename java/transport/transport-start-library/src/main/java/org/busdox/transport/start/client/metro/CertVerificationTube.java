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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;

/**
 * class is used by an accesspoint client to compare the certificate extracted
 * from smp metadata with the cert used by the foreign accesspoint to create
 * signatures this is required to decide whether the foreign accesspoint uses
 * the same certificate for signing as was registered on smp during registration
 * process the standard jax-ws handlers don't seem to work because the ws-*
 * specific headers are removed after being processed by the metro tube.
 *
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
public class CertVerificationTube extends AbstractFilterTubeImpl {
  private static final Logger logger = LoggerFactory.getLogger (CertVerificationTube.class);

  public enum ESide {
    SERVER,
    CLIENT
  }

  private final ESide m_eSide;

  /**
   * @param aNextTube
   */
  public CertVerificationTube (final Tube aNextTube, final ESide side) {
    super (aNextTube);
    m_eSide = side;
    if (logger.isDebugEnabled ())
      logger.debug ("constructor CertVerificationTube(Tube next)...");
  }

  /**
   * @param that
   * @param cloner
   */
  public CertVerificationTube (final AbstractFilterTubeImpl that, final TubeCloner cloner, final ESide side) {
    super (that, cloner);
    this.m_eSide = side;
    if (logger.isDebugEnabled ())
      logger.debug ("constructor CertVerificationTube(AbstractFilterTubeImpl that, TubeCloner cloner)...");
  }

  @Override
  public AbstractTubeImpl copy (final TubeCloner cloner) {
    if (logger.isDebugEnabled ())
      logger.debug ("method AbstractTubeImpl copy(TubeCloner cloner)...");
    return new CertVerificationTube (this, cloner, this.m_eSide);

  }

  @Override
  public NextAction processRequest (final Packet request) {
    logger.info ("method NextAction processRequest(Packet request)" +
                 ", received message of type '" +
                 request.getMessage ().getClass () +
                 "'");
    // check of smp certificate only applies to client side
    if (m_eSide == ESide.CLIENT && request.getWSDLOperation ().getLocalPart ().equalsIgnoreCase ("create")) {
      // do some stuff.....
      // blblblb
    }
    return super.processRequest (request);
  }

  @Override
  public NextAction processResponse (final Packet response) {
    logger.info ("method NextAction processResponse(Packet response)" +
                 ", received message of type '" +
                 response.getMessage ().getClass () +
                 "'");

    // check of smp certificate only applies to client side
    if (m_eSide == ESide.CLIENT && response.getWSDLOperation ().getLocalPart ().equalsIgnoreCase ("create")) {
      logger.info (MetroUtils.getLogDetails (response));
      logger.info ("checking the smp certificate on client side...");
    }
    return super.processResponse (response);
  }

  @Override
  public Packet process (final Packet p) {
    logger.info ("method Packet process(Packet p)" + ", received message of type '" + p.getMessage ().getClass () + "'");

    return super.process (p);
  }

  @Override
  public NextAction processException (final Throwable t) {
    logger.info ("method NextAction processException(Throwable t)" +
                 ", received exception of type '" +
                 t.getClass () +
                 "'");
    logger.info ("exception details: \n", t);
    return super.processException (t);
  }
}

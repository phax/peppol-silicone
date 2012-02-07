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
package org.busdox.transport.lime.impl;

import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.busdox.identifier.IReadonlyDocumentIdentifier;
import org.busdox.identifier.IReadonlyParticipantIdentifier;
import org.busdox.identifier.IReadonlyProcessIdentifier;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime.soapheader.SoapHeaderHandler;
import org.w3c.dom.Element;

import eu.peppol.busdox.identifier.SimpleDocumentIdentifier;
import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class SoapHeaderMapper {

  public void setupHandlerChain (final BindingProvider bindingProvider,
                                 final String channelID,
                                 final String messageID,
                                 final List <Element> referenceParameters) {
    setupHandlerChain (bindingProvider, null, null, null, null, channelID, messageID, referenceParameters);
  }

  public void setupHandlerChain (final BindingProvider bindingProvider, final String channelID, final String messageID) {
    setupHandlerChain (bindingProvider, null, null, null, null, channelID, messageID, null);
  }

  public void setupHandlerChain (final BindingProvider bindingProvider,
                                 final IReadonlyParticipantIdentifier sender,
                                 final IReadonlyParticipantIdentifier receiver,
                                 final IReadonlyDocumentIdentifier documentType,
                                 final IReadonlyProcessIdentifier processType,
                                 final String channelID,
                                 final String messageID,
                                 final List <Element> referenceParameters) {
    final Binding binding = bindingProvider.getBinding ();
    @SuppressWarnings ("rawtypes")
    final List <Handler> handlerList = binding.getHandlerChain ();
    handlerList.add (createSoapHeaderHandler (sender,
                                              receiver,
                                              documentType,
                                              processType,
                                              channelID,
                                              messageID,
                                              referenceParameters));
    binding.setHandlerChain (handlerList);
  }

  private static SoapHeaderHandler createSoapHeaderHandler (final IReadonlyParticipantIdentifier sender,
                                                            final IReadonlyParticipantIdentifier recipient,
                                                            final IReadonlyDocumentIdentifier documentType,
                                                            final IReadonlyProcessIdentifier processType,
                                                            final String channelID,
                                                            final String messageID,
                                                            final List <Element> referenceParameters) {
    if (documentType != null) {
      final DocumentIdentifierType headerDocumentType = new SimpleDocumentIdentifier (documentType);
      final ParticipantIdentifierType headerRecipient = new SimpleParticipantIdentifier (recipient);
      final ParticipantIdentifierType headerSender = new SimpleParticipantIdentifier (sender);
      final ProcessIdentifierType headerProcessInformation = new SimpleProcessIdentifier (processType);

      return new SoapHeaderHandler (headerSender,
                                    headerRecipient,
                                    headerDocumentType,
                                    headerProcessInformation,
                                    channelID,
                                    messageID,
                                    referenceParameters);
    }
    return new SoapHeaderHandler (null, null, null, null, channelID, messageID, referenceParameters);
  }
}

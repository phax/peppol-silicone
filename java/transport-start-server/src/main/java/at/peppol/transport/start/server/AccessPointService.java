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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.transport.start.server;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;

import org.busdox.transport.start.cert.ServerConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Delete;
import org.w3._2009._02.ws_tra.DeleteResponse;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Get;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.PutResponse;

import at.peppol.busdox.CBusDox;
import at.peppol.busdox.identifier.IParticipantIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;
import at.peppol.commons.utils.ExceptionUtils;
import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadataHelper;
import at.peppol.transport.PingMessageHelper;

import com.phloc.commons.CGlobal;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.io.misc.SizeHelper;
import com.phloc.commons.log.LogMessage;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.impl.SuccessWithValue;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 * WebService implementation.
 * 
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com) Dante<br>
 *         Malaga(dante@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService (serviceName = "accessPointService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = CBusDox.START_WSDL_PATH)
@BindingType (value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@Addressing
public class AccessPointService {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessPointService.class);
  private static final ISMLInfo SML_INFO = ESML.PRODUCTION;
  private static final List <IAccessPointServiceReceiverSPI> s_aReceivers;
  private static final X509Certificate s_aConfiguredCert;

  static {
    // Load all SPI implementations
    s_aReceivers = ContainerHelper.newUnmodifiableList (ServiceLoader.load (IAccessPointServiceReceiverSPI.class));
    if (s_aReceivers.isEmpty ())
      s_aLogger.error ("No implementation of the SPI interface " +
                       IAccessPointServiceReceiverSPI.class +
                       " found! Incoming documents will be discarded!");

    // Read certificate from configuration only once, so it is cached for reuse
    try {
      final String sKeyStorePath = ServerConfigFile.getKeyStorePath ();
      final String sKeyStorePassword = ServerConfigFile.getKeyStorePassword ();
      final String sKeyStoreAlias = ServerConfigFile.getKeyStoreAlias ();
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (sKeyStorePath, sKeyStorePassword);
      s_aConfiguredCert = (X509Certificate) aKeyStore.getCertificate (sKeyStoreAlias);
      s_aLogger.info ("Our Certificate - Serial Number: " + s_aConfiguredCert.getSerialNumber ());
    }
    catch (final Exception ex) {
      throw new InitializationException ("Failed to read the configured certificate", ex);
    }
  }

  @Resource
  private WebServiceContext webServiceContext;

  /**
   * @param aMetadata
   * @return The access point URL
   * @throws FaultMessage
   *         In case the endpoint address could not be resolved.
   */
  @Nullable
  private static String _getAPURL (@Nonnull final IMessageMetadata aMetadata) throws FaultMessage {
    final IParticipantIdentifier aRecipientID = aMetadata.getRecipientID ();
    try {
      // Query the SMP
      return new SMPServiceCaller (aRecipientID, SML_INFO).getEndpointAddress (aRecipientID,
                                                                               aMetadata.getDocumentTypeID (),
                                                                               aMetadata.getProcessID ());
    }
    catch (final Throwable t) {
      throw ExceptionUtils.createFaultMessage (t,
                                               "Failed to retrieve endpoint address of recipient " +
                                                   IdentifierUtils.getIdentifierURIEncoded (aRecipientID));
    }
  }

  /**
   * Get the certificate of the recipient access point as stored in the SMP
   * 
   * @param aMetadata
   *        The current message meta data
   * @return <code>null</code> if no such certificate was found
   * @throws FaultMessage
   *         In case of an error
   */
  @Nullable
  private static X509Certificate _getRecipientCert (@Nonnull final IMessageMetadata aMetadata) throws FaultMessage {
    final IParticipantIdentifier aRecipientID = aMetadata.getRecipientID ();
    try {
      return new SMPServiceCaller (aRecipientID, SML_INFO).getEndpointCertificate (aRecipientID,
                                                                                   aMetadata.getDocumentTypeID (),
                                                                                   aMetadata.getProcessID ());
    }
    catch (final Throwable t) {
      if (GlobalDebug.isDebugMode ()) {
        // In development mode it is okay, if this AccessPoint is not registered
        // in an SML
        return null;
      }
      throw ExceptionUtils.createFaultMessage (t, "Failed to retrieve endpoint certificate of recipient " +
                                                  IdentifierUtils.getIdentifierURIEncoded (aRecipientID));
    }
  }

  /**
   * Check if the certificate of the receiver is identical to the configured
   * one. This is done by checking the certificate serial numbers.
   * 
   * @param aReceiverCert
   *        The certificate of the receiver
   * @return <code>true</code> if equal
   */
  private static boolean _isTheSameCert (@Nullable final X509Certificate aReceiverCert) {
    if (GlobalDebug.isDebugMode ()) {
      s_aLogger.info ("In debug mode the certificate is always approved");
      return true;
    }

    if (aReceiverCert == null) {
      s_aLogger.error ("No receiver certificate present");
      return false;
    }

    final BigInteger aMySerial = s_aConfiguredCert.getSerialNumber ();
    final BigInteger aReceiverSerial = aReceiverCert.getSerialNumber ();
    if (!aMySerial.equals (aReceiverSerial)) {
      s_aLogger.error ("Certificate serial number mismatch!");
      s_aLogger.info ("My certificate serial number: " + aMySerial.toString ());
      s_aLogger.info ("      Receiver serial number: " + aReceiverSerial.toString ());
      return false;
    }
    return true;
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public GetResponse get (final Get body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public PutResponse put (final Put body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public DeleteResponse delete (final Delete body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * Main action for receiving.
   * 
   * @param aBody
   * @return Never <code>null</code>
   * @throws FaultMessage
   *         In case of an error
   */
  @Action (input = "http://www.w3.org/2009/02/ws-tra/Create",
           output = "http://www.w3.org/2009/02/ws-tra/CreateResponse",
           fault = { @FaultAction (className = FaultMessage.class, value = "http://busdox.org/2010/02/channel/fault") })
  public CreateResponse create (final Create aBody) throws FaultMessage {
    s_aLogger.info ("AccesspointService.create called");

    // Grabs the list of headers from the SOAP message
    final HeaderList aHeaderList = (HeaderList) webServiceContext.getMessageContext ()
                                                                 .get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
    final IMessageMetadata aMetadata = MessageMetadataHelper.createMetadataFromHeaders (aHeaderList);

    // TODO do we need a check, whether the message ID was already received
    try {
      MDC.put ("msgId", aMetadata.getMessageID ());
      if (aMetadata.getChannelID () != null)
        MDC.put ("channelId", aMetadata.getChannelID ());
      MDC.put ("senderId", aMetadata.getSenderID ().getValue ());

      if (PingMessageHelper.isPingMessage (aMetadata)) {
        // It's a PING message - no actions to be taken!
        s_aLogger.info ("Got a ping message - discarding it!");
      }
      else {
        // Not a ping message

        // Get our public endpoint address from the config file
        final String sOwnAPUrl = ServerConfigFile.getOwnAPURL ();

        // In debug mode, use our recipient URL, so that the URL check will work
        final String sRecipientAPUrl = GlobalDebug.isDebugMode () ? sOwnAPUrl : _getAPURL (aMetadata);

        // Get the recipient certificate from the SMP
        final X509Certificate aRecipientSMPCert = _getRecipientCert (aMetadata);
        if (aRecipientSMPCert == null)
          s_aLogger.error ("No Metadata Certificate found! Is this AP maybe not contained in an SMP? Recipient ID is " +
                           IdentifierUtils.getIdentifierURIEncoded (aMetadata.getRecipientID ()));

        if (s_aLogger.isDebugEnabled ()) {
          s_aLogger.debug ("Our endpoint: " + sOwnAPUrl);
          s_aLogger.debug ("Recipient endpoint: " + sRecipientAPUrl);
          if (aRecipientSMPCert == null)
            s_aLogger.debug ("No Recipient certificate found");
          else
            s_aLogger.debug ("Recipient certificate present: " + aRecipientSMPCert.toString ());
        }

        if (_isTheSameCert (aRecipientSMPCert)) {
          // Is it for us?
          if (sRecipientAPUrl.indexOf (sOwnAPUrl) >= 0) {
            s_aLogger.info ("This is a handled request for " + aMetadata.getRecipientID ().getValue ());

            // Invoke all available SPI implementations
            ESuccess eOverallSuccess = ESuccess.SUCCESS;
            final List <LogMessage> aProcessingMessages = new ArrayList <LogMessage> ();
            try {
              // Invoke all available SPI implementations
              for (final IAccessPointServiceReceiverSPI aReceiver : s_aReceivers) {
                final SuccessWithValue <AccessPointReceiveError> aSV = aReceiver.receiveDocument (webServiceContext,
                                                                                                  aMetadata,
                                                                                                  aBody);
                eOverallSuccess = eOverallSuccess.and (aSV);
                final AccessPointReceiveError aError = aSV.get ();
                if (aError != null) {
                  // Remember all messages
                  aProcessingMessages.addAll (aError.getAllMessages ());
                }
              }
            }
            catch (final Exception ex) {
              aProcessingMessages.add (new LogMessage (EErrorLevel.ERROR,
                                                       "Internal error in processing incoming message",
                                                       ex));
              eOverallSuccess = ESuccess.FAILURE;
            }

            if (!aProcessingMessages.isEmpty ()) {
              // Log all messages from processing
              s_aLogger.info ("Messages from " +
                              (eOverallSuccess.isSuccess () ? "successfuly" : "failed") +
                              " processing of document " +
                              aMetadata.getMessageID () +
                              ":");
              for (final LogMessage aLogMsg : aProcessingMessages)
                s_aLogger.info ("  [" + aLogMsg.getErrorLevel ().getID () + "] " + aLogMsg.getMessage (),
                                aLogMsg.getThrowable ());
            }

            if (eOverallSuccess.isFailure ()) {
              s_aLogger.error ("Failed to handle incoming document from PEPPOL");
              throw ExceptionUtils.createFaultMessage (new IllegalStateException ("Failure in processing document from PEPPOL"),
                                                       "Internal error in processing the incoming PEPPOL document");
            }

            // Log success
            s_aLogger.info ("Done handling incoming document from PEPPOL");
          }
          else {
            s_aLogger.error ("The received document is not for us!");
            s_aLogger.error ("Request is for: " + sRecipientAPUrl);
            s_aLogger.error ("    Our URL is: " + sOwnAPUrl);

            // Avoid endless loop
            ExceptionUtils.createFaultMessage (new IllegalStateException ("Receiver(" +
                                                                          sRecipientAPUrl +
                                                                          ") invalid for us (" +
                                                                          sOwnAPUrl +
                                                                          ")"), "The received document is not for us!");
          }
        }
        else {
          s_aLogger.error ("Metadata Certificate (" +
                           aRecipientSMPCert +
                           ") does not match Access Point Certificate (" +
                           s_aConfiguredCert +
                           ") - ignoring document");
        }
      }

      if (GlobalDebug.isDebugMode ())
        _checkMemoryUsage ();

      final CreateResponse aResponse = new CreateResponse ();
      return aResponse;
    }
    finally {
      MDC.clear ();
    }
  }

  private static final long MEMORY_THRESHOLD_BYTES = 10 * CGlobal.BYTES_PER_MEGABYTE;
  private static long s_nLastUsageInBytes = 0;

  private static void _checkMemoryUsage () {
    System.gc ();
    final Runtime aRuntime = Runtime.getRuntime ();
    final long nFreeMemory = aRuntime.freeMemory ();
    final long nTotalMemory = aRuntime.totalMemory ();
    final long nUsedMemory = nTotalMemory - nFreeMemory;
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.US);
    final String sMemoryStatus = aSH.getAsMatching (nUsedMemory, 1) +
                                 " / " +
                                 aSH.getAsMatching (nTotalMemory, 1) +
                                 " / " +
                                 aSH.getAsMatching (aRuntime.maxMemory (), 1);

    if (nUsedMemory <= (s_nLastUsageInBytes - MEMORY_THRESHOLD_BYTES) ||
        nUsedMemory >= (s_nLastUsageInBytes + MEMORY_THRESHOLD_BYTES)) {
      final String sThreadName = Thread.currentThread ().getName ();
      s_aLogger.info ("%%% [" + sThreadName + "] Memory usage: " + sMemoryStatus);
      s_nLastUsageInBytes = nUsedMemory;
    }
  }
}

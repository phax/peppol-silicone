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
package eu.peppol.start.server;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;

import org.busdox.CBusDox;
import org.busdox.identifier.IParticipantIdentifier;
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

import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.security.KeyStoreUtils;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.sml.ISMLInfo;
import at.peppol.commons.utils.ExceptionUtils;

import com.phloc.commons.CGlobal;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.io.misc.SizeHelper;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.peppol.registry.smp.client.SMPServiceCaller;
import eu.peppol.start.IMessageMetadata;
import eu.peppol.start.MessageMetadataHelper;
import eu.peppol.start.PingMessageHelper;
import eu.peppol.start.client.AccessPointClient;

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
  private static final String SERVICE_NAME = AccessPointService.class.getAnnotation (WebService.class).serviceName ();
  private static final List <IAccessPointServiceReceiverSPI> s_aReceivers;
  private static final X509Certificate s_aConfiguredCert;

  static {
    // Load all SPI implementations
    s_aReceivers = ContainerHelper.newUnmodifiableList (ServiceLoader.load (IAccessPointServiceReceiverSPI.class));
    if (s_aReceivers.isEmpty ())
      s_aLogger.error ("No implementation of the SPI interface " +
                       IAccessPointServiceReceiverSPI.class +
                       " found! Incoming documents will be discarded!");

    // Read certificate from configuration
    try {
      final String sKeyStorePath = ServerConfigFile.getKeyStorePath ();
      final String sKeyStorePassword = ServerConfigFile.getKeyStorePassword ();
      final String sKeyStoreAlias = ServerConfigFile.getKeyStoreAlias ();
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStoreFromClassPath (sKeyStorePath, sKeyStorePassword);
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
   * @return Our own URL
   */
  private String _getOwnAPUrl () {
    // TODO verify what the result of this method is (e.g. for a Tomcat behind
    // an Apache httpd)
    final ServletRequest aServletRequest = (ServletRequest) webServiceContext.getMessageContext ()
                                                                             .get (MessageContext.SERVLET_REQUEST);

    // Context path we're running in
    final String sContextPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                                   .get (MessageContext.SERVLET_CONTEXT)).getContextPath ();
    final String sThisAPUrl = aServletRequest.getScheme () +
                              "://" +
                              aServletRequest.getServerName () +
                              ":" +
                              aServletRequest.getLocalPort () +
                              sContextPath +
                              '/';

    return sThisAPUrl + SERVICE_NAME;
  }

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
   * @param aReceiverCert
   * @return <code>true</code> if equal
   */
  private static boolean _isTheSameCert (@Nullable final X509Certificate aReceiverCert) {
    return aReceiverCert != null && s_aConfiguredCert.getSerialNumber ().equals (aReceiverCert.getSerialNumber ());
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

    MDC.put ("msgId", aMetadata.getMessageID ());
    if (aMetadata.getChannelID () != null)
      MDC.put ("channelId", aMetadata.getChannelID ());
    MDC.put ("senderId", aMetadata.getSenderID ().getValue ());

    if (PingMessageHelper.isPingMessage (aMetadata)) {
      // It's a PING message - no actions to be taken!
      s_aLogger.info ("Got a ping message!");
    }
    else {
      final String sOwnAPUrl = _getOwnAPUrl ();
      s_aLogger.info ("Our endpoint: " + sOwnAPUrl);

      final String sRecipientAPUrl = GlobalDebug.isDebugMode () ? sOwnAPUrl : _getAPURL (aMetadata);
      s_aLogger.info ("Recipient endpoint: " + sOwnAPUrl);

      final X509Certificate aRecipientSMPCert = _getRecipientCert (aMetadata);
      if (aRecipientSMPCert != null)
        s_aLogger.debug ("Metadata Certificate: \n" + aRecipientSMPCert);
      else
        s_aLogger.error ("No Metadata Certificate found! Is this AP maybe not contained in an SMP?");

      // Not a ping message
      if (GlobalDebug.isDebugMode () || _isTheSameCert (aRecipientSMPCert)) {
        // Is it for us?
        if (sRecipientAPUrl.indexOf (sOwnAPUrl) >= 0) {
          s_aLogger.info ("Sender Access Point and Receiver Access Point are the same");
          s_aLogger.info ("This is a local request - storage directly " + aMetadata.getRecipientID ().getValue ());

          // Invoke all available SPI implementations
          try {
            for (final IAccessPointServiceReceiverSPI aReceiver : s_aReceivers)
              aReceiver.receiveDocument (webServiceContext, aMetadata, aBody);
          }
          catch (final Exception ex) {
            throw ExceptionUtils.createFaultMessage (ex, "Receive document via SPI");
          }
        }
        else {
          // TODO throw an exception if it is not for us
          s_aLogger.info ("Sender Access Point and Receiver Access Point are different");
          s_aLogger.info ("This is a request for a remote Access Point: " + sRecipientAPUrl);
          try {
            AccessPointClient.send (sRecipientAPUrl, aMetadata, aBody);
          }
          catch (final Exception ex) {
            throw ExceptionUtils.createFaultMessage (ex, "Deliver to remote Access Point");
          }
        }
      }
      else {
        s_aLogger.error ("Metadata Certificate (" +
                         aRecipientSMPCert +
                         ") does final not match Access Point Certificate (" +
                         s_aConfiguredCert +
                         ")");
      }
    }

    s_aLogger.info ("Done");

    _checkMemoryUsage ();

    return new CreateResponse ();
  }

  private static final long MEMORY_THRESHOLD_BYTES = 10 * CGlobal.BYTES_PER_MEGABYTE;
  private static long s_nLastUsageInBytes = 0;

  private static void _checkMemoryUsage () {
    System.gc ();
    final Runtime runtime = Runtime.getRuntime ();
    final long freeMemory = runtime.freeMemory ();
    final long totalMemory = runtime.totalMemory ();
    final long usedMemory = totalMemory - freeMemory;
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.US);
    final String memoryStatus = aSH.getAsMatching (usedMemory, 1) +
                                " / " +
                                aSH.getAsMatching (totalMemory, 1) +
                                " / " +
                                aSH.getAsMatching (runtime.maxMemory (), 1);

    if (usedMemory <= (s_nLastUsageInBytes - MEMORY_THRESHOLD_BYTES) ||
        usedMemory >= (s_nLastUsageInBytes + MEMORY_THRESHOLD_BYTES)) {
      final String sThreadName = Thread.currentThread ().getName ();
      s_aLogger.info ("%%% [" + sThreadName + "] Memory usage: " + memoryStatus);
      s_nLastUsageInBytes = usedMemory;
    }
  }
}

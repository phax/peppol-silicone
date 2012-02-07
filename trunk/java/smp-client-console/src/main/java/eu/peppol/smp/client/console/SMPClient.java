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
package eu.peppol.smp.client.console;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.SimpleDocumentIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.ReadonlyUsernamePWCredentials;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.file.SimpleFileIO;
import com.phloc.commons.lang.CGStringHelper;

import eu.peppol.registry.smp.client.SMPServiceCaller;
import eu.peppol.registry.smp.client.UserId;

/**
 * SMP commandline client
 *
 * @author Itella
 */
public final class SMPClient {
  static enum Command {
    ADDGROUP,
    ADD,
    DELGROUP,
    DEL,
    LIST,
    UNDEFINED
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPClient.class);

  private final URI m_aSMPAddress;
  private final String m_sSMPUsername;
  private final String m_sSMPPassword;
  private final String m_sAPAddress;
  private final String m_sAPWSDLAddress;
  private final String m_sDocumentType;
  private final String m_sCertificateContent;
  private final String m_sParticipantID;
  private final String m_sProcessID;

  public SMPClient (final URI aSMPAddress,
                    final String sSMPUsername,
                    final String sSMPPassword,
                    final String sAPAddress,
                    final String sDocumentType,
                    final String sCertificateContent,
                    final String sParticipantID,
                    final String sProcessID) {
    m_aSMPAddress = aSMPAddress;
    m_sSMPUsername = sSMPUsername;
    m_sSMPPassword = sSMPPassword;
    m_sAPAddress = sAPAddress;
    m_sAPWSDLAddress = sAPAddress + "?wsdl";
    m_sDocumentType = sDocumentType;
    m_sProcessID = sProcessID;
    m_sCertificateContent = sCertificateContent;
    m_sParticipantID = sParticipantID;
  }

  public static void main (final String [] args) throws Exception {
    final SMPClientOptions options = new SMPClientOptions ();
    final CommandLineParser parser = new PosixParser ();
    final CommandLine cmd = parser.parse (options, args);
    final HelpFormatter formatter = new HelpFormatter ();

    Command action = Command.UNDEFINED;
    boolean goodCMD = true;
    String cert = null;

    if (!cmd.hasOption ("h")) {
      s_aLogger.error ("No Host specified use -h to specify Host");
      goodCMD = false;
    }

    if (!cmd.hasOption ("u")) {
      s_aLogger.error ("No Username specified use -u to specify username");
      goodCMD = false;
    }

    if (!cmd.hasOption ("p")) {
      s_aLogger.error ("No Password specified use -p to specify Password");
      goodCMD = false;
    }

    if (!cmd.hasOption ("c")) {
      s_aLogger.error ("No Action specified please use -c parameter to specify command(ADD,DEL,LIST)");
      goodCMD = false;
    }
    else {
      final String optValue = cmd.getOptionValue ("c").toUpperCase ();
      for (final Command element : Command.values ()) {
        if (element.toString ().equals (optValue)) {
          action = element;
          break;
        }
      }
      if (action == null || action == Command.UNDEFINED) {
        s_aLogger.error ("Illegal Action specified:" + cmd.getOptionValue ("c") + " allowed commands(ADD,DEL,LIST)");
        goodCMD = false;
      }
      if (action == Command.ADD) {
        if (!cmd.hasOption ("a")) {
          s_aLogger.error ("No Accesspoint URL defined use -a to Specifify APurl");
          goodCMD = false;
        }
        if (!cmd.hasOption ("b")) {
          s_aLogger.error ("No Business ID specified use -b to specify Participant ID");
          goodCMD = false;
        }
        if (!cmd.hasOption ("d")) {
          s_aLogger.error ("No DocumentID specified use -d to specify Document TypeID");
          goodCMD = false;
        }
        if (!cmd.hasOption ("r")) {
          s_aLogger.error ("No ProcessID specified use -r to specify Process TypeID");
          goodCMD = false;
        }
        if (!cmd.hasOption ("e")) {
          s_aLogger.error ("No Certificate PEM file specified use -e to specify Certificate PEM file");
          goodCMD = false;
        }
        else {
          cert = readFile (cmd.getOptionValue ('e'));
        }
      }
      if (action == Command.DEL) {
        if (!cmd.hasOption ("b")) {
          s_aLogger.error ("No Business ID specified use -b to specify Participant ID");
          goodCMD = false;
        }
        if (!cmd.hasOption ("d")) {
          s_aLogger.error ("No DocumentID specified use -d to specify Document TypeID");
          goodCMD = false;
        }
      }
    }

    if (!goodCMD) {
      final StringWriter aSW = new StringWriter ();
      formatter.printHelp (new PrintWriter (aSW),
                           HelpFormatter.DEFAULT_WIDTH,
                           CGStringHelper.getClassLocalName (SMPClient.class),
                           null,
                           options,
                           HelpFormatter.DEFAULT_LEFT_PAD,
                           HelpFormatter.DEFAULT_DESC_PAD,
                           null);
      s_aLogger.info (aSW.toString ());
      System.exit (-3);
    }

    final SMPClient client = new SMPClient (new URI (cmd.getOptionValue ('h')),
                                            cmd.getOptionValue ('u'),
                                            cmd.getOptionValue ('p'),
                                            cmd.getOptionValue ('a'),
                                            cmd.getOptionValue ('d'),
                                            cert,
                                            cmd.getOptionValue ('b'),
                                            cmd.getOptionValue ('r'));

    switch (action) {
      case ADDGROUP:
        client.createServiceGroup ();
        break;
      case DELGROUP:
        client.deleteServiceGroup ();
        break;
      case ADD:
        client.addDocument ();
        break;
      case DEL:
        client.delDocument ();
        break;
      case LIST:
        client.listDocuments ();
    }
  }

  private void deleteServiceGroup () {
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (m_sParticipantID);
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final IReadonlyUsernamePWCredentials smpCreds = new ReadonlyUsernamePWCredentials (m_sSMPUsername, m_sSMPPassword);
    try {
      client.deleteServiceGroup (aPI, smpCreds);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to delete service group", e);
    }
  }

  public void createServiceGroup () {
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (m_sParticipantID);
    final ServiceGroupType serviceGroup = new ObjectFactory ().createServiceGroupType ();
    serviceGroup.setParticipantIdentifier (aPI);
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final IReadonlyUsernamePWCredentials smpCreds = new ReadonlyUsernamePWCredentials (m_sSMPUsername, m_sSMPPassword);
    try {
      client.saveServiceGroup (serviceGroup, smpCreds);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to create service group", e);
    }
  }

  public void listDocuments () {
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final IReadonlyUsernamePWCredentials smpCreds = new ReadonlyUsernamePWCredentials (m_sSMPUsername, m_sSMPPassword);
    try {
      final ServiceGroupReferenceListType list = client.getServiceGroupReferenceList (new UserId (m_sSMPUsername),
                                                                                      smpCreds);
      for (final ServiceGroupReferenceType gr : list.getServiceGroupReference ())
        System.out.print (gr.getValue () + ":" + gr.getHref () + "\n");
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to list documents", e);
    }
  }

  private void delDocument () {
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (m_sParticipantID);
    final DocumentIdentifierType documentIdentifier = SimpleDocumentIdentifier.createWithDefaultScheme (m_sDocumentType);
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final IReadonlyUsernamePWCredentials smpCreds = new ReadonlyUsernamePWCredentials (m_sSMPUsername, m_sSMPPassword);
    try {
      client.deleteServiceRegistration (aPI, documentIdentifier, smpCreds);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to delete document", e);
    }
  }

  private void addDocument () {
    final ProcessIdentifierType processIdentifier = SimpleProcessIdentifier.createWithDefaultScheme (m_sProcessID);
    final ParticipantIdentifierType bi = SimpleParticipantIdentifier.createWithDefaultScheme (m_sParticipantID);
    final DocumentIdentifierType documentIdentifier = SimpleDocumentIdentifier.createWithDefaultScheme (m_sDocumentType);
    final IReadonlyUsernamePWCredentials smpCreds = new ReadonlyUsernamePWCredentials (m_sSMPUsername, m_sSMPPassword);
    final SMPServiceCaller client = new SMPServiceCaller (m_aSMPAddress);
    final W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder ();
    builder.wsdlDocumentLocation (m_sAPWSDLAddress);
    builder.address (m_sAPAddress);
    final W3CEndpointReference endpointReferenceType = builder.build ();

    final ObjectFactory aObjFactory = new ObjectFactory ();
    final EndpointType endpointType = aObjFactory.createEndpointType ();
    endpointType.setEndpointReference (endpointReferenceType);
    endpointType.setTransportProfile ("busdox-transport-start");

    endpointType.setCertificate (m_sCertificateContent);
    endpointType.setServiceActivationDate (new Date (System.currentTimeMillis ()));
    endpointType.setServiceDescription ("Test service. For Interoperability test usage.");
    final Calendar exp = Calendar.getInstance ();
    exp.roll (Calendar.YEAR, 10);
    endpointType.setServiceExpirationDate (exp.getTime ());
    endpointType.setTechnicalContactUrl ("");
    endpointType.setMinimumAuthenticationLevel ("1");
    endpointType.setRequireBusinessLevelSignature (false);
    final ServiceEndpointList serviceEndpointList = aObjFactory.createServiceEndpointList ();
    serviceEndpointList.getEndpoint ().add (endpointType);
    final ProcessType processType = aObjFactory.createProcessType ();
    processType.setProcessIdentifier (processIdentifier);
    processType.setServiceEndpointList (serviceEndpointList);
    final ProcessListType processListType = aObjFactory.createProcessListType ();
    processListType.getProcess ().add (processType);
    final ServiceInformationType serviceInformationType = aObjFactory.createServiceInformationType ();
    serviceInformationType.setDocumentIdentifier (documentIdentifier);
    serviceInformationType.setParticipantIdentifier (bi);
    serviceInformationType.setProcessList (processListType);
    final ServiceMetadataType serviceMetadata = aObjFactory.createServiceMetadataType ();
    serviceMetadata.setServiceInformation (serviceInformationType);
    try {
      client.saveServiceRegistration (serviceMetadata, smpCreds);
    }
    catch (final Exception e) {
      s_aLogger.error ("Failed to add document", e);
    }
  }

  private static String readFile (final String filename) {
    return SimpleFileIO.readFileAsString (new File (filename), CCharset.CHARSET_ISO_8859_1);
  }
}

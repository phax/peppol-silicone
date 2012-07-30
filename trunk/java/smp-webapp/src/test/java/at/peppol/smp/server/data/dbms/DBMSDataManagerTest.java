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
package at.peppol.smp.server.data.dbms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ExtensionType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.commons.identifier.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.utils.ExtensionConverter;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.ReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.smp.client.CSMPIdentifier;
import at.peppol.smp.server.exception.UnauthorizedException;
import at.peppol.smp.server.exception.UnknownUserException;
import at.peppol.smp.server.hook.DoNothingRegistrationHook;

import com.phloc.commons.annotations.DevelopersNote;
import com.sun.jersey.api.NotFoundException;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore ("Cannot be enabled by default, because it would fail without the correct configuration")
@DevelopersNote ("You need to adjust your local META-INF/persistence.xml file to run this test")
public class DBMSDataManagerTest {
  private static final String PARTICIPANT_IDENTIFIER_SCHEME = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String DOCUMENT_SCHEME = CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME;
  private static final String PROCESS_SCHEME = CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME;

  private static final String PARTICIPANT_IDENTIFIER1 = "0010:599900000000A";
  private static final String PARTICIPANT_IDENTIFIER2 = "0010:599900000000B";

  private static final String TEST_DOCUMENT = "urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##OIOUBL-2.02";
  private static final String TEST_PROCESS = "BII03";

  private static final String USERNAME = "peppol_user";
  private static final String PASSWORD = "Test1234";

  private static final String CERTIFICATE = "82df1n8923fn8923fn8f23n8f23n8023fn803cn80c38cn823n8cndsnc";
  private static final String ADDRESS = "http://test.eu/accesspoint.svc";
  private static final boolean REQUIRE_SIGNATURE = true;
  private static final String MINIMUM_AUTH_LEVEL = "1";
  private static final Date ACTIVIATION_DATE = new Date ();
  private static final String DESCRIPTION = "description123";
  private static final Date EXPIRATION_DATE = new Date ();
  private static final String TECH_CONTACT = "fake@peppol.eu";
  private static final String TECH_INFO = "http://fake.peppol.eu/";
  private static final String TRANSPORT_PROFILE = CSMPIdentifier.TRANSPORT_PROFILE_START;

  private static final ParticipantIdentifierType PARTY_ID = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER1);
  private static final ParticipantIdentifierType SERVICEGROUP_ID = PARTY_ID;
  private static final DocumentIdentifierType DOCTYPE_ID = new SimpleDocumentTypeIdentifier (DOCUMENT_SCHEME,
                                                                                             TEST_DOCUMENT);
  private static final IReadonlyUsernamePWCredentials CREDENTIALS = new ReadonlyUsernamePWCredentials (USERNAME,
                                                                                                       PASSWORD);

  private static DBMSDataManager s_aDataMgr;

  @BeforeClass
  public static void init () {
    try {
      SMPJPAWrapper.getInstance ();
    }
    catch (final ExceptionInInitializerError ex) {
      // No database present
      throw new AssumptionViolatedException ("No database connection could be established");
    }

    s_aDataMgr = new DBMSDataManager (new DoNothingRegistrationHook ());
  }

  private ServiceGroupType m_aServiceGroup;
  private ServiceMetadataType m_aServiceMetadata;

  @Before
  public void createDefaultServiceGroup () {
    final ExtensionType aExtension = ExtensionConverter.convert ("<root><any>value</any></root>");
    assertNotNull (aExtension);
    assertNotNull (aExtension.getAny ());

    final ObjectFactory aObjFactory = new ObjectFactory ();
    m_aServiceGroup = aObjFactory.createServiceGroupType ();
    m_aServiceGroup.setParticipantIdentifier (PARTY_ID);

    // Be sure to delete if it exists.
    s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);

    s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    m_aServiceMetadata = aObjFactory.createServiceMetadataType ();
    final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
    aServiceInformation.setDocumentIdentifier (DOCTYPE_ID);
    aServiceInformation.setParticipantIdentifier (PARTY_ID);
    aServiceInformation.setExtension (aExtension);
    {
      final ProcessListType processList = aObjFactory.createProcessListType ();
      {
        final ProcessType process = aObjFactory.createProcessType ();
        process.setProcessIdentifier (new SimpleProcessIdentifier (PROCESS_SCHEME, TEST_PROCESS));
        process.setExtension (aExtension);
        {
          final ServiceEndpointList serviceEndpointList = aObjFactory.createServiceEndpointList ();
          {
            final EndpointType endpoint = aObjFactory.createEndpointType ();
            endpoint.setCertificate (CERTIFICATE);
            endpoint.setEndpointReference (W3CEndpointReferenceUtils.createEndpointReference (ADDRESS));
            endpoint.setMinimumAuthenticationLevel (MINIMUM_AUTH_LEVEL);
            endpoint.setRequireBusinessLevelSignature (REQUIRE_SIGNATURE);
            endpoint.setServiceActivationDate (ACTIVIATION_DATE);
            endpoint.setServiceDescription (DESCRIPTION);
            endpoint.setServiceExpirationDate (EXPIRATION_DATE);
            endpoint.setExtension (aExtension);
            endpoint.setTechnicalContactUrl (TECH_CONTACT);
            endpoint.setTechnicalInformationUrl (TECH_INFO);
            endpoint.setTransportProfile (TRANSPORT_PROFILE);
            serviceEndpointList.getEndpoint ().add (endpoint);
          }
          process.setServiceEndpointList (serviceEndpointList);
        }
        processList.getProcess ().add (process);
      }
      aServiceInformation.setProcessList (processList);
    }
    m_aServiceMetadata.setServiceInformation (aServiceInformation);
  }

  @Test
  public void testCreateServiceGroup () {
    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    final ParticipantIdentifierType aParticipantIdentifier2 = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER2);
    final ServiceGroupType result = s_aDataMgr.getServiceGroup (aParticipantIdentifier2);

    assertNull (result.getServiceMetadataReferenceCollection ());
    assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier ().getScheme ());
    assertTrue (IdentifierUtils.areParticipantIdentifierValuesEqual (PARTICIPANT_IDENTIFIER2,
                                                                     result.getParticipantIdentifier ().getValue ()));
  }

  @Test
  public void testCreateServiceGroupInvalidPassword () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (USERNAME, "WRONG_PASSWORD");

    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    try {
      s_aDataMgr.saveServiceGroup (m_aServiceGroup, aCredentials);
      fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testCreateServiceGroupUnknownUser () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials ("Unknown_User", PASSWORD);

    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    try {
      s_aDataMgr.saveServiceGroup (m_aServiceGroup, aCredentials);
      fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceGroup () {
    s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);

    assertNull (s_aDataMgr.getServiceGroup (SERVICEGROUP_ID));
  }

  @Test
  public void testDeleteServiceGroupUnknownID () {
    final ParticipantIdentifierType aServiceGroupID2 = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER2);
    s_aDataMgr.deleteServiceGroup (aServiceGroupID2, CREDENTIALS);

    assertNull (s_aDataMgr.getServiceGroup (aServiceGroupID2));
  }

  @Test
  public void testDeleteServiceGroupUnknownUser () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials ("Unknown_User", PASSWORD);
    try {
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, aCredentials);
      fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceGroupWrongPass () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (USERNAME, "WrongPassword");
    try {
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, aCredentials);
      fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testCreateServiceMetadata () {
    // Save to DB
    s_aDataMgr.saveService (m_aServiceMetadata, CREDENTIALS);

    // Retrieve from DB
    final ServiceMetadataType aDBServiceMetadata = s_aDataMgr.getService (SERVICEGROUP_ID, DOCTYPE_ID);

    final ProcessListType aOrigProcessList = m_aServiceMetadata.getServiceInformation ().getProcessList ();
    assertEquals (1, aOrigProcessList.getProcess ().size ());
    final ProcessType aOrigProcess = aOrigProcessList.getProcess ().get (0);
    assertEquals (1, aOrigProcess.getServiceEndpointList ().getEndpoint ().size ());
    final EndpointType aOrigEndpoint = aOrigProcess.getServiceEndpointList ().getEndpoint ().get (0);

    final ProcessType aDBProcess = aDBServiceMetadata.getServiceInformation ().getProcessList ().getProcess ().get (0);
    final EndpointType aDBEndpoint = aDBProcess.getServiceEndpointList ().getEndpoint ().get (0);

    assertTrue (IdentifierUtils.areIdentifiersEqual (m_aServiceMetadata.getServiceInformation ()
                                                                       .getDocumentIdentifier (),
                                                     aDBServiceMetadata.getServiceInformation ()
                                                                       .getDocumentIdentifier ()));
    assertTrue (IdentifierUtils.areIdentifiersEqual (m_aServiceMetadata.getServiceInformation ()
                                                                       .getParticipantIdentifier (),
                                                     aDBServiceMetadata.getServiceInformation ()
                                                                       .getParticipantIdentifier ()));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aOrigProcess.getProcessIdentifier (),
                                                     aDBProcess.getProcessIdentifier ()));
    assertEquals (aOrigEndpoint.getCertificate (), aDBEndpoint.getCertificate ());
    assertEquals (aOrigEndpoint.getMinimumAuthenticationLevel (), aDBEndpoint.getMinimumAuthenticationLevel ());
    assertEquals (aOrigEndpoint.getServiceDescription (), aDBEndpoint.getServiceDescription ());
    assertEquals (aOrigEndpoint.getTechnicalContactUrl (), aDBEndpoint.getTechnicalContactUrl ());
    assertEquals (aOrigEndpoint.getTechnicalInformationUrl (), aDBEndpoint.getTechnicalInformationUrl ());
    assertEquals (aOrigEndpoint.getTransportProfile (), aDBEndpoint.getTransportProfile ());
    assertEquals (W3CEndpointReferenceUtils.getAddress (aOrigEndpoint.getEndpointReference ()),
                  W3CEndpointReferenceUtils.getAddress (aDBEndpoint.getEndpointReference ()));
  }

  @Test
  public void testCreateServiceMetadataUnknownUser () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials ("Unknown_User", PASSWORD);
    try {
      s_aDataMgr.saveService (m_aServiceMetadata, aCredentials);
      fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testCreateServiceMetadataWrongPass () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (USERNAME, "WrongPassword");
    try {
      s_aDataMgr.saveService (m_aServiceMetadata, aCredentials);
      fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testDeleteServiceMetadata () {
    // First deletion succeeds
    s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, CREDENTIALS);
    try {
      // Second deletion fails
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, CREDENTIALS);
      fail ();
    }
    catch (final NotFoundException ex) {}
  }

  @Test
  public void testDeleteServiceMetadataUnknownUser () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials ("Unknown_User", PASSWORD);
    try {
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, aCredentials);
      fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceMetadataWrongPass () {
    final IReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (USERNAME, "WrongPassword");
    try {
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, aCredentials);
      fail ();
    }
    catch (final UnauthorizedException ex) {}
  }
}

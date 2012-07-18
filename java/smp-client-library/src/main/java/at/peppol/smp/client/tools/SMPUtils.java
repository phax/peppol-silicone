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
package at.peppol.smp.client.tools;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ExtensionType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataReferenceCollectionType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataReferenceType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;

import at.peppol.busdox.identifier.IReadonlyIdentifier;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;

@Immutable
public final class SMPUtils {
  private SMPUtils () {}

  @Nonnull
  public static String getAsString (@Nonnull final IReadonlyIdentifier aIdentifier) {
    return aIdentifier.getScheme () + "::" + aIdentifier.getValue ();
  }

  @Nonnull
  public static String getAsString (@Nonnull final ServiceGroupType aServiceGroup) {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("ServiceGroup information:\n");
    aSB.append ("ParticipantIdentifier: ")
       .append (getAsString (aServiceGroup.getParticipantIdentifier ()))
       .append ('\n');

    // References
    final ServiceMetadataReferenceCollectionType aSMRC = aServiceGroup.getServiceMetadataReferenceCollection ();
    if (aSMRC != null && !aSMRC.getServiceMetadataReference ().isEmpty ()) {
      aSB.append ("ServiceMetadataReferenceCollection:\n");
      for (final ServiceMetadataReferenceType aSMR : aSMRC.getServiceMetadataReference ())
        aSB.append ("  ").append (aSMR.getHref ()).append ('\n');
    }

    // Extension
    final ExtensionType aExt = aServiceGroup.getExtension ();
    if (aExt != null && aExt.getAny () != null) {
      aSB.append ("Extension:\n");
      aSB.append ("  Class = ").append (aExt.getAny ().getClass ().getName ()).append ('\n');
      aSB.append ("  Value = ").append (aExt.getAny ()).append ('\n');
    }
    return aSB.toString ();
  }

  @Nonnull
  public static String getAsString (@Nonnull final ServiceMetadataType aServiceMetadata) {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("Service meta data:\n");

    final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation ();
    if (aServiceInformation != null) {
      aSB.append ("  Service information:\n");
      aSB.append ("    Participant: ")
         .append (getAsString (aServiceInformation.getParticipantIdentifier ()))
         .append ('\n');
      aSB.append ("    Document type: ")
         .append (getAsString (aServiceInformation.getDocumentIdentifier ()))
         .append ('\n');
      for (final ProcessType aProcess : aServiceInformation.getProcessList ().getProcess ()) {
        aSB.append ("      Process: ").append (getAsString (aProcess.getProcessIdentifier ())).append ('\n');
        for (final EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ()) {
          aSB.append ("        Endpoint: ")
             .append (W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ()))
             .append ('\n');
          aSB.append ("        Transport profile: ").append (aEndpoint.getTransportProfile ()).append ('\n');
          aSB.append ("        Business level signature: ")
             .append (aEndpoint.isRequireBusinessLevelSignature ())
             .append ('\n');
          aSB.append ("        Min auth level: ").append (aEndpoint.getMinimumAuthenticationLevel ()).append ('\n');
          if (aEndpoint.getServiceActivationDate () != null)
            aSB.append ("        Valid from: ").append (aEndpoint.getServiceActivationDate ()).append ('\n');
          if (aEndpoint.getServiceExpirationDate () != null)
            aSB.append ("        Valid to: ").append (aEndpoint.getServiceExpirationDate ()).append ('\n');
          aSB.append ("        Certficiate string: ").append (aEndpoint.getCertificate ()).append ('\n');
          aSB.append ("        Service description: ").append (aEndpoint.getServiceDescription ()).append ('\n');
          aSB.append ("        Contact URL: ").append (aEndpoint.getTechnicalContactUrl ()).append ('\n');
          aSB.append ("        Info URL: ").append (aEndpoint.getTechnicalInformationUrl ()).append ('\n');
        }
      }
    }

    final RedirectType aRedirect = aServiceMetadata.getRedirect ();
    if (aRedirect != null) {
      aSB.append ("  Service redirect:\n");
      aSB.append ("    Certificate UID: ").append (aRedirect.getCertificateUID ()).append ('\n');
      aSB.append ("    Href: ").append (aRedirect.getHref ()).append ('\n');
    }
    return aSB.toString ();
  }
}

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
package at.peppol.commons.sml;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * This file contains default constants for the usage of the SML. It is strongly
 * recommended that the real values are out-sourced into a configuration file.
 * You can of course use the values in this file as the default values in case
 * no configuration item is specified.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CSMLDefault {
  /**
   * The root DNS zone for PEPPOL SML usage.
   */
  public static final String DNS_ZONE = "peppolcentral.org.";

  /**
   * The production SML DNS zone.
   */
  public static final String PRODUCTION_DNS_ZONE = "sml." + DNS_ZONE;

  /**
   * The testing SML DNS zone.
   */
  public static final String TEST_DNS_ZONE = "smk." + DNS_ZONE;

  /**
   * The DNS zone name part used for SMP information in the SML.
   */
  public static final String DNS_PUBLISHER_SUBZONE = "publisher.";

  /**
   * The host name where the production WebServices for managing the SML reside.
   */
  public static final String PRODUCTION_MANAGEMENT_HOSTNAME = "https://sml.peppolcentral.org";

  /**
   * The URL of the SML production management service.
   */
  public static final String PRODUCTION_MANAGEMENT_SERVICE = PRODUCTION_MANAGEMENT_HOSTNAME;

  /**
   * The host name where the production WebServices for managing the SML reside.
   */
  public static final String TEST_MANAGEMENT_HOSTNAME = "https://smk.peppolcentral.org";

  /**
   * The URL of the SML production management service.
   */
  public static final String TEST_MANAGEMENT_SERVICE = TEST_MANAGEMENT_HOSTNAME;

  /**
   * The name of the service to manage SMP meta data. Valid for all SML
   * instances.
   */
  public static final String MANAGEMENT_SERVICE_METADATA = "manageservicemetadata";

  /**
   * The name of the service to manage participant identifiers. Valid for all
   * SML instances.
   */
  public static final String MANAGEMENT_SERVICE_PARTICIPANTIDENTIFIER = "manageparticipantidentifier";

  /**
   * The maximum length of a migration code identifier.
   */
  public static final int MAX_MIGRATION_CODE_LENGTH = 100;

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CSMLDefault s_aInstance = new CSMLDefault ();

  private CSMLDefault () {}
}

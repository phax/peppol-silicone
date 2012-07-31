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
package at.peppol.commons.identifier.doctype;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import at.peppol.busdox.identifier.BusdoxDocumentTypeIdentifierParts;
import at.peppol.busdox.identifier.IBusdoxDocumentTypeIdentifierParts;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * A standalone wrapper class for the {@link IPeppolDocumentTypeIdentifierParts}
 * interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class PeppolDocumentTypeIdentifierParts implements IPeppolDocumentTypeIdentifierParts {
  private final IBusdoxDocumentTypeIdentifierParts m_aBusdoxParts;
  private final String m_sTransactionID;
  private final List <String> m_aExtensionIDs;
  private final String m_sVersion;

  /**
   * Build the BusDox sub type identifier from the PEPPOL specific components.
   * 
   * @param sTransactionID
   *        Transaction ID
   * @param aExtensionIDs
   *        extension IDs (at least one)
   * @param sVersion
   *        Version number
   * @return The sub type identifier.
   */
  @Nonnull
  private static String _buildSubTypeIdentifier (@Nonnull final String sTransactionID,
                                                 @Nonnull @Nonempty final List <String> aExtensionIDs,
                                                 @Nonnull final String sVersion) {
    return sTransactionID +
           TRANSACTIONID_SEPARATOR +
           StringHelper.getImploded (EXTENSION_SEPARATOR, aExtensionIDs) +
           VERSION_SEPARATOR +
           sVersion;
  }

  private PeppolDocumentTypeIdentifierParts (@Nonnull final IBusdoxDocumentTypeIdentifierParts aBusdoxParts,
                                             @Nonnull @Nonempty final String sTransactionID,
                                             @Nonnull @Nonempty final List <String> aExtensionIDs,
                                             @Nonnull @Nonempty final String sVersion) {
    if (StringHelper.hasNoText (sTransactionID))
      throw new IllegalArgumentException ("transactionID");
    if (ContainerHelper.isEmpty (aExtensionIDs))
      throw new IllegalArgumentException ("extensionIDs");
    for (final String sExtensionID : aExtensionIDs)
      if (StringHelper.hasNoText (sExtensionID))
        throw new IllegalArgumentException ("the extension IDs contain at least one empty element!");
    if (StringHelper.hasNoText (sVersion))
      throw new IllegalArgumentException ("version");

    m_aBusdoxParts = aBusdoxParts;
    m_sTransactionID = sTransactionID;
    m_aExtensionIDs = ContainerHelper.newUnmodifiableList (aExtensionIDs);
    m_sVersion = sVersion;
  }

  public PeppolDocumentTypeIdentifierParts (@Nonnull @Nonempty final String sRootNS,
                                            @Nonnull @Nonempty final String sLocalName,
                                            @Nonnull @Nonempty final String sTransactionID,
                                            @Nonnull @Nonempty final List <String> aExtensionIDs,
                                            @Nonnull @Nonempty final String sVersion) {
    this (new BusdoxDocumentTypeIdentifierParts (sRootNS, sLocalName, _buildSubTypeIdentifier (sTransactionID,
                                                                                               aExtensionIDs,
                                                                                               sVersion)),
          sTransactionID,
          aExtensionIDs,
          sVersion);
  }

  @Nonnull
  @Nonempty
  public String getRootNS () {
    return m_aBusdoxParts.getRootNS ();
  }

  @Nonnull
  @Nonempty
  public String getLocalName () {
    return m_aBusdoxParts.getLocalName ();
  }

  /**
   * @return The whole sub-type identifier, incl. PEPPOL transaction ID,
   *         extensions and version ID.
   */
  @Nonnull
  @Nonempty
  public String getSubTypeIdentifier () {
    return m_aBusdoxParts.getSubTypeIdentifier ();
  }

  @Nonnull
  public String getTransactionID () {
    return m_sTransactionID;
  }

  @Nonnull
  @Nonempty
  public List <String> getExtensionIDs () {
    return m_aExtensionIDs;
  }

  @Nonnull
  @Nonempty
  public String getVersion () {
    return m_sVersion;
  }

  @Nonnull
  @Nonempty
  public String getAsUBLCustomizationID () {
    return m_sTransactionID + TRANSACTIONID_SEPARATOR + StringHelper.getImploded (EXTENSION_SEPARATOR, m_aExtensionIDs);
  }

  @Nonnull
  @Nonempty
  public String getAsDocumentTypeIdentifierValue () {
    return m_aBusdoxParts.getAsDocumentTypeIdentifierValue ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("busdoxParts", m_aBusdoxParts)
                                       .append ("transactionID", m_sTransactionID)
                                       .append ("extensionIDs", m_aExtensionIDs)
                                       .append ("version", m_sVersion)
                                       .toString ();
  }

  /**
   * Extract the PEPPOL document identifier elements from the passed document
   * identifier value. The different of the PEPPOL document identifier parts to
   * the BusDox document identifier parts is, that the PEPPOL subtype identifier
   * is further defined as <code>&lt;customization id>::&lt;version></code>. The
   * customization ID can be further detailed into
   * <code>&lt;transactionId>:#&lt;extensionId>[#&lt;extensionId>]</code>
   * 
   * @param sDocTypeID
   *        The document identifier value to be split. May neither be
   *        <code>null</code> nor empty.
   * @return The non-<code>null</code> PEPPOL identifier parts
   * @throws IllegalArgumentException
   *         if the passed document identifier value does not match the
   *         specifications
   */
  @Nonnull
  public static IPeppolDocumentTypeIdentifierParts extractFromString (@Nonnull @Nonempty final String sDocTypeID) {
    // Extract the main 3 elements (root namespace, local name and sub-type)
    final IBusdoxDocumentTypeIdentifierParts aBusdoxParts = BusdoxDocumentTypeIdentifierParts.extractFromString (sDocTypeID);

    // Now start splitting the sub-type identifier
    final String sSubTypeIdentifier = aBusdoxParts.getSubTypeIdentifier ();
    if (StringHelper.hasNoText (sSubTypeIdentifier))
      throw new IllegalArgumentException ("The passed document identifier has an empty sub type identifier which is not PEPPOL compliant!");

    // PEPPOL sub-type identifier
    // <customization id>::<version>
    // --> even more detailed:
    // <transactionId>:#<extensionId>[#<extensionId>]::<version>
    final String [] aSubTypeParts = RegExHelper.getSplitToArray (sSubTypeIdentifier, VERSION_SEPARATOR, 2);
    if (aSubTypeParts.length < 2)
      throw new IllegalArgumentException ("The sub type identifier '" +
                                          sSubTypeIdentifier +
                                          "' is missing the separation between customization ID and version!");
    final String sVersion = aSubTypeParts[1];

    final String [] aCustomizationIDs = RegExHelper.getSplitToArray (aSubTypeParts[0], TRANSACTIONID_SEPARATOR, 2);
    if (aCustomizationIDs.length < 2)
      throw new IllegalArgumentException ("The sub type identifier '" +
                                          sSubTypeIdentifier +
                                          "' is missing the separation between transaction ID and the extensions!");

    final String sTransactionID = aCustomizationIDs[0];
    final String sExtensionIDs = aCustomizationIDs[1];
    final List <String> aExtensionIDs = StringHelper.getExploded (EXTENSION_SEPARATOR, sExtensionIDs);
    return new PeppolDocumentTypeIdentifierParts (aBusdoxParts, sTransactionID, aExtensionIDs, sVersion);
  }
}

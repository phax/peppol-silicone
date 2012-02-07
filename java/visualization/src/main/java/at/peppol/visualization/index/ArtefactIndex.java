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
package at.peppol.visualization.index;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.serialize.MicroReader;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.StringHelper;

/**
 * Represents the content of the index file for a single visualization artefact.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ArtefactIndex {
  private final Set <Locale> m_aLanguages;
  private final String m_sStylesheetFilename;
  private final Set <ArtefactResource> m_aResources = new HashSet <ArtefactResource> ();

  private ArtefactIndex (@Nonnull @Nonempty final Set <Locale> aLanguages,
                         @Nonnull @Nonempty final String sStylesheetFilename) {
    if (ContainerHelper.isEmpty (aLanguages))
      throw new NullPointerException ("languages");
    if (aLanguages.contains (CGlobal.LOCALE_INDEPENDENT) && aLanguages.size () > 1)
      throw new IllegalArgumentException ("If indepdenent is contained, no other language may be contained!");
    if (StringHelper.hasNoText (sStylesheetFilename))
      throw new IllegalArgumentException ("stylesheetFilename");
    m_aLanguages = ContainerHelper.makeUnmodifiable (aLanguages);
    m_sStylesheetFilename = sStylesheetFilename;
  }

  @Nonnull
  private void _addResource (@Nonnull final ArtefactResource aResource) {
    if (aResource == null)
      throw new NullPointerException ("resource");
    m_aResources.add (aResource);
  }

  /**
   * @return The languages this stylesheet supports.
   */
  @Nonnull
  @ReturnsImmutableObject
  public Set <Locale> getLanguages () {
    return m_aLanguages;
  }

  /**
   * Check if the passed locale is contained.
   *
   * @param aLocale
   *        The locale to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the locale is contained directly, or if the
   *         special locale "independent" is contained
   */
  public boolean containsLanguage (@Nonnull final Locale aLocale) {
    if (aLocale == null)
      throw new NullPointerException ("locale");
    return m_aLanguages.contains (aLocale) || m_aLanguages.contains (CGlobal.LOCALE_INDEPENDENT);
  }

  /**
   * @return The name of the stylesheet file, relative to the directory where
   *         the index resides.
   */
  @Nonnull
  @Nonempty
  public String getStylesheetFilename () {
    return m_sStylesheetFilename;
  }

  @Nonnull
  public IReadableResource getStylesheetResource (final String sBaseDir) {
    return new ClassPathResource (sBaseDir + m_sStylesheetFilename);
  }

  /**
   * @return An unmodifiable set with all resources for this artefact.
   */
  @Nonnull
  @ReturnsImmutableObject
  public Set <ArtefactResource> getAllResources () {
    return ContainerHelper.makeUnmodifiable (m_aResources);
  }

  @Nullable
  public static ArtefactIndex createFromXML (@Nonnull final IReadableResource aRes) {
    if (aRes == null)
      throw new NullPointerException ("resource");

    final IMicroDocument aDoc = MicroReader.readMicroXML (aRes);
    if (aDoc == null)
      return null;

    final IMicroElement eRoot = aDoc.getDocumentElement ();

    // Read languages - separated by ',' or ';'
    final String sLanguages = eRoot.getAttribute ("languages");
    final Set <Locale> aLanguages = new HashSet <Locale> ();
    if (sLanguages != null)
      for (final String sLanguage : RegExHelper.split (sLanguages, "[,;]"))
        aLanguages.add (LocaleCache.get (sLanguage));

    // Stylesheet filename
    final String sStylesheet = eRoot.getAttribute ("xsl");
    final ArtefactIndex ret = new ArtefactIndex (aLanguages, sStylesheet);

    // Read all resources
    for (final IMicroElement eResource : eRoot.getChildElements ("resource")) {
      // Type
      final String sType = eResource.getAttribute ("type");
      final EArtefactResourceType eType = EArtefactResourceType.getFromIDOrNull (sType);

      // Filename
      final String sFilename = eResource.getTextContent ();

      // Add
      ret._addResource (new ArtefactResource (eType, sFilename));
    }
    return ret;
  }
}

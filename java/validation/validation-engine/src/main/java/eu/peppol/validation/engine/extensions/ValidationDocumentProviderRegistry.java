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
package eu.peppol.validation.engine.extensions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.ETriState;
import com.phloc.commons.string.StringHelper;

/**
 * This class manages the different document providers for different types.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 * @param <T>
 *        The type of document provider.
 */
@ThreadSafe
public final class ValidationDocumentProviderRegistry <T extends IValidationDocumentProvider> {
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final Map <String, T> m_aMap = new HashMap <String, T> ();

  /**
   * Register a new validation document provider
   *
   * @param aValidationDocumentProvider
   *        The provider to be registered. May not be <code>null</code>.
   */
  public void registerValidationDocumentProvider (@Nonnull final T aValidationDocumentProvider) {
    if (aValidationDocumentProvider == null)
      throw new NullPointerException ("validationDocumentProvider");

    // Check the handled prefix
    final String sPrefix = aValidationDocumentProvider.getHandledPrefix ();
    if (!StringHelper.endsWith (sPrefix, ':'))
      throw new IllegalArgumentException ("The prefix must end with a colon!");
    if (StringHelper.length (sPrefix) <= 1)
      throw new IllegalArgumentException ("Invalid empty prefix");

    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aMap.containsKey (sPrefix))
        throw new IllegalArgumentException ("The prefix '" + sPrefix + "' has already been registered!");
      m_aMap.put (sPrefix, aValidationDocumentProvider);
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Get the provider that can handle the passed document.
   *
   * @param sDocumentPath
   *        The document including the prefix. May not be <code>null</code>.
   * @return <code>null</code> if no handler can handle the passed document, the
   *         provider otherwise.
   */
  @Nullable
  public T getProvider (@Nonnull final String sDocumentPath) {
    m_aRWLock.readLock ().lock ();
    try {
      // For all registered document provider
      for (final Map.Entry <String, T> aEntry : m_aMap.entrySet ())
        if (sDocumentPath.startsWith (aEntry.getKey ()))
          return aEntry.getValue ();
      return null;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Check if the passed document is supported
   *
   * @param sDocumentPath
   *        The document path (incl. the prefix). May not be <code>null</code>.
   * @return {@link ETriState#TRUE} if it is supported, {@link ETriState#FALSE}
   *         if not and {@link ETriState#UNDEFINED} if no handler is present for
   *         the passed document.
   */
  @Nonnull
  public ETriState isSupportedDocument (@Nonnull final String sDocumentPath) {
    m_aRWLock.readLock ().lock ();
    try {
      final T aProvider = getProvider (sDocumentPath);
      if (aProvider == null)
        return ETriState.UNDEFINED;

      // Remove the prefix from the name
      final String sDocumentName = sDocumentPath.substring (aProvider.getHandledPrefix ().length ());

      // And check if is supported or not
      return aProvider.isSupportedDocument (sDocumentName) ? ETriState.TRUE : ETriState.FALSE;
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return A non-<code>null</code> unmodifiable list of all contained document
   *         validators.
   */
  @Nonnull
  @ReturnsImmutableObject
  public Collection <T> getAllRegisteredProviders () {
    m_aRWLock.readLock ().lock ();
    try {
      return ContainerHelper.makeUnmodifiable (m_aMap.values ());
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }
}

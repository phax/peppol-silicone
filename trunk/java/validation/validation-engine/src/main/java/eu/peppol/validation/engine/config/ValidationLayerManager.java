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
package eu.peppol.validation.engine.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.LoggingValidationEventHandler;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.schema.XMLSchemaCache;
import com.phloc.commons.xml.transform.ResourceStreamSource;

import eu.peppol.validation.engine.jaxb.layers.LayerType;
import eu.peppol.validation.engine.jaxb.layers.LayersType;
import eu.peppol.validation.engine.validator.SchemaValidator;
import eu.peppol.validation.engine.validator.SchematronValidator;

/**
 * Class for manage all about the Layers.
 *
 * @version 2.0 27/09/2010
 * @author Jose Gorvenia Narvaez (jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class ValidationLayerManager {
  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  private static final Map <String, List <LayerType>> s_aMap = new HashMap <String, List <LayerType>> ();

  private ValidationLayerManager () {}

  /**
   * Clean the validation document list and resolve all external XSD schemas
   *
   * @param aLayer
   *        The layer where the validation documents should be resolved. May not
   *        be <code>null</code>.
   * @throws IllegalArgumentException
   *         If a document cannot be resolved
   */
  private static void _checkValidationDocs (@Nonnull final LayerType aLayer) {
    final List <String> aCleanedList = new ArrayList <String> ();
    for (final String sValue : aLayer.getValidationDoc ())
      // Ignore all empty elements
      if (StringHelper.hasText (sValue)) {
        // Small consistency check
        switch (aLayer.getValidationType ()) {
          case SCHEMA:
            if (SchemaValidator.isValidXSDPath (sValue).isFailure ())
              throw new IllegalArgumentException ("The XML Schema document '" + sValue + "' cannot be resolved!");
            break;
          case SCHEMATRON:
            if (SchematronValidator.isValidSchematronPath (sValue).isFailure ())
              throw new IllegalArgumentException ("The Schematron file '" + sValue + "' cannot be resolved!");
            break;
          default:
            throw new IllegalStateException ("No check for the validation type " + aLayer.getValidationType ());
        }

        // Only add non-empty values to the list
        aCleanedList.add (sValue);
      }

    // Set the cleaned list in the layer
    aLayer.getValidationDoc ().clear ();
    aLayer.getValidationDoc ().addAll (aCleanedList);
  }

  /**
   * Method to load Layers from file storage.
   *
   * @param sProfile
   *        CENBII Profile code.
   * @param sDocument
   *        Core Data Model code.
   * @return ArrayList filled with ValidationLayerBeans.
   * @throws JAXBException
   *         Throws the exception to be handler by other method.
   */
  public static List <LayerType> getAllLayers (@Nonnull final String sProfile, @Nonnull final String sDocument) throws JAXBException {
    final String sKey = "ValidationEngine/layers/" + sProfile + "/" + sDocument + "_Layer.xml";

    List <LayerType> ret;

    // Check if there is already a cached item.
    s_aRWLock.readLock ().lock ();
    try {
      ret = s_aMap.get (sKey);
      if (ret != null)
        return ret;
    }
    finally {
      s_aRWLock.readLock ().unlock ();
    }

    // Not in cache -> read a new one
    final IReadableResource aLayerRes = new ClassPathResource (sKey);
    if (!aLayerRes.exists ())
      throw new IllegalArgumentException ("For the combination of " +
                                          sProfile +
                                          " and " +
                                          sDocument +
                                          " no validation layer definition exists");

    final JAXBContext aContext = JAXBContextCache.getInstance ().getFromCache (LayersType.class);
    final Unmarshaller aUnmarshaller = aContext.createUnmarshaller ();
    aUnmarshaller.setEventHandler (new LoggingValidationEventHandler (aUnmarshaller.getEventHandler ()));
    aUnmarshaller.setSchema (XMLSchemaCache.getInstance ()
                                           .getSchema (new ClassPathResource ("schemas/Validation_layers.xsd")));
    final LayersType aJAXBLayers = aUnmarshaller.unmarshal (new ResourceStreamSource (aLayerRes), LayersType.class)
                                                .getValue ();

    // Check all layers consistency
    for (final LayerType aJAXBLayer : aJAXBLayers.getLayer ())
      _checkValidationDocs (aJAXBLayer);

    // Create a new list and add it to the cache
    s_aRWLock.writeLock ().lock ();
    try {
      // Create a copy for putting in the map
      ret = ContainerHelper.newList (aJAXBLayers.getLayer ());
      s_aMap.put (sKey, ret);
    }
    finally {
      s_aRWLock.writeLock ().unlock ();
    }
    return ret;
  }
}

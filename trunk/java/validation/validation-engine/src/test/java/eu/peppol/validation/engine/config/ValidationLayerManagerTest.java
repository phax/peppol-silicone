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

import javax.xml.bind.JAXBException;

import org.junit.Test;

/**
 * Test class for class {@link ValidationLayerManager}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ValidationLayerManagerTest {
  @Test
  public void testReadAll () throws JAXBException {
    // Read all available layer definitions
    ValidationLayerManager.getAllLayers ("BII01", "BiiCoreTrdm019");
    ValidationLayerManager.getAllLayers ("BII01", "BiiCoreTrdm057");
    ValidationLayerManager.getAllLayers ("BII01", "BiiCoreTrdm058");

    ValidationLayerManager.getAllLayers ("BII03", "BiiCoreTrdm001");

    ValidationLayerManager.getAllLayers ("BII04", "BiiCoreTrdm010");

    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm001");
    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm002");
    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm003");
    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm010");
    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm014");
    ValidationLayerManager.getAllLayers ("BII06", "BiiCoreTrdm015");

    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm001");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm002");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm003");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm010");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm013");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm014");
    ValidationLayerManager.getAllLayers ("BII07", "BiiCoreTrdm015");

    ValidationLayerManager.getAllLayers ("BII17", "BiiCoreTrdm018");
    ValidationLayerManager.getAllLayers ("BII17", "BiiCoreTrdm054");
    ValidationLayerManager.getAllLayers ("BII17", "BiiCoreTrdm055");

    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm001");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm002");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm003");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm004");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm005");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm006");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm010");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm013");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm014");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm015");
    ValidationLayerManager.getAllLayers ("BII19", "BiiCoreTrdm017");

    ValidationLayerManager.getAllLayers ("PreAward", "PA001");
    ValidationLayerManager.getAllLayers ("PreAward", "PA002");
  }
}

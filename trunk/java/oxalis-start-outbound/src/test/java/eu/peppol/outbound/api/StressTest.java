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
package eu.peppol.outbound.api;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.procid.EPredefinedProcessIdentifier;

/**
 * User: nigel Date: Dec 7, 2011 Time: 7:47:11 PM
 */
@Ignore
public class StressTest {
  private static final Logger log = LoggerFactory.getLogger ("oxalis-out");

  private static final long MESSAGES = 100;
  private static final int THREADS = 30;
  // public static final String KEYSTORE_FILE =
  // "/usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks";
  public static final String KEYSTORE_FILE = "/Users/steinar/appl/apache-tomcat-7.0.22/conf/keystore/keystore.jks";
  protected static final String START_SERVICE_END_POINT = "https://localhost:8443/oxalis/accessPointService";

  @Test
  public void test01 () throws Exception {

    final File keystore = new File (KEYSTORE_FILE);
    assertTrue (KEYSTORE_FILE + " not found, check the path :-)", keystore.exists () && keystore.canRead ());
    final DocumentSender documentSender = new DocumentSenderBuilder ().setDocumentTypeIdentifier (EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ())
                                                                      .setPeppolProcessTypeId (EPredefinedProcessIdentifier.BIS5A.getAsProcessIdentifier ())
                                                                      .setKeystoreFile (new File (KEYSTORE_FILE))
                                                                      .setKeystorePassword ("peppol")
                                                                      .build ();

    final List <Callable <Integer>> partitions = new ArrayList <Callable <Integer>> ();

    final long start = System.currentTimeMillis ();

    for (int i = 1; i <= MESSAGES; i++) {

      partitions.add (new Callable <Integer> () {
        public Integer call () throws Exception {
          sendDocument (documentSender);
          getMemoryUsage ();
          return Integer.valueOf (1);
        }
      });
    }

    final ExecutorService executorPool = Executors.newFixedThreadPool (THREADS);
    final List <Future <Integer>> values = executorPool.invokeAll (partitions, 1000, TimeUnit.SECONDS);
    int sum = 0;
    for (final Future <Integer> result : values)
      sum += result.get ().intValue ();

    executorPool.shutdown ();
    final long millis = System.currentTimeMillis () - start;
    final long seconds = millis / 1000;
    final long rate = sum / seconds;
    System.out.println ("");
    System.out.println ("");
    System.out.println ("%%% " + sum + " messages in " + seconds + " seconds, " + rate + " messages per second");
    System.out.println ("");
  }

  private void sendDocument (final DocumentSender documentSender) throws Exception {
    final InputStream inputStream = getClass ().getClassLoader ().getResourceAsStream ("logback-test.xml");

    documentSender.sendInvoice (inputStream, "9908:976098897", "9908:976098897", new URL (START_SERVICE_END_POINT), "");

    inputStream.close ();
  }

  private static final long MEMORY_THRESHOLD = 10;
  private static long lastUsage = 0;

  /**
   * returns a String describing current memory utilization. In addition
   * unusually large changes in memory usage will be logged.
   */
  public static String getMemoryUsage () {

    final Runtime runtime = Runtime.getRuntime ();
    final long freeMemory = runtime.freeMemory ();
    final long totalMemory = runtime.totalMemory ();
    final long usedMemory = totalMemory - freeMemory;
    final long mega = 1048576;
    final long usedInMegabytes = usedMemory / mega;
    final long totalInMegabytes = totalMemory / mega;
    final String memoryStatus = usedInMegabytes +
                                "M / " +
                                totalInMegabytes +
                                "M / " +
                                (runtime.maxMemory () / mega) +
                                "M";

    if (usedInMegabytes <= lastUsage - MEMORY_THRESHOLD || usedInMegabytes >= lastUsage + MEMORY_THRESHOLD) {
      log.info ("Memory usage: " + memoryStatus);
      lastUsage = usedInMegabytes;
    }

    return memoryStatus;
  }

}

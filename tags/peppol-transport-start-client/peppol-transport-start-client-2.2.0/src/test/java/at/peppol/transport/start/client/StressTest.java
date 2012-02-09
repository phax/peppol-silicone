package at.peppol.transport.start.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.peppol.commons.identifier.SimpleDocumentIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;

import com.phloc.commons.CGlobal;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

/**
 * @author nigel@mazeppa.no
 * @author Steinar Overbeck Cook <steinar@sendregning.no>
 *         <p/>
 *         Created by User: steinar Date: 14.12.11 Time: 20:42
 */
@Ignore ("Takes too long and requires a local AP up and running")
public final class StressTest {

  private static final long MESSAGES = 90;
  private static final int THREADS = 6;

  private static final Logger log = LoggerFactory.getLogger (StressTest.class);

  @Test
  public void test01 () throws Exception {
    final List <Callable <Integer>> partitions = new ArrayList <Callable <Integer>> ();

    final long start = System.currentTimeMillis ();

    final Document document = XMLReader.readXMLDOM (new ClassPathResource ("/ehf-test-invoice.xml"));

    for (int i = 1; i <= MESSAGES; i++) {
      partitions.add (new Callable <Integer> () {
        public Integer call () throws Exception {
          // Sends the message
          AccessPointClient.send ("http://localhost:8090/accessPointService", _createMetadata (), document);
          getMemoryUsage ();
          return Integer.valueOf (1);
        }
      });
    }

    final ExecutorService executorPool = Executors.newFixedThreadPool (THREADS);
    final List <Future <Integer>> values = executorPool.invokeAll (partitions, 1000, TimeUnit.SECONDS);
    int sum = 0;

    for (final Future <Integer> result : values) {
      sum += result.get ().intValue ();
    }

    executorPool.shutdown ();
    final long millis = System.currentTimeMillis () - start;
    final long seconds = millis / CGlobal.MILLISECONDS_PER_SECOND;
    final long rate = sum / seconds;
    System.out.println ();
    System.out.println ();
    System.out.println ("%%% " + sum + " messages in " + seconds + " seconds, " + rate + " messages per second");
    System.out.println ();
  }

  private static IMessageMetadata _createMetadata () {
    final String senderValue = "9908:976098897";
    final String recipientValue = "9908:976098897";
    // TODO: replace hardcoded document identifer with enum
    final String documentIdValue = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    // TODO: replace hardcoded process identifer with enum
    final String processIdValue = "urn:www.cenbii.eu:profile:bii04:ver1.0";

    final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme (senderValue);
    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (recipientValue);
    final DocumentIdentifierType aDocumentType = SimpleDocumentIdentifier.createWithDefaultScheme (documentIdValue);
    final ProcessIdentifierType aProcessIdentifierType = SimpleProcessIdentifier.createWithDefaultScheme (processIdValue);
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();

    return new MessageMetadata (sMessageID, null, aSender, aRecipient, aDocumentType, aProcessIdentifierType);
  }

  private static final long MEMORY_THRESHOLD = 10;
  private static long lastUsage = 0;

  /**
   * returns a String describing current memory utilization. In addition
   * unusually large changes in memory usage will be logged.
   * 
   * @return string holding a formatted representation of the current memory
   *         consumption
   */
  private static String getMemoryUsage () {
    final Runtime runtime = Runtime.getRuntime ();
    final long freeMemory = runtime.freeMemory ();
    final long totalMemory = runtime.totalMemory ();
    final long usedMemory = totalMemory - freeMemory;
    final long usedInMegabytes = usedMemory / CGlobal.BYTES_PER_MEGABYTE;
    final long totalInMegabytes = totalMemory / CGlobal.BYTES_PER_MEGABYTE;
    final String memoryStatus = usedInMegabytes +
                                "M / " +
                                totalInMegabytes +
                                "M / " +
                                (runtime.maxMemory () / CGlobal.BYTES_PER_MEGABYTE) +
                                "M";

    if (usedInMegabytes <= lastUsage - MEMORY_THRESHOLD || usedInMegabytes >= lastUsage + MEMORY_THRESHOLD) {
      log.info ("%%% Memory usage: " + memoryStatus);
      lastUsage = usedInMegabytes;
    }

    return memoryStatus;
  }
}

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
package eu.peppol.registry.sml.dns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.WillNotClose;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Record;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.streams.StreamUtils;


/**
 * Utility servlet to list all DNS Records.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ServletListDNS extends HttpServlet {
  private static final Logger log = LoggerFactory.getLogger (ServletListDNS.class);
  private static final AtomicBoolean s_aAlreadyRunning = new AtomicBoolean (false);

  /**
   * Write log line to servlet output stream.
   *
   * @param os
   * @param msg
   * @throws IOException
   */
  private static void _writeToStream (final OutputStream os, final String msg) throws IOException {
    os.write (msg.getBytes ());
    os.write ("\n".getBytes ());
    os.flush ();
  }

  private static void _writeToStreamAndLog (final OutputStream os, final String msg) throws IOException {
    if (log.isDebugEnabled ())
      log.debug (msg);
    _writeToStream (os, msg);
  }

  /**
   * Write exception to servlet output stream.
   *
   * @param os
   * @param e
   * @throws IOException
   */
  private static void _writeToStream (final OutputStream os, final Exception e) throws IOException {
    final PrintWriter pw = new PrintWriter (os);
    e.printStackTrace (pw);
    pw.flush ();
    pw.close ();
    os.write ("\n".getBytes ());
  }

  /**
   * List all DNS records to servlet output stream.
   *
   * @param os
   *        The {@link OutputStream} to write to. Will not be closed in here!
   * @throws Exception
   */
  private static void _doit (@WillNotClose final OutputStream os) throws Exception {
    // Is DNS listing already running?
    if (!s_aAlreadyRunning.compareAndSet (false, true)) {
      if (log.isInfoEnabled ())
        log.info ("List DNS is already running...");
      _writeToStream (os, "List DNS is already running...");
      return;
    }

    try {
      final IDNSClient aDNSClient = DNSClientFactory.getInstance ();

      _writeToStreamAndLog (os, "DNSClient is : " + aDNSClient.toString ());
      _writeToStreamAndLog (os,
                            "DNSServer is : " +
                                aDNSClient.getServer () +
                                " - handling DNS Zone : " +
                                aDNSClient.getDNSZoneName () +
                                " - SML Zone : " +
                                aDNSClient.getSMLZoneName ());
      _writeToStream (os, "");
      _writeToStream (os, "=== List Records in DNS ===");

      // Get all domain records
      final List <Record> aAllRecords = aDNSClient.getAllRecords ();

      // Filter the ones for the current SML domain only
      final List <Record> aFilteredRecords = new ArrayList <Record> ();
      {
        final String sSMLZoneName = aDNSClient.getSMLZoneName ();
        for (final Record aRecord : aAllRecords) {
          if (aRecord instanceof ARecord || aRecord instanceof CNAMERecord) {
            // For "address records" and "CNAME records" only the ones for the
            // current SML zone name (sml.peppolcentral.org) are displayed!
            if (!aRecord.getName ().toString ().contains (sSMLZoneName))
              continue;
          }
          aFilteredRecords.add (aRecord);
        }
      }
      _writeToStreamAndLog (os, " - retrieved # of records : " + aFilteredRecords.size ());
      _writeToStream (os, "");

      // Emit all records sorted
      for (final Record aRecord : ContainerHelper.getSortedInline (aFilteredRecords, new ComparatorDNSRecord ()))
        _writeToStreamAndLog (os, aRecord.toString ());

      if (log.isInfoEnabled ())
        log.info ("List DNS done!");
      _writeToStream (os, "");
      _writeToStream (os, "List DNS done!");
    }
    finally {
      // Mark DNS listing as not running
      s_aAlreadyRunning.set (false);
    }
  }

  /**
   * Handle both get/post request.
   *
   * @param req
   *        HTTP request
   * @param resp
   *        HTTP response
   * @throws IOException
   */
  private static void _handleRequest (final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    final ServletOutputStream os = resp.getOutputStream ();
    try {
      if (DNSClientConfiguration.isEnabled ()) {
        final String sInitMsg = "List DNS Records : " + new Date ().toString ();
        if (log.isInfoEnabled ())
          log.info (sInitMsg);
        _writeToStream (os, sInitMsg);
        _doit (os);
      }
      else {
        // DNS is not active - no need to list anything
        _writeToStreamAndLog (os, "DNS is not active, so listing is deactivated as well!");
      }
    }
    catch (final Exception ex) {
      log.error ("Failed to list DNS Records", ex);
      _writeToStream (os, "Failed to list DNS Records : " + ex.getMessage ());
      _writeToStream (os, ex);
    }
    finally {
      // Close the output stream
      StreamUtils.close (os);
    }
  }

  @Override
  protected void doGet (final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    _handleRequest (req, resp);
  }

  @Override
  protected void doPost (final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    _handleRequest (req, resp);
  }

  /**
   * Run listing locally. NOTE: DNS ZoneTransfer must be enabled for client.
   * Check your DNS administrator for details
   *
   * @param args
   * @throws Exception
   */
  public static void main (final String [] args) throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    new ServletListDNS ();
    ServletListDNS._doit (baos);

    System.out.println ("=================================================");
    System.out.println (baos.toString ("cp1252"));
  }
}

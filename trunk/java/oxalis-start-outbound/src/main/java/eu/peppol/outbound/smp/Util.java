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
package eu.peppol.outbound.smp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.xml.sax.InputSource;

import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.xml.sax.InputSourceFactory;

/**
 * User: nigel Date: Oct 25, 2011 Time: 11:08:22 PM
 */
public class Util {

  private static final String ENCODING_GZIP = "gzip";
  private static final String ENCODING_DEFLATE = "deflate";

  /**
   * Gets the content of a given url.
   */
  public static InputSource getUrlContent (final URL url) {

    BufferedReader bufferedReader = null;
    final StringBuilder sb = new StringBuilder ();

    HttpURLConnection httpURLConnection = null;
    try {
      httpURLConnection = (HttpURLConnection) url.openConnection ();
      httpURLConnection.connect ();
    }
    catch (final IOException e) {
      throw new IllegalStateException ("Unable to connect to " + url + " ; " + e.getMessage (), e);
    }

    try {
      final String encoding = httpURLConnection.getContentEncoding ();
      final InputStream in = httpURLConnection.getInputStream ();
      InputStream result;

      if (encoding != null && encoding.equalsIgnoreCase (ENCODING_GZIP)) {
        result = new GZIPInputStream (in);
      }
      else
        if (encoding != null && encoding.equalsIgnoreCase (ENCODING_DEFLATE)) {
          result = new InflaterInputStream (in);
        }
        else {
          result = in;
        }

      bufferedReader = new BufferedReader (new InputStreamReader (result));
      String line;

      while ((line = bufferedReader.readLine ()) != null) {
        sb.append (line).append ("\n");
      }

    }
    catch (final Exception e) {
      throw new RuntimeException ("Problem reading SMP data at " + url.toExternalForm (), e);
    }
    finally {
      StreamUtils.close (bufferedReader);
    }

    return InputSourceFactory.create (sb.toString ());
  }
}

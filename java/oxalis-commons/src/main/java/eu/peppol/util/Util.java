package eu.peppol.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.xml.sax.InputSource;

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
      try {
        // noinspection ConstantConditions
        bufferedReader.close ();
      }
      catch (final Exception e) {}
    }

    final String xml = sb.toString ();
    return new InputSource (new StringReader (xml));
  }
}

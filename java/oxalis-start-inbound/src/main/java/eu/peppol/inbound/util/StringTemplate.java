package eu.peppol.inbound.util;

import java.util.Map;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 13.11.11
 *         Time: 22:23
 */
public class StringTemplate {
  public static String interpolate (final String s, final Map <String, String> m) {

    String result = s;

    for (final Map.Entry <String, String> entry : m.entrySet ()) {

      final String regex = "\\$\\{" + entry.getKey () + "\\}";

      // Replaces all characters which are illegal or problematic in filenames
      // with _
      final String replacement = entry.getValue ().replaceAll ("[/:]", "_");
      result = result.replaceAll (regex, replacement);
    }

    return result.replaceAll ("//", "/");
  }
}

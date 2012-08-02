package at.peppol.validation.tools.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Immutable
public final class Utils {
  private static final Logger s_aLogger = LoggerFactory.getLogger (Utils.class);

  private Utils () {}

  @Nonnull
  public static String makeID (@Nonnull final String s) {
    return s.replaceAll ("\\b[ \\t]+\\b", "_");
  }

  public static void log (final String s) {
    s_aLogger.info (s);
  }

  public static void warn (final String s) {
    s_aLogger.warn (s);
  }
}

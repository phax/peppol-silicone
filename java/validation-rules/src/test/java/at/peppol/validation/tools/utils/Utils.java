package at.peppol.validation.tools.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Utils {
  private Utils () {}

  @Nonnull
  public static String makeID (@Nonnull final String s) {
    return s.replaceAll ("\\b[ \\t]+\\b", "_");
  }

  public static void log (final String s) {
    System.out.println (s);
  }
}

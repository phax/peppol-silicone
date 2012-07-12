package eu.peppol.start.identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: nigel Date: Oct 7, 2011 Time: 7:23:20 PM
 */
public final class Log {

  private static final Logger log = LoggerFactory.getLogger ("oxalis-com");

  public static void error (final String s, final Throwable throwable) {
    log.error (s, throwable);
  }

  public static void debug (final String s) {
    log.debug (s);
  }

  public static void error (final String s) {
    log.error (s);
  }

  public static void info (final String s) {
    log.info (s);
  }

  public static void warn (final String s) {
    log.warn (s);
  }
}

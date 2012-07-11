package eu.peppol.inbound.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: nigel Date: Oct 8, 2011 Time: 10:59:25 AM
 */
public class Log {

  private static Logger log = LoggerFactory.getLogger ("oxalis-inb");

  public static void error (String s, Throwable throwable) {
    log.error (s, throwable);
  }

  public static void debug (String s) {
    log.debug (s);
  }

  public static void error (String s) {
    log.error (s);
  }

  public static void info (String s) {
    log.info (s);
  }

  public static void warn (String s) {
    log.warn (s);
  }
}

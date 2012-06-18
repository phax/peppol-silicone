package eu.peppol.outbound.util;

import org.junit.Before;

/**
 * User: nigel Date: Oct 21, 2011 Time: 10:07:55 AM
 */
public class TestBase {

  @Before
  public void beforeTestBaseClass () {
    System.out.println ("___________________________________________ " + getClass ().getName ());
  }

  public static void signal (final Throwable t) throws Throwable {
    final StackTraceElement [] stackTrace = t.getStackTrace ();

    for (final StackTraceElement stackTraceElement : stackTrace) {
      if (!stackTraceElement.getClassName ().startsWith ("org.testng")) {
        System.out.println ("");
        System.out.println ("     *** " + t + " at " + stackTraceElement);
        System.out.println ("");
        break;
      }
    }

    throw t;
  }
}

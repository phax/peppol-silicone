package eu.peppol.outbound.util;

import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

import com.phloc.commons.jaxb.JAXBContextCache;

/**
 * User: nigel Date: Oct 25, 2011 Time: 9:05:52 AM
 */
public class JaxbContextCacheTest extends TestBase {
  @Test
  public void test01 () throws Throwable {
    try {

      final JAXBContext context1 = JAXBContextCache.getInstance ().getFromCache (String.class);
      final JAXBContext context2 = JAXBContextCache.getInstance ().getFromCache (String.class);
      assertTrue (context1 == context2);

    }
    catch (final Throwable t) {
      signal (t);
    }
  }
}

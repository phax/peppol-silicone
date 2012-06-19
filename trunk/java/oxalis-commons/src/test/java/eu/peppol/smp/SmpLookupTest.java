/* Created by steinar on 18.05.12 at 13:41 */
package eu.peppol.smp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.sml.ESML;

import com.phloc.commons.url.URLUtils;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpLookupTest {

  @Test
  public void lookupServicesUrl () throws SmpLookupException {
    final SmpLookup smpLookup = new SmpLookup (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:810017902"));
    final URL url = smpLookup.servicesUrl ();
    assertEquals ("http://B-ddc207601e442e1b751e5655d39371cd.iso6523-actorid-upis." +
                  ESML.PRODUCTION.getDNSZone () +
                  "/iso6523-actorid-upis%3A%3A9908%3A810017902", url.toExternalForm ());
  }

  @Test
  public void performLookup () throws SmpLookupException {
    final SmpLookup smpLookup = new SmpLookup (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:810017902"));
    final List <URL> result = smpLookup.getServiceUrlList ();

    for (final URL url : result) {

      final String s = URLUtils.urlDecode (url.getPath ());
      // All URL encoded characters shall have been translated, so we don't
      // expected to see any "%" characters
      assertTrue (s.indexOf ("%") < 0);
    }
  }

  @Test
  public void parseServiceMetadataReferences () throws SmpLookupException {
    final SmpLookup smpLookup = new SmpLookup (SimpleParticipantIdentifier.createWithDefaultScheme ("9908:810017902"));

    final List <IDocumentTypeIdentifier> result = smpLookup.parseServiceMetadataReferences ();
    for (final IDocumentTypeIdentifier documentTypeIdentifier : result) {
      System.out.println (documentTypeIdentifier);
    }
  }

  @Test
  public void parse () {
    final Pattern p = Pattern.compile ("(.*):#([^#]*)(?:#(.*))?");
    final Matcher m = p.matcher ("/iso6523-actorid-upis::9908:810017902/services/busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");
    if (m.find ()) {
      System.out.println ("Match");

      for (int i = 1; i <= m.groupCount (); i++) {
        System.out.println (i + ": " + m.group (i));
      }
    }
    else {
      System.out.println ("No match!");
    }
  }
}

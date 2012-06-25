/* Created by steinar on 18.05.12 at 13:25 */
package eu.peppol.smp;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import at.peppol.busdox.identifier.IDocumentTypeIdentifier;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.commons.uri.BusdoxURLUtils;

import com.phloc.commons.regex.RegExPool;
import com.phloc.commons.url.URLUtils;

import eu.peppol.util.Util;

/**
 * Simple SmpLookup service, which does not use JAXB to parse the response from
 * the SMP. This class is experimental and not currently (May 22, 2012) in use.
 * 
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SmpLookup {
  private static final Pattern documentTypeIdentifierPattern = RegExPool.getPattern ("/services/busdox-docid-qns::(.*)");

  private final SimpleParticipantIdentifier m_aParticipantId;

  public SmpLookup (final SimpleParticipantIdentifier participantId) {
    this.m_aParticipantId = participantId;
  }

  URL servicesUrl () {
    // iso6523-actorid-upis%3A%3A9908:810017902
    return URLUtils.getAsURL (BusdoxURLUtils.getSMPURLOfParticipant (m_aParticipantId, ESML.PRODUCTION)
                                            .toExternalForm () + "/" + m_aParticipantId.getURIPercentEncoded ());
  }

  public List <URL> getServiceUrlList () throws SmpLookupException {
    final URL servicesUrl = servicesUrl ();

    final InputSource inputSource = Util.getUrlContent (servicesUrl);
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    documentBuilderFactory.setNamespaceAware (false); // Makes life with XPath
                                                      // much simpler
    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder ();
      final Document document = documentBuilder.parse (inputSource);

      final XPath xPath = XPathFactory.newInstance ().newXPath ();
      final XPathExpression expr = xPath.compile ("//ServiceMetadataReference/@href");

      final NodeList nodes = (NodeList) expr.evaluate (document, XPathConstants.NODESET);
      final List <URL> result = new ArrayList <URL> ();
      for (int i = 0; i < nodes.getLength (); i++) {
        final String hrefString = nodes.item (i).getNodeValue ();
        result.add (new URL (hrefString));
      }

      return result;
    }
    catch (final Exception e) {
      throw new SmpLookupException (m_aParticipantId, servicesUrl, e);
    }
  }

  public List <IDocumentTypeIdentifier> parseServiceMetadataReferences () throws SmpLookupException {

    final List <IDocumentTypeIdentifier> documentTypeIdentifiers = new ArrayList <IDocumentTypeIdentifier> ();

    final List <URL> urls = getServiceUrlList ();
    for (final URL url : urls) {
      // Retrieves the parth of the URL, coming after the hostname
      try {
        final String urlPathComponent = URLDecoder.decode (url.getPath (), "UTF-8");
        System.out.println (urlPathComponent);
        final Matcher matcher = documentTypeIdentifierPattern.matcher (urlPathComponent);
        if (matcher.find ()) {
          final String sDocID = matcher.group (1);
          for (final EPredefinedDocumentTypeIdentifier e : EPredefinedDocumentTypeIdentifier.values ())
            if (e.getValue ().equals (sDocID)) {
              documentTypeIdentifiers.add (e.getAsDocumentTypeIdentifier ());
              break;
            }
        }
        else
          throw new IllegalStateException ("documentTypeIdentifierPattern did not match ");

      }
      catch (final UnsupportedEncodingException e) {
        throw new SmpLookupException (m_aParticipantId, e);
      }
    }

    return documentTypeIdentifiers;
  }
}

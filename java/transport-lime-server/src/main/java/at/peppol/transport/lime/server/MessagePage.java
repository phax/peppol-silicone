/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.transport.lime.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.lime._1.Entry;
import org.busdox.transport.lime._1.NextPageIdentifierType;
import org.busdox.transport.lime._1.ObjectFactory;
import org.busdox.transport.lime._1.PageListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.transport.lime.Identifiers;
import at.peppol.transport.lime.server.storage.Channel;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.JAXBMarshallerUtils;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class MessagePage {
  public static final int MESSAGE_PAGE_SIZE = 100;
  private static final Logger s_aLogger = LoggerFactory.getLogger (MessagePage.class);

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  public Document getPageList (final int pageNum, final String endpoint, final Channel channel, final String channelID) throws JAXBException {
    final int pageSize = MESSAGE_PAGE_SIZE;
    final String [] messageIDs = channel.getMessageIDs (channelID);

    if (pageNum < 0 || pageNum > (messageIDs.length / pageSize))
      throw new IllegalArgumentException ("Page number must be between 0 and " + (messageIDs.length / pageSize));

    s_aLogger.info ("Messages found in inbox: " + messageIDs.length);
    return createPageListDocument (messageIDs, pageSize, pageNum, channel, channelID, endpoint);
  }

  private Document getPageListDocument (final int pageNum,
                                        final int pageSize,
                                        final String [] messageIDs,
                                        final Channel channel,
                                        final String channelID,
                                        final String endpoint) throws JAXBException {
    final int fromMsg = pageNum * pageSize;
    final int toMsg = Math.min (((pageNum + 1) * pageSize) - 1, messageIDs.length - 1);

    final PageListType pageList = m_aObjFactory.createPageListType ();
    pageList.setNumberOfEntries (new Long (toMsg - fromMsg + 1));
    addPageListEntries (fromMsg, toMsg, messageIDs, channel, channelID, endpoint, pageList);
    if ((messageIDs.length / pageSize) >= pageNum + 1) {
      addNextPageIdentifier (endpoint, pageNum, pageList, channelID);
    }
    return marshallPageList (pageList);
  }

  private Document marshallPageList (final PageListType pageList) throws JAXBException {
    final Marshaller marshaller = JAXBContextCache.getInstance ().getFromCache (PageListType.class).createMarshaller ();
    JAXBMarshallerUtils.setSunNamespacePrefixMapper (marshaller, new NamespacePrefixMapper () {
      @Override
      public String getPreferredPrefix (final String namespaceUri, final String suggestion, final boolean requirePrefix) {
        if (Identifiers.NAMESPACE_LIME.equalsIgnoreCase (namespaceUri))
          return "peppol";
        return suggestion;
      }
    });

    final Document document = XMLFactory.newDocument ();
    marshaller.marshal (m_aObjFactory.createPageList (pageList), new DOMResult (document));
    s_aLogger.info (xmlToString (document));
    return document;
  }

  private void addNextPageIdentifier (final String endpoint,
                                      final int curPageNum,
                                      final PageListType pageList,
                                      final String channelID) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final List <Element> referenceParametersType = new ArrayList <Element> ();
    Element aElement = aDummyDoc.createElementNS (Identifiers.NAMESPACE_LIME, Identifiers.PAGEIDENTIFIER);
    aElement.appendChild (aDummyDoc.createTextNode (Integer.toString (curPageNum + 1)));
    referenceParametersType.add (aElement);

    aElement = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, Identifiers.CHANNELID);
    aElement.appendChild (aDummyDoc.createTextNode (channelID));
    referenceParametersType.add (aElement);

    final NextPageIdentifierType nextPageIdentifierType = m_aObjFactory.createNextPageIdentifierType ();
    final W3CEndpointReference endpointReferenceType = W3CEndpointReferenceUtils.createEndpointReference (endpoint,
                                                                                                          referenceParametersType);
    nextPageIdentifierType.setEndpointReference (endpointReferenceType);
    pageList.setNextPageIdentifier (nextPageIdentifierType);
  }

  private void addPageListEntries (final int fromMsg,
                                   final int toMsg,
                                   final String [] messageIDs,
                                   final Channel channel,
                                   final String channelID,
                                   final String endpoint,
                                   final PageListType pageList) {
    pageList.setEntryList (m_aObjFactory.createEntryListType ());

    for (int i = fromMsg; i <= toMsg; i++) {
      final String messageID = messageIDs[i];
      final Entry entry = m_aObjFactory.createEntry ();
      entry.setSize (Long.valueOf (channel.getSize (channelID, messageID)));
      entry.setCreationTime (channel.getCreationTime (channelID, messageID));

      final List <Element> referenceParametersType = new ArrayList <Element> ();
      final Document aDummyDoc = XMLFactory.newDocument ();
      Element element = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, Identifiers.CHANNELID);
      element.appendChild (aDummyDoc.createTextNode (channelID));
      referenceParametersType.add (element);
      element = aDummyDoc.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, Identifiers.MESSAGEID);
      element.appendChild (aDummyDoc.createTextNode (messageID));
      referenceParametersType.add (element);
      final W3CEndpointReference endpointReferenceType = W3CEndpointReferenceUtils.createEndpointReference (endpoint,
                                                                                                            referenceParametersType);
      entry.setEndpointReference (endpointReferenceType);
      pageList.getEntryList ().getEntry ().add (entry);
    }
  }

  private Document createPageListDocument (final String [] messageIDs,
                                           final int pageSize,
                                           final int pageNum,
                                           final Channel channel,
                                           final String channelID,
                                           final String endpoint) throws JAXBException {
    Document pageListDocument = null;
    if (messageIDs.length > 0 && (messageIDs.length / pageSize) >= pageNum) {

      s_aLogger.info ("Messages in inbox: " + messageIDs.length);

      pageListDocument = getPageListDocument (pageNum, pageSize, messageIDs, channel, channelID, endpoint);

      s_aLogger.info ("Page List created. MessageIDs=" +
                      messageIDs.length +
                      " pageSize=" +
                      pageSize +
                      " pageNum=" +
                      pageNum);
    }
    else {
      s_aLogger.info ("Page List not created. MessageIDs=" +
                      messageIDs.length +
                      " pageSize=" +
                      pageSize +
                      " pageNum=" +
                      pageNum);
    }
    return pageListDocument;
  }

  private static String xmlToString (final Node node) {
    return XMLWriter.getNodeAsString (node, XMLWriterSettings.SUGGESTED_XML_SETTINGS);
  }
}

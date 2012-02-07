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
package org.busdox.transport.lime.soapheader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceContext;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ObjectFactory;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime.Identifiers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.peppol.busdox.identifier.SimpleDocumentIdentifier;
import eu.peppol.busdox.identifier.SimpleParticipantIdentifier;
import eu.peppol.busdox.identifier.SimpleProcessIdentifier;
import eu.peppol.start.IMessageMetadata;
import eu.peppol.start.MessageMetadata;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SoapHeaderReader {
  private SoapHeaderReader () {}

  @Nonnull
  public static MessageMetadata getSoapHeader (final HeaderList hl) throws Exception {
    return new MessageMetadata (getMessageID (hl),
                                getChannelID (hl),
                                _getSender (hl),
                                _getRecipient (hl),
                                _getDocumentInfoType (hl),
                                _getProcessType (hl));
  }

  @Nullable
  private static HeaderList _getHeaderList (@Nonnull final WebServiceContext webServiceContext) {
    return (HeaderList) webServiceContext.getMessageContext ().get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
  }

  @Nonnull
  public static IMessageMetadata getSoapHeader (@Nonnull final WebServiceContext wsc) throws Exception {
    return getSoapHeader (_getHeaderList (wsc));
  }

  @Nullable
  public static Header getHeader (@Nullable final HeaderList hl, final String headerName) {
    return getHeader (hl, headerName, null);
  }

  @Nullable
  public static Header getHeader (@Nullable final HeaderList hl,
                                  final String headerName,
                                  @Nullable final String nameSpaceURI) {
    if (hl != null)
      for (final Header header : hl)
        if (header.getLocalPart ().equals (headerName))
          if (nameSpaceURI == null || nameSpaceURI.equalsIgnoreCase (header.getNamespaceURI ()))
            return header;
    return null;
  }

  @Nullable
  public static String getHeaderValue (@Nullable final HeaderList hl, final String headerName) {
    return getHeaderValue (hl, headerName, null);
  }

  @Nullable
  public static String getHeaderValue (@Nullable final HeaderList hl,
                                       final String headerName,
                                       @Nullable final String nameSpaceURI) {
    final Header aHeader = getHeader (hl, headerName, nameSpaceURI);
    return aHeader == null ? null : aHeader.getStringContent ();
  }

  public static String getHeaderValue (final WebServiceContext webServiceContext, final String headerName) {
    return getHeaderValue (webServiceContext, headerName, null);
  }

  public static String getHeaderValue (@Nonnull final WebServiceContext webServiceContext,
                                       final String headerName,
                                       final String nameSpaceURI) {
    return getHeaderValue (_getHeaderList (webServiceContext), headerName, nameSpaceURI);
  }

  public static String getHeaderAttribute (final HeaderList hl, final String headerName, final String attrName) {
    final Header aHeader = getHeader (hl, headerName);
    return aHeader == null ? null : aHeader.getAttribute (new QName (null, attrName));
  }

  public static String getHeaderAttribute (@Nonnull final WebServiceContext webServiceContext,
                                           final String headerName,
                                           final String attrName) {
    return getHeaderAttribute (_getHeaderList (webServiceContext), headerName, attrName);
  }

  public static String getPageNumber (@Nonnull final WebServiceContext webServiceContext) {
    return getHeaderValue (webServiceContext, Identifiers.PAGEIDENTIFIER, Identifiers.NAMESPACE_LIME);
  }

  public static String getMessageID (@Nonnull final WebServiceContext webServiceContext) {
    return getMessageID (_getHeaderList (webServiceContext));
  }

  public static String getMessageID (final HeaderList hl) {
    return getHeaderValue (hl, Identifiers.MESSAGEID, Identifiers.NAMESPACE_TRANSPORT_IDS);
  }

  public static String getChannelID (@Nonnull final WebServiceContext webServiceContext) {
    return getChannelID (_getHeaderList (webServiceContext));
  }

  public static String getChannelID (final HeaderList hl) {
    return getHeaderValue (hl, Identifiers.CHANNELID, Identifiers.NAMESPACE_TRANSPORT_IDS);
  }

  private static ParticipantIdentifierType _getSender (final HeaderList hl) throws Exception {
    final String [] senderHeaders = getHeaderText (hl, Identifiers.SENDERID);
    final String scheme = getHeaderAttribute (hl, Identifiers.SENDERID, Identifiers.SCHEME_ATTR);
    if (senderHeaders == null) {
      throw new Exception ("SenderIdentifier is missing in SOAP header");
    }
    final ParticipantIdentifierType businessIdentifier = new ParticipantIdentifierType ();
    businessIdentifier.setValue (senderHeaders[0]);
    businessIdentifier.setScheme (scheme);
    return businessIdentifier;
  }

  private static ParticipantIdentifierType _getRecipient (final HeaderList hl) throws Exception {
    final String [] receiverHeaders = getHeaderText (hl, Identifiers.RECIPIENTID);
    final String scheme = getHeaderAttribute (hl, Identifiers.RECIPIENTID, Identifiers.SCHEME_ATTR);
    if (receiverHeaders == null) {
      throw new Exception ("Recipient Identifier is missing in SOAP header");
    }
    return new SimpleParticipantIdentifier (scheme, receiverHeaders[0]);
  }

  private static DocumentIdentifierType _getDocumentInfoType (final HeaderList hl) throws Exception {
    final String [] documentTypeHeaders = getHeaderText (hl, Identifiers.DOCUMENTID);
    final String scheme = getHeaderAttribute (hl, Identifiers.DOCUMENTID, Identifiers.SCHEME_ATTR);
    if (documentTypeHeaders == null) {
      throw new Exception ("DocumentType is missing in SOAP header");
    }
    return new SimpleDocumentIdentifier (scheme, documentTypeHeaders[0]);
  }

  private static ProcessIdentifierType _getProcessType (final HeaderList hl) throws Exception {
    final String [] processTypeHeaders = getHeaderText (hl, Identifiers.PROCESSID);
    final String scheme = getHeaderAttribute (hl, Identifiers.PROCESSID, Identifiers.SCHEME_ATTR);
    if (processTypeHeaders == null) {
      throw new Exception ("Process Type is missing in SOAP header");
    }
    return new SimpleProcessIdentifier (scheme, processTypeHeaders[0]);
  }

  public static String [] getHeaderText (final WebServiceContext webServiceContext, final String headerName) throws XMLStreamException {
    String [] textNodes = null;
    final HeaderList hl = (HeaderList) webServiceContext.getMessageContext ()
                                                        .get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);

    if (hl != null) {
      for (final Header header : hl) {
        if (header.getLocalPart ().equals (headerName)) {
          final XMLStreamReader xMLStreamReader = header.readHeader ();
          textNodes = HeaderParser.getTextNodes (xMLStreamReader);
          break;
        }
      }
    }
    return textNodes;
  }

  public static String [] getHeaderText (final HeaderList hl, final String headerName) throws Exception {
    String [] textNodes = null;
    if (hl != null) {
      for (final Header header : hl) {
        if (header.getLocalPart ().equals (headerName)) {
          final XMLStreamReader xMLStreamReader = header.readHeader ();
          textNodes = HeaderParser.getTextNodes (xMLStreamReader);
          break;
        }
      }
    }
    return textNodes;
  }

  public static Document createSoapHeaderDocument (final IMessageMetadata soapHdr) throws JAXBException {
    final ObjectFactory objFactory = new ObjectFactory ();

    final Document document = XMLFactory.newDocument ();
    final Element top = document.createElementNS (Identifiers.NAMESPACE_TRANSPORT_IDS, "Headers");
    document.appendChild (top);

    Marshaller marshaller = JAXBContextCache.getInstance ()
                                            .getFromCache (ParticipantIdentifierType.class)
                                            .createMarshaller ();
    marshaller.marshal (objFactory.createSenderIdentifier (soapHdr.getSenderID ()), top);
    marshaller.marshal (objFactory.createRecipientIdentifier (soapHdr.getRecipientID ()), top);

    marshaller = JAXBContextCache.getInstance ().getFromCache (DocumentIdentifierType.class).createMarshaller ();
    marshaller.marshal (objFactory.createDocumentIdentifier (soapHdr.getDocumentTypeID ()), top);

    marshaller = JAXBContextCache.getInstance ().getFromCache (ProcessIdentifierType.class).createMarshaller ();
    marshaller.marshal (objFactory.createProcessIdentifier (soapHdr.getProcessID ()), top);
    return document;
  }
}

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
package org.busdox.transport.messagestore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.CGlobal;
import com.phloc.commons.io.file.filter.FilenameFilterFactory;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class Channel {
  public static final String EXT_METADATA = ".metadata";
  public static final String EXT_PAYLOAD = ".payload";
  public static final String INBOX_DIR = "inbox";
  public static final long MESSAGE_INVALID_TIME_IN_MILLIS = CGlobal.MILLISECONDS_PER_HOUR * 2L;
  private static final Logger log = LoggerFactory.getLogger (Channel.class);

  private final String m_sStorePath;

  public Channel (final String storePath) {
    m_sStorePath = storePath;
  }

  public void saveDocument (final String channelID,
                            final String messageID,
                            final Document metadataDocument,
                            final Document payloadDocument) throws Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);

    final File metadataFile = getMetadataFile (channelInboxDir, messageID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);

    if (!metadataFile.createNewFile ()) {
      log.info ("Metadata filename: " + metadataFile.getAbsolutePath ());
      throw new Exception ("Cannot create new metadata file for message ID " +
                           messageID +
                           " in inbox for channel " +
                           channelID);

    }
    if (!payloadFile.createNewFile ()) {
      log.info ("Payload filename: " + payloadFile.getAbsolutePath ());
      metadataFile.delete ();
      throw new Exception ("Cannot create new document file for message ID " +
                           messageID +
                           " in inbox for channel " +
                           channelID);
    }

    try {
      writeDocumentToFile (metadataFile, metadataDocument);
      writeDocumentToFile (payloadFile, payloadDocument);
    }
    catch (final Exception ex) {
      metadataFile.delete ();
      payloadFile.delete ();
      throw ex;
    }
  }

  public void deleteDocument (final String channelID, final String messageID) throws Exception {
    if (channelID != null && messageID != null) {
      final File channelInboxDir = getChannelInboxDir (channelID);
      final File metadataFile = getMetadataFile (channelInboxDir, messageID);
      final File payloadFile = getPayloadFile (channelInboxDir, messageID);
      // TODO: log if only one of the two exists?
      if (metadataFile.exists ()) {
        metadataFile.delete ();
      }
      if (payloadFile.exists ()) {
        payloadFile.delete ();
      }
    }
  }

  public String [] getMessageIDs (final String channelID) throws Exception {
    final File dir = getChannelInboxDir (channelID);
    final File [] files = dir.listFiles (FilenameFilterFactory.getEndsWithFilter (EXT_PAYLOAD));
    if (files == null)
      return new String [0];

    final String [] messageIDs = new String [files.length];
    int i = 0;
    for (final File payloadFile : files) {
      final String curMessageId = getMessageIDFromPayloadFile (payloadFile);

      if ((System.currentTimeMillis () - payloadFile.lastModified ()) > MESSAGE_INVALID_TIME_IN_MILLIS) {
        deleteDocument (channelID, curMessageId);
      }
      else {
        messageIDs[i] = curMessageId;
        i++;
      }
    }
    return messageIDs;
  }

  public Document getDocumentMetadata (final String channelID, final String messageID) throws SAXException,
                                                                                      IOException,
                                                                                      ParserConfigurationException,
                                                                                      Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);
    final File metadataFile = getMetadataFile (channelInboxDir, messageID);
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    documentBuilderFactory.setNamespaceAware (false);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
    return documentBuilder.parse (metadataFile);
  }

  public Document getDocument (final String channelID, final String messageID) throws SAXException,
                                                                              IOException,
                                                                              ParserConfigurationException,
                                                                              Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
    return documentBuilder.parse (payloadFile);
  }

  public long getSize (final String channelID, final String messageID) throws Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    final long fileLength = payloadFile.length ();
    // calculate length in Kilobytes and round up
    final long fileLenghtInKB = (fileLength + 1023) / 1024;
    return fileLenghtInKB;
  }

  public Date getCreationTime (final String channelID, final String messageID) throws Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    return new Date (payloadFile.lastModified ());
  }

  private static String getMessageIDFromPayloadFile (final File payloadFile) {
    final String str = payloadFile.getName ();
    String messageID = str.substring (0, str.length () - EXT_PAYLOAD.length ());
    messageID = messageID.replace ('_', ':');
    return messageID;
  }

  private static File getMetadataFile (final File channelInboxDir, final String messageID) {
    final String sRealMessageID = removeSpecialChars (messageID);
    return new File (channelInboxDir, sRealMessageID + EXT_METADATA);
  }

  private static File getPayloadFile (final File channelInboxDir, final String messageID) {
    final String sRealMessageID = removeSpecialChars (messageID);
    final File file = new File (channelInboxDir, sRealMessageID + EXT_PAYLOAD);
    log.info ("Getting payload file: " + file.getAbsolutePath ());
    return file;
  }

  private File getChannelInboxDir (final String channelID) throws Exception {
    final File inboxDir = new File (m_sStorePath, INBOX_DIR);
    if (inboxDir.exists () == false) {
      inboxDir.mkdir ();
    }
    final String sRealChannelID = removeSpecialChars (channelID);

    final File channelDir = new File (inboxDir, sRealChannelID);
    if (channelDir.exists () == false) {
      channelDir.mkdir ();
    }
    if (!channelDir.exists ()) {
      throw new Exception ("Inbox for channel \"" +
                           sRealChannelID +
                           "\" could not be found or created: " +
                           channelDir.getAbsolutePath ());
    }
    return channelDir;
  }

  @Nonnull
  private static String removeSpecialChars (@Nonnull final String fileOrDirName) {
    return fileOrDirName.replace (':', '_');
  }

  private static void writeDocumentToFile (final File messageFile, final Document document) throws FileNotFoundException,
                                                                                           TransformerFactoryConfigurationError,
                                                                                           TransformerException,
                                                                                           TransformerConfigurationException,
                                                                                           TransformerException {
    final BufferedOutputStream bos = new BufferedOutputStream (new FileOutputStream (messageFile));
    final TransformerFactory transformerFactory = TransformerFactory.newInstance ();
    final Transformer transformer = transformerFactory.newTransformer ();
    final DOMSource source = new DOMSource (document);
    final StreamResult result = new StreamResult (bos);
    transformer.transform (source, result);
  }
}

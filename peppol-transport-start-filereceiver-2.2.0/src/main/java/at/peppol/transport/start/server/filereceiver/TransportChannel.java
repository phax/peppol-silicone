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
package at.peppol.transport.start.server.filereceiver;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.CGlobal;
import com.phloc.commons.exceptions.LoggedException;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.file.filter.FilenameFilterFactory;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
final class TransportChannel {
  /**
   * Extension of the Metadata.
   */
  public static final String EXT_METADATA = ".metadata";

  /**
   * Extension of the Payload.
   */
  public static final String EXT_PAYLOAD = ".payload";

  /**
   * Directory of the Inbox.
   */
  public static final String INBOX_DIR = "inbox";

  /**
   * Time limit for Messages.
   */
  public static final long MESSAGE_INVALID_TIME_IN_MILLIS = CGlobal.MILLISECONDS_PER_HOUR * 2L;

  /**
   * Logger to follow this class behavior.
   */
  private static final Logger s_aLogger = LoggerFactory.getLogger (TransportChannel.class);

  /**
   * Path of the Store.
   */
  protected String m_sStorePath;

  /**
   * Indicates if the document was saved.
   */
  public boolean m_bIsSaved = false;

  /**
   * Indicates if the document was deleted.
   */
  public boolean m_bIsMetadataRemoved = false;

  /**
   * Indicates if the document was deleted.
   */
  public boolean m_bIsPayloadRemoved = false;

  /**
   * Set the path of the Store.
   * 
   * @param storePath
   *        Path of the store.
   */
  public TransportChannel (final String storePath) {
    m_sStorePath = storePath;
  }

  /**
   * Save a Document.
   * 
   * @param channelID
   *        ID for channel.
   * @param messageID
   *        ID for message.
   * @param metadataDocument
   *        XML Document for Metadata.
   * @param payloadDocument
   *        XML Document for Payload.
   * @throws Exception
   *         Exception if document cannot be saved.
   */
  public final void saveDocument (final String channelID,
                                  final String messageID,
                                  final Document metadataDocument,
                                  final Document payloadDocument) throws Exception {
    m_bIsSaved = false;

    final File channelInboxDir = getChannelInboxDir (channelID);
    s_aLogger.info ("TransportChannel");

    final File metadataFile = getMetadataFile (channelInboxDir, messageID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);

    if (!metadataFile.createNewFile ())
      throw new LoggedException ("Cannot create new metadata file for message ID " +
                                 messageID +
                                 " in inbox for channel " +
                                 channelID);

    if (!payloadFile.createNewFile ()) {
      s_aLogger.error ("Cannot create new payload file for message ID " +
                       messageID +
                       " in inbox for channel " +
                       channelID);
      if (!metadataFile.delete ()) {
        s_aLogger.debug ("Cannot delete metadata file for message ID " +
                         messageID +
                         " in inbox for channel " +
                         channelID);
      }
      else {
        s_aLogger.debug ("Metadata file deleted: " + metadataFile.getAbsolutePath ());
      }
      throw new Exception ("Cannot create new payload file for message ID " +
                           messageID +
                           " in inbox for channel " +
                           channelID);
    }

    try {

      writeDocumentToFile (metadataFile, metadataDocument);
      s_aLogger.info ("Metadata created: " + metadataFile.getName ());
      writeDocumentToFile (payloadFile, payloadDocument);
      s_aLogger.info ("Payload created: " + payloadFile.getName ());

      m_bIsSaved = true;
    }
    catch (final Exception ex) {
      if (metadataFile.delete ()) {
        s_aLogger.debug ("Metadata file deleted: " + metadataFile.getAbsolutePath ());
      }
      else {
        s_aLogger.debug ("Cannot delete Metadata file: " + metadataFile.getAbsolutePath ());
      }
      if (payloadFile.delete ()) {
        s_aLogger.debug ("Payload file deleted: " + payloadFile.getAbsolutePath ());
      }
      else {
        s_aLogger.debug ("Cannot delete Payload file: " + payloadFile.getAbsolutePath ());
      }

      s_aLogger.error ("Error saving a document.", ex);

      throw ex;
    }
  }

  /**
   * Delete a Document.
   * 
   * @param channelID
   *        ChannelID directory.
   * @param messageID
   *        ID of the Message.
   * @throws Exception
   *         Throw the exception.
   */
  public final void deleteDocument (final String channelID, final String messageID) throws Exception {

    if (channelID != null && messageID != null) {
      final File channelInboxDir = getChannelInboxDir (channelID);
      final File metadataFile = getMetadataFile (channelInboxDir, messageID);
      final File payloadFile = getPayloadFile (channelInboxDir, messageID);

      if (metadataFile.exists ()) {
        if (metadataFile.delete ()) {
          m_bIsMetadataRemoved = true;
          s_aLogger.debug ("Metadata file deleted: " + metadataFile.getAbsolutePath ());
        }
        else {
          s_aLogger.debug ("Cannot delete Metadata file: " + metadataFile.getAbsolutePath ());
        }
      }
      if (payloadFile.exists ()) {
        if (payloadFile.delete ()) {
          m_bIsPayloadRemoved = true;
          s_aLogger.debug ("Payload file deleted: " + payloadFile.getAbsolutePath ());
        }
        else {
          s_aLogger.debug ("Cannot delete Payload file: " + payloadFile.getAbsolutePath ());
        }
      }
    }
  }

  /**
   * Get MessagesID from a Channel.
   * 
   * @param channelID
   *        ID of the Channel.
   * @return Array of MessagesID.
   * @throws Exception
   *         Throws an exception.
   */
  public final String [] getMessageIDs (final String channelID) throws Exception {

    final File dir = getChannelInboxDir (channelID);
    final File [] files = dir.listFiles (FilenameFilterFactory.getEndsWithFilter (EXT_PAYLOAD));

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

  /**
   * Get Metadata of a Document.
   * 
   * @param channelID
   *        ID of the Channel.
   * @param messageID
   *        ID of the Message.
   * @return Metadata Document
   * @throws Exception
   *         throws an exception.
   */
  public final Document getDocumentMetadata (final String channelID, final String messageID) throws Exception {

    final File channelInboxDir = getChannelInboxDir (channelID);
    final File metadataFile = getMetadataFile (channelInboxDir, messageID);
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    documentBuilderFactory.setNamespaceAware (false);
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
    return documentBuilder.parse (metadataFile);
  }

  public final Document getDocument (final String channelID, final String messageID) throws Exception {

    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
    return documentBuilder.parse (payloadFile);
  }

  public final long getSize (final String channelID, final String messageID) throws Exception {

    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    final long fileLength = payloadFile.length ();
    // calculate length in Kilobytes and round up
    final int kb = 1023;
    final int size = 1024;
    final long fileLenghtInKB = (fileLength + kb) / size;
    return fileLenghtInKB;
  }

  public final Date getCreationTime (final String channelID, final String messageID) throws Exception {
    final File channelInboxDir = getChannelInboxDir (channelID);
    final File payloadFile = getPayloadFile (channelInboxDir, messageID);
    return new Date (payloadFile.lastModified ());
  }

  private static String getMessageIDFromPayloadFile (final File payloadFile) {
    final String str = payloadFile.getName ();

    // Remove payload extension
    final String messageID = str.substring (0, str.length () - EXT_PAYLOAD.length ());

    return messageID.replace ('_', ':');
  }

  private static File getMetadataFile (final File channelInboxDir, final String messageID) {
    final String sRealMessageID = _removeSpecialChars (messageID);
    return new File (channelInboxDir, sRealMessageID + EXT_METADATA);
  }

  private static File getPayloadFile (final File channelInboxDir, final String messageID) {
    final String sRealMessageID = _removeSpecialChars (messageID);
    final File file = new File (channelInboxDir, sRealMessageID + EXT_PAYLOAD);

    return file;
  }

  private File getChannelInboxDir (final String channelID) throws Exception {

    final File inboxDir = new File (m_sStorePath, INBOX_DIR);
    if (FileOperations.createDirIfNotExisting (inboxDir).isFailure ())
      s_aLogger.debug ("Cannot create the inbox directory: " + m_sStorePath + "/" + inboxDir);
    final String sRealChannelID = _removeSpecialChars (channelID);

    final File channelDir = new File (inboxDir, sRealChannelID);
    if (FileOperations.createDirRecursiveIfNotExisting (channelDir).isFailure ())
      s_aLogger.debug ("Cannot create the channel directory: " + inboxDir + "/" + sRealChannelID);
    if (!channelDir.exists ())
      throw new LoggedException ("Inbox for channel \"" +
                                 channelID +
                                 "\" could not be found or created: " +
                                 channelDir.getAbsolutePath ());
    return channelDir;
  }

  private static String _removeSpecialChars (final String fileOrDirName) {
    return fileOrDirName == null ? "$$$" : fileOrDirName.replace (':', '_');
  }

  private static void writeDocumentToFile (final File messageFile, final Document document) {
    XMLWriter.writeToStream (document, FileUtils.getOutputStream (messageFile), XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}

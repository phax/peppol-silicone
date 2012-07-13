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
package eu.peppol.start.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.peppol.commons.identifier.IdentifierUtils;
import at.peppol.transport.IMessageMetadata;

import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLWriter;

/**
 * @author $Author$ (of last change) Created by User: steinar Date: 28.11.11
 *         Time: 21:09
 */
public class SimpleMessageRepository implements MessageRepository {

  private static final Logger log = LoggerFactory.getLogger (SimpleMessageRepository.class);

  public void saveInboundMessage (final String inboundMessageStore,
                                  final IMessageMetadata peppolMessageHeader,
                                  final Document document) {
    log.info ("Default message handler " + peppolMessageHeader);

    final File messageDirectory = prepareMessageDirectory (inboundMessageStore, peppolMessageHeader);

    try {
      final String messageFileName = peppolMessageHeader.getMessageID ().replace (":", "_") + ".xml";
      final File messageFullPath = new File (messageDirectory, messageFileName);
      saveDocument (document, messageFullPath);

      final String headerFileName = peppolMessageHeader.getMessageID ().replace (":", "_") + ".txt";
      final File messageHeaderFilePath = new File (messageDirectory, headerFileName);
      saveHeader (peppolMessageHeader, messageHeaderFilePath, messageFullPath);

    }
    catch (final Exception e) {
      throw new IllegalStateException ("Unable to persist message " + peppolMessageHeader.getMessageID (), e);
    }

  }

  File prepareMessageDirectory (final String inboundMessageStore, final IMessageMetadata peppolMessageHeader) {
    // Computes the full path of the directory in which message and routing data
    // should be stored.
    final File messageDirectory = computeDirectoryNameForInboundMessage (inboundMessageStore, peppolMessageHeader);
    if (!messageDirectory.exists ()) {
      if (!messageDirectory.mkdirs ()) {
        throw new IllegalStateException ("Unable to create directory " + messageDirectory.toString ());
      }
    }

    if (!messageDirectory.isDirectory () || !messageDirectory.canWrite ()) {
      throw new IllegalStateException ("Directory " + messageDirectory + " does not exist, or there is no access");
    }
    return messageDirectory;
  }

  void saveHeader (final IMessageMetadata peppolMessageHeader,
                   final File messageHeaderFilerPath,
                   final File messageFullPath) {
    try {
      final FileOutputStream fos = new FileOutputStream (messageHeaderFilerPath);
      final PrintWriter pw = new PrintWriter (new OutputStreamWriter (fos, "UTF-8"));
      final Date date = new Date ();

      // Formats the current time and date according to the ISO8601 standard.
      pw.append ("TimeStamp=").format ("%tFT%tT%tz\n", date, date, date);

      pw.append ("MessageFileName=").append (messageFullPath.toString ()).append ('\n');
      pw.append (IdentifierName.MESSAGE_ID.stringValue ())
        .append ("=")
        .append (peppolMessageHeader.getMessageID ())
        .append ('\n');
      pw.append (IdentifierName.CHANNEL_ID.stringValue ())
        .append ("=")
        .append (peppolMessageHeader.getChannelID ())
        .append ('\n');
      pw.append (IdentifierName.RECIPIENT_ID.stringValue ())
        .append ('=')
        .append (IdentifierUtils.getIdentifierURIEncoded (peppolMessageHeader.getRecipientID ()))
        .append ('\n');
      pw.append (IdentifierName.SENDER_ID.stringValue ())
        .append ('=')
        .append (IdentifierUtils.getIdentifierURIEncoded (peppolMessageHeader.getSenderID ()))
        .append ('\n');
      pw.append (IdentifierName.DOCUMENT_ID.stringValue ())
        .append ('=')
        .append (IdentifierUtils.getIdentifierURIEncoded (peppolMessageHeader.getDocumentTypeID ()))
        .append ('\n');
      pw.append (IdentifierName.PROCESS_ID.stringValue ())
        .append ('=')
        .append (IdentifierUtils.getIdentifierURIEncoded (peppolMessageHeader.getProcessID ()))
        .append ('\n');
      pw.close ();
      log.debug ("File " + messageHeaderFilerPath + " written");

    }
    catch (final FileNotFoundException e) {
      throw new IllegalStateException ("Unable to create file " + messageHeaderFilerPath + "; " + e, e);
    }
    catch (final UnsupportedEncodingException e) {
      throw new IllegalStateException ("Unable to create writer for " + messageHeaderFilerPath + "; " + e, e);
    }
  }

  /**
   * Transforms an XML document into a String
   * 
   * @param document
   *        the XML document to be transformed
   */
  void saveDocument (final Document document, final File outputFile) {
    try {
      XMLWriter.writeToStream (document, FileUtils.getOutputStream (outputFile));
      log.debug ("File " + outputFile + " written");
    }
    catch (final Exception e) {
      throw new SimpleMessageRepositoryException (outputFile, e);
    }
  }

  @Override
  public String toString () {
    return SimpleMessageRepository.class.getSimpleName ();
  }

  /**
   * Computes the directory name for inbound messages.
   * 
   * <pre>
   *     /basedir/{recipientId}/{channelId}/{senderId}
   * </pre>
   * 
   * @param inboundMessageStore
   * @param peppolMessageHeader
   * @return Never null
   */
  File computeDirectoryNameForInboundMessage (final String inboundMessageStore,
                                              final IMessageMetadata peppolMessageHeader) {
    if (peppolMessageHeader == null) {
      throw new IllegalArgumentException ("peppolMessageHeader required");
    }

    final String path = String.format ("%s/%s/%s",
                                       peppolMessageHeader.getRecipientID ().getValue ().replace (":", "_"),
                                       StringHelper.getNotNull (peppolMessageHeader.getChannelID ()),
                                       peppolMessageHeader.getSenderID ().getValue ().replace (":", "_"));
    return new File (inboundMessageStore, path);
  }

  /**
   * Computes the directory
   * 
   * @param outboundMessageStore
   * @param peppolMessageHeader
   * @return never null
   */
  File computeDirectoryNameForOutboundMessages (final String outboundMessageStore,
                                                final IMessageMetadata peppolMessageHeader) {
    if (peppolMessageHeader == null) {
      throw new IllegalArgumentException ("peppolMessageHeader required");
    }

    final String path = String.format ("%s/%s/%s",
                                       peppolMessageHeader.getSenderID ().getValue ().replace (":", "_"),
                                       StringHelper.getNotNull (peppolMessageHeader.getChannelID ()),
                                       peppolMessageHeader.getRecipientID ().getValue ().replace (":", "_"));
    return new File (outboundMessageStore, path);
  }
}

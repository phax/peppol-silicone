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
package eu.sendregning.oxalis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;

import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import eu.peppol.outbound.api.DocumentSender;
import eu.peppol.outbound.api.DocumentSenderBuilder;
import eu.peppol.outbound.smp.SmpLookupManager;

/**
 * @author ravnholt
 * @author Steinar O. Cook
 * @author Nigel Parker
 */
public final class Main {
  private static OptionSpec <File> xmlDocument;
  private static OptionSpec <DocumentIdentifierType> documentType;
  private static OptionSpec <ProcessIdentifierType> processType;
  private static OptionSpec <String> sender;
  private static OptionSpec <String> recipient;
  private static OptionSpec <File> keystore;
  private static OptionSpec <String> keystorePassword;
  private static OptionSpec <String> destinationUrl;
  private static OptionSpec <String> channel;

  public static void main (final String [] args) throws Exception {

    final OptionParser optionParser = getOptionParser ();

    if (args.length == 0) {
      System.out.println ();
      optionParser.printHelpOn (System.out);
      System.out.println ();
      return;
    }

    OptionSet optionSet;

    try {
      optionSet = optionParser.parse (args);
    }
    catch (final Exception e) {
      printErrorMessage (e.getMessage ());
      return;
    }

    final File xmlInvoice = xmlDocument.value (optionSet);

    if (!xmlInvoice.exists ()) {
      printErrorMessage ("XML document " + xmlInvoice + " does not exist");
      return;
    }

    final String recipientId = recipient.value (optionSet);
    final String senderId = sender.value (optionSet);
    final String channelId = optionSet.has (channel) ? channel.value (optionSet) : null;
    String password;

    if (optionSet.has (keystorePassword)) {
      password = keystorePassword.value (optionSet);
    }
    else {
      password = enterPassword ();
    }

    final DocumentIdentifierType documentId = optionSet.has (documentType)
                                                                          ? documentType.value (optionSet)
                                                                          : EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A.getAsDocumentTypeIdentifier ();
    final ProcessIdentifierType processId = optionSet.has (processType)
                                                                       ? processType.value (optionSet)
                                                                       : getDefaultProcess (SimpleParticipantIdentifier.createWithDefaultScheme (recipientId),
                                                                                            documentId);
    DocumentSender documentSender;

    try {
      documentSender = new DocumentSenderBuilder ().setDocumentTypeIdentifier (documentId)
                                                   .setPeppolProcessTypeId (processId)
                                                   .setKeystoreFile (keystore.value (optionSet))
                                                   .setKeystorePassword (password)
                                                   .build ();
    }
    catch (final Exception e) {
      printErrorMessage (e.getMessage ());
      return;
    }

    try {
      System.out.println ();
      System.out.println ();

      // Holds the messageId assigned upon successful transmission
      String messageId = null;

      if (optionSet.has (destinationUrl)) {
        final String destinationString = destinationUrl.value (optionSet);
        URL destination;

        try {
          destination = new URL (destinationString);
        }
        catch (final MalformedURLException e) {
          printErrorMessage ("Invalid destination URL " + destinationString);
          return;
        }

        messageId = documentSender.sendInvoice (xmlInvoice, senderId, recipientId, destination, channelId);
      }
      else {
        messageId = documentSender.sendInvoice (xmlInvoice, senderId, recipientId, channelId);
      }

      System.out.println ("Message sent, assigned message id:" + messageId);

    }
    catch (final Exception e) {
      System.out.println ();
      e.printStackTrace ();
      System.out.println ();
    }
  }

  private static void printErrorMessage (final String message) {
    System.out.println ();
    System.out.println ("*** " + message);
    System.out.println ();
  }

  private static ProcessIdentifierType getDefaultProcess (final IReadonlyParticipantIdentifier participantId,
                                                          final IReadonlyDocumentTypeIdentifier documentId) throws Exception {
    return SmpLookupManager.getProcessIdentifierForDocumentType (participantId, documentId);
  }

  private static OptionParser getOptionParser () {
    final OptionParser optionParser = new OptionParser ();
    xmlDocument = optionParser.accepts ("f", "XML document file to be sent")
                              .withRequiredArg ()
                              .ofType (File.class)
                              .required ();

    // TODO: add option allowing us to use the acronyms
    documentType = optionParser.accepts ("d", "Document type").withRequiredArg ().ofType (DocumentIdentifierType.class);

    // TODO: add option to use the process type acronym
    processType = optionParser.accepts ("p", "Process type").withRequiredArg ().ofType (ProcessIdentifierType.class);
    channel = optionParser.accepts ("c", "Channel identification").withRequiredArg ();
    sender = optionParser.accepts ("s", "sender [e.g. 9908:976098897]").withRequiredArg ().required ();
    recipient = optionParser.accepts ("r", "recipient [e.g. 9908:976098897]").withRequiredArg ().required ();
    keystore = optionParser.accepts ("kf", "keystore file").withRequiredArg ().ofType (File.class).required ();
    keystorePassword = optionParser.accepts ("kp", "keystore password").withRequiredArg ();
    destinationUrl = optionParser.accepts ("u", "destination URL").withRequiredArg ();
    return optionParser;
  }

  private static String enterPassword () {
    System.out.print ("Keystore password: ");
    final BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (System.in));
    String password = null;

    try {
      password = bufferedReader.readLine ();
    }
    catch (final Exception e) {
      e.printStackTrace ();
      System.exit (1);
    }
    finally {
      try {
        bufferedReader.close ();
      }
      catch (final Exception e) {}
    }

    return password;
  }
}

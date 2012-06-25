package eu.peppol.start.persistence;

/**
 * Represents the identifiers used in the START SOAP Headers, which are used for
 * routing, thus preventing the need to open the XML messages.
 * 
 * @author Steinar Overbeck Cook Created by User: steinar Date: 29.11.11 Time:
 *         13:44
 */
enum IdentifierName {

  MESSAGE_ID ("MessageIdentifier"),
  CHANNEL_ID ("ChannelIdentifier"),
  RECIPIENT_ID ("RecipientIdentifier"),
  SENDER_ID ("SenderIdentifier"),
  DOCUMENT_ID ("DocumentIdentifier"),
  PROCESS_ID ("ProcessIdentifier"),
  SCHEME ("scheme");

  private final String m_sValue;

  private IdentifierName (final String sValue) {
    this.m_sValue = sValue;
  }

  public String stringValue () {
    return m_sValue;
  }

  public static IdentifierName valueOfIdentifier (final String stringValue) {
    for (final IdentifierName id : IdentifierName.values ()) {
      if (id.m_sValue.equals (stringValue))
        return id;
    }

    throw new IllegalArgumentException ("Unknown identifer: " + stringValue);
  }
}

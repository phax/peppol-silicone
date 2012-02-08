package at.peppol.sml.server.dns;

import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;

import at.peppol.commons.identifier.CIdentifier;

import com.phloc.commons.compare.AbstractComparator;


/**
 * Comparator for sorting DNS records alphabetically.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ComparatorDNSRecord extends AbstractComparator <Record> {
  @Override
  protected int mainCompare (final Record r1, final Record r2) {
    // SOA records first
    final boolean bSOA1 = r1 instanceof SOARecord;
    final boolean bSOA2 = r2 instanceof SOARecord;
    if (bSOA1 && !bSOA2)
      return -1;
    if (!bSOA1 && bSOA2)
      return 1;

    // Next come NS records
    final boolean bNS1 = r1 instanceof NSRecord;
    final boolean bNS2 = r2 instanceof NSRecord;
    if (bNS1 && !bNS2)
      return -1;
    if (!bNS1 && bNS2)
      return 1;

    // Next come address and CNAME records but the participant IDs come last
    // Participant records are identified by the leading "B-"
    final String sName1 = r1.getName ().toString ();
    final String sName2 = r2.getName ().toString ();
    final boolean bParticipant1 = sName1.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
    final boolean bParticipant2 = sName2.startsWith (CIdentifier.DNS_HASHED_IDENTIFIER_PREFIX);
    if (bParticipant1 && !bParticipant2)
      return 1;
    if (!bParticipant1 && bParticipant2)
      return -1;

    // If all are equal so far, sort by name alphabetically
    return sName1.compareTo (sName2);
  }
}

package eu.peppol.start.identifier;

/**
 * Represents a PEPPOL Document Identifier acronym, textually represented thus:
 * <pre>
 *     &lt;root NS>::&lt;document element local name>##&lt;customization id>::&lt;version>
 * </pre>
 *
 * Provides short hand notation for PEPPOL Document Type Identifiers, which are otherwise fairly lengthy and complex.
 * This is just a simple helper class to make life easier :-)
 *
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 04.12.11
 *         Time: 18:52
 * @see "PEPPOL Policy for us of Identifiers v2.2, POLICY 13"
 */
public enum PeppolDocumentTypeIdAcronym {

    // PEPPOL Catalogues (PEPPOL BIS profile 1a)
    PEPPOL_CATALOGUE(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0")),

    // Basic Order according to PEPPOL BIS 3a
    ORDER(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0")),

    // Standard PEPPOL BIS profile 4a invoice
    INVOICE(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0")),

    // EHF Invoice
    EHF_INVOICE(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0")),

    // Standalone Credit Note according to EHF
    EHF_CREDIT_NOTE(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0")),

    // PEPPOL Billing (PEPPOL BIS Profile 5a)
    INVOICE_BILLING(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0")),

    // PEPPOL Billing (PEPPOL BIS Profile 5a)
    CREDIT_NOTE_BILLLING(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0")),

    // Credit invoice according to PEPPOL BIS 6a (Procurement)
    CREDIT_NOTE(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0")),
    ;

    private final static String scheme = "busdox-docid-qns";

    private final PeppolDocumentTypeId documentTypeIdentifier;

    PeppolDocumentTypeIdAcronym(PeppolDocumentTypeId identifier) {
        this.documentTypeIdentifier = identifier;
    }

    public static String getScheme() {
        return scheme;
    }

    @Override
    public String toString() {
        return documentTypeIdentifier.toString();
    }

    public PeppolDocumentTypeId getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }
}

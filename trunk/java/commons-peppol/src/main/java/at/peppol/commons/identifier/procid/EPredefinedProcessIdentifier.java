
package at.peppol.commons.identifier.procid;

import java.util.List;
import javax.annotation.Nonnull;
import at.peppol.commons.identifier.CIdentifier;
import at.peppol.commons.identifier.SimpleProcessIdentifier;
import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.docid.IPredefinedDocumentIdentifier;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.version.Version;


/**
 * This file is generated. Do NOT edit!
 * 
 */
public enum EPredefinedProcessIdentifier
    implements IPredefinedProcessIdentifier
{


    /**
     * urn:www.cenbii.eu:profile:bii01:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii01_ver1_0("urn:www.cenbii.eu:profile:bii01:ver1.0", "urn:www.peppol.eu:bis:peppol1a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.CATALOGUE_T019_BIS1A, EPredefinedDocumentTypeIdentifier.APPLICATIONRESPONSE_T057_BIS1A, EPredefinedDocumentTypeIdentifier.APPLICATIONRESPONSE_T058_BIS1A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii03:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii03_ver1_0("urn:www.cenbii.eu:profile:bii03:ver1.0", "urn:www.peppol.eu:bis:peppol3a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.ORDER_T001_BIS3A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii04:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii04_ver1_0("urn:www.cenbii.eu:profile:bii04:ver1.0", "urn:www.peppol.eu:bis:peppol4a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A }, new Version("1.0.0")),

    /**
     * urn:www.cenbii.eu:profile:bii05:ver1.0
     * @since code list 1.1.0
     * 
     */
    urn_www_cenbii_eu_profile_bii05_ver1_0("urn:www.cenbii.eu:profile:bii05:ver1.0", "urn:www.peppol.eu:bis:peppol5a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS5A, EPredefinedDocumentTypeIdentifier.CREDITNOTE_T014_BIS5A, EPredefinedDocumentTypeIdentifier.INVOICE_T015_BIS5A }, new Version("1.1.0")),

    /**
     * urn:www.cenbii.eu:profile:bii06:ver1.0
     * @since code list 1.0.0
     * 
     */
    urn_www_cenbii_eu_profile_bii06_ver1_0("urn:www.cenbii.eu:profile:bii06:ver1.0", "urn:www.peppol.eu:bis:peppol6a:ver1.0", new EPredefinedDocumentTypeIdentifier[] {EPredefinedDocumentTypeIdentifier.ORDER_T001_BIS6A, EPredefinedDocumentTypeIdentifier.ORDERRESPONSESIMPLE_T002_BIS6A, EPredefinedDocumentTypeIdentifier.ORDERRESPONSESIMPLE_T003_BIS6A, EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS6A, EPredefinedDocumentTypeIdentifier.CREDITNOTE_T014_BIS6A, EPredefinedDocumentTypeIdentifier.INVOICE_T015_BIS6A }, new Version("1.0.0"));
    public final static EPredefinedProcessIdentifier BIS1A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii01_ver1_0;
    public final static EPredefinedProcessIdentifier BIS3A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii03_ver1_0;
    public final static EPredefinedProcessIdentifier BIS4A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii04_ver1_0;
    public final static EPredefinedProcessIdentifier BIS5A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii05_ver1_0;
    public final static EPredefinedProcessIdentifier BIS6A = EPredefinedProcessIdentifier.urn_www_cenbii_eu_profile_bii06_ver1_0;
    private final String m_sID;
    private final String m_sBISID;
    private final EPredefinedDocumentTypeIdentifier[] m_aDocIDs;
    private final Version m_aSince;

    private EPredefinedProcessIdentifier(
        @Nonnull
        @Nonempty
        final String sID,
        @Nonnull
        @Nonempty
        final String sBISID,
        @Nonnull
        @Nonempty
        final EPredefinedDocumentTypeIdentifier[] aDocIDs,
        @Nonnull
        final Version aSince) {
        m_sID = sID;
        m_sBISID = sBISID;
        m_aDocIDs = aDocIDs;
        m_aSince = aSince;
    }

    @Nonnull
    @Nonempty
    public String getScheme() {
        return CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME;
    }

    @Nonnull
    @Nonempty
    public String getValue() {
        return m_sID;
    }

    @Nonnull
    @Nonempty
    public String getBISID() {
        return m_sBISID;
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<? extends IPredefinedDocumentIdentifier> getDocumentIdentifiers() {
        return ContainerHelper.newList(m_aDocIDs);
    }

    @Nonnull
    public SimpleProcessIdentifier getAsProcessIdentifier() {
        return new SimpleProcessIdentifier(this);
    }

    @Nonnull
    public Version getSince() {
        return m_aSince;
    }

}

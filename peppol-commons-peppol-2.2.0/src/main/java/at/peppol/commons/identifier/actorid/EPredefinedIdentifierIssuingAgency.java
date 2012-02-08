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

package at.peppol.commons.identifier.actorid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import at.peppol.commons.identifier.SimpleParticipantIdentifier;
import com.phloc.commons.annotations.Nonempty;


/**
 * This file is generated. Do NOT edit!
 * 
 */
public enum EPredefinedIdentifierIssuingAgency
    implements IIdentifierIssuingAgency
{

    FR_SIRET("FR:SIRET", "Institut National de la Statistique et des Etudes Economiques, (I.N.S.E.E.)", "0002", false),
    SE_ORGNR("SE:ORGNR", "The National Tax Board", "0007", false),
    FI_OVT("FI:OVT", "National Board of Taxes, (Verohallitus)", "0037", false),
    DUNS("DUNS", "Dun and Bradstreet Ltd", "0060", false),
    GLN("GLN", "EAN International", "0088", false),
    DK_P("DK:P", "Danish Chamber of Commerce", "0096", false),
    IT_FTI("IT:FTI", "FTI - Ediforum Italia", "0097", false),
    IT_SIA("IT:SIA", "SIA-Societ\u00e0 Interbancaria per l'Automazione S.p.A.", "0135", false),
    IT_SECETI("IT:SECETI", "Servizi Centralizzati SECETI S.p.A.", "0142", false),
    DK_CPR("DK:CPR", "Danish Ministry of the Interior and Health", "9901", false),
    DK_CVR("DK:CVR", "The Danish Commerce and Companies Agency", "9902", false),
    DK_SE("DK:SE", "Danish Ministry of Taxation, Central Customs and Tax Administration", "9904", false),
    DK_VANS("DK:VANS", "Danish VANS providers", "9905", false),
    IT_VAT("IT:VAT", "Ufficio responsabile gestione partite IVA", "9906", false),
    IT_CF("IT:CF", "TAX Authority", "9907", false),
    NO_ORGNR("NO:ORGNR", "Enhetsregisteret ved Bronnoysundregisterne", "9908", false),
    NO_VAT("NO:VAT", "Enhetsregisteret ved Bronnoysundregisterne", "9909", false),
    HU_VAT("HU:VAT", null, "9910", false),
    EU_VAT("EU:VAT", "National ministries of Economy", "9912", false),
    EU_REID("EU:REID", "Business Registers Network", "9913", false),
    AT_VAT("AT:VAT", "\u00d6sterreichische Umsatzsteuer-Identifikationsnummer", "9914", false),
    AT_GOV("AT:GOV", "\u00d6sterreichisches Verwaltungs bzw. Organisationskennzeichen", "9915", false),
    AT_CID("AT:CID", "Firmenidentifikationsnummer der Statistik Austria", "9916", true),
    IS_KT("IS:KT", "Icelandic National Registry", "9917", false),
    IBAN("IBAN", "SOCIETY FOR WORLDWIDE INTERBANK FINANCIAL, TELECOMMUNICATION S.W.I.F.T", "9918", false),
    AT_KUR("AT:KUR", "Kennziffer des Unternehmensregisters", "9919", false),
    ES_VAT("ES:VAT", "Agencia Espa\u00f1ola de Administraci\u00f3n Tributaria", "9920", false);
    private String m_sSchemeID;
    private String m_sSchemeAgency;
    private String m_sISO6523;
    private boolean m_bDeprecated;

    private EPredefinedIdentifierIssuingAgency(
        @Nonnull
        @Nonempty
        final String sSchemeID,
        @Nullable
        final String sSchemeAgency,
        @Nonnull
        @Nonempty
        final String sISO6523, final boolean bDeprecated) {
        m_sSchemeID = sSchemeID;
        m_sSchemeAgency = sSchemeAgency;
        m_sISO6523 = sISO6523;
        m_bDeprecated = bDeprecated;
    }

    @Nonnull
    @Nonempty
    public String getSchemeID() {
        return m_sSchemeID;
    }

    @Nullable
    public String getSchemeAgency() {
        return m_sSchemeAgency;
    }

    @Nonnull
    @Nonempty
    public String getISO6523Code() {
        return m_sISO6523;
    }

    @Nonnull
    @Nonempty
    public String createIdentifierValue(
        @Nonnull
        @Nonempty
        final String sIdentifier) {
        return ((m_sISO6523 +":")+ sIdentifier);
    }

    @Nonnull
    public SimpleParticipantIdentifier createParticipantIdentifier(
        @Nonnull
        @Nonempty
        final String sIdentifier) {
        return SimpleParticipantIdentifier.createWithDefaultScheme(createIdentifierValue(sIdentifier));
    }

    public boolean isDeprecated() {
        return m_bDeprecated;
    }

}

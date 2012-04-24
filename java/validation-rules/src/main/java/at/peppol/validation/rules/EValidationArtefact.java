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
package at.peppol.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.commons.cenbii.profiles.ETransaction;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.locale.country.CountryCache;
import com.phloc.commons.string.StringHelper;

/**
 * Contains all available invoice validation artefacts.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EValidationArtefact implements IValidationArtefact {
  // Technical
  ORDER_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.ORDER, "biicore", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T01) }),
  INVOICE_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.INVOICE, "biicore", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                           ValidationTransaction.createUBLTransaction (ETransaction.T15) }),
  CREDITNOTE_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.CREDIT_NOTE, "biicore", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) }),
  // Transaction
  CATALOGUE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CATALOGUE, "eugen", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T19) }),
  ORDER_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDER, "eugen", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T01) }),
  INVOICE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.INVOICE, "eugen", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                            ValidationTransaction.createUBLTransaction (ETransaction.T15) }),
  CREDITNOTE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "eugen", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) }),
  ORDER_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDER, "biirules", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T01) }),
  ORDERRESPONSE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDERRESPONSE, "biirules", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T02),
                                                                                                                                                              ValidationTransaction.createUBLTransaction (ETransaction.T03) }),
  INVOICE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.INVOICE, "biirules", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                  ValidationTransaction.createUBLTransaction (ETransaction.T15) }),
  CREDITNOTE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "biirules", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) }),
  // Profile
  INVOICE_BII_PROFILES (EValidationLevel.PROFILE_REQUIREMENTS, EValidationDocumentType.INVOICE, "biiprofiles", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                    ValidationTransaction.createUBLTransaction (ETransaction.T15) }),
  CREDITNOTE_BII_PROFILES (EValidationLevel.PROFILE_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "biiprofiles", null, new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) }),
  // Legal
  INVOICE_AUSTRIA_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "atnat", CountryCache.getCountry ("AT"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10) }),
  INVOICE_DENMARK_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "dknat", CountryCache.getCountry ("DK"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10) }),
  INVOICE_ITALY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "itnat", CountryCache.getCountry ("IT"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10) }),
  INVOICE_NORWAY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "nonat", CountryCache.getCountry ("NO"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                                         ValidationTransaction.createUBLTransaction (ETransaction.T15),
                                                                                                                                                                         ValidationTransaction.createUBLTransaction (ETransaction.T17) }),
  CREDITNOTE_NORWAY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "nonat", CountryCache.getCountry ("NO"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) }),
  // Industry
  INVOICE_AUSTRIA_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.INVOICE, "atgov", CountryCache.getCountry ("AT"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10) }),
  INVOICE_NORWAY_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.INVOICE, "nogov", CountryCache.getCountry ("NO"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                                          ValidationTransaction.createUBLTransaction (ETransaction.T15) }),
  CREDITNOTE_NORWAY_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.CREDIT_NOTE, "nogov", CountryCache.getCountry ("NO"), new IValidationTransaction [] { ValidationTransaction.createUBLTransaction (ETransaction.T14) });

  private static final Logger s_aLogger = LoggerFactory.getLogger (EValidationArtefact.class);
  private static final String BASE_DIRECTORY = "/rules/";

  private EValidationLevel m_eLevel;
  private IValidationDocumentType m_aDocType;
  private final String m_sDirName;
  private final String m_sFileNamePrefix;
  private final Locale m_aCountry;
  private final Set <IValidationTransaction> m_aTransactions;

  /**
   * Constructor for invoice validation artefacts.
   * 
   * @param eLevel
   *        The validation level of this artefact. May not be <code>null</code>.
   * @param aDocType
   *        The document type of this artefact. May not be <code>null</code>.
   * @param sDirName
   *        The name of the directory with this document type. May neither be
   *        <code>null</code> nor empty.
   * @param aCountry
   *        An optional country for which this artefact set applies. May be
   *        <code>null</code> for country independent artefacts.
   * @param aTransactions
   *        The transactions that are available for this artefact. May neither
   *        be <code>null</code> nor empty.
   */
  private EValidationArtefact (@Nonnull final EValidationLevel eLevel,
                               @Nonnull final IValidationDocumentType aDocType,
                               @Nonnull @Nonempty final String sDirName,
                               @Nullable final Locale aCountry,
                               @Nonnull @Nonempty final IValidationTransaction [] aTransactions) {
    if (StringHelper.hasNoText (sDirName))
      throw new IllegalArgumentException ("dirName is empty");
    if (aCountry != null && !eLevel.canHaveCountrySpecificArtefacts ())
      throw new IllegalArgumentException ("The validation level " +
                                          eLevel +
                                          " cannot have country specific artefacts but the country '" +
                                          aCountry.getCountry () +
                                          "' is provided!");
    if (ArrayHelper.isEmpty (aTransactions))
      throw new IllegalArgumentException ("no transaction specified");

    // Check if all transactions are supported by the Core data set!
    for (final IValidationTransaction aTransaction : aTransactions)
      if (!aTransaction.getTransaction ().isInCoreSupported ())
        throw new IllegalArgumentException ("The transaction '" +
                                            aTransaction.getTransaction ().getID () +
                                            "' is not supported by the BII core data set!");

    m_eLevel = eLevel;
    m_aDocType = aDocType;
    m_sDirName = sDirName;
    m_sFileNamePrefix = sDirName.toUpperCase ();
    m_aCountry = aCountry;
    m_aTransactions = ContainerHelper.newUnmodifiableSet (aTransactions);
  }

  @Nonnull
  public EValidationLevel getValidationLevel () {
    return m_eLevel;
  }

  @Nonnull
  public IValidationDocumentType getValidationDocumentType () {
    return m_aDocType;
  }

  @Nullable
  public Locale getValidationCountry () {
    return m_aCountry;
  }

  public boolean isValidationCountryIndependent () {
    return m_aCountry == null;
  }

  @Nonnull
  @ReturnsImmutableObject
  @Nonempty
  public Set <IValidationTransaction> getAllValidationTransactions () {
    return m_aTransactions;
  }

  @Nonnull
  @ReturnsMutableCopy
  @Nonempty
  public Set <ETransaction> getAllTransactions () {
    final Set <ETransaction> ret = new HashSet <ETransaction> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
      ret.add (aTransaction.getTransaction ());
    return ret;
  }

  public boolean containsTransaction (@Nullable final ETransaction eTransaction) {
    if (eTransaction != null)
      for (final IValidationTransaction aTransaction : m_aTransactions)
        if (aTransaction.getTransaction ().equals (eTransaction))
          return true;
    return false;
  }

  @Nullable
  public IReadableResource getValidationSchematronResource (@Nonnull final IValidationTransaction aTransaction) {
    if (aTransaction == null)
      throw new NullPointerException ("transaction");

    if (!m_aTransactions.contains (aTransaction)) {
      s_aLogger.warn ("Validation artifact does not contain transaction: " + aTransaction.getAsString ());
      return null;
    }

    // Assemble the file name:
    // 1. The directory name of this artefact
    // 2. Constant part "validation-xslt"
    // 3. The file name itself:
    // 3.1. The directory name of this artefact in uppercase
    // 3.2. The syntax binding
    // 3.3. The transaction ID
    final String sFileName = BASE_DIRECTORY +
                             m_sDirName +
                             "/schematron/" +
                             m_sFileNamePrefix +
                             '-' +
                             aTransaction.getSyntaxBinding ().getFileNamePart () +
                             '-' +
                             aTransaction.getTransaction () +
                             ".sch";
    return new ClassPathResource (sFileName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IReadableResource> getAllValidationSchematronResources () {
    final List <IReadableResource> aList = new ArrayList <IReadableResource> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
      aList.add (getValidationSchematronResource (aTransaction));
    return aList;
  }

  @Nullable
  public IReadableResource getValidationXSLTResource (@Nonnull final IValidationTransaction aTransaction) {
    if (aTransaction == null)
      throw new NullPointerException ("transaction");

    if (!m_aTransactions.contains (aTransaction)) {
      s_aLogger.warn ("Validation artifact does not contain transaction: " + aTransaction.getAsString ());
      return null;
    }

    // Assemble the file name:
    // 1. The directory name of this artefact
    // 2. Constant part "validation-xslt"
    // 3. The file name itself:
    // 3.1. The directory name of this artefact in uppercase
    // 3.2. The syntax binding
    // 3.3. The transaction ID
    final String sFileName = BASE_DIRECTORY +
                             m_sDirName +
                             "/validation-xslt/" +
                             m_sFileNamePrefix +
                             '-' +
                             aTransaction.getSyntaxBinding ().getFileNamePart () +
                             '-' +
                             aTransaction.getTransaction () +
                             ".xsl";
    return new ClassPathResource (sFileName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IReadableResource> getAllValidationXSLTResources () {
    final List <IReadableResource> aList = new ArrayList <IReadableResource> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
      aList.add (getValidationXSLTResource (aTransaction));
    return aList;
  }

  /**
   * Get all matching artefacts, in the correct order.
   * 
   * @param eLevel
   *        The desired validation level. If it is <code>null</code> all levels
   *        are considered.
   * @param eDocType
   *        The desired document type. If it is <code>null</code> all document
   *        types are considered.
   * @param aCountry
   *        The desired country. If the country of an artefact does not matter
   *        pass in <code>null</code>. If you want to have only the artefacts
   *        that are not country dependent and do NOT belong to a specific
   *        country, pass in {@link CGlobal#LOCALE_INDEPENDENT}.
   * @return A non-<code>null</code> list of all matching validation artefacts
   * @see EValidationLevel#canHaveCountrySpecificArtefacts()
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationArtefact> getAllMatchingArtefacts (@Nullable final EValidationLevel eLevel,
                                                                    @Nullable final IValidationDocumentType eDocType,
                                                                    @Nullable final Locale aCountry) {
    final List <EValidationArtefact> ret = new ArrayList <EValidationArtefact> ();
    for (final EValidationArtefact eArtefact : values ()) {
      // Does the level match?
      if (eLevel == null || eArtefact.getValidationLevel ().equals (eLevel)) {
        // Does the document type match?
        if (eDocType == null || eArtefact.getValidationDocumentType ().equals (eDocType)) {
          // Does the country match?
          if (!eArtefact.getValidationLevel ().canHaveCountrySpecificArtefacts () ||
              aCountry == null ||
              aCountry.equals (eArtefact.getValidationCountry ()))
            ret.add (eArtefact);
        }
      }
    }
    return ret;
  }

  /**
   * @return A set of all countries (Locale objects) for which at least one
   *         special artefact is contained. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <Locale> getAllCountriesWithSpecialRules () {
    return getAllCountriesWithSpecialRules (null, null);
  }

  /**
   * Get a set of all countries that have specific rules, matching the
   * parameters.
   * 
   * @param eLevel
   *        The desired validation level. If it is <code>null</code> all levels
   *        are considered.
   * @param eDocType
   *        The desired document type. If it is <code>null</code> all document
   *        types are considered.
   * @return A set of all countries (Locale objects) for which at least one
   *         special artefact is contained. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <Locale> getAllCountriesWithSpecialRules (@Nullable final EValidationLevel eLevel,
                                                              @Nullable final IValidationDocumentType eDocType) {
    final Set <Locale> ret = new HashSet <Locale> ();
    for (final IValidationArtefact eArtefact : values ()) {
      // Does the level match?
      if (eLevel == null || eArtefact.getValidationLevel ().equals (eLevel)) {
        // Does the document type match?
        if (eDocType == null || eArtefact.getValidationDocumentType ().equals (eDocType)) {
          if (!eArtefact.isValidationCountryIndependent ())
            ret.add (eArtefact.getValidationCountry ());
        }
      }
    }
    return ret;
  }

  /**
   * Get all artefacts that have rules for the specified transaction.
   * 
   * @param eTransaction
   *        The transaction to search. May be <code>null</code>.
   * @return A non-<code>null</code> list with all artefacts supporting the
   *         specified transaction.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationArtefact> getAllArtefactsForTransaction (@Nullable final ETransaction eTransaction) {
    final List <EValidationArtefact> ret = new ArrayList <EValidationArtefact> ();
    for (final EValidationArtefact eArtefact : values ())
      if (eArtefact.containsTransaction (eTransaction))
        ret.add (eArtefact);
    return ret;
  }
}

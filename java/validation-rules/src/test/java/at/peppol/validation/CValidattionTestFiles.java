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
package at.peppol.validation;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.collections.ContainerHelper;

/**
 * Contains a list of the validation test files.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class CValidattionTestFiles {
  public static final String PATH_CATALOGUE_TESTFILES = "/test-catalogues/";
  public static final String PATH_INVOICE_TESTFILES = "/test-invoices/";
  public static final String PATH_ORDER_TESTFILES = "/test-orders/";
  public static final String PATH_TENDERINGCATALOGUE_TESTFILES = "/test-tenderingcatalogues/";

  public static final String PATH_SUCCESS = "success/";
  public static final String PATH_ERROR = "error/";

  public static final List <String> TEST_CATALOGUES_SUCCESS = ContainerHelper.newUnmodifiableList ("Consip_Catalogo_UBL.xml");
  public static final List <String> TEST_CREDITNOTES_SUCCESS = ContainerHelper.newUnmodifiableList ("BII05 TRDM014 example is.xml");
  public static final List <String> TEST_INVOICES_SUCCESS = ContainerHelper.newUnmodifiableList ("BII04 minimal invoice example 03.xml",
                                                                                                 "BII04 minimal invoice wo addr id.xml",
                                                                                                 "BII04 minimal VAT invoice example 02.xml",
                                                                                                 "BII04 XML example full core data 01.xml",
                                                                                                 "BII04 XML example full core data 02.xml",
                                                                                                 "invoice-it-c98fe7f7-8b46-4972-b61b-3b8824f16658.xml",
                                                                                                 "invoice-it-uuid8a69941e-52ae-4cbb-b284-a3a78fb89d07.xml",
                                                                                                 "PEP BII04 minimal invoice example 03.xml",
                                                                                                 "PEP BII04 minimal Reverce Charge VAT invoice example 01.xml",
                                                                                                 "PEP BII04 minimal Reverce Charge VAT invoice example no line 01.xml",
                                                                                                 "PEP BII04 minimal VAT invoice example 02.xml",
                                                                                                 "SubmitInvoice.008660-AA.b1478257-5bd1-4756-bd20-3262afb22923.xml",
                                                                                                 "TC10.3.TS1.xml",
                                                                                                 "TC10.4.TS1.xml",
                                                                                                 "TC10.15.TS1.xml",
                                                                                                 "test-invoice.xml");
  public static final List <String> TEST_INVOICES_AT_SUCCESS = ContainerHelper.newUnmodifiableList ("at-ubl-42-8.xml");
  public static final List <String> TEST_ORDERS_SUCCESS = ContainerHelper.newUnmodifiableList ("ADVORD_03_03_00_Order_v2p2.xml",
                                                                                               "BII03 Order example 01.xml",
                                                                                               "PEPPOL BIS-3a-FULL.xml",
                                                                                               "PEPPOL BIS-3a-Small.xml",
                                                                                               "TC01.0.TS1.xml",
                                                                                               "test-order.xml",
                                                                                               "UBL-Order-2.0-Example-International.xml",
                                                                                               "UBL-Order-2.0-Example.xml");
  public static final List <String> TEST_TENDERINGCATALOGUES_SUCCESS = ContainerHelper.newUnmodifiableList ("Tender-Sample.xml");
  public static final List <String> TEST_CALLFORTENDERS_SUCCESS = ContainerHelper.newUnmodifiableList ();
  public static final List <String> TEST_TENDER_SUCCESS = ContainerHelper.newUnmodifiableList ();

  private CValidattionTestFiles () {}
}

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
package at.peppol.webgui.app.components;

import javax.xml.transform.Source;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProfileIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UBLVersionIDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;

import org.slf4j.LoggerFactory;

import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;
//import at.peppol.commons.cenbii.profiles.EProfile;
//import at.peppol.commons.cenbii.profiles.ETransaction;
import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
//import at.peppol.validation.pyramid.ValidationPyramid;
//import at.peppol.validation.pyramid.ValidationPyramidResultLayer;
//import at.peppol.validation.rules.EValidationDocumentType;
//import at.peppol.validation.rules.ValidationTransaction;
import at.peppol.webgui.app.components.adapters.InvoiceLineAdapter;
import at.peppol.webgui.app.validator.ValidatorHandler;
import at.peppol.webgui.app.validator.ValidatorsList;
import at.peppol.webgui.app.validator.global.GlobalValidationsRegistry;
import at.peppol.webgui.app.validator.global.ValidationError;
import at.peppol.commons.identifier.process.EPredefinedProcessIdentifier;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.state.ESuccess;
import com.phloc.ubl.AbstractUBLDocumentMarshaller;
import com.phloc.ubl.EUBL20DocumentType;
import com.phloc.ubl.UBL20DocumentMarshaller;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings ("serial")
public class InvoiceTabForm extends Form {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger (TabInvoiceHeader.class);
  private final ObjectFactory invObjFactory;

  private InvoiceType invoice;

  private TabInvoiceHeader tTabInvoiceHeader;
  private TabInvoiceLine tTabInvoiceLine;
  private TabInvoiceDelivery tTabInvoiceDelivery;
  private TabInvoicePayment tTabInvoicePayment;
  private TabInvoiceAllowanceCharge tTabInvoiceAllowanceCharge;
  private TabInvoiceTaxTotal tTabInvoiceTaxTotal;
  private TabInvoiceMonetaryTotal tTabInvoiceMonetaryTotal;

  // TODO: Naming convension as tTabInvoiceCustomerParty,
  // tTabInvoiceSupplierParty
  private CustomerPartyType customer;
  private SupplierPartyType supplier;
  private PartyDetailForm supplierForm;
  private PartyDetailForm customerForm;

  // GUI related
  private GridLayout mainLayout;
  private TabSheet invTabSheet;

  /**
   * The constructor
   */
  public InvoiceTabForm () {
    invObjFactory = new ObjectFactory ();
    initInvoiceData ();
    initElements ();
    buildMainLayout ();
    GlobalValidationsRegistry.setMainComponents(this, invoice);
  }
  
  public TabInvoiceLine getInvoiceLineTab() {
	  return tTabInvoiceLine;
  }
  public TabInvoiceHeader getTabInvoiceHeader() {
	  return tTabInvoiceHeader;
  }
  public TabInvoiceDelivery gettTabInvoiceDelivery() {
	  return tTabInvoiceDelivery;
  }
  public TabInvoicePayment getTabInvoicePayment() {
	  return tTabInvoicePayment;
  }
  public TabInvoiceAllowanceCharge getTabInvoiceAllowanceCharge() {
	  return tTabInvoiceAllowanceCharge;
  }
  public TabInvoiceTaxTotal getTabInvoiceTaxTotal() {
	  return tTabInvoiceTaxTotal;
  }
  public PartyDetailForm getSupplierForm() {
	  return supplierForm;
  }
  public PartyDetailForm getCustomerForm() {
	  return customerForm;
  }

  private void initInvoiceData () {
    invoice = invObjFactory.createInvoiceType ();

    tTabInvoiceHeader = new TabInvoiceHeader (this);
    tTabInvoiceLine = new TabInvoiceLine (this);
    tTabInvoiceDelivery = new TabInvoiceDelivery (this);
    tTabInvoicePayment = new TabInvoicePayment (this);
    tTabInvoiceAllowanceCharge = new TabInvoiceAllowanceCharge (this);
    tTabInvoiceTaxTotal = new TabInvoiceTaxTotal (this);
    tTabInvoiceMonetaryTotal = new TabInvoiceMonetaryTotal (this);

    supplier = new SupplierPartyType ();
    supplier.setParty (new PartyType ());

    customer = new CustomerPartyType ();
    customer.setParty (new PartyType ());

    // Standard input, not using user input
    final UBLVersionIDType version = new UBLVersionIDType ();
    version.setValue ("2.0");

    // Use PEPPOL Codelists
    final CustomizationIDType custID = new CustomizationIDType ();
    custID.setValue (EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getTransactionID ());
    custID.setSchemeID ("PEPPOL");
    
    ProfileIDType profileID = new ProfileIDType();
    profileID.setValue(EPredefinedProcessIdentifier.BIS4A.getValue());
    invoice.setProfileID(profileID);

    // Setting invoice type code to 380: Commercial Invoice
    invoice.setInvoiceTypeCode (new InvoiceTypeCodeType ());
    invoice.getInvoiceTypeCode ().setValue ("380");

    invoice.setUBLVersionID (version);
    invoice.setCustomizationID (custID);
    invoice.setID (new IDType ());
    
    invoice.setAccountingCustomerParty (customer);
    invoice.setAccountingSupplierParty (supplier);
    // invoice.setLegalMonetaryTotal(new MonetaryTotalType());
  }

  private void initElements () {
    supplierForm = new PartyDetailForm ("Supplier", supplier.getParty ());
    supplierForm.setImmediate(true);
    customerForm = new PartyDetailForm ("Customer", customer.getParty ());
    customerForm.setImmediate(true);
    //supplierForm.setSizeFull ();
    //customerForm.setSizeFull ();

    final HorizontalLayout footerLayout = new HorizontalLayout ();
    footerLayout.setSpacing (true);
    footerLayout.setMargin (true);
    footerLayout.addComponent (new Button ("Validate Invoice", new Button.ClickListener () {

      @Override
      public void buttonClick (final Button.ClickEvent event) {
    	  SetCommonCurrency ();
          ValidatorHandler vh = new ValidatorHandler(footerLayout);
          AbstractUBLDocumentMarshaller.setGlobalValidationEventHandler (vh);
          vh.clearErrors();
          clearTabErrorStyles();
         
          List<ValidationError> errors = GlobalValidationsRegistry.runAll();
          if (errors.size() > 0) {
        	  Window errorWindow = new Window("Errors");
        	  //position and size of the window
        	  errorWindow.setPositionX(200);
        	  errorWindow.setPositionY(200);
        	  errorWindow.setWidth("600px");
        	  errorWindow.setHeight("300px");
        	  
        	  //add the error messages
        	  errorWindow.addComponent(new Label("<ol>", Label.CONTENT_XHTML));
        	  for (int i=0;i<errors.size();i++) {
        		  String errorMessage = errors.get(i).getRuleID()+": "+errors.get(i).getErrorInfo();
        		  errorWindow.addComponent(new Label("<li style=\"margin-top: 3px;\">"+errorMessage+"</li>", Label.CONTENT_XHTML));
        		  
        		  //mark the appropriate Tab as error
        		  Tab tab = invTabSheet.getTab(errors.get(i).getMainComponent());
        		  if (tab != null)
        			  tab.setStyleName("test111");
        	  }
        	  errorWindow.addComponent(new Label("</ol>", Label.CONTENT_XHTML));
        	  
        	  //show the error window
        	  getParent().getWindow().addWindow(errorWindow);
        	  errors.clear();
          }
          
          UBL20DocumentMarshaller.writeInvoice(invoice, new StreamResult(new
       			  File("invoice.xml")));
          
          /*PEPPOL validation
           * final ValidationPyramid vp = new ValidationPyramid (EValidationDocumentType.INVOICE,
                  ValidationTransaction.createUBLTransaction (ETransaction.T10));
	     	final List <ValidationPyramidResultLayer> aResults = vp.applyValidation (new FileSystemResource ("invoice.xml"))
	              .getAllValidationResultLayers ();
	     	if (aResults.isEmpty ())
	          System.out.println ("  The document is valid!");
	        else
	          for (final ValidationPyramidResultLayer aResultLayer : aResults)
	            for (final IResourceError aError : aResultLayer.getValidationErrors ())
	              System.out.println ("  " + aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
         */	
          //InvoiceTabForm.this.invTabSheet.getTab(supplierForm).setCaption(caption)
          ValidatorsList.validateListenersNotify();
          if (ValidatorsList.validateListeners() == false) {
        	  getParent().getWindow().showNotification("Validation error... ",Notification.TYPE_TRAY_NOTIFICATION);
          }
          else
        	  getParent().getWindow().showNotification("Validation passed! ",Notification.TYPE_TRAY_NOTIFICATION);
        	  
      }
    }));

    footerLayout.addComponent (new Button ("Save Invoice", new Button.ClickListener () {

      @Override
      public void buttonClick (final Button.ClickEvent event) {
        try {
          SetCommonCurrency ();          
          System.out.println (invoice.getDelivery ().get (0).getDeliveryAddress ().getStreetName ().getValue ());
        }
        catch (final Exception ex) {
          LOGGER.error ("Error creating files. ", ex);
        }
      }
    }));
    
    footerLayout.addComponent (new Button ("Read Invoice from disk", new Button.ClickListener () {

        @Override
        public void buttonClick (final Button.ClickEvent event) {
          try {
            SetCommonCurrency ();
            InvoiceType inv = UBL20DocumentMarshaller.readInvoice(new StreamSource(new FileInputStream(new File("invoice.xml"))));
          }
          catch (final Exception ex) {
            LOGGER.error ("Error creating files. ", ex);
          }
        }
      }));

    
    getFooter ().addComponent (footerLayout);
     
  }

  private GridLayout buildMainLayout () {
    // common part: create layout
    mainLayout = new GridLayout ();
    mainLayout.setImmediate (true);
    mainLayout.setWidth ("100%");
    mainLayout.setHeight ("100%");
    mainLayout.setMargin (false);

    // top-level component properties
    setWidth ("100.0%");
    setHeight ("100.0%");

    // set form layout
    setLayout (mainLayout);

    // invTabSheet
    invTabSheet = new TabSheet ();
    invTabSheet.setImmediate (true);
    invTabSheet.setWidth ("100.0%");
    invTabSheet.setHeight ("100.0%");

    invTabSheet.addTab (tTabInvoiceHeader, "Invoice Header");
    invTabSheet.addTab (supplierForm, "Supplier Party");
    invTabSheet.addTab (customerForm, "Customer Party");
    // invTabSheet.addTab (new
    // Label("move payee party here? or merge all parties here!"),
    // "Payee Party");
    invTabSheet.addTab (tTabInvoiceDelivery, "Delivery");
    invTabSheet.addTab (tTabInvoicePayment, "Payment");
    invTabSheet.addTab (tTabInvoiceAllowanceCharge, "Allowance/Charge");
    invTabSheet.addTab (tTabInvoiceLine, "Invoice Lines");
    invTabSheet.addTab (tTabInvoiceTaxTotal, "Tax Total");
    invTabSheet.addTab (tTabInvoiceMonetaryTotal, "Monetary Total");

    mainLayout.addComponent (invTabSheet, 0, 0);

    //BIIRULE-T10-R011
    tTabInvoiceLine.getTable().addListener(new LinesTotalAmountListener());
     
    //BIIRULE-T10-R012
    TaxExclusiveAmountListener taxExclusiveAmountListener = new TaxExclusiveAmountListener();
    tTabInvoiceLine.getTable().addListener(taxExclusiveAmountListener);
    tTabInvoiceAllowanceCharge.getTable().addListener(taxExclusiveAmountListener);
    
    //BIIRULE-T10-R013
    TaxInclusiveAmountListener taxInclusiveAmountListener = new TaxInclusiveAmountListener();
    tTabInvoiceTaxTotal.getInvoiceTaxTotalTopForm().getField(TabInvoiceTaxTotal.taxTotalAmount).addListener(
    		taxInclusiveAmountListener);
    tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(TabInvoiceMonetaryTotal.taxExclusiveAmount).addListener(
    		taxInclusiveAmountListener);
    
    //BIIRULE-T10-R015 & BIIRULE-T10-R016 
    BIIRULE_T10_R015_R016 biirule_t10_r015_r016 = new BIIRULE_T10_R015_R016();
    tTabInvoiceLine.getTable().addListener(biirule_t10_r015_r016);
    tTabInvoiceAllowanceCharge.getTable().addListener(biirule_t10_r015_r016);
    
    //BIIRULE-T10-R017
    BIIRULE_T10_R017 biirule_t10_r017 = new BIIRULE_T10_R017();
    tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(TabInvoiceMonetaryTotal.taxInclusiveAmount).addListener(
    		biirule_t10_r017);
    tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(TabInvoiceMonetaryTotal.prepaidAmount).addListener(
    		biirule_t10_r017);
    
    return mainLayout;
  }

  public InvoiceType getInvoice () {
    return this.invoice;
  }

  public JAXBElement <InvoiceType> getInvoiceAsJAXB () {
    return invObjFactory.createInvoice (invoice);
  }
  
  public void clearTabErrorStyles() {
	  int position = 0;
	  while (true) {
		  Tab tab = invTabSheet.getTab(position++);
		  if (tab == null)
			  break;
		  
		  tab.setStyleName("");
	  }
  }

  public void SetCommonCurrency () {
    final CurrencyCodeContentType cur = CurrencyCodeContentType.valueOf (invoice.getDocumentCurrencyCode ().getValue ());
    // monetary total
    invoice.getLegalMonetaryTotal ().getLineExtensionAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getTaxExclusiveAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getTaxInclusiveAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getAllowanceTotalAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getChargeTotalAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getPrepaidAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getPayableRoundingAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal ().getPayableAmount ().setCurrencyID (cur);

    // tax total
    invoice.getTaxTotal ()
           .get (0)
           .getTaxAmount ()
           .setCurrencyID (CurrencyCodeContentType.valueOf (invoice.getDocumentCurrencyCode ().getValue ()));
    final List <TaxSubtotalType> taxSubtotalList = invoice.getTaxTotal ().get (0).getTaxSubtotal ();
    final Iterator <TaxSubtotalType> iterator = taxSubtotalList.iterator ();
    while (iterator.hasNext ()) {
      final TaxSubtotalType ac = iterator.next ();
      ac.getTaxAmount ().setCurrencyID (cur);
      ac.getTaxableAmount ().setCurrencyID (cur);
    }
    
    Collection<?> col = tTabInvoiceLine.getTable().getContainerDataSource().getItemIds(); 
    for (Object itemId : col) {
    	System.out.println("Table item id: "+itemId);
    	tTabInvoiceLine.getTable().getContainerDataSource().getItem(itemId).
    		getItemProperty("CommonCurrency").setValue(cur);
    }
    
    // lines
    final List <InvoiceLineType> invoiceLineList = invoice.getInvoiceLine ();
    final Iterator <InvoiceLineType> iter = invoiceLineList.iterator ();
    while (iter.hasNext ()) {
      final InvoiceLineType il = iter.next ();
      il.getLineExtensionAmount ().setCurrencyID (cur);
      il.getTaxTotal ().get (0).getTaxAmount ().setCurrencyID (cur);
      il.getPrice ().getPriceAmount ().setCurrencyID (cur);
      il.getPrice ().getAllowanceCharge ().get (0).getAmount ().setCurrencyID (cur);
      il.getPrice ().getAllowanceCharge ().get (0).getBaseAmount ().setCurrencyID (cur);
    }
  }
  
/*  public void linesTotalAmountListener(ItemSetChangeEvent event) {
	  Field lineTotalField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField("Line Extension Amount");
	   
	  BigDecimal total = new BigDecimal(0.0);
	  List<InvoiceLineType> list = tTabInvoiceLine.getInvoiceLineList();
	  for (InvoiceLineType line : list) {
		  total = total.add(line.getLineExtensionAmount().getValue());
	  }
	  System.out.println("Total is "+total.floatValue());
	  
	  lineTotalField.setValue(total);
  }*/
  
  public class LinesTotalAmountListener implements ItemSetChangeListener {
	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		Field lineTotalField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
											 TabInvoiceMonetaryTotal.lineExtensionAmount);
		
		BigDecimal total = new BigDecimal(0.0);
		//List<InvoiceLineType> list = tTabInvoiceLine.getInvoiceLineList();
		List<InvoiceLineType> list = invoice.getInvoiceLine();
		for (InvoiceLineType line : list) {
			total = total.add(line.getLineExtensionAmount().getValue());
		}
		lineTotalField.setValue(total);
	}
  }
  
  public class TaxExclusiveAmountListener implements ItemSetChangeListener {
	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		Field taxExclusiveField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
											  TabInvoiceMonetaryTotal.taxExclusiveAmount);
		   
		BigDecimal lineTotal = new BigDecimal(0.0);
		List<InvoiceLineType> list = invoice.getInvoiceLine();
		for (InvoiceLineType line : list) {
			lineTotal = lineTotal.add(line.getLineExtensionAmount().getValue());
		}
		
		BigDecimal chargesTotal = new BigDecimal(0.0);
		BigDecimal allowancesTotal = new BigDecimal(0.0);
		List<AllowanceChargeType> list2 = invoice.getAllowanceCharge();
		for (AllowanceChargeType ac : list2) {
			if (ac.getChargeIndicator().isValue()) {
				chargesTotal = chargesTotal.add(ac.getAmount().getValue());
			}
			else {
				allowancesTotal = allowancesTotal.add(ac.getAmount().getValue());
			}
		}
		
		BigDecimal result = lineTotal.add(chargesTotal).subtract(allowancesTotal);
		if (result.doubleValue() < 0)
			taxExclusiveField.setValue(new BigDecimal(0.0));
		else
			taxExclusiveField.setValue(result);
	}
  }
  
  public class TaxInclusiveAmountListener implements ValueChangeListener {
	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		// TODO Auto-generated method stub
		Field taxInclusiveAmountField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
				  									   TabInvoiceMonetaryTotal.taxInclusiveAmount);
	
		BigDecimal taxEx = invoice.getLegalMonetaryTotal().getTaxExclusiveAmount().getValue();
		BigDecimal taxTot = invoice.getTaxTotal().get(0).getTaxAmount().getValue();
		
		taxInclusiveAmountField.setValue(taxEx.add(taxTot));
	}
	  
  }
  
  public class BIIRULE_T10_R015_R016 implements ItemSetChangeListener {

	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		Field totalAllowanceField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
				   TabInvoiceMonetaryTotal.allowanceTotalAmount);
		Field totalChargeField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
				   TabInvoiceMonetaryTotal.chargeTotalAmount);
		
		List<AllowanceChargeType> invoiceAllowanceCharge = invoice.getAllowanceCharge();
		BigDecimal allowancesTotal = new BigDecimal(0.0);
		BigDecimal chargesTotal = new BigDecimal(0.0);
		for (AllowanceChargeType ac : invoiceAllowanceCharge) {
			if (!ac.getChargeIndicator().isValue()) {
				allowancesTotal = allowancesTotal.add(ac.getAmount().getValue());
			}
			else {
				chargesTotal = chargesTotal.add(ac.getAmount().getValue());
			}
		}
		
		List<InvoiceLineType> lines = invoice.getInvoiceLine();
		for (InvoiceLineType line : lines) {
			List<AllowanceChargeType> lineAllowanceCharge = line.getAllowanceCharge();
			for (AllowanceChargeType ac : lineAllowanceCharge) {
				if (!ac.getChargeIndicator().isValue()) {
					allowancesTotal = allowancesTotal.add(ac.getAmount().getValue());
				}
				else {
					chargesTotal = chargesTotal.add(ac.getAmount().getValue());
				}
			}
		}
		
		totalAllowanceField.setValue(allowancesTotal);
		totalChargeField.setValue(chargesTotal);
	}
	  
  }
  
  public class BIIRULE_T10_R017 implements ValueChangeListener {

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		Field payableAmountField = tTabInvoiceMonetaryTotal.getMonetaryTotalForm().getField(
				   TabInvoiceMonetaryTotal.payableAmount);
		
		BigDecimal payable = new BigDecimal(0.0);
		payable = payable.add(invoice.getLegalMonetaryTotal().getTaxInclusiveAmount().getValue())
						 .subtract(invoice.getLegalMonetaryTotal().getPrepaidAmount().getValue());
		
		if (payable.doubleValue() < 0)
			payableAmountField.setValue(new BigDecimal(0.00));
		else
			payableAmountField.setValue(payable);
		
	}
	  
  }
}

package at.peppol.webgui.app.components;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamResult;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UBLVersionIDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;

import org.slf4j.LoggerFactory;

import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;

import com.phloc.ubl.AbstractUBLDocumentMarshaller;
import com.phloc.ubl.UBL20DocumentMarshaller;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceTabForm extends Form {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TabInvoiceHeader.class);
  private final ObjectFactory invObjFactory;
  
  private InvoiceType invoice;
  
  private TabInvoiceHeader tTabInvoiceHeader;
  private TabInvoiceLine tTabInvoiceLine;
  private TabInvoiceDelivery tTabInvoiceDelivery;
  private TabInvoicePayment tTabInvoicePayment;
  private TabInvoiceAllowanceCharge tTabInvoiceAllowanceCharge;
  private TabInvoiceTaxTotal tTabInvoiceTaxTotal;
  private TabInvoiceMonetaryTotal tTabInvoiceMonetaryTotal;
  
  //TODO: Naming convension as tTabInvoiceCustomerParty, tTabInvoiceSupplierParty
  private CustomerPartyType customer;
  private SupplierPartyType supplier;
  private PartyDetailForm supplierForm;
  private PartyDetailForm customerForm;
  
  //GUI related
  private GridLayout mainLayout;
  private TabSheet invTabSheet;

  /**
   * The constructor 
   */
  public InvoiceTabForm () {
    invObjFactory = new ObjectFactory();
    initInvoiceData();
    initElements();
    buildMainLayout();
  }

  private void initInvoiceData() {
    invoice = invObjFactory.createInvoiceType();
    
    tTabInvoiceHeader = new TabInvoiceHeader(this);
    tTabInvoiceLine = new TabInvoiceLine(this);
    tTabInvoiceDelivery = new TabInvoiceDelivery(this);
    tTabInvoicePayment = new TabInvoicePayment(this);
    tTabInvoiceAllowanceCharge = new TabInvoiceAllowanceCharge(this);
    tTabInvoiceTaxTotal = new TabInvoiceTaxTotal(this);
    tTabInvoiceMonetaryTotal = new TabInvoiceMonetaryTotal(this);
    
    supplier = new SupplierPartyType();
    supplier.setParty(new PartyType());

    customer = new CustomerPartyType();
    customer.setParty(new PartyType());
    
    // Standard input, not using user input
    final UBLVersionIDType version = new UBLVersionIDType();
    version.setValue("2.0");

    // Use PEPPOL Codelists
    final CustomizationIDType custID = new CustomizationIDType();
    custID.setValue(EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getTransactionID());
    custID.setSchemeID("PEPPOL");

    // Setting invoice type code to 380: Commercial Invoice
    invoice.setInvoiceTypeCode(new InvoiceTypeCodeType());
    invoice.getInvoiceTypeCode().setValue("380");

    invoice.setUBLVersionID(version);
    invoice.setCustomizationID(custID);
    invoice.setID(new IDType());

    invoice.setAccountingCustomerParty(customer);
    invoice.setAccountingSupplierParty(supplier);
    //invoice.setLegalMonetaryTotal(new MonetaryTotalType());
   
  }  
  
  private void initElements () {
    supplierForm = new PartyDetailForm("Supplier", supplier.getParty());
    customerForm = new PartyDetailForm("Customer", customer.getParty());
    supplierForm.setSizeFull();
    customerForm.setSizeFull();
    
    HorizontalLayout footerLayout = new HorizontalLayout ();
    footerLayout.setSpacing (true);
    footerLayout.setMargin (true);
    footerLayout.addComponent(new Button("Validate Invoice", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
          try {
              SetCommonCurrency();
              AbstractUBLDocumentMarshaller.setGlobalValidationEventHandler(null);
              UBL20DocumentMarshaller.writeInvoice(invoice, new StreamResult(new OutputStreamWriter(System.out)));
              //ByteArrayOutputStream baos = new ByteArrayOutputStream ();
              //UBL20DocumentMarshaller.writeInvoice(invoice, new StreamResult(new OutputStreamWriter(baos)));
              //getParent().getWindow ().showNotification("Info", baos.toString (), Window.Notification.TYPE_HUMANIZED_MESSAGE);
          } catch (Exception ex) {
              getParent().getWindow ().showNotification("Error", ex.getMessage (), Window.Notification.TYPE_HUMANIZED_MESSAGE);
              LOGGER.error("Error creating files. ", ex);
          }
      }
    }));    
    
    footerLayout.addComponent(new Button("Save Invoice", new Button.ClickListener() {

      @Override
      public void buttonClick(final Button.ClickEvent event) {
          try {
              SetCommonCurrency();
              System.out.println(invoice.getDelivery ().get (0).getDeliveryAddress ().getStreetName ().getValue ());
          } catch (final Exception ex) {
              LOGGER.error("Error creating files. ", ex);
          }
      }
    }));    
    getFooter().addComponent (footerLayout);
  }

  private GridLayout buildMainLayout () {
    // common part: create layout
    mainLayout = new GridLayout ();
    mainLayout.setImmediate (false);
    mainLayout.setWidth ("100%");
    mainLayout.setHeight ("100%");
    mainLayout.setMargin (false);
    
    // top-level component properties
    setWidth ("100.0%");
    setHeight ("100.0%");
    
    //set form layout
    setLayout(mainLayout);
    
    // invTabSheet
    invTabSheet = new TabSheet ();
    invTabSheet.setImmediate (false);
    invTabSheet.setWidth ("100.0%");
    invTabSheet.setHeight ("100.0%");
    
    invTabSheet.addTab (tTabInvoiceHeader, "Invoice Header");
    invTabSheet.addTab (supplierForm, "Supplier Party");
    invTabSheet.addTab (customerForm, "Customer Party");
    //invTabSheet.addTab (new Label("move payee party here? or merge all parties here!"), "Payee Party");
    invTabSheet.addTab (tTabInvoiceDelivery, "Delivery");
    invTabSheet.addTab (tTabInvoicePayment, "Payment");
    invTabSheet.addTab (tTabInvoiceAllowanceCharge, "Allowance/Charge");
    invTabSheet.addTab (tTabInvoiceLine, "Invoice Lines");    
    invTabSheet.addTab (tTabInvoiceTaxTotal, "Tax Total");
    invTabSheet.addTab (tTabInvoiceMonetaryTotal, "Monetary Total");
    
    mainLayout.addComponent (invTabSheet, 0, 0);
    
    return mainLayout;
  }

  public InvoiceType getInvoice() {
    return this.invoice;
  }
  
  public JAXBElement<InvoiceType> getInvoiceAsJAXB() {
    return invObjFactory.createInvoice(invoice);
  }  

  public void SetCommonCurrency () {
    CurrencyCodeContentType cur = CurrencyCodeContentType.valueOf (invoice.getDocumentCurrencyCode ().getValue ());
    //monetary total
    invoice.getLegalMonetaryTotal().getLineExtensionAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getTaxExclusiveAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getTaxInclusiveAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getAllowanceTotalAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getChargeTotalAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getPrepaidAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getPayableRoundingAmount ().setCurrencyID (cur);
    invoice.getLegalMonetaryTotal().getPayableAmount ().setCurrencyID (cur);
  
    //tax total
    invoice.getTaxTotal ().get (0).getTaxAmount ().setCurrencyID (CurrencyCodeContentType.valueOf (invoice.getDocumentCurrencyCode ().getValue ()));
    List <TaxSubtotalType> taxSubtotalList = invoice.getTaxTotal ().get (0).getTaxSubtotal ();
    Iterator <TaxSubtotalType> iterator = taxSubtotalList.iterator ();
    while (iterator.hasNext()) {
      TaxSubtotalType ac = (TaxSubtotalType) iterator.next();
      ac.getTaxAmount ().setCurrencyID (cur);
      ac.getTaxableAmount ().setCurrencyID (cur);
    }
    
    //lines
    List<InvoiceLineType> invoiceLineList = invoice.getInvoiceLine ();
    Iterator <InvoiceLineType> iter = invoiceLineList.iterator ();
    while (iter.hasNext()) {
      InvoiceLineType il = (InvoiceLineType) iter.next();
      il.getLineExtensionAmount ().setCurrencyID (cur);
      il.getTaxTotal ().get (0).getTaxAmount ().setCurrencyID (cur);
      il.getPrice ().getPriceAmount ().setCurrencyID (cur);
      il.getPrice ().getAllowanceCharge ().get (0).getAmount ().setCurrencyID (cur);
      il.getPrice ().getAllowanceCharge ().get (0).getBaseAmount ().setCurrencyID (cur);
    }    
    
  }
}

package at.peppol.webgui.app.components;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamResult;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomizationIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoiceTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.InvoicedQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UBLVersionIDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;

import org.slf4j.LoggerFactory;

import at.peppol.commons.identifier.docid.EPredefinedDocumentTypeIdentifier;

import com.phloc.ubl.AbstractUBLDocumentMarshaller;
import com.phloc.ubl.UBL20DocumentMarshaller;
import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Jerouris
 */
public class InvoiceForm extends Form {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger (InvoiceForm.class);
  private final ObjectFactory invObjFactory;

  private InvoiceType invoice;
  private CustomerPartyType customer;
  private SupplierPartyType supplier;

  public InvoiceForm () {
    invObjFactory = new ObjectFactory ();
    initInvoiceData ();
    initElements ();
  }

  private void initInvoiceData () {
    invoice = invObjFactory.createInvoiceType ();

    // Standard input, not using user input
    final UBLVersionIDType version = new UBLVersionIDType ();
    version.setValue ("2.0");

    // Use PEPPOL Codelists
    final CustomizationIDType custID = new CustomizationIDType ();
    custID.setValue (EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getTransactionID ());
    custID.setSchemeID ("PEPPOL");

    supplier = new SupplierPartyType ();
    supplier.setParty (new PartyType ());

    customer = new CustomerPartyType ();
    customer.setParty (new PartyType ());

    // Setting invoice type code to 380: Commercial Invoice
    invoice.setInvoiceTypeCode (new InvoiceTypeCodeType ());
    invoice.getInvoiceTypeCode ().setValue ("380");

    invoice.setUBLVersionID (version);
    invoice.setCustomizationID (custID);
    invoice.setID (new IDType ());

    invoice.setAccountingCustomerParty (customer);
    invoice.setAccountingSupplierParty (supplier);
    final List <InvoiceLineType> items = invoice.getInvoiceLine ();
    items.add (createInvoiceLine ());
  }

  public JAXBElement <InvoiceType> getInvoice () {

    return invObjFactory.createInvoice (invoice);
  }

  private InvoiceLineType createInvoiceLine () {
    final InvoiceLineType il = new InvoiceLineType ();
    il.setID (new IDType ());
    il.getID ().setValue ("1");
    il.setInvoicedQuantity (new InvoicedQuantityType ());
    il.getInvoicedQuantity ().setValue (BigDecimal.ZERO);

    return il;
  }

  private void initElements () {

    final GridLayout grid = new GridLayout (2, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();
    final Panel outerPanel = new Panel ("Invoice Creation Form");
    outerPanel.addComponent (grid);
    outerLayout.addComponent (outerPanel);
    setLayout (outerLayout);
    grid.setStyleName ("margin");
    grid.setSpacing (true);

    final Panel invoiceDetailsPanel = new Panel ("Invoice Details");
    invoiceDetailsPanel.setStyleName ("light");
    invoiceDetailsPanel.setSizeFull ();
    invoiceDetailsPanel.addComponent (createInvoiceTopForm ());
    grid.addComponent (invoiceDetailsPanel, 0, 0, 1, 0);
    final PartyDetailForm supplierForm = new PartyDetailForm ("Supplier", supplier.getParty ());
    final PartyDetailForm customerForm = new PartyDetailForm ("Customer", customer.getParty ());
    grid.addComponent (supplierForm, 0, 1);
    grid.addComponent (customerForm, 1, 1);
    grid.setSizeUndefined ();
    getFooter ().addComponent (new Button ("Check", new Button.ClickListener () {

      @Override
      public void buttonClick (final Button.ClickEvent event) {
        try {
          AbstractUBLDocumentMarshaller.setGlobalValidationEventHandler (null);
          UBL20DocumentMarshaller.writeInvoice (invoice, new StreamResult (new OutputStreamWriter (System.out)));
        }
        catch (final Exception ex) {
          LOGGER.error ("Error creating files. ", ex);
        }

      }
    }));

    outerPanel.requestRepaintAll ();

  }

  public Form createInvoiceTopForm () {

    final Form invoiceTopForm = new Form (new FormLayout (), new InvoiceFieldFactory ());
    invoiceTopForm.setImmediate (true);

    invoice.setID (new IDType ());
    invoiceTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (invoice.getID (), "value"));

    invoice.setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    // invoice.getDocumentCurrencyCode().setValue("EUR");

    invoice.setIssueDate (new IssueDateType ());
    invoiceTopForm.addItemProperty ("Currency", new NestedMethodProperty (invoice.getDocumentCurrencyCode (), "value"));

    final Date issueDate = new Date ();
    invoiceTopForm.addItemProperty ("Issue Date", new ObjectProperty <Date> (issueDate));
    return invoiceTopForm;
  }

  class InvoiceFieldFactory implements FormFieldFactory {

    @Override
    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Currency".equals (pid)) {
        final CurrencySelect curSelect = new CurrencySelect ("Currency");
        return curSelect;
      }

      if ("Issue Date".equals (pid)) {
        final PopupDateField issueDateField = new PopupDateField ("Issue Date");
        issueDateField.setValue (new Date ());
        issueDateField.setResolution (DateField.RESOLUTION_DAY);
        issueDateField.addListener (new ValueChangeListener () {

          @Override
          public void valueChange (final com.vaadin.data.Property.ValueChangeEvent event) {
            try {

              final Date issueDate = (Date) issueDateField.getValue ();
              final GregorianCalendar greg = new GregorianCalendar ();
              greg.setTime (issueDate);

              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance ().newXMLGregorianCalendar ();
              XMLDate.setYear (greg.get (Calendar.YEAR));
              XMLDate.setMonth (greg.get (Calendar.MONTH) + 1);
              XMLDate.setDay (greg.get (Calendar.DATE));

              invoice.getIssueDate ().setValue (XMLDate);
            }
            catch (final DatatypeConfigurationException ex) {
              Logger.getLogger (InvoiceForm.class.getName ()).log (Level.SEVERE, null, ex);
            }
          }

        });
        return issueDateField;
      }

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
      }
      return field;
    }
  }

}

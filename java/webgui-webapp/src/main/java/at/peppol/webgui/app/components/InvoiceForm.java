package at.peppol.webgui.app.components;

import at.peppol.webgui.app.PawgApp;
import at.peppol.webgui.app.components.PartyDetailForm.PartyFieldFactory;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.ObjectFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jerouris
 */
public class InvoiceForm extends Form {
     private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InvoiceForm.class);
    private final ObjectFactory invObjFactory;

    private InvoiceType invoice;
    private JAXBElement<InvoiceType> inv;
    private CustomerPartyType customer;
    private SupplierPartyType supplier;
    

    public InvoiceForm() {
        invObjFactory = new ObjectFactory();
        initInvoiceData();
        initElements();
    }

    private void initInvoiceData() {
        invoice = invObjFactory.createInvoiceType();

        // Standard input, not using user input 
        UBLVersionIDType version = new UBLVersionIDType();
        version.setValue("2.0");

        // TODO: Use PEPPOL Codelists
        CustomizationIDType custID = new CustomizationIDType();
        custID.setValue("urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0");
        custID.setSchemeID("PEPPOL");
        

        supplier = new SupplierPartyType();
        supplier.setParty(new PartyType());
        
        customer = new CustomerPartyType();
        customer.setParty(new PartyType());
        
        //Setting invoice type code to 380: Commercial Invoice
        invoice.setInvoiceTypeCode(new InvoiceTypeCodeType());
        invoice.getInvoiceTypeCode().setValue("380");
        
        invoice.setUBLVersionID(version);
        invoice.setCustomizationID(custID);
        invoice.setID(new IDType());
        
        invoice.setAccountingCustomerParty(customer);
        invoice.setAccountingSupplierParty(supplier);
        List<InvoiceLineType> items = invoice.getInvoiceLine();
        items.add(createInvoiceLine());
    }
    
    
    public JAXBElement<InvoiceType> getInvoice()
    {
         
        return invObjFactory.createInvoice(invoice);
    }

    private InvoiceLineType createInvoiceLine() {
        InvoiceLineType il = new InvoiceLineType();
        il.setID(new IDType());
        il.getID().setValue("1");
        il.setInvoicedQuantity(new InvoicedQuantityType());
        il.getInvoicedQuantity().setValue(BigDecimal.ZERO);

        return il;
    }
    
    private <T> void printInvoiceXML(JAXBElement<T> elem,  OutputStream os)
    {
        try {
           
            JAXBContext jc = JAXBContext.newInstance(elem.getValue().getClass());
            
            Marshaller m = jc.createMarshaller();
            
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Create a printWriter. A good way to show UTF8 in the console
            PrintWriter out = new PrintWriter(new OutputStreamWriter(os));
            m.marshal(elem,out);
        } catch (JAXBException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void initElements() {
        
        GridLayout grid = new GridLayout(2, 4);
        VerticalLayout outerLayout = new VerticalLayout();
        Panel outerPanel = new Panel("Invoice Creation Form");
        outerPanel.addComponent(grid);
        outerLayout.addComponent(outerPanel);
        setLayout(outerLayout);
        grid.setStyleName("margin");
        grid.setSpacing(true);

        Panel invoiceDetailsPanel = new Panel("Invoice Details");
        invoiceDetailsPanel.setStyleName("light");
        invoiceDetailsPanel.setSizeFull();
        invoiceDetailsPanel.addComponent(createInvoiceTopForm());
        grid.addComponent(invoiceDetailsPanel, 0,0, 1,0);
        PartyDetailForm supplierForm = new PartyDetailForm("Supplier",supplier.getParty());
        PartyDetailForm customerForm = new PartyDetailForm("Customer",customer.getParty());
        grid.addComponent(supplierForm,0,1);
        grid.addComponent(customerForm,1,1);
        grid.setSizeUndefined();
        getFooter().addComponent(new Button("Check", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    
                    printInvoiceXML(invObjFactory.createInvoice(invoice),System.out);
                } catch (Exception ex) {
                   LOGGER.error("Error creating files. ",ex);
                }

            }
        }));
        
        outerPanel.requestRepaintAll();
        
    } 
    
    public Form createInvoiceTopForm() {
        
        Form invoiceTopForm = new Form(new FormLayout(), new InvoiceFieldFactory());
        invoiceTopForm.setImmediate(true);

        invoice.setID(new IDType());
        invoiceTopForm.addItemProperty("Invoice ID", 
                new NestedMethodProperty(invoice.getID(),"value"));
        
        invoice.setDocumentCurrencyCode(new DocumentCurrencyCodeType());
        invoice.getDocumentCurrencyCode().setValue("EUR");
        
        invoice.setIssueDate(new IssueDateType());
        invoiceTopForm.addItemProperty("Currency",
                new NestedMethodProperty(invoice.getDocumentCurrencyCode(), "value"));
        
        Date issueDate = new Date();
        invoiceTopForm.addItemProperty("Issue Date", new ObjectProperty(issueDate));
        return invoiceTopForm;
    }
    
     class InvoiceFieldFactory implements FormFieldFactory {

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            // Identify the fields by their Property ID.
            String pid = (String) propertyId;
            
            if ("Currency".equals(pid)) {
                final CurrencySelect curSelect = new CurrencySelect("Currency");
                return curSelect;
            }
            
            if ("Issue Date".equals(pid)) {
                final PopupDateField issueDateField = new PopupDateField("Issue Date");
                issueDateField.setValue(new Date());
                issueDateField.setResolution(PopupDateField.RESOLUTION_DAY);
                issueDateField.addListener(new ValueChangeListener() {

                    @Override
                    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                        try {
                            
                            Date issueDate = (Date) issueDateField.getValue();
                            GregorianCalendar greg = new GregorianCalendar();
                            greg.setTime(issueDate);
                            
                            // Workaround to print only the date and not the time.
                            XMLGregorianCalendar XMLDate = DatatypeFactory
                                    .newInstance()
                                    .newXMLGregorianCalendar();
                            XMLDate.setYear(greg.get(Calendar.YEAR));
                            XMLDate.setMonth(greg.get(Calendar.MONTH));
                            XMLDate.setDay(greg.get(Calendar.DATE));
                            
                             invoice.getIssueDate().setValue(XMLDate);
                        } catch (DatatypeConfigurationException ex) {
                            Logger.getLogger(InvoiceForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                   
                });
                return issueDateField;
            }

            Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
            if (field instanceof AbstractTextField){
                ((AbstractTextField) field).setNullRepresentation("");
            }
            
            return field;
        }
     }
   
}

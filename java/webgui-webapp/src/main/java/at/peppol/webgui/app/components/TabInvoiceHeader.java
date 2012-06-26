package at.peppol.webgui.app.components;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AccountingCostType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDateType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
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
public class TabInvoiceHeader extends Form {
  
  private InvoiceTabForm parent;
  
  public TabInvoiceHeader(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
    
    parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
  }

  private void initElements() {
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    
    final Panel outerPanel = new Panel("Invoice Header");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Invoice Header Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTopForm());
    grid.addComponent(invoiceDetailsPanel, 0, 0, 3, 0);
    grid.setSizeUndefined();
     
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }

  public Form createInvoiceTopForm() {
    final Form invoiceTopForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceTopForm.setImmediate(true);
      
    parent.getInvoice().setID (new IDType ());
    invoiceTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    // invoice.getDocumentCurrencyCode().setValue("EUR");

    parent.getInvoice().setIssueDate (new IssueDateType ());
    invoiceTopForm.addItemProperty ("Currency", new NestedMethodProperty (parent.getInvoice().getDocumentCurrencyCode (), "value"));

    Date issueDate = new Date ();
    invoiceTopForm.addItemProperty ("Issue Date", new ObjectProperty <Date> (issueDate));
   
    parent.getInvoice().getNote ().add (new NoteType ());
    invoiceTopForm.addItemProperty ("Invoice Note", new NestedMethodProperty (parent.getInvoice().getNote ().get (0), "value"));
    
    Date taxPointDate = new Date ();
    invoiceTopForm.addItemProperty ("Tax Point Date", new ObjectProperty <Date> (issueDate));
    
    parent.getInvoice().setAccountingCost (new AccountingCostType ());
    invoiceTopForm.addItemProperty ("Accounting Cost", new NestedMethodProperty (parent.getInvoice().getAccountingCost (), "value"));
    
    Date startDate = new Date ();
    invoiceTopForm.addItemProperty ("Invoice Period Start Date", new ObjectProperty <Date> (issueDate));

    Date endDate = new Date ();
    invoiceTopForm.addItemProperty ("Invoice Period End Date", new ObjectProperty <Date> (issueDate));
    
    
    return invoiceTopForm;
  }

  @SuppressWarnings ("serial")
  class InvoiceFieldFactory implements FormFieldFactory {
    
    @Override
    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Currency".equals(pid)) {
        final CurrencySelect curSelect = new CurrencySelect("Currency");
        return curSelect;
      }

      if ("Issue Date".equals(pid)) {
        final PopupDateField issueDateField = new PopupDateField("Issue Date");
        issueDateField.setValue(new Date());
        issueDateField.setResolution(DateField.RESOLUTION_DAY);
        issueDateField.addListener(new ValueChangeListener() {

          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date issueDate = (Date) issueDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(issueDate);

              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));

              parent.getInvoice().getIssueDate().setValue(XMLDate);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
       

        return issueDateField;
      }
      
      if ("Tax Point Date".equals(pid)) {
        final PopupDateField taxPointDateField = new PopupDateField("Tax Point Date");
        taxPointDateField.setValue(new Date());
        taxPointDateField.setResolution(DateField.RESOLUTION_DAY);
        taxPointDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date taxPointDate = (Date) taxPointDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(taxPointDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              parent.getInvoice().getIssueDate().setValue(XMLDate);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        
        
        return taxPointDateField;
      }
      
      
      if ("Invoice Period Start Date".equals(pid)) {
        final PopupDateField startDateField = new PopupDateField("Invoice Period Start Date");
        startDateField.setValue(new Date());
        startDateField.setResolution(DateField.RESOLUTION_DAY);
        startDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date startDate = (Date) startDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(startDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
              StartDateType sdt = new StartDateType ();
              sdt.setValue (XMLDate);
              parent.getInvoice().getInvoicePeriod ().get (0).setStartDate (sdt);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        return startDateField;
      }
      
      if ("Invoice Period End Date".equals(pid)) {
        final PopupDateField endDateField = new PopupDateField("Invoice Period End Date");
        endDateField.setValue(new Date());
        endDateField.setResolution(DateField.RESOLUTION_DAY);
        endDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date endDate = (Date) endDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(endDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              //parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
              EndDateType edt = new EndDateType ();
              edt.setValue (XMLDate);
              parent.getInvoice().getInvoicePeriod ().get (0).setEndDate (edt);
              
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });      
        return endDateField;
      }      
      
      final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
          ((AbstractTextField) field).setNullRepresentation("");
      }
      return field;
    }
  }
}

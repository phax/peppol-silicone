package at.peppol.webgui.app.components;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitutionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentTermsType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentChannelCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentDueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentMeansCodeType;
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

public class TabInvoicePayment extends Form {
  private InvoiceTabForm parent;
  
  private List<PaymentMeansType> paymentMeansList;
  private PaymentMeansType paymentMeansItem;

  private List<PaymentTermsType> paymentTermsList;
  private PaymentTermsType paymentTermsItem;
  
  private PartyType payeeParty;
  private PartyDetailForm payeeForm;  
  
  public TabInvoicePayment(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    paymentMeansList = parent.getInvoice().getPaymentMeans ();
    paymentMeansItem = createPaymentMeansItem();
    paymentMeansList.add (paymentMeansItem);

    paymentTermsList = parent.getInvoice().getPaymentTerms ();
    PaymentTermsType pt = new PaymentTermsType();
    paymentTermsList.add (pt);
      
    
    payeeParty = parent.getInvoice().getPayeeParty ();
    payeeParty = createPayeePartyItem ();
    //payeeParty = new PartyType();
    //payeeParty.setParty(new PartyType());
    
    final GridLayout grid = new GridLayout(2, 2);
    grid.setSpacing (true);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Payment");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Payment Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoicePaymentTopForm());
    grid.addComponent(invoiceDetailsPanel, 0, 0);
    
    final Panel payeePartyPanel = new Panel("Payee Details");
    payeePartyPanel.setStyleName("light");
    payeePartyPanel.setSizeFull();
    //payeePartyPanel.addComponent(createInvoicePayeePartyForm());
    payeeForm = new PartyDetailForm("Payee", payeeParty);
    payeePartyPanel.addComponent(payeeForm);
    grid.addComponent(payeePartyPanel, 1, 0);
    
    grid.setSizeUndefined();
     
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }
  
  private PaymentMeansType createPaymentMeansItem() {
    final PaymentMeansType pm = new PaymentMeansType();
   
    pm.setPaymentMeansCode (new PaymentMeansCodeType ());
    pm.setPaymentChannelCode (new PaymentChannelCodeType ());
    pm.setID (new IDType ());
    
    FinancialAccountType fa = new FinancialAccountType ();
    fa.setID (new IDType ());
    BranchType bt = new BranchType();
    bt.setID (new IDType ());
    FinancialInstitutionType fi = new FinancialInstitutionType ();
    fi.setID (new IDType ());
    bt.setFinancialInstitution (fi);
    fa.setFinancialInstitutionBranch (bt);
    
    pm.setPayeeFinancialAccount (fa);
   
    return pm;
  }
 
  
  private PartyType createPayeePartyItem() {
    final PartyType pt = new PartyType();
    return pt;
  }  
  
  public Form createInvoicePaymentTopForm() {
    final Form invoicePaymentTopForm = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
    invoicePaymentTopForm.setImmediate(true);
      
    invoicePaymentTopForm.addItemProperty ("Payment ID", new NestedMethodProperty(paymentMeansItem.getID (), "value") );
    invoicePaymentTopForm.addItemProperty ("Payment Means Code", new NestedMethodProperty(paymentMeansItem.getPaymentMeansCode (), "value") );
    final Date paymentDueDate = new Date ();
    invoicePaymentTopForm.addItemProperty ("Payment Due Date", new ObjectProperty <Date> (paymentDueDate));    
    invoicePaymentTopForm.addItemProperty ("Payment Channel Code", new NestedMethodProperty(paymentMeansItem.getPaymentChannelCode (), "value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Account ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount (), "ID.value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Branch ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount ().getFinancialInstitutionBranch (), "ID.value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Institution ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount ().getFinancialInstitutionBranch ().getFinancialInstitution (), "ID.value") );    
    
    return invoicePaymentTopForm;
  }  
  
  public Form createInvoicePayeePartyForm() {
    final Form invoicePayeePartyForm = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
    invoicePayeePartyForm.setImmediate(true);
      
    
    return invoicePayeePartyForm;

  }    
  
  @SuppressWarnings ("serial")
  class InvoicePaymentFieldFactory implements FormFieldFactory {

    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
        // Identify the fields by their Property ID.
        final String pid = (String) propertyId;
        if ("Payment Due Date".equals(pid)) {
          final PopupDateField dueDateField = new PopupDateField("Payment Due Date");
          dueDateField.setValue(new Date());
          dueDateField.setResolution(DateField.RESOLUTION_DAY);
          dueDateField.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
              try {
                final Date dueDate = (Date) dueDateField.getValue();
                final GregorianCalendar greg = new GregorianCalendar();
                greg.setTime(dueDate);

                // Workaround to print only the date and not the time.
                final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                XMLDate.setYear(greg.get(Calendar.YEAR));
                XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
                XMLDate.setDay(greg.get(Calendar.DATE));

                parent.getInvoice().getPaymentMeans ().add (new PaymentMeansType ());
                PaymentDueDateType sdt = new PaymentDueDateType ();
                sdt.setValue (XMLDate);
                parent.getInvoice().getPaymentMeans ().get (0).setPaymentDueDate (sdt);
              } catch (final DatatypeConfigurationException ex) {
                Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
              }
            }
          });
         

          return dueDateField;
        }
        
        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }    
}

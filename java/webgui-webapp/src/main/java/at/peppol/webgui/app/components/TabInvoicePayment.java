package at.peppol.webgui.app.components;

import java.util.Date;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CustomerPartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DeliveryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitutionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentTermsType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ActualDeliveryDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalStreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CountrySubentityType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DepartmentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentChannelCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentDueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentMeansCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;
import at.peppol.webgui.app.components.TabInvoiceDelivery.InvoiceDeliveryFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
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
    pm.setPaymentDueDate (new PaymentDueDateType ());
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
 
  
  //TODO: delete this function
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
          //TODO: process date here...
          
        }
        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }    
}

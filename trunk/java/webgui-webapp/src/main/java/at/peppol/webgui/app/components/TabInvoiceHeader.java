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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AccountingCostType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentTypeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDateType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Jerouris
 */
public class TabInvoiceHeader extends Form {
  private InvoiceTabForm parent;

  private List <DocumentReferenceType> additionalDocRefList;
  private InvoiceAdditionalDocRefAdapter additionalDocRefItem;
  
  private InvoiceAdditionalDocRefAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceAdditionalDocRefTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceHeader(InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;    
    initElements();
    
    parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
  }

  private void initElements() {
    additionalDocRefList = parent.getInvoice().getAdditionalDocumentReference ();
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);    
    
    final Panel outerPanel = new Panel("Invoice Header");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Invoice Header Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTopForm());

    final Panel orderReferencePanel = new Panel("Invoice Order Reference");
    orderReferencePanel.setStyleName("light");
    orderReferencePanel.setSizeFull();
    orderReferencePanel.addComponent(createInvoiceOrderReferenceForm());
    
    table = new InvoiceAdditionalDocRefTable(parent.getInvoice().getAdditionalDocumentReference ());
    table.setSelectable(true);
    table.setImmediate(true);
    table.setNullSelectionAllowed(false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.addStyleName ("striped strong");    
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);    
    
    //buttons Add, Edit, Delete
    Button addBtn = new Button("Add New", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        
        addMode = true;
        hiddenContent.removeAllComponents ();
        additionalDocRefItem = createAdditionalDocRefItem();
        
        Label formLabel = new Label("<h3>Adding new additional document reference line</h3>", Label.CONTENT_XHTML);
        
        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent(createInvoiceAdditionalDocRefForm());
        
        //Save new line button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent(new Button("Save additional doc ref line",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            //update table (and consequently add new item to allowanceChargeList list)
            table.addAdditionalDocRefLine (additionalDocRefItem);
            //hide form
            hiddenContent.setVisible(false);
            addMode = false;
          }
        }));
        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            hiddenContent.removeAllComponents ();
            //hide form
            hiddenContent.setVisible(false);
            addMode = false;
          }
        }));
        
        hiddenContent.addComponent(buttonLayout);
        
        //hiddenContent.setVisible(!hiddenContent.isVisible());
        hiddenContent.setVisible(true);
      }
    });    
    Button editBtn = new Button("Edit Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot edit while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          final String sid = (String)table.getContainerProperty(rowId,"AdditionalDocRefID").getValue();
          
          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();
          
          //get selected item
          additionalDocRefItem = (InvoiceAdditionalDocRefAdapter) additionalDocRefList.get (table.getIndexFromID (sid));
          //clone it to original item
          originalItem = new InvoiceAdditionalDocRefAdapter ();
          cloneInvoiceAdditionalDocRefItem(additionalDocRefItem, originalItem);
          
          Label formLabel = new Label("<h3>Editing additional document reference line</h3>", Label.CONTENT_XHTML);
          
          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent(createInvoiceAdditionalDocRefForm());
          
          //Save new line button
          HorizontalLayout buttonLayout = new HorizontalLayout();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              //update table (and consequently edit item to allowanceChargeList list)
              table.setAdditionalDocRefLine (sid, additionalDocRefItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              hiddenContent.removeAllComponents ();
              
              table.setAdditionalDocRefLine (sid, originalItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          
          hiddenContent.addComponent(buttonLayout);
          
          //hiddenContent.setVisible(!hiddenContent.isVisible());
          hiddenContent.setVisible(true);          
          // TODO: PUT THIS IN FUNCTION ENDS
        }
        else {
          parent.getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }

      }
    });    
    Button deleteBtn = new Button("Delete Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot delete while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          if(table.getContainerProperty(rowId,"AdditionalDocRefID").getValue() != null){
            String sid = (String)table.getContainerProperty(rowId,"AdditionalDocRefID").getValue();
            table.removeAdditionalDocRefLine (sid);
          }
        }
        else {
          parent.getWindow ().showNotification("Info", "No table line is selected", Window.Notification.TYPE_HUMANIZED_MESSAGE);
          
        }
      }
    });    
    
    VerticalLayout buttonsContainer = new VerticalLayout();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addBtn);
    buttonsContainer.addComponent (editBtn);
    buttonsContainer.addComponent (deleteBtn);    
    
    grid.addComponent(invoiceDetailsPanel, 0, 0);
    grid.addComponent(orderReferencePanel, 0, 1);
    grid.addComponent(tableContainer, 0, 2);
    grid.addComponent(buttonsContainer, 1, 2);
    grid.setSizeUndefined();
    grid.setSpacing (true);
    
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    
    outerLayout.addComponent(formLayout);
    // ---- HIDDEN FORM ENDS -----
    
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }

  public Form createInvoiceTopForm() {
    final Form invoiceTopForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceTopForm.setImmediate(true);
      
    parent.getInvoice().setID (new IDType ());
    invoiceTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    //parent.getInvoice().getDocumentCurrencyCode().setValue("EUR");
    invoiceTopForm.addItemProperty ("Currency", new NestedMethodProperty (parent.getInvoice().getDocumentCurrencyCode (), "value"));

    Date issueDate = new Date ();
    parent.getInvoice().setIssueDate (new IssueDateType ());
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

  public Form createInvoiceOrderReferenceForm() {
    final Form invoiceOrderRefForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceOrderRefForm.setImmediate(true);
      
    
    OrderReferenceType rt = new OrderReferenceType ();
    rt.setID (new IDType ());
    parent.getInvoice().setOrderReference (rt);

    DocumentReferenceType dr = new DocumentReferenceType ();
    dr.setID (new IDType ());
    dr.setDocumentType (new DocumentTypeType ());
    
    parent.getInvoice().getContractDocumentReference ().add (dr);
    
    invoiceOrderRefForm.addItemProperty ("Order Reference ID", new NestedMethodProperty (parent.getInvoice().getOrderReference ().getID (), "value"));
    invoiceOrderRefForm.addItemProperty ("Document Reference ID", new NestedMethodProperty (parent.getInvoice().getContractDocumentReference ().get(0).getID (), "value"));
    invoiceOrderRefForm.addItemProperty ("Document Reference Type", new NestedMethodProperty (parent.getInvoice().getContractDocumentReference ().get(0).getDocumentType (), "value"));

    return invoiceOrderRefForm;
  }
  

  
  
  
  
  
  public Form createInvoiceAdditionalDocRefForm() {
    final Form invoiceAdditionalDocRefForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceAdditionalDocRefForm.setImmediate(true);

    NestedMethodProperty mp = new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefID");
    if(!editMode){
      IDType num = new IDType();
      num.setValue (String.valueOf (additionalDocRefList.size ()+1));
      additionalDocRefItem.setID(num);
    }
    else {
      mp.setReadOnly (true);
    }
    
    invoiceAdditionalDocRefForm.addItemProperty ("Additional Doc Ref Type ID", mp );
    invoiceAdditionalDocRefForm.addItemProperty ("Additional Doc Ref Type", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefDocumentType") );
    //invoiceAdditionalDocRefForm.addItemProperty ("Embedded Doc", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefEmbeddedDocumentBinaryObject") );
    invoiceAdditionalDocRefForm.addItemProperty ("External Ref URI", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefExternalReference") );

    return invoiceAdditionalDocRefForm;
  }  
  
  private InvoiceAdditionalDocRefAdapter createAdditionalDocRefItem() {
    InvoiceAdditionalDocRefAdapter ac = new InvoiceAdditionalDocRefAdapter();
   
    ac.setAdditionalDocRefID ("");
    ac.setAdditionalDocRefDocumentType ("");
    //ac.setAdditionalDocRefEmbeddedDocumentBinaryObject (null);
    ac.setAdditionalDocRefExternalReference("");
    
    return ac;
  }  
  
  private void cloneInvoiceAdditionalDocRefItem(InvoiceAdditionalDocRefAdapter srcItem, InvoiceAdditionalDocRefAdapter dstItem)
  {
    dstItem.setAdditionalDocRefID (srcItem.getAdditionalDocRefID ());
    dstItem.setAdditionalDocRefDocumentType (srcItem.getAdditionalDocRefDocumentType ());
    //dstItem.setAdditionalDocRefEmbeddedDocumentBinaryObject (srcItem.getAdditionalDocRefEmbeddedDocumentBinaryObject ());
    dstItem.setAdditionalDocRefExternalReference (srcItem.getAdditionalDocRefExternalReference ());
 
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

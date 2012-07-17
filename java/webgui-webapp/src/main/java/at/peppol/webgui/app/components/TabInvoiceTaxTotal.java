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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxAmountType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class TabInvoiceTaxTotal extends Form {
  private InvoiceTabForm parent;

  private List <TaxSubtotalType> taxSubtotalList;
  private InvoiceTaxSubtotalAdapter taxSubtotalItem;
  
  private InvoiceTaxSubtotalAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceTaxSubtotalTable table;
  private VerticalLayout hiddenContent;
  
  private Form invoiceTaxTotalTopForm;

  private List<TaxTotalType> taxTotalList;
  private TaxTotalType taxTotalItem;
  
  public TabInvoiceTaxTotal(InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;
    initElements();
  }

  private void initElements() {
    taxTotalList = parent.getInvoice().getTaxTotal ();
    taxTotalItem = createTaxTotalItem();
    taxTotalList.add (taxTotalItem);

    taxSubtotalList = parent.getInvoice().getTaxTotal ().get (0).getTaxSubtotal ();    
    
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    final Panel outerPanel = new Panel("Tax Total");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    table = new InvoiceTaxSubtotalTable(taxTotalList.get (0).getTaxSubtotal ());
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
        taxSubtotalItem = createTaxSubtotalItem();
        
        Label formLabel = new Label("<h3>Adding new tax subtotal line</h3>", Label.CONTENT_XHTML);
        
        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent(createInvoiceTaxSubtotalForm());
        
        //Save new line button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent(new Button("Save tax subtotal line",new Button.ClickListener(){
          @Override
          public void buttonClick (ClickEvent event) {
            //update table (and consequently add new item to taxSubtotalList list)
            table.addTaxSubtotalLine (taxSubtotalItem);
            //hide form
            hiddenContent.setVisible(false);
            addMode = false;
            
            //update Total Tax Amount
            taxTotalItem.getTaxAmount ().setValue (SumTaxSubtotalAmount());

            //update form as well
            //invoiceTaxTotalTopForm.getField("Tax Total Amount").setRequired(true);
            invoiceTaxTotalTopForm.getField("Tax Total Amount").setValue(taxTotalItem.getTaxAmount ().getValue ());
          }
        }));
        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener() {
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
        /*
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot edit while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          final String sid = (String)table.getContainerProperty(rowId,"ID.value").getValue();
          
          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();
          
          //get selected item
          allowanceChargeItem = (InvoiceAllowanceChargeAdapter) allowanceChargeList.get (table.getIndexFromID (sid));
          //clone it to original item
          originalItem = new InvoiceAllowanceChargeAdapter ();
          cloneInvoiceAllowanceChargeItem(allowanceChargeItem, originalItem);
          
          Label formLabel = new Label("<h3>Editing allowance charge line</h3>", Label.CONTENT_XHTML);
          
          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent(createInvoiceAllowanceChargeForm());
          
          //Save new line button
          HorizontalLayout buttonLayout = new HorizontalLayout();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              //update table (and consequently edit item to allowanceChargeList list)
              table.setAllowanceChargeLine (sid, allowanceChargeItem);
              //hide form
              hiddenContent.setVisible(false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
            @Override
            public void buttonClick (ClickEvent event) {
              hiddenContent.removeAllComponents ();
              
              table.setAllowanceChargeLine (sid, originalItem);
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
        */
      }
    }); 
    editBtn.setEnabled (false);
    Button deleteBtn = new Button("Delete Selected", new Button.ClickListener() {
      @Override
      public void buttonClick(final Button.ClickEvent event) {
        
        Object rowId = table.getValue(); // get the selected rows id
        if(rowId != null){
          if(addMode || editMode){
            parent.getWindow ().showNotification("Info", "You cannot delete while in add/edit mode", Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          if(table.getContainerProperty(rowId,"TableLineID").getValue() != null){
            String sid = (String)table.getContainerProperty(rowId,"TableLineID").getValue();
            table.removeTaxSubtotalLine (sid);
            
            //update Total Tax Amount
            taxTotalItem.getTaxAmount ().setValue (SumTaxSubtotalAmount());
            
            invoiceTaxTotalTopForm.getField("Tax Total Amount").setValue(taxTotalItem.getTaxAmount ().getValue ());

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
    
    final Panel invoiceDetailsPanel = new Panel("Tax Total Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTaxTotalTopForm());
    
    grid.setSpacing (true);
    grid.addComponent(invoiceDetailsPanel, 0, 0);
    grid.addComponent(tableContainer, 0, 1);
    grid.addComponent(buttonsContainer, 1, 1);      
    grid.setSizeUndefined();
    
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    
    outerLayout.addComponent(formLayout);
    // ---- HIDDEN FORM ENDS -----     
    
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }
  
  private TaxTotalType createTaxTotalItem() {
    final TaxTotalType tt = new TaxTotalType();
    tt.setTaxAmount (new TaxAmountType ());
    tt.getTaxAmount ().setValue (new BigDecimal (0));
    
    return tt;
  }  
  
  public Form createInvoiceTaxTotalTopForm() {
    invoiceTaxTotalTopForm = new Form(new FormLayout(), new InvoiceTaxTotalFieldFactory());
    invoiceTaxTotalTopForm.setImmediate(true);
    
    invoiceTaxTotalTopForm.addItemProperty ("Tax Total Amount", new NestedMethodProperty(taxTotalItem.getTaxAmount (), "value") );
    
    return invoiceTaxTotalTopForm;
  }  
  
  
  
  
  
  public Form createInvoiceTaxSubtotalForm() {
    final Form invoiceTaxSubtotalForm = new Form(new FormLayout(), new InvoiceTaxTotalFieldFactory());
    invoiceTaxSubtotalForm.setImmediate(true);

    NestedMethodProperty mp = new NestedMethodProperty(taxSubtotalItem, "TableLineID");
    if(!editMode){
     //taxSubtotalItem.setTableLineID (String.valueOf (taxSubtotalList.size () + 1));
     taxSubtotalItem.setTableLineID ("");
    }
    else {
      mp.setReadOnly (true);
    }
    
    invoiceTaxSubtotalForm.addItemProperty ("Line ID #", mp );
    invoiceTaxSubtotalForm.addItemProperty ("Taxable Amount", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalTaxableAmount") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Amount", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalTaxAmount") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Category ID", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalCategoryID") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Category Percent", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalCategoryPercent") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason Code", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalCategoryExemptionReasonCode") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalCategoryExemptionReason") );
    invoiceTaxSubtotalForm.addItemProperty ("Tax Scheme ID", new NestedMethodProperty(taxSubtotalItem, "TaxSubTotalCategoryTaxSchemeID") );
    

    return invoiceTaxSubtotalForm;
  }  
  
  private InvoiceTaxSubtotalAdapter createTaxSubtotalItem() {
    InvoiceTaxSubtotalAdapter ac = new InvoiceTaxSubtotalAdapter();
   
    ac.setTableLineID ("");
    ac.setTaxSubTotalTaxAmount (new BigDecimal (0));
    ac.setTaxSubTotalTaxableAmount (new BigDecimal (0));
    ac.setTaxSubTotalCategoryID ("");
    ac.setTaxSubTotalCategoryPercent (new BigDecimal (0));
    ac.setTaxSubTotalCategoryExemptionReasonCode ("");
    ac.setTaxSubTotalCategoryExemptionReason ("");
    ac.setTaxSubTotalCategoryTaxSchemeID ("");
    
    return ac;
  }  
  
  private void cloneInvoiceSubTaxtotalItem(InvoiceTaxSubtotalAdapter srcItem, InvoiceTaxSubtotalAdapter dstItem)
  {
    //TODO: // Enable buffering.
    //      form.setWriteThrough(false);  
    //      No clone is necessary this way...
    
    dstItem.setTableLineID (srcItem.getTableLineID());
    dstItem.setTaxSubTotalTaxAmount (srcItem.getTaxSubTotalTaxAmount());
    dstItem.setTaxSubTotalTaxableAmount (srcItem.getTaxSubTotalTaxableAmount());
    dstItem.setTaxSubTotalCategoryID (srcItem.getTaxSubTotalCategoryID());
    dstItem.setTaxSubTotalCategoryPercent (srcItem.getTaxSubTotalCategoryPercent());
    dstItem.setTaxSubTotalCategoryExemptionReasonCode (srcItem.getTaxSubTotalCategoryExemptionReasonCode());
    dstItem.setTaxSubTotalCategoryExemptionReason (srcItem.getTaxSubTotalCategoryExemptionReason());
    dstItem.setTaxSubTotalCategoryTaxSchemeID (srcItem.getTaxSubTotalCategoryTaxSchemeID());    
    
  }

  private BigDecimal SumTaxSubtotalAmount () {
    double sum = 0.0;
    Iterator <TaxSubtotalType> iterator = taxSubtotalList.iterator ();
    while (iterator.hasNext()) {
       InvoiceTaxSubtotalAdapter ac = (InvoiceTaxSubtotalAdapter) iterator.next();
       sum += ac.getTaxAmount ().getValue ().doubleValue ();
    }    

    return new BigDecimal(sum);
  }
  
  @SuppressWarnings ("serial")
  class InvoiceTaxTotalFieldFactory implements FormFieldFactory {

    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
        // Identify the fields by their Property ID.
        final String pid = (String) propertyId;

        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }    
}
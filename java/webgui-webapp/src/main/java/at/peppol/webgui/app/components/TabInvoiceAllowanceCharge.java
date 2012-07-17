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
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class TabInvoiceAllowanceCharge extends Form {
  private final InvoiceTabForm parent;
  private List <AllowanceChargeType> allowanceChargeList;
  private InvoiceAllowanceChargeAdapter allowanceChargeItem;

  private InvoiceAllowanceChargeAdapter originalItem;

  private boolean addMode;
  private boolean editMode;

  public InvoiceAllowanceChargeTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceAllowanceCharge (final InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;
    initElements ();
  }

  private void initElements () {
    allowanceChargeList = parent.getInvoice ().getAllowanceCharge ();

    final GridLayout grid = new GridLayout (4, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();
    hiddenContent = new VerticalLayout ();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);

    table = new InvoiceAllowanceChargeTable (parent.getInvoice ().getAllowanceCharge ());
    table.setSelectable (true);
    table.setImmediate (true);
    table.setNullSelectionAllowed (false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.addStyleName ("striped strong");

    final VerticalLayout tableContainer = new VerticalLayout ();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);

    // buttons Add, Edit, Delete
    final Button addBtn = new Button ("Add New", new Button.ClickListener () {
      @Override
      public void buttonClick (final Button.ClickEvent event) {

        addMode = true;
        hiddenContent.removeAllComponents ();
        allowanceChargeItem = createAllowanceChargeItem ();

        final Label formLabel = new Label ("<h3>Adding new allowance charge line</h3>", Label.CONTENT_XHTML);

        hiddenContent.addComponent (formLabel);
        hiddenContent.addComponent (createInvoiceAllowanceChargeForm ());

        // Save new line button
        final HorizontalLayout buttonLayout = new HorizontalLayout ();
        buttonLayout.setSpacing (true);
        buttonLayout.addComponent (new Button ("Save allowance charge line", new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            // update table (and consequently add new item to
            // allowanceChargeList list)
            table.addAllowanceChargeLine (allowanceChargeItem);
            // hide form
            hiddenContent.setVisible (false);
            addMode = false;
          }
        }));
        buttonLayout.addComponent (new Button ("Cancel", new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            hiddenContent.removeAllComponents ();
            // hide form
            hiddenContent.setVisible (false);
            addMode = false;
          }
        }));

        hiddenContent.addComponent (buttonLayout);

        // hiddenContent.setVisible(!hiddenContent.isVisible());
        hiddenContent.setVisible (true);
      }
    });
    final Button editBtn = new Button ("Edit Selected", new Button.ClickListener () {
      @Override
      public void buttonClick (final Button.ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
          if (addMode || editMode) {
            parent.getWindow ().showNotification ("Info",
                                                  "You cannot edit while in add/edit mode",
                                                  Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          final String sid = (String) table.getContainerProperty (rowId, "ID.value").getValue ();

          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();

          // get selected item
          allowanceChargeItem = (InvoiceAllowanceChargeAdapter) allowanceChargeList.get (table.getIndexFromID (sid));
          // clone it to original item
          originalItem = new InvoiceAllowanceChargeAdapter ();
          cloneInvoiceAllowanceChargeItem (allowanceChargeItem, originalItem);

          final Label formLabel = new Label ("<h3>Editing allowance charge line</h3>", Label.CONTENT_XHTML);

          hiddenContent.addComponent (formLabel);
          hiddenContent.addComponent (createInvoiceAllowanceChargeForm ());

          // Save new line button
          final HorizontalLayout buttonLayout = new HorizontalLayout ();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent (new Button ("Save changes", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
              // update table (and consequently edit item to allowanceChargeList
              // list)
              table.setAllowanceChargeLine (sid, allowanceChargeItem);
              // hide form
              hiddenContent.setVisible (false);
              editMode = false;
            }
          }));
          buttonLayout.addComponent (new Button ("Cancel editing", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
              hiddenContent.removeAllComponents ();

              table.setAllowanceChargeLine (sid, originalItem);
              // hide form
              hiddenContent.setVisible (false);
              editMode = false;
            }
          }));

          hiddenContent.addComponent (buttonLayout);

          // hiddenContent.setVisible(!hiddenContent.isVisible());
          hiddenContent.setVisible (true);
          // TODO: PUT THIS IN FUNCTION ENDS
        }
        else {
          parent.getWindow ().showNotification ("Info",
                                                "No table line is selected",
                                                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }

      }
    });
    final Button deleteBtn = new Button ("Delete Selected", new Button.ClickListener () {
      @Override
      public void buttonClick (final Button.ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
          if (addMode || editMode) {
            parent.getWindow ().showNotification ("Info",
                                                  "You cannot delete while in add/edit mode",
                                                  Window.Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          }
          if (table.getContainerProperty (rowId, "ID.value").getValue () != null) {
            final String sid = (String) table.getContainerProperty (rowId, "ID.value").getValue ();
            table.removeAllowanceChargeLine (sid);
          }
        }
        else {
          parent.getWindow ().showNotification ("Info",
                                                "No table line is selected",
                                                Window.Notification.TYPE_HUMANIZED_MESSAGE);

        }
      }
    });

    final VerticalLayout buttonsContainer = new VerticalLayout ();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addBtn);
    buttonsContainer.addComponent (editBtn);
    buttonsContainer.addComponent (deleteBtn);

    final Panel outerPanel = new Panel ("Allowance Charge");

    grid.addComponent (tableContainer, 0, 0);
    grid.addComponent (buttonsContainer, 1, 0);

    outerPanel.addComponent (grid);
    outerLayout.addComponent (outerPanel);

    // ---- HIDDEN FORM BEGINS -----
    final VerticalLayout formLayout = new VerticalLayout ();
    formLayout.addComponent (hiddenContent);
    hiddenContent.setVisible (false);
    outerLayout.addComponent (formLayout);
    // ---- HIDDEN FORM ENDS -----

    setLayout (outerLayout);
    grid.setSizeUndefined ();
    outerPanel.requestRepaintAll ();
  }

  public Form createInvoiceAllowanceChargeForm () {
    final Form invoiceAllowanceChargeForm = new Form (new FormLayout (), new InvoiceAllowanceChargeFieldFactory ());
    invoiceAllowanceChargeForm.setImmediate (true);

    final NestedMethodProperty mp = new NestedMethodProperty (allowanceChargeItem, "ID.value");
    if (!editMode) {
      final IDType num = new IDType ();
      num.setValue (String.valueOf (allowanceChargeList.size () + 1));
      allowanceChargeItem.setID (num);
    }
    else {
      mp.setReadOnly (true);
    }

    // invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new
    // NestedMethodProperty(allowanceChargeItem, "ID.value") );
    invoiceAllowanceChargeForm.addItemProperty ("Line ID #", mp);
    invoiceAllowanceChargeForm.addItemProperty ("Charge Indicator", new NestedMethodProperty (allowanceChargeItem,
                                                                                              "indicator"));
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Reason",
                                                new NestedMethodProperty (allowanceChargeItem, "reason"));
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Amount",
                                                new NestedMethodProperty (allowanceChargeItem, "chargeAmount"));
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category ID", new NestedMethodProperty (allowanceChargeItem,
                                                                                             "taxCategoryID"));
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category Percent", new NestedMethodProperty (allowanceChargeItem,
                                                                                                  "taxCategoryPercent"));
    invoiceAllowanceChargeForm.addItemProperty ("Tax Category Scheme ID",
                                                new NestedMethodProperty (allowanceChargeItem, "taxCategorySchemeID"));

    return invoiceAllowanceChargeForm;
  }

  private InvoiceAllowanceChargeAdapter createAllowanceChargeItem () {
    final InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter ();

    ac.setID (new IDType ());
    ac.setIndicator (Boolean.FALSE);
    ac.setReason ("");
    ac.setChargeAmount (BigDecimal.ZERO);
    ac.setTaxCategoryID ("");
    ac.setTaxCategoryPercent (BigDecimal.ZERO);
    ac.setTaxCategorySchemeID ("");

    ac.getAmount ().setCurrencyID (CurrencyCodeContentType.valueOf (parent.getInvoice ()
                                                                          .getDocumentCurrencyCode ()
                                                                          .getValue ()));

    return ac;
  }

  private void cloneInvoiceAllowanceChargeItem (final InvoiceAllowanceChargeAdapter srcItem,
                                                final InvoiceAllowanceChargeAdapter dstItem) {
    dstItem.setAllowanceChargeID (srcItem.getAllowanceChargeID ());
    dstItem.setIndicator (srcItem.getIndicator ());
    dstItem.setReason (srcItem.getReason ());
    dstItem.setChargeAmount (srcItem.getChargeAmount ());
    dstItem.setTaxCategoryID (srcItem.getTaxCategoryID ());
    dstItem.setTaxCategoryPercent (srcItem.getTaxCategoryPercent ());
    dstItem.setTaxCategorySchemeID (srcItem.getTaxCategorySchemeID ());
  }

  class InvoiceAllowanceChargeFieldFactory implements FormFieldFactory {

    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Charge Indicator".equals (pid)) {
        final Select indicatorSelect = new Select ("Charge or Allowance?");
        indicatorSelect.setNullSelectionAllowed (false);
        indicatorSelect.addItem (Boolean.TRUE);
        indicatorSelect.addItem (Boolean.FALSE);
        indicatorSelect.setItemCaption (Boolean.TRUE, "Charge");
        indicatorSelect.setItemCaption (Boolean.FALSE, "Allowance");

        return indicatorSelect;
      }

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
      }
      return field;
    }
  }
}

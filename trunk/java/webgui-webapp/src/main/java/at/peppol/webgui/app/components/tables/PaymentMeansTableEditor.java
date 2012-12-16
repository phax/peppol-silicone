package at.peppol.webgui.app.components.tables;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import at.peppol.webgui.app.components.PartyAgencyIDSelect;
import at.peppol.webgui.app.components.PaymentMeansSelect;
import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;
import at.peppol.webgui.app.utils.MyField;
import at.peppol.webgui.app.utils.Utils;

public class PaymentMeansTableEditor extends GenericTableEditor<PaymentMeansType, PaymentMeansAdapter> {

	public PaymentMeansTableEditor(boolean editMode) {
		super(editMode);
	}
	public PaymentMeansTableEditor(boolean editMode, InvoiceType inv) {
		super(editMode, inv);
	}
	
	@Override
	public void cloneItem(PaymentMeansAdapter srcItem, PaymentMeansAdapter dstItem) {
		dstItem.setIDAdapter(srcItem.getIDAdapter());
		dstItem.setBranchIDAdapter(srcItem.getBranchIDAdapter());
		dstItem.setFinancialAccountIDAdapter(srcItem.getFinancialAccountIDAdapter());
		dstItem.setInstitutionIDAdapter(srcItem.getInstitutionIDAdapter());
		dstItem.setPaymentDueDateAdapter(srcItem.getPaymentDueDateAdapter());
		dstItem.setPaymentChannelCodeAdapter(srcItem.getPaymentChannelCodeAdapter());
		dstItem.setPaymentMeansCodeAdapter(srcItem.getPaymentMeansCodeAdapter());
	}
	
	@Override
	public PaymentMeansAdapter createItem() {
		return new PaymentMeansAdapter();
	}
		
	@Override
	public AccountForm createTableForm(PaymentMeansAdapter paymentMeansAdapterItem, List<PaymentMeansType> paymentMeansList) {
		//Form form = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
		AccountForm form = new AccountForm();
		form.setFormFieldFactory(new InvoicePaymentFieldFactory());
		
		  form.setImmediate(true);
		  form.setWriteThrough(true);
		  
		  //automatically set the id
		  if (!editMode) {
			  IDType num = new IDType();
		      //num.setValue (String.valueOf (paymentMeansList.size ()+1));
		      //paymentMeansAdapterItem.setID(num);
		      int max = 0;
		      for (PaymentMeansType payment : paymentMeansList) {
		    	  if (Integer.parseInt(payment.getID().getValue()) > max)
		    		  max = Integer.parseInt(payment.getID().getValue());
		      }
		      num.setValue(String.valueOf(max+1));
		      paymentMeansAdapterItem.setID(num);
		  }
		  
	      form.addItemProperty("Payment Means Code", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentMeansCodeAdapter"));
	      form.addItemProperty("Payment Due Date", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentDueDateAdapter"));
	      form.addItemProperty("Payment Channel Code", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentChannelCodeAdapter"));
	      form.addItemProperty("Account Number", new NestedMethodProperty(paymentMeansAdapterItem, "FinancialAccountIDAdapter"));
	      form.addItemProperty("Branch ID", new NestedMethodProperty(paymentMeansAdapterItem, "BranchIDAdapter"));
	      form.addItemProperty("Financial Institution ID", new NestedMethodProperty(paymentMeansAdapterItem, "InstitutionIDAdapter"));
	      
		  return form;
	}
	
	@Override
	public Button.ClickListener addButtonListener(
			final Button editButton, final Button deleteButton, 
			final Layout hiddenContent, final GenericTable<PaymentMeansType,PaymentMeansAdapter> table, 
			final List<PaymentMeansType> invoiceList, final Label label) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//addMode = true;
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
		        hiddenContent.removeAllComponents();
		        
		        final PaymentMeansAdapter adapterItem = createItem();
		        
		        hiddenContent.addComponent(label);
		        final AccountForm tableForm = createTableForm(adapterItem, invoiceList);
		        hiddenContent.addComponent(tableForm);
		        
		        final Button saveNewLine = new Button("Save");
		                
		  	  	saveNewLine.addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						boolean errorDate = false;
						boolean errorCode = false;
						boolean errorIBAN = false;
						
						if (adapterItem.getIDAdapter() != null) {
							if (!adapterItem.getIDAdapter().equals("")) {
								
								PopupDateField dueDateField = (PopupDateField)tableForm.getField("Payment Due Date");
								Date issueDate = invoice.getIssueDate().getValue().toGregorianCalendar().getTime();
								Date dueDate = adapterItem.getPaymentDueDateAdapter();
								if (dueDate.compareTo(issueDate) < 0) {
									dueDateField.setComponentError(new UserError("Payment due date must be later than invoice issue date"));
									errorDate = true;
								}
								
								ComboBox meansCode = (ComboBox)tableForm.getField("Payment Means Code");
								AbstractTextField accountField = (AbstractTextField)tableForm.getField("Account Number");
								if (meansCode.getValue() != null) {
									if (((String)meansCode.getValue()).equals("42")) {
										
										if (accountField.getValue() != null) {
											if (((String)accountField.getValue()).trim().equals("")) {
												accountField.setComponentError(new UserError("You should provide a financial account"));
												errorCode = true;
											}
										}
										else {
											accountField.setComponentError(new UserError("You should provide a financial account"));
											errorCode = true;
										}
									}
								}
								else {
									meansCode.setComponentError(new UserError("You should provide a Payment Means Code"));
									errorCode = true;
								}
								
								if (tableForm.getIBAN().getValue() != null) {
									if (((String)tableForm.getIBAN().getValue()).trim().equals("IBAN")) {
										if (accountField.getValue()!= null) {
											AbstractTextField bankID = (AbstractTextField)tableForm.getField("Financial Institution ID"); 
											if (bankID.getValue() != null) {
												if (((String)bankID.getValue()).trim().equals("")) {
													bankID.setComponentError(new UserError("You should provide an ID"));
													errorIBAN = true;
												}
											}
											else {
												bankID.setComponentError(new UserError("You should provide an ID"));
												errorIBAN = true;
											}
										}
									}
								}
								
								if (!errorCode && !errorDate && !errorIBAN) {
									dueDateField.setComponentError(null);
									accountField.setComponentError(null);
									errorDate = false;
									errorCode = false;
									table.addLine(adapterItem);
									table.requestRepaint();
									//hide form
									hiddenContent.setVisible(false);
								}
			        		}
			        		else {
			        			hiddenContent.getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
			        		}
			        	}
			        	else {
			        		hiddenContent.getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
			        	}
			        	
						if (!errorDate && !errorCode) {
							editButton.setEnabled(true);
							deleteButton.setEnabled(true);
						}
					}
				});

		        //Save new line button
		        HorizontalLayout buttonLayout = new HorizontalLayout();
		        buttonLayout.setSpacing (true);
		        buttonLayout.setMargin (true);
		        buttonLayout.addComponent(saveNewLine);

		        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener(){
		          @Override
		          public void buttonClick (ClickEvent event) {
		        	editButton.setEnabled(true);
		  			deleteButton.setEnabled(true);
		            hiddenContent.removeAllComponents ();
		            //hide form
		            tableForm.discard();
		            hiddenContent.setVisible(false);
		            //addMode = false;
		          }
		        }));
		        
		        hiddenContent.addComponent(buttonLayout);
		        
		        //hiddenContent.setVisible(!hiddenContent.isVisible());
		        hiddenContent.setVisible(true);
		      }
	    };
	    
	    return b;
	}
	
	@Override
	public Button.ClickListener editButtonListener(
			final Button addButton, final Button deleteButton,
			final Layout hiddenContent, final GenericTable<PaymentMeansType,PaymentMeansAdapter> table, 
			final List<PaymentMeansType> invoiceList, final Label label) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object rowId = table.getValue(); // get the selected rows id
		        if(rowId != null){
		        	if (table.getContainerProperty(rowId,"IDAdapter") != null) {
			        	hiddenContent.removeAllComponents ();
			        	editMode = true;
			        	addButton.setEnabled(false);
						deleteButton.setEnabled(false);
			        	
			        	final String sid = (String)table.getContainerProperty(rowId,"IDAdapter").getValue();
			        		          
			        	//get selected item
			        	final PaymentMeansAdapter adapterItem = (PaymentMeansAdapter)invoiceList.get(table.getIndexFromID(sid));
			        	//paymentMeansAdapterItem = table.getEntryFromID(sid);
			        	
			        	//clone it to original item
			        	final PaymentMeansAdapter originalItem = createItem();
			        	cloneItem(adapterItem, originalItem);
			          
			        	//Label formLabel = new Label("<h3>Edit payment means line</h3>", Label.CONTENT_XHTML);
			          
			        	hiddenContent.addComponent(label);
			        	final AccountForm tableForm = createTableForm(adapterItem, invoiceList);
			        	tableForm.setImmediate(true);
			        	hiddenContent.addComponent(tableForm);
			          
			        	//Save new line button
			        	HorizontalLayout buttonLayout = new HorizontalLayout();
			        	buttonLayout.setSpacing (true);
			        	buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
				            @Override
				            public void buttonClick (ClickEvent event) {
				            	//paymentMeansForm.commit();
				            	boolean errorDate = false;
				            	boolean errorCode = false;
				            	boolean errorIBAN = false;
				            	
				            	PopupDateField dueDateField = (PopupDateField)tableForm.getField("Payment Due Date");
								Date issueDate = invoice.getIssueDate().getValue().toGregorianCalendar().getTime();
								Date dueDate = adapterItem.getPaymentDueDateAdapter();
								if (dueDate.compareTo(issueDate) < 0) {
									dueDateField.setComponentError(new UserError("Payment due date must be later than invoice issue date"));
									errorDate = true;
								}
								
								ComboBox meansCode = (ComboBox)tableForm.getField("Payment Means Code");
								AbstractTextField accountField = (AbstractTextField)tableForm.getField("Account Number");
								if (meansCode.getValue() != null) {
									if (((String)meansCode.getValue()).equals("42")) {
										
										if (accountField.getValue() != null) {
											if (((String)accountField.getValue()).trim().equals("")) {
												accountField.setComponentError(new UserError("You should provide a financial account"));
												errorCode = true;
											}
										}
										else {
											accountField.setComponentError(new UserError("You should provide a financial account"));
											errorCode = true;
										}
									}
								}
								else {
									meansCode.setComponentError(new UserError("You should provide a Payment Means Code"));
									errorCode = true;
								}
				            	
								if (tableForm.getIBAN().getValue() != null) {
									if (((String)tableForm.getIBAN().getValue()).trim().equals("IBAN")) {
										AbstractTextField bankID = (AbstractTextField)tableForm.getField("Financial Institution ID"); 
										if (bankID.getValue() != null) {
											if (((String)bankID.getValue()).trim().equals("")) {
												bankID.setComponentError(new UserError("You should provide an ID"));
												errorIBAN = true;
											}
										}
										else {
											bankID.setComponentError(new UserError("You should provide an ID"));
											errorIBAN = true;
										}
									}
								}
								
								if (!errorCode && !errorDate && !errorIBAN) {
									table.setLine(sid, adapterItem);
					            	//hide form
					            	hiddenContent.setVisible(false);
					            	editMode = false;
					            	addButton.setEnabled(true);
									deleteButton.setEnabled(true);
								}
				            }
			        	}));
			        	buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
				            @Override
				            public void buttonClick (ClickEvent event) {
				            	//paymentMeansForm.discard();
				            	table.setLine(sid, originalItem);
				            	//hide form
				            	hiddenContent.removeAllComponents ();
				            	hiddenContent.setVisible(false);
				            	editMode = false;
				            	addButton.setEnabled(true);
								deleteButton.setEnabled(true);
				            }
			        	}));
			          
			        	hiddenContent.addComponent(buttonLayout);
			        	hiddenContent.setVisible(true);
		        	}
		        	else {
		        		hiddenContent.getWindow().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        	}
		        }
		        else {
		        	hiddenContent.getWindow().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        }
			}
			
		};
		
		return b;
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
	          
	          return dueDateField;
	        }
	        
	        if ("Payment Means Code".equals(pid)) {
	            PaymentMeansSelect select = new PaymentMeansSelect(pid);
	            select.setRequired(true);
	            return select;
	        }
	        
	        
	/*        if ("Branch ID".equals(pid)) {
	        	Table tab = new Table("Label of table");
	        	// Define table columns. 
	        	tab.addContainerProperty(
	        	    "date",     Date.class,   null, "Date",         null, null);
	        	tab.addContainerProperty(
	        	    "quantity", Double.class, null, "Quantity (l)", null, null);
	        	tab.addContainerProperty(
	        	    "price",    Double.class, null, "Price (e/l)",  null, null);
	        	tab.addContainerProperty(
	        	    "total",    Double.class, null, "Total (e)",    null, null);
	        	
	        	return tab;
	        }
	*/
	        
	        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
	        if (field instanceof AbstractTextField) {
	            ((AbstractTextField) field).setNullRepresentation("");
	        }
	        return field;
	    }
	  }
	
	private class AccountForm extends Form {
		
		GridLayout layout;
		int counter;
		int split;
		Select iban, bic;
		Label ibanLabel, bicLabel;
		
		public AccountForm() {
			split = 10;
			layout = new GridLayout(4,split);
			layout.setSpacing(true);
			layout.setMargin(true);
			setLayout(layout);
			counter = 0;
			
			iban = new Select();
			iban.setWidth(6,UNITS_EM);
			iban.addItem("IBAN");
			iban.addItem("Other");
			ibanLabel = new Label("Account type");
			
			bic = new Select();
			bic.setWidth(6,UNITS_EM);
			bic.addItem("BIC");
			bic.addItem("Other");
			bicLabel = new Label("ID type");
		}
		
		public Select getIBAN() {
			return iban;
		}
		
		public Select getBIC() {
			return bic;
		}
		  
		@Override
		protected void attachField(Object propertyId, Field field) {
			if (counter%split == 0 && counter!=0) {
				layout.setColumns(layout.getColumns()+2);
		    }
			
			int col = counter/split;
			Label fieldLabel;
			
			fieldLabel = new Label(field.getCaption());
			if (propertyId.equals("Account Number") || propertyId.equals("Financial Institution ID")) {
				HorizontalLayout hl = new HorizontalLayout();
				hl.setSpacing(true);
				field.setCaption(null);
				hl.addComponent(field);
				if (propertyId.equals("Account Number")) {
					hl.addComponent(ibanLabel);
					hl.addComponent(iban);
				}
				else if (propertyId.equals("Financial Institution ID")) {
					hl.addComponent(bicLabel);
					hl.addComponent(bic);
				}
				hl.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
				layout.addComponent(hl, 2*col+1, counter%split);
				layout.addComponent(fieldLabel, 2*col, counter%split);
				layout.setComponentAlignment(fieldLabel, Alignment.MIDDLE_RIGHT);
			}
			else {				
				field.setCaption(null);
				layout.addComponent(fieldLabel, 2*col, counter%split);
				layout.addComponent(field, 2*col+1, counter%split);
				layout.setComponentAlignment(fieldLabel, Alignment.MIDDLE_RIGHT);
				layout.setComponentAlignment(field, Alignment.MIDDLE_LEFT);
			}
			counter++;
		}
	}
}

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class PaymentMeansTable extends Table {
 
  private final List <PaymentMeansType> paymentMeansLines;
  private final BeanItemContainer<PaymentMeansAdapter> tableLines =
          new BeanItemContainer<PaymentMeansAdapter>(PaymentMeansAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();
  
  public PaymentMeansTable(List <PaymentMeansType> list) {
    this.paymentMeansLines = list;
    setContainerDataSource(tableLines);

    addPropertyWithHeader("IDAdapter", "#ID");
    addPropertyWithHeader("PaymentMeansCodeAdapter", "Payment Means Code");
    addPropertyWithHeader("PaymentDueDateAdapterAsString", "Payment Due Date");
    addPropertyWithHeader("PaymentChannelCodeAdapter", "Payment Channel Code");
    addPropertyWithHeader("FinancialAccountIDAdapter", "Account Number");
    addPropertyWithHeader("BranchIDAdapter", "Branch ID");
    addPropertyWithHeader("InstitutionIDAdapter", "Financial Institution ID");
    
    setDefinedPropertiesAsVisible();
    setPageLength(7);
    
    //setColumnWidth("AdditionalDocRefExternalReference", 200);
    //setColumnExpandRatio("AdditionalDocRefExternalReference", 2);
  }  
  
  private void addPropertyWithHeader(String property, String headerName) {
    tableLines.addNestedContainerProperty(property);
    setColumnHeader(property, headerName);
    visibleHeaderNames.add(property);
  }

  private void setDefinedPropertiesAsVisible() {
    setVisibleColumns(visibleHeaderNames.toArray());
  }
  
  public void addPaymentMeans(PaymentMeansAdapter pms) {
	  paymentMeansLines.add(pms);
      tableLines.addBean(pms);   
  }
  
  public void setPaymentMeans(String lineID, PaymentMeansAdapter pms) {
	  //use for editing....
	  if(getIndexFromID(lineID) > -1){
		  paymentMeansLines.set(getIndexFromID(lineID), pms);
		  tableLines.removeAllItems ();
		  Iterator<PaymentMeansType> iterator = paymentMeansLines.iterator();
		  while (iterator.hasNext()) {
			  PaymentMeansType ac = iterator.next();
			  tableLines.addBean((PaymentMeansAdapter) ac);
		  }
	  }
  }  
  
  public void removePaymentMeans(String lineID) {
    Iterator<PaymentMeansType> iterator = paymentMeansLines.iterator ();
    while (iterator.hasNext()) {
    	PaymentMeansAdapter ac = (PaymentMeansAdapter) iterator.next();
      if (ac.getIDAdapter().equals (lineID)) {
        tableLines.removeItem (ac);
        paymentMeansLines.remove (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator <PaymentMeansType> iterator = paymentMeansLines.iterator ();
    while (iterator.hasNext()) {
    	PaymentMeansAdapter ac = (PaymentMeansAdapter) iterator.next();
      if (ac.getIDAdapter().equals (lineID)) {
        int index = paymentMeansLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }    
  
  public PaymentMeansAdapter getEntryFromID(String lineID) {
    Iterator <PaymentMeansType> iterator = paymentMeansLines.iterator ();
    while (iterator.hasNext()) {
    	PaymentMeansAdapter ac = (PaymentMeansAdapter) iterator.next();
    	if (ac.getIDAdapter().equals (lineID)) {
    		return ac;
    	}
    }    
    return null;
  }
  
}

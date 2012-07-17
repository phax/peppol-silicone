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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CountryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyTaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IdentificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PostalZoneType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StreetNameType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;

/**
 *
 * @author Jerouris
 */
@SuppressWarnings ("serial")
public class PartyDetailForm extends Panel{

    private final PartyType partyBean;
    private final String party;
    
    public PartyDetailForm(String partyType, PartyType partyBean) {
        this.party = partyType;
        this.partyBean = partyBean;
        
        initElements();
    
    }
        private void initElements() {

        setCaption(party + " Party");
        //setStyleName("light");
        
        PropertysetItem partyItemSet = new PropertysetItem();
        
        final PartyIdentificationType supplierPartyID = new PartyIdentificationType();
        supplierPartyID.setID(new IDType());
        partyBean.getPartyIdentification().add(supplierPartyID);
       
        partyItemSet.addItemProperty("Party Id",
                            new NestedMethodProperty(supplierPartyID, "ID.value"));
        
        final PartyNameType partyName = new PartyNameType();
        partyName.setName(new NameType());
        partyBean.getPartyName().add(partyName);
        
        partyItemSet.addItemProperty("Agency Name", 
                new NestedMethodProperty(supplierPartyID, "ID.SchemeAgencyID") );
        partyItemSet.addItemProperty("Party Name",
                            new NestedMethodProperty(partyName, "name.value"));
        
        /*
        final AddressType partyrAddress = new AddressType();
        
        partyBean.setPostalAddress(partyrAddress);
        partyrAddress.setStreetName(new StreetNameType());
        partyrAddress.setCityName(new CityNameType());
        partyrAddress.setPostalZone(new PostalZoneType());
        partyrAddress.setCountry(new CountryType());
        
        partyrAddress.getCountry().setIdentificationCode(new IdentificationCodeType());

        partyItemSet.addItemProperty("Street Name",
                            new NestedMethodProperty(partyrAddress, "streetName.value"));
        partyItemSet.addItemProperty("City",
                            new NestedMethodProperty(partyrAddress, "cityName.value"));
        partyItemSet.addItemProperty("Postal Zone",
                            new NestedMethodProperty(partyrAddress, "postalZone.value"));
        partyItemSet.addItemProperty("Country",
                            new NestedMethodProperty(partyrAddress, "country.identificationCode.value"));
        */
        AddressType address = new AddressType ();
        AddressDetailForm partyAddressForm = new AddressDetailForm (party, address);
        partyBean.setPostalAddress (address);        
        
        
        PartyTaxSchemeType taxScheme = new PartyTaxSchemeType();
        taxScheme.setCompanyID(new CompanyIDType());
        
        // TODO: Hardcoded ShemeID etc for TaxScheme. Should be from a codelist?
        taxScheme.setTaxScheme(new TaxSchemeType());
        taxScheme.getTaxScheme().setID(new IDType());
        taxScheme.getTaxScheme().getID().setValue("VAT");
        taxScheme.getTaxScheme().getID().setSchemeID("UN/ECE 5153");
        taxScheme.getTaxScheme().getID().setSchemeAgencyID("6");
        partyBean.getPartyTaxScheme().add(taxScheme);
        
        partyItemSet.addItemProperty("Tax ID",
                            new NestedMethodProperty(taxScheme.getCompanyID(),"value"));

        final Form partyForm = new Form();
        partyForm.setFormFieldFactory(new PartyFieldFactory());
        partyForm.setItemDataSource(partyItemSet);
        partyForm.setImmediate(true);
        
        addComponent(partyForm);
        addComponent(partyAddressForm);
    }
    
     @SuppressWarnings ("serial")
    class PartyFieldFactory implements FormFieldFactory {

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            // Identify the fields by their Property ID.
            String pid = (String) propertyId;
            if ("Agency Name".equals(pid)) {
                final PartyAgencyIDSelect select = new PartyAgencyIDSelect("Agency Name");
                select.addListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                        partyBean.getPartyIdentification().get(0)
                                .getID()
                                .setSchemeAgencyName(select.getSelectedAgencyName());
                    }
                });
                return select;
            }

            Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
            if (field instanceof AbstractTextField){
                ((AbstractTextField) field).setNullRepresentation("");
            }
            
            return field;
        }
     }
}

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

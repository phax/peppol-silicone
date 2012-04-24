package at.peppol.webgui.app.components;

import at.peppol.commons.identifier.actorid.EPredefinedIdentifierIssuingAgency;

import com.vaadin.ui.ComboBox;

/**
 *
 * @author Jerouris
 */
public class PartyAgencyIDSelect extends ComboBox {
    
    public PartyAgencyIDSelect(String caption){
        super(caption);
        setWidth(13,UNITS_EM);
        initData();
    }
    
    private void initData() {
      
        for (EPredefinedIdentifierIssuingAgency a : EPredefinedIdentifierIssuingAgency.values()) {
            if (!a.isDeprecated()) 
            {
                addItem(a.getSchemeID());
                setItemCaption(a.getSchemeID(),a.getSchemeAgency());
            }
       }
    }
    
    public String getSelectedAgencyName() 
    {
        return getItemCaption(getValue());
    
    }
    
    public String getSelectedAgencyID() 
    {
        return (String) getValue();
    }
    
}

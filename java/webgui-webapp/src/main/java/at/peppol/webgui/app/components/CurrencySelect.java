/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import com.vaadin.ui.ComboBox;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

/**
 *
 * @author Jerouris
 */
public class CurrencySelect extends ComboBox{
    
    public CurrencySelect(String caption) {
        super(caption);
        setWidth(4,UNITS_EM);
        initData();
    }

    private void initData() {

        for (CurrencyCodeContentType cc : CurrencyCodeContentType.values()) {
            addItem(cc.value());
            setItemCaption(cc.value(),cc.toString());
        }
    }
}

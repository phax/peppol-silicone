/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Select;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

/**
 *
 * @author Jerouris
 */
public class CurrencySelect extends Select{
    
    public CurrencySelect(String caption) {
        setCaption(caption);
        initData();
    }

    private void initData() {

      for (CurrencyCodeContentType cc : CurrencyCodeContentType.values()) {
            addItem(cc.value());
      }
      setValue("EUR");
    }
}

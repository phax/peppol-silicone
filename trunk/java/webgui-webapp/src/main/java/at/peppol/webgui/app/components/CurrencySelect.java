/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components;

import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import com.vaadin.ui.Select;

/**
 * @author Jerouris
 */
public class CurrencySelect extends Select {

  public CurrencySelect (final String caption) {
    setCaption (caption);
    initData ();
  }

  private void initData () {
    for (final CurrencyCodeContentType cc : CurrencyCodeContentType.values ()) {
      addItem (cc.value ());
    }
  }
  @Override
    public void attach() {
        setValue(CurrencyCodeContentType.EUR.value());
    }
}


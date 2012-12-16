package at.peppol.webgui.app.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;

public class Utils {
	
	public static XMLGregorianCalendar DateToGregorian(Date date) {
		GregorianCalendar greg = new GregorianCalendar();
        greg.setTime(date);

        XMLGregorianCalendar XMLDate = null;
		try {
			XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			XMLDate.setYear(greg.get(Calendar.YEAR));
	        XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
	        XMLDate.setDay(greg.get(Calendar.DATE));
		} catch (DatatypeConfigurationException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}
        
		return XMLDate;
	}
	
	public static Label requiredLabel(String text) {
		return new Label("<span>"+text+" <span style=\"color: red;\">*</span></span>", Label.CONTENT_XHTML);
	}
	
	public static void validateFormFields(Form form) throws InvalidValueException {
		Collection<String> props = (Collection<String>) form.getItemPropertyIds();
		List<Field> fields = new ArrayList<Field>();
		for (String property : props) {
			fields.add(form.getField(property));
		}
		List<BlurListener> listeners = new ArrayList<BlurListener>();
		for (Field f : fields) {
			if (f instanceof AbstractField) {
				AbstractField ff = (AbstractField)f;
				listeners.addAll((Collection<BlurListener>) ff.getListeners(BlurEvent.class));
			}
		}
		ValidatorsList.validateListenersNotify(listeners);
		form.validate();
	}
}

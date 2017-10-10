package main.java.utils;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import main.java.mgbean.UserService;
import main.java.pojos.Accounts;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* Az AccountsConverter a Converter interface implementációja
* A paraméterként kapott Stringből az adott id-jű Accounts objektumot,
* a paraméterként kapott Accounts objektumból az adott objktum id-ját kapjuk vissza
* 
*/

@ManagedBean(value = "AccountsConverter")
@SessionScoped
public class AccountsConverter implements Converter {

	@ManagedProperty("#{userService}")
	private UserService userService;

	public AccountsConverter() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if (arg2 == null || arg2.isEmpty()) {
			return null;
		}
		try {
			userService = findBean("userService");
			return userService.getAccount(Integer.valueOf(arg2));
		} catch (NumberFormatException e) {
			throw new ConverterException(new FacesMessage(arg2 + "is not valid ID"));
			
		}

	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 == null) {
			return "";
		}
		if (arg2 instanceof Accounts) {
			return String.valueOf(((Accounts) arg2).getId());
		} else {
			throw new ConverterException(new FacesMessage(arg2 + "is not a valid Accounts"));
		}
	}

}

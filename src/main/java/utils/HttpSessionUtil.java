package main.java.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* A httpSession, a servletRequest és a sessionben tárolt userName
* elérése
* 
*/

public class HttpSessionUtil {
	/**
	* 
	* @return HttpSession httpSession 
	* 
	*/
	public static HttpSession getHttpSession() {
		HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false);
		return httpSession;
	}
	/**
	*
	* @return HttpServletRequest servletRequest
	* 
	*/
	public static HttpServletRequest getServletRequest() {
		HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		return servletRequest;
	}
	/**
	*
	* @return String userName
	* 
	*/
	public static String getUserName() {
		String uName = getHttpSession().getAttribute("username").toString();
		return uName;
	}
}

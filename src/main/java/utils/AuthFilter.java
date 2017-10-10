package main.java.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* Az AuthFilter a Filter interface implementációja
* ellenőrzi, hogy van-e bejelentkezett felhasználó a HttpSession-ben
* és e- szerint módosítja a response-t
* 
*/
@WebFilter(filterName = "AuthFilter", urlPatterns={"/faces/*"}, servletNames={"Faces Servlet"}  )
public class AuthFilter implements Filter {

    public AuthFilter() {
    }

    /**
     * A metódus leellenőrzi, van-e bejelentkezet User tárolva a Http sessionben
     * Ha nincs, az index.xhtml-re küldi a felhasználót,
     * @param ServletRequest request
     * @param ServletResponse response
     * @param FilterChain chain
     * 
     * @throws IOException, ServletException
     * 
     */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		try{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		String reqUri = req.getRequestURI();
		if(reqUri.indexOf("/faces/index.xhtml")>0 || (session != null && session.getAttribute("username") != null) || 
				reqUri.contains("javax.faces.resource")){
			resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0); 
			chain.doFilter(request, response);
		}else{
			resp.sendRedirect(req.getContextPath()+"/faces/index.xhtml");
		}
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}

	public void destroy() {

	}

}

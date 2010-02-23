package org.ttdc.struts.network.interceptors;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecureUser;
import org.ttdc.util.Cookies;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class SecurityHelper {
	private static Logger log = Logger.getLogger(SecurityHelper.class);
	/*
	public static class HitThread extends Thread {
        String personId;
        HitThread(String personId) {
            this.personId = personId;
            start();
        }
        public void run() {
        	try{
        		UserService.getInstance().userHit(personId);
        	}
        	catch(Throwable t){
        		log.error(t);
        	}
        
        }
    }
    */

	
	
	/**
	 * @param context
	 * @param action
	 * @return
	 */
	public static Object loadUser(final ActionContext context, Object action) {
		SecureUser user = null;
		HttpServletRequest request = (HttpServletRequest) context.get(org.apache.struts2.StrutsStatics.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) context.get(org.apache.struts2.StrutsStatics.HTTP_RESPONSE);
		HttpSession session = request.getSession(true);
		session.setAttribute(Constants.SESSION_KEY_LAST_ACCESS_TIME,null);
		Object obj = session.getAttribute(Constants.SESSION_KEY_WEBUSER);
		
		if(obj != null){
			try{
				user = UserService.getInstance().loadPerson((String)obj);
				log.info("User \""+user.getLogin()+"\" loaded from session.");
			}
			catch(ServiceException e){
				//Bad mojo
				log.info("Session has a corrupt guid.");
				clearSessionAndCookies(request, response, session);
			}
			
		}
		else{
			String guid = Cookies.getCookieValue(request, Constants.COOKIE_USER_GUID);
			String password = Cookies.getCookieValue(request, Constants.COOKIE_USER_PWD);
			try{
				if(!(guid.equals("") && password.equals("")))
					user = UserService.getInstance().authenticate(guid,password,true);
				
				if(user != null){
					log.info("User \"" + user.getLogin() + "\" was authenticated against the DB.");
				}
			}
			catch(ServiceException e){
				user = null;
				clearSessionAndCookies(request, response, session);
						
				if (action instanceof ActionSupport) {
					((ActionSupport) action).addActionError(e.getSummary());
				}
			}
				
			if(user != null){
				session.setAttribute(Constants.SESSION_KEY_WEBUSER,user.getPersonId());
				log.info("User \""+user.getLogin()+"\" added to session.");
			}
			else{
				//This shouldn't ever really happen.  It will only happen if authenticate returns a null and doesnt throw an exception
				clearSessionAndCookies(request, response, session);
			}
		}
		
		session.setAttribute(Constants.SESSION_KEY_LAST_ACCESS_TIME,new Date());
		return user;
	}

	public static void clearSessionAndCookies(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		session.setAttribute(Constants.SESSION_KEY_WEBUSER,null);
		Cookies.deleteCookie(request, response, Constants.COOKIE_USER_GUID);
		Cookies.deleteCookie(request, response, Constants.COOKIE_USER_PWD);
	}
	
	/**
	 * This method updates the user object stored in the session. I'm initially creating this so that 
	 * if a user object is modified the session version can reflect the change.
	 * 
	 * 
	 * I'm fairly certain that this method does nothing useful now. I never save the person instance in session. 
	 * I just use the hibernate L2 cache.
	 * 
	 * @param context
	 * @param person
	 * 
	 * @deprecated
	 * 
	 */
	public static void refreshUserInSession(HttpServletRequest request, SecureUser person){
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(Constants.SESSION_KEY_WEBUSER);
		if(obj != null){
			session.setAttribute(Constants.SESSION_KEY_WEBUSER,person.getPersonId());
		}
		
	}
	
	/**
	 * Loads the last authenticated time from the session and returns that. If it doesnt exist, null is returned.
	 * @return
	 */
	/*
	public static Date getLastAccessTime(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(Constants.SESSION_KEY_WEBUSER);
		if(obj != null){
			return (Date)obj;
		}
		else
			return null;
	}
	*/
}

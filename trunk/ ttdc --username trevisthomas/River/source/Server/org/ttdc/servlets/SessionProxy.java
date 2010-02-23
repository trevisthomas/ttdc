package org.ttdc.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Figure out if you can use this class in RemoteServiceSession
 *
 */
public class SessionProxy {
	private HttpServletRequest request;
	public SessionProxy(HttpServletRequest request){
		this.request = request;
	}
	
	public final static String SESSION_KEY_PERSON_ID = "personIdSessionKey"; 
	
	public String getPersonIdFromSession(){
		String personId = null;
		if(exists(SESSION_KEY_PERSON_ID)){
			personId = readSessionString(SESSION_KEY_PERSON_ID);
		}
		return personId;
	}
	
	private boolean exists(String key){
		Object obj = readSessionObject(key);
		return obj != null;
	}
	
	public String readSessionString(String key){
		Object obj = readSessionObject(key);
		if(obj != null)
			return (String) obj;
		else
			throw new RuntimeException("Session does not contain a value at the requested key");
	}
	public void writeSessionString(String key, String value){
		getSession().setAttribute(key, value);
	}
	
	public Object readSessionObject(String key){
		Object obj = getSession().getAttribute(key);
		return obj;
	}
	
	private HttpSession getSession(){
		return request.getSession();
	}
}

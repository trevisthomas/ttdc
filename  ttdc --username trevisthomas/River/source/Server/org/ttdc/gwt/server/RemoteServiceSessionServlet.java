package org.ttdc.gwt.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.ttdc.biz.network.services.UserService;
import org.ttdc.gwt.client.services.RemoteServiceException;
import org.ttdc.gwt.server.dao.AccountDao;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;

import static org.ttdc.servlets.SessionProxy.SESSION_KEY_PERSON_ID;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * Consider removing this class and using SessionProxy in your servlets folder instead
 *
 */
public abstract class RemoteServiceSessionServlet extends RemoteServiceServlet{
	private final static Logger log = Logger.getLogger(RemoteServiceSessionServlet.class);
	//private final static String SESSION_KEY_PERSON_ID = "personIdSessionKey";
	private HttpSession getSession(){
		return getThreadLocalRequest().getSession();
	}
	
	protected boolean exists(String key){
		Object obj = readSessionObject(key);
		return obj != null;
	}
	
	protected String readSessionString(String key){
		Object obj = readSessionObject(key);
		if(obj != null)
			return (String) obj;
		else
			throw new RuntimeException("Session does not contain a value at the requested key");
	}
	protected void writeSessionString(String key, String value){
		getSession().setAttribute(key, value);
	}
	
	protected Object readSessionObject(String key){
		Object obj = getSession().getAttribute(key);
		return obj;
	}
	
	protected void removeSessionObject(String key){
		getSession().removeAttribute(SESSION_KEY_PERSON_ID);
	}
	
	protected String getPersonIdFromSession(){
		String personId = null;
		if(exists(SESSION_KEY_PERSON_ID)){
			personId = readSessionString(SESSION_KEY_PERSON_ID);
		}
		return personId;
	}
	
	/**
	 * @deprecated
	 */
	protected Person loadOrCreateActiveUser(){
		try{
			Person person;
			if(exists(SESSION_KEY_PERSON_ID)){
				Persistence.beginSession();
				String personId = readSessionString(SESSION_KEY_PERSON_ID);
				person = PersonDao.loadPerson(personId);
				AccountDao.userHit(person.getPersonId());
				Persistence.commit();
			}
			else{
				person = InitConstants.ANONYMOUS;
				//person = UserService.getInstance().getAnonymousUser();
				//rememberActiveUser(person); Should have had a test for this
			}
			return person;
		} catch (Throwable t) {
			Persistence.rollback();
			throw new RuntimeException(t);
		}
	}
	
	
		
	protected void rememberActiveUser(Person person){
		writeSessionString(SESSION_KEY_PERSON_ID, person.getPersonId());
	}
	
	protected void forgetActiveUser(){
		removeSessionObject(SESSION_KEY_PERSON_ID);
	}
	
	protected RemoteServiceException buildRemoteServiceException(Throwable t){
		if(t instanceof RemoteServiceException) 
			return (RemoteServiceException)t;
		
		getLogger().error(t);
		
		return new RemoteServiceException(getStackTrace(t));
//		if(StringUtils.isNotEmpty(t.getMessage()))
//			return new RemoteServiceException(t.getMessage());
//		else
//			return new RemoteServiceException(t.toString());
	} 
	
	public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
	/**
	 * Override this method to get context sensitive logging
	 * 
	 * @return
	 */
	protected Logger getLogger(){
		return log;
	}
	
}

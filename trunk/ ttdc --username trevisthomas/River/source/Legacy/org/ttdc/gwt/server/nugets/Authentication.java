package org.ttdc.gwt.server.nugets;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.Cryptographer;
import org.ttdc.util.ServiceException;

public class Authentication {
	private final static  Logger log = Logger.getLogger(Authentication.class);
	
	public static Person authenticateEncrypted(String guid, String passwordEncrypted) throws ServiceException{
		return authenticate(guid, passwordEncrypted, true);
	}
	public static Person authenticate(String login, String password) throws ServiceException{
		return authenticate(login, password, false);
	}
	
	/**
	 * Authenticate a user form login and password.  Or from GUID and encrypted password, when encrypted is set
	 * to true.  If the password is encrypted the key should be a guid, if the pwd is plain text the key should be
	 * the login.
	 * 
	 * Trevis, this is a poor public method. to complicated. Should have made two methods perhaps having them
	 * call a single private one to keep the exception logic in one place.
	 * 
	 * @param key
	 * @param password
	 * @param encrypted - Set this value to true if the password is encrypted.
	 * @return
	 * @throws ServiceException
	 */
	private static Person authenticate(String key, String password, boolean encrypted) throws ServiceException{
		try{
			Cryptographer crypto = new Cryptographer(null);
			Session session = Persistence.beginSession();
			Person p;
			Query query = null;
			if(!encrypted){
				query = session.getNamedQuery("person.authenticate")
					.setCacheable(true)
					.setString("login", key)
					.setString("password", crypto.encrypt(password));
			}
			else{
				query = session.getNamedQuery("person.authenticateByGuid")
					.setCacheable(true)
					.setString("guid", key)
					.setString("password", password);
			}
			
			p = (Person)query.uniqueResult();
			
			if(p == null){
				throw new ServiceException("Invalid login or password");
			}
			else if (p.getStatus().equals(Person.STATUS_INACTIVE)){
				EmailHelper.sendActivateionEmail(p);
				throw new ServiceException("Your account has not been activated.  An email has been sent to "+p.getEmail()
						+" with instructions to activate your account. ");
			}
			else if(p.getStatus().equals(Person.STATUS_LOCKED)){
				throw new ServiceException("Your account has been locked. Please contact an administrator for more informaion.");
			}
			else{
				p.initialize();
				Persistence.commit();
				return p;
			}
		}
		
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			throw new ServiceException(t);
		}
	}
	
	
	
}

package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.util.BizException;
import org.ttdc.util.Cryptographer;
import org.ttdc.util.SendGmail;
//import org.ttdc.util.ServiceException;
import org.ttdc.util.UrlEncoder;

public class AccountDao {
	private final static Logger log = Logger.getLogger(AccountDao.class);
	private String personId;
	private String login;
	private String password;
	private String name;
	private String email;
	private Date birthday;
	private String bio;
	private Image image;
	private Style style;
//	public final static String ACTIVATE_URL_SUFFIX = ""; //?
//	public final static String RESET_PASSWORD_URL_SUFFIX = "";
	
	
	public static void userHit(String personId){
		Person person = (Person)session().load(Person.class, personId);
		int hits = person.getHits();
		hits++;
		person.setHits(hits);
		person.setLastAccessDate(new Date());
		
		//This was newly added on Jan 2, 2011!!  (Copied from RpcServlet)
		GPerson gPerson = FastPostBeanConverter.convertPerson(person);
		PersonEvent event = new PersonEvent(PersonEventType.TRAFFIC,gPerson);
		ServerEventBroadcaster.getInstance().broadcastEvent(event);

		// And this on Dec 22, 2016
		ServerEventBroadcaster.getInstance().pushBadgeToZero(person.getPersonId());

	}
	
	/**
	 * Validates a password string.  Throws an exception if it fails, returns the encrypted password if it's good.
	 * 
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	private static String validateAndEncryptPassword(String password){
		String pwd;
		Cryptographer crypto = new Cryptographer(null);
		if(password == null)
			throw new RuntimeException("Password is garbage");
		else if (password.length() < 3)
			throw new RuntimeException("Password is too short");
		else{
			pwd = crypto.encrypt(password);
		}
		return pwd;
	}
	/**
	 * 
	 * Create a new user. Users are created in a pending state and must be activated 
	 *  before they can login.
	 *  
	 * @param login
	 * @param password
	 * @param name
	 * @param email
	 * @param birthday
	 * @param bio
	 * @throws ServiceException
	 */
	public Person create(){
		try{
			Person p;
			if(login == null || login.length() < 3){
				throw new RuntimeException("Login must be longer");
			}
			if(password == null || password.length() < 3){
				throw new RuntimeException("Password must be longer.");
			}
			Query query = session().getNamedQuery("person.getByLogin").setString("login", login);
			p = (Person)query.uniqueResult();
			if(p != null){
				throw new RuntimeException("Login name is in use.");
			}
			p = new Person();
			
			p.setLogin(login);
			p.setBio(bio);
			p.setBirthday(birthday);
			p.setEmail(email);
			p.setName(name);
			p.setDate(new Date());
			p.setPassword(validateAndEncryptPassword(password));
			p.setStatus(Person.STATUS_INACTIVE);
			
			session().save(p);
			session().flush(); //This used to commit. Now im counting on flush to insure that it can save before sending the email
			return p;
		}
		catch(RuntimeException e){
			log.error(e);
			rollback();
			throw e;
		}
	}
	
	/**
	 * Update person modifiable attributes.  
	 * 
	 */
	public Person update(){
		try{
			Person p = PersonDao.loadPerson(personId);
			if(StringUtil.notEmpty(bio))
				p.setBio(bio);
			if(birthday != null)
				p.setBirthday(birthday);
			if(StringUtil.notEmpty(email))
				p.setEmail(email);
			if(StringUtil.notEmpty(name))
				p.setName(name);
			if(image != null)
				p.setImage(image);
			if(style != null)
				p.setStyle(style);
			
			session().save(p);
			session().flush();

			return p;
		}
		catch (RuntimeException t) {
			log.error(t);
			rollback();
			throw t;
		} 
	}
	
	public static void sendActivateionEmail(Person p, String activateUrl){
		if(StringUtils.isEmpty(activateUrl)) 
			throw new RuntimeException("AccountDao is not properly configured to send an activation email.");
		String html = "<html><body><h2>Welcome to TrevisThomasDotCom!</h2><p>Click the following link to activate your account. <a href=\""+activateUrl+"?key="+p.getPersonId()+"\">Activate!</a></p></body></html>";
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "Instructions to complete activation", "Trevis Thomas", p.getEmail());
		} catch (BizException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sends an email to the user with the guid and a magic value to allow me to manage the lifespan of this link
	 * 
	 * @param p
	 * @throws ServiceException
	 */
	public static void sendPasswordResetEmail(Person p, String resetPwdUrl){
		//Trevis, you need to figure out a way to not need to hard code this crap
		DateFormat defaultDateTimeFormat = new SimpleDateFormat();
		String now = defaultDateTimeFormat.format(new Date());
		Cryptographer crypto = new Cryptographer(null);
		String magic = UrlEncoder.encode(crypto.encrypt(now));
		String glitter = UrlEncoder.encode(crypto.encrypt(p.getPersonId().toString()));
		
		String html = "<html><body><h2>Password reset instructions</h2><p>Click the following link to reset your password. <a href=\""+resetPwdUrl+"?glitter="+glitter+"&magic="+magic+"\">Reset Password</a></p></body></html>";
		
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "Instructions to reset your password", "Trevis Thomas", p.getEmail());
		} catch (BizException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
	
	
//	/**
//	 * Sends out a limited time use email for the user to come in and reset their password
//	 * @param login
//	 * @param email
//	 */
//	public void resetPasswordRequest(Person person, String email) throws ServiceException{
//		try{
//			if(person == null){
//				throw new ServiceException("Login name not regocnized");
//			}
//			else if(!person.getEmail().equals(email.trim())){
//				throw new ServiceException("The email address you entered doesnt match your login. Contact an admin for furter assistance.");
//			}
//			else{
//				sendPasswordResetEmail(person);
//			}
//				
//		}
//		catch(ServiceException e){
//			throw e;
//		}
//		catch(Throwable t){
//			log.error(t.getMessage());
//			throw new ServiceException(t);
//		}
//	}
	
	/**
	 * Takes the magic value emailed to the user and validates it
	 * 
	 * @param magic
	 * @return
	 */
	public static Person resetPasswordMagicValidator(String glitter, String magic){
		try{
			Cryptographer crypto = new Cryptographer(null);
			String value = UrlEncoder.decode(crypto.decrypt(magic));
			DateFormat defaultDateTimeFormat = new SimpleDateFormat();
			Date then = defaultDateTimeFormat.parse(value);
			Date now = new Date();
			if((now.getTime() - then.getTime()) > 60*60*1000){
				throw new RuntimeException("Link is nolonger valid. Resubmit request to reset password");
			}
			
			String personId =  UrlEncoder.decode(crypto.decrypt(glitter));
			Person p = PersonDao.loadPerson(personId);
			if(p != null)
				return p;
			else
				throw new RuntimeException("Resubmit request to reset password or contact administrator.");
		}
		catch(Throwable t){
			log.error(t);
			throw new RuntimeException("Internal error. Resubmit request or contact administrator.");
		}
	}
	
	/**
	 * This method should only be called after things have been checked out because it 
	 * the least secure version
	 * 
	 * @param guid
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	public static Person resetPassword(String guid, String password){
		try{
			Person p = PersonDao.loadPerson(guid);
			p.setPassword(validateAndEncryptPassword(password));
//			session().update(p);
//			session().refresh(p);
			session().save(p);
			return p;
		}
		catch(RuntimeException e){
			rollback();
			log.error(e);
			throw new RuntimeException(e.getMessage() + " Password reset failed. ");
		}
	}
	
	
	/**
	 * Authenticates a person based on guid and encrypted password.  Basically from the cookie
	 * @param guid
	 * @param ecryptedPassword
	 * @return
	 */
	public static Person authenticate(String guid, String ecryptedPassword){
		return authenticate(guid,ecryptedPassword,true);
	}
	
	/**
	 * Plain text authentication
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	public static Person login(String login, String password){
		return authenticate(login,password,false);
	}
	
	/**
	 * Authenticate a user form login and password.  Or from GUID and encrypted password, when encrypted is set
	 * to true.  If the password is encrypted the key should be a guid, if the pwd is plain text the key should be
	 * the login.
	 * 
	 * Trevis, this is a poor public method. to complicated. Should have made two methods perhaps having them
	 * call a single private one to keep the exception logic in one place.
	 * 
	 */
	private static Person authenticate(String key, String password, boolean encrypted){
		Cryptographer crypto = new Cryptographer(null);
		Person p;
		Query query = null;

		if(!encrypted){
			query = session().getNamedQuery("person.authenticate")
				.setCacheable(true)
				.setString("login", key)
				.setString("password", crypto.encrypt(password));
		}
		else{
			query = session().getNamedQuery("person.authenticateByGuid")
				.setCacheable(true)
				.setString("guid", key)
				.setString("password", password);
		}
		
		p = (Person)query.uniqueResult();
		
		if(p == null){
			throw new RuntimeException("Invalid login or password.");
		}
		else if (p.getStatus().equals(Person.STATUS_INACTIVE)){
			throw new RuntimeException("Your account has not been activated.  An email was sent to "+p.getEmail()
					+" with instructions to activate your account. If you never recieved the email or if the address is incorrect shoot me an email.");
		}
		else if(p.getStatus().equals(Person.STATUS_LOCKED)){
			throw new RuntimeException("Your account has been locked. Please contact an administrator for more informaion.");
		}
		else{
			p.initialize();
			return p;
		}
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}
}

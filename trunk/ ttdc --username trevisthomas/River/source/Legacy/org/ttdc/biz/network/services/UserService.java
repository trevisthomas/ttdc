package org.ttdc.biz.network.services;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.biz.network.services.helpers.TrafficCache;
import org.ttdc.biz.network.services.helpers.UserPrivilege;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Style;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.util.BizException;
import org.ttdc.util.Cryptographer;
import org.ttdc.util.SendGmail;
import org.ttdc.util.ServiceException;
import org.ttdc.util.UrlEncoder;
import org.ttdc.util.struts.WebHelper;

/**
 * Do all sorts of stuff with users. 
 * 
 * @author Trevis
 *
 */
public final class UserService {
	private final static  Logger log = Logger.getLogger(UserService.class);
	private String nwsTagId;
	private String privateTagId;
	
	private  DateFormat defaultDateTimeFormat = new SimpleDateFormat();
	
	private UserService(){
//		try{
//			Session session = Persistence.beginSession();
//			Query query = session.getNamedQuery("tag.getByValueAndType").setString("value", Tag.VALUE_NWS).setString("type", Tag.TYPE_DISPLAY);
//			Tag tag = (Tag)query.uniqueResult();
//			this.nwsTagId = tag.getTagId();
//			
//			query = session.getNamedQuery("tag.getByValueAndType").setString("value", Tag.VALUE_PRIVATE).setString("type", Tag.TYPE_DISPLAY);
//			tag = (Tag)query.uniqueResult();
//			privateTagId = tag.getTagId();
//			
//			log.info("UserService singleton instance created.");
//		}
//		catch(Throwable t){
//			log.error(t);
//			throw new ExceptionInInitializerError(t);
//		}
		
	}
	
	private static Person anonymous;
	static{
		try{
			anonymous = new Person();
			anonymous.setAnonymous(true);
			anonymous.setPersonId("ANON_PERSON_ID");
			anonymous.setEmail("trevisthomas@gmail.com");
			anonymous.setLastAccessDate(new Date());
			anonymous.setLogin("anonymous");
			anonymous.setPassword("anonymous");
			anonymous.setStatus(Person.STATUS_INACTIVE);
			anonymous.setName("anonymous");
			anonymous.setStyle(ThemeService.getInstance().getDefaultStyle());
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("userObjectTemplate.getForValue").setString("value", UserObjectTemplate.WIDGET_CALENDAR);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			
			UserObject uo = new UserObject();
			uo.setType(UserObject.TYPE_WIDGET);
			uo.setValue(t.getValue());
			uo.setTemplate(t);
			uo.setOwner(anonymous);
			uo.setName(UserObject.POSITION_RIGHT);
			//anonymous.objects.(uo);
			//anonymous.objects = new ArrayList<UserObject>();
			//anonymous.objects.add(uo);
			anonymous.getObjects().add(uo);
		}
		catch(Exception e){
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public Person getAnonymousUser(){
		return anonymous;
	}
	
	public static UserService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private final static UserService INSTANCE = new UserService();
	}
	
	/**
	 * Validates a password string.  Throws an exception if it fails, returns the encrypted password if it's good.
	 * 
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	private String prepPassword(String password) throws ServiceException{
		String pwd;
		Cryptographer crypto = new Cryptographer(null);
		if(password == null)
			throw new ServiceException("Password is garbage");
		else if (password.length() < 3)
			throw new ServiceException("Password is too short");
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
	public void createUser(String login, String password, String name, String email, String birthday, String bio) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			
			Person p;
			if(login == null || login.length() < 3){
				throw new ServiceException("Login must be longer");
			}
			if(password == null || password.length() < 3){
				throw new ServiceException("Password must be longer.");
			}
			Query query = session.getNamedQuery("person.getByLogin").setString("login", login);
			p = (Person)query.uniqueResult();
			if(p != null){
				throw new ServiceException("Login name is in use.");
			}
			p = new Person();
			
			p.setLogin(login);
			p.setBio(bio);
			if(!birthday.trim().equals(""))
				p.setBirthday(df.parse(birthday));
			p.setEmail(email);
			p.setName(name);
			p.setDate(new Date());
			p.setPassword(prepPassword(password));
			p.setStatus(Person.STATUS_INACTIVE);
			
			session.save(p);
			Persistence.commit();
			sendActivateionEmail(p);
			
		}
		catch(ParseException e){
			throw new ServiceException("Birthdate is invalid.",e);
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	private void sendActivateionEmail(Person p) throws ServiceException{
		//Trevis, you need to figure out a way to not need to hard code this crap 
		
		String html = "<html><body><h2>Welcome to We Be Friends!</h2><p>Click the following link to activate your account. <a href=\""+WebHelper.getSiteName()+"/user/activate.action?key="+p.getPersonId()+"\">Activate!</a></p></body></html>";
		
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "Instructions to complete activation", "Trevis Thomas", p.getEmail());
		} catch (BizException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Sends an email to the user with the guid and a magic value to allow me to manage the lifespan of this link
	 * 
	 * @param p
	 * @throws ServiceException
	 */
	private void sendPasswordResetEmail(Person p) throws ServiceException{
		//Trevis, you need to figure out a way to not need to hard code this crap
		String now = defaultDateTimeFormat.format(new Date());
		Cryptographer crypto = new Cryptographer(null);
		String magic = UrlEncoder.encode(crypto.encrypt(now));
		String glitter = UrlEncoder.encode(crypto.encrypt(p.getPersonId().toString()));
		
		String html = "<html><body><h2>Password reset instructions</h2><p>Click the following link to reset your password. <a href=\""+WebHelper.getSiteName()+"/user/resetPassword.action?glitter="+glitter+"&magic="+magic+"\">Reset Password</a></p></body></html>";
		
		try {
			SendGmail.sendMail(html, SendGmail.ContentType.HTML, "Instructions to reset your password", "Trevis Thomas", p.getEmail());
		} catch (BizException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Sends out a limited time use email for the user to come in and reset their password
	 * @param login
	 * @param email
	 */
	public void resetPasswordRequest(String login, String email) throws ServiceException{
		try{
			Person person = readUser(login);
			if(person == null){
				throw new ServiceException("Login name not regocnized");
			}
			else if(!person.getEmail().equals(email.trim())){
				throw new ServiceException("The email address you entered doesnt match your login. Contact an admin for furter assistance.");
			}
			else{
				sendPasswordResetEmail(person);
			}
				
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t.getMessage());
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Takes the magic value emailed to the user and validates it
	 * 
	 * @param magic
	 * @return
	 */
	public Person resetPasswordMagicValidator(String glitter, String magic) throws ServiceException{
		try{
			Cryptographer crypto = new Cryptographer(null);
			String value = UrlEncoder.decode(crypto.decrypt(magic));
			Date then = defaultDateTimeFormat.parse(value);
			Date now = new Date();
			if((now.getTime() - then.getTime()) > 60*60*1000){
				throw new ServiceException("Link is nolonger valid. Resubmit request to reset password");
			}
			
			String personId =  UrlEncoder.decode(crypto.decrypt(glitter));
			Person p = loadPerson(personId);
			if(p != null)
				return p;
			else
				throw new ServiceException("Resubmit request to reset password");
		}
		catch(Throwable t){
			log.error(t);
			throw new ServiceException("Resubmit request to reset password.");
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
	public Person resetPassword(String guid, String password) throws ServiceException{
		try{
			Person p = loadPerson(guid);
			
			Session session = Persistence.beginSession();
			p.setPassword(prepPassword(password));
			session.update(p);
			Persistence.commit();
			
			return loadPerson(guid);
		}
		catch(Throwable t){
			Persistence.rollback();
			if(t instanceof ServiceException)
				throw (ServiceException)t;
			else{
				log.error(t);
				throw new ServiceException("Password reset failed.");
			}
		}
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
	public Person authenticate(String key, String password, boolean encrypted) throws ServiceException{
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
				sendActivateionEmail(p);
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
	
	/**
	 * Loads a list of user object templates that are available for this user. 
	 * this method filters out templates that the user has already used.
	 * 
	 * @param guid
	 * @return
	 */
	public List<UserObjectTemplate> readAvailableUserObjectWebPageTemplates(String guid) throws ServiceException{
		try{
			List<UserObjectTemplate> list = new ArrayList<UserObjectTemplate>();
			List<UserObjectTemplate> all = readUserObjectTemplates(UserObjectTemplate.TEMPLATE_WEBPAGE);
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person user = (Person)query.uniqueResult();
			
			
			Map<String,UserObject> map = new HashMap<String,UserObject>();
			
			for(UserObject uo : user.getObjects()){
				if(uo.getTemplate() != null && UserObjectTemplate.TEMPLATE_WEBPAGE.equals(uo.getTemplate().getType()))
					map.put(uo.getTemplate().getTemplateId(),uo);
			}
			
			for(UserObjectTemplate t : all){
				UserObject uo = map.get(t.getTemplateId());
				if(uo == null)
					list.add(t);
			}
			Persistence.commit();
			return list;
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
		
	}
	
	/**
	 * Same as above, only this one is for widgets.
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceException
	 */
	public List<UserObjectTemplate> readAvailableUserObjectWidgetTemplates(String guid) throws ServiceException{
		try{
			List<UserObjectTemplate> list = new ArrayList<UserObjectTemplate>();
			List<UserObjectTemplate> all = readUserObjectTemplates(UserObjectTemplate.TEMPLATE_WIDGET);
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person user = (Person)query.uniqueResult();
			
			Map<String,UserObject> map = new HashMap<String,UserObject>();
			
			for(UserObject uo : user.getObjects()){
				if(uo.getTemplate() != null && UserObjectTemplate.TEMPLATE_WIDGET.equals(uo.getTemplate().getType()))
					map.put(uo.getTemplate().getTemplateId(),uo);
			}
			
			for(UserObjectTemplate t : all){
				UserObject uo = map.get(t.getTemplateId());
				if(uo == null)
					list.add(t);
			}
			Persistence.commit();
			return list;
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
		
	}
	
	
	
	/**
	 * Generates an anonymous Person object because every page requires
	 * an person object. 
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Person getAnnonymousUser(){
		return anonymous;			
	}
	
	/**
	 * Updates the the person hit record when the user access the site.
	 *  
	 * @param person
	 */
	public void userHit(Person person) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			person = (Person)session.get(Person.class, person.getPersonId());
			person.initialize();
			
			int hits = person.getHits();
			hits++;
			person.setHits(hits);
			person.setLastAccessDate(new Date());
			
			Persistence.commit();
			
			
			
			
			TrafficCache.getInstance().insert(person);
			
			
		}
		catch(Throwable t){
			log.error(t.getMessage());
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Updates a persons style to the one requested by styleId
	 * 
	 * @param person person to choose style for
	 * @param styleId style to use
	 * @throws ServiceException if styleId is invalid, person is invalid, or in case of a hibernate problem 
	 */
	public void chooseStyle(Person person, String styleId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			person = (Person)session.get(Person.class, person.getPersonId());
			Style style = (Style)session.get(Style.class, styleId);
			person.setStyle(style);
			session.update(person);
			Persistence.commit();			
		}
		catch(Exception e){
			log.error(e.getMessage());
			Persistence.rollback();
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Updates the front page mode for a user.  This method was added when i made the site persist this choice when selecting
	 * the tab from the front page.  If the user is anonoymous this method just returns
	 * 
	 * @param person
	 * @param mode
	 * @throws ServiceException
	 */
	public void updateFrontPageMode(Person person, String mode) throws ServiceException{
		try{
			if(person.isAnonymous()) return;
			Session session = Persistence.beginSession();
			
			
			UserObject uo = person.getObjectType(UserObject.TYPE_FRONTPAGE_MODE);;
			if(mode != null && mode.trim().length() > 0){
				if(!person.getFrontPageMode().equals(mode)){
					if(uo == null){
						uo = new UserObject();
						uo.setType(UserObject.TYPE_FRONTPAGE_MODE);
						uo.setValue(mode.trim());
						uo.setOwner(person);
						session.save(uo);
					}
					else{
						if(!uo.getValue().equals(mode.trim())){
							uo.setValue(mode.trim());
						}
						session.update(uo);
					}
				}
			}
			
			Persistence.commit();
			
			session = Persistence.beginSession();
			
			session.refresh(person);			
			
			//For some reason session.refresh() wasnt cutting it.
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", person.getPersonId());
			person = (Person)query.uniqueResult();
			person.initialize();
			Persistence.commit();
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 		
	}
	
	/**
	 * Update person modifiable attributes.  
	 * 
	 * @param guid
	 * @param person
	 * @return
	 * @throws ServiceException
	 */
	public Person updateUser(String guid, Person user) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			p.setBio(user.getBio());
			p.setBirthday(user.getBirthday());
			p.setEmail(user.getEmail());
			p.setName(user.getName());

			UserObject uo = p.getObjectType(UserObject.TYPE_AUTO_UPDATE_MODE);
			if(uo != null){
				session.delete(uo);
			}
			/*
			String mode = user.getNotificationMode();
			if(mode != null && mode.trim().length()>0){
				uo = new UserObject();
				uo.setType(UserObject.TYPE_AUTO_UPDATE_MODE);
				uo.setOwner(p);
				uo.setValue(mode.trim());
				session.save(uo);
			}
			*/
			uo = p.getObjectType(UserObject.TYPE_FRONTPAGE_MODE);
			String mode = user.getFrontPageMode();
			if(mode != null && mode.trim().length() > 0){
				if(uo == null){
					uo = new UserObject();
					uo.setType(UserObject.TYPE_FRONTPAGE_MODE);
					uo.setValue(mode.trim());
					uo.setOwner(p);
					session.save(uo);
					//p.getObjects().add(uo);
				}
				else{
					if(!uo.getValue().equals(mode.trim())){
						uo.setValue(mode.trim());
					}
					session.update(uo);
				}
			}
			
			//Front Page
			
			//session.update(p);
						
			Persistence.commit();
			
			session = Persistence.beginSession();
			
			session.refresh(p);			

			//For some reason session.refresh() wasnt cutting it.
			query = session.getNamedQuery("person.getByGuid").setString("guid", p.getPersonId());
			p = (Person)query.uniqueResult();
			p.initialize();
			Persistence.commit();
			
			return p;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
	}
	
	/**
	 * Authenticates the old password and then sets the new one.
	 * 
	 * @param guid
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws ServiceException
	 */
	public Person resetPassword(String guid, String oldPassword, String newPassword) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			Persistence.commit();
			
			Person user = authenticate(p.getLogin(), oldPassword, false);
			
			session = Persistence.beginSession();
			user.setPassword(prepPassword(newPassword));
			session.update(user);
			Persistence.commit();
			
			return loadPerson(guid);
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
	}
	
	
	/**
	 * Add, update user Avatar.
	 * @param guid
	 * @param file
	 * @return returns the updated person
	 */
	public Person updateUserAvatar(String guid, File file, String fileName) throws ServiceException{
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			p.initialize();
			Persistence.commit();
			
			if(p.getImage() != null){
				//Clearing the image from the user first to be safe
				String imageId = p.getImage().getImageId();
				session = Persistence.beginSession();
				p.setImage(null);
				session.update(p);
				Persistence.commit();
				ImageService.getInstance().deleteImage(imageId);
			}
			
			session = Persistence.beginSession();
			String newFileName = p.getLogin()+"_"+fileName;
			ImageFull fullImage = ImageService.getInstance().createImageAndSave(p, file, newFileName, true);
			
			//This little trick is so that the Person can be associatd with the proper guid but 
			//not have to really know anything about the blob. This is for caching
			Image image = ImageService.getInstance().readImageById(fullImage.getImageId());
			
			session = Persistence.beginSession();
			p.setImage(image);
			session.update(p);
			//session.refresh(p);
			//p.initialize();
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(p);
			p.initialize();
			
			return p;
			//return readUserById(guid);
			
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
	}
	
	public void deleteUserAvatar(String guid){
		
	}
	
	/**
	 * Loads a person from hibernate cache or from db
	 * 
	 * @param personId
	 * @return
	 */
	
	public Person loadPerson(String personId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Person p = (Person)session.load(Person.class, personId);
			
			p.initialize();	
			
			Persistence.commit();
			
			return p;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Reads a person from the DB
	 * 
	 * @param personId GUID of the person you're looking for
	 * @return
	 * @throws ServiceException
	 */
	public Person readPerson(String personId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Person p;
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", personId);
			p = (Person)query.uniqueResult();
			//p = (Person)session.load(Person.class, personId);
			
			if(p == null){
				throw new ServiceException("Invalid person id.");
			}
			else{
				p.initialize();	
			}
			Persistence.commit();
			return p;
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
	}
	
	
	/**
	 * Loads a user object from a login name.
	 * 
	 * @param login
	 * @return
	 * @throws ServiceException
	 */
	public Person readUser(String login) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByLogin").setString("login", login);
			Person p = (Person)query.uniqueResult();
			
			
			if(p == null){
				throw new ServiceException("Invalid person id.");
			}
			else{
				p.initialize();	
			}
			
			Persistence.commit();
			return p;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	
	/**
	 * I should probably not have this but... lets just call it a place holder 
	 * 
	 * @param userId
	 * @throws ServiceException
	 */
	public void deleteUser(String personId) throws ServiceException{
		/* delete the user and any comments? Maybe just mark the account deleted
		 * somehow 
		 */
	}
	/**
	 * Add a user privilege
	 * @param privilege - the privilege to add
	 * @param guid - personid
	 */
	public Person grantPrivilege(String guid, String privilege) throws ServiceException{
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			
			query = session.getNamedQuery("privilege.getByValue").setString("value", privilege);
			Privilege priv = (Privilege) query.uniqueResult();
			
			if(!p.addPrivilege(priv)){
				throw new ServiceException(p.getLogin()+" already has "+priv.getName());
			}
			else{
				session.save(p);
			}
			//session.refresh(p);
			Persistence.commit();
			
			return p;
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
	}
	
	/**
	 * One click to grant default privileges.
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceException
	 */
	public Person grantDefaultPrivileges(String guid){
		Person p = null;
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			p = (Person) query.uniqueResult();
			
			query = session.getNamedQuery("privilege.getByValue").setString("value", Privilege.POST);
			Privilege priv = (Privilege) query.uniqueResult();
			p.addPrivilege(priv);
			
			query = session.getNamedQuery("privilege.getByValue").setString("value", Privilege.VOTER);
			priv = (Privilege) query.uniqueResult();
			p.addPrivilege(priv);
			
			session.save(p);
			
			//session.refresh(p);
			Persistence.commit();
			
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
		}
		return p;
	}
	
	
	/**
	 * Revoke a user privilege
	 * @param privilege - the privilige to revoke
	 * @param guid - personId
	 */
	public Person revokePrivilege(String guid, String privilege) throws ServiceException{
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			
			query = session.getNamedQuery("privilege.getByValue").setString("value", privilege);
			Privilege priv = (Privilege) query.uniqueResult();
			
			if(!p.removePrivilege(priv)){
				throw new ServiceException(p.getLogin()+" does not have "+priv.getName());
			}
			else{
				session.save(p);
			}
			//session.refresh(p);
			Persistence.commit();
			
			return p;
		}
		catch(ServiceException e){
			throw e;
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		} 
		
	}
	
	
	/**
	 * returns the full list of privileges available
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Privilege> getAllPrivileges() throws ServiceException{
		List<Privilege> list = new ArrayList<Privilege>();
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("privilege.getAll");
			list.addAll(query.list());
			Persistence.commit();
		}
		catch (Throwable t) {
			log.error(t);
			throw new ServiceException(t);
		} 
		return list;
	}
	
	/**
	 * Get UserPrivileges.  This returns a list all privileges stuffed into a UserPrivilege helper objects 
	 * with the "granted" field set for privileges that the user has been granted. 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UserPrivilege> readUserPrivileges(String guid) throws ServiceException{
		 List<UserPrivilege> list = new ArrayList<UserPrivilege>();
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person) query.uniqueResult();
			
			query = session.getNamedQuery("privilege.getAll");
			for(Privilege priv : (List<Privilege>)query.list()){
				list.add(new UserPrivilege(priv,p.hasPrivilege(priv.getValue())));
			}
			Persistence.commit();
		}
		catch (Throwable t) {
			log.error(t);
			throw new ServiceException(t);
		} 
		return list;
	}
	
	/**
	 * Locks a user account so that they cant post. It's probably ok that they 
	 * still see widgets or whatever... maybe. 
	 * 
	 * @param userId
	 * @throws ServiceException
	 */
	public void lockUser(String personId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", personId);
			Person p = (Person)query.uniqueResult();
			if(p == null){
				throw new ServiceException("Can not find user to lock him or her out.");
			}
			p.setStatus(Person.STATUS_LOCKED);
			session.update(p);
			Persistence.commit();
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	/**
	 * Unlock a user's account
	 * 
	 * @param userId
	 * @throws ServiceException
	 */
	public void unlockUser(String personId) throws ServiceException{
		//set status to active
	}
	
	/**
	 * Person activation is for new accounts.  Accounts are created in an inactive state until
	 * the user activates the account.  Probably via email or something. 
	 * 
	 * This method is used by the users when they follow the activation link in their email
	 * 
	 * @param guid 
	 * @throws ServiceException
	 */
	public Person activateUser(String guid) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person)query.uniqueResult();
			
			if(p == null){
				throw new ServiceException("Account can not be activated. You better talk to someone.");
			}
			else if(!p.getStatus().equals(Person.STATUS_INACTIVE)){
				throw new ServiceException("You sneeky devil, this account isn't inactive. Try again pal.");
			}
			else{
				p.setStatus(Person.STATUS_ACTIVE);
				session.update(p);
				Persistence.commit();
				
				if(getActiveUsers().size() == 1){
					grantPrivilege(guid, Privilege.ADMINISTRATOR); //If this is the first user make them admin
				}
				else{
					grantDefaultPrivileges(guid);
				}
				

				return p;
			}
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	
	
	/**
	 * Update user status. This is an admin function accessed from the Admin User Properties page
	 * 
	 * I added sync here to to test some things. It may not be necessary.  Should try going without
	 * to see if all is good.  I was trying to stop a "bizare concurrent session container access"
	 * exception that i was getting. It may have been completly un releated because i had other problems.
	 *  
	 * @param guid
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	public synchronized Person updateStatus(String guid, String status) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getByGuid").setString("guid", guid);
			Person p = (Person)query.uniqueResult();
			
			p.setStatus(status);
			session.update(p);
			Hibernate.initialize(p);
			Persistence.commit();
			return p;
		}
		catch(Throwable t){
			log.error(t.getMessage());
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	/**
	 * Not sure why i'd ever do this but... i guess i could deactivate a user.  This means that 
	 * they cant log in at all.
	 * 
	 * @param userId
	 * @throws ServiceException
	 */
	public void deactivateUser(String personId) throws ServiceException{
		//set status to inactive
	}
	
	/**
	 * gets all active users. 
	 * 
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<Person> getActiveUsers() throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getAllActive");
			List<Person> persons = (List<Person>)query.list();
			
			for(Person p : persons){
				p.initialize();
			}
			
			Persistence.commit();
			
			return persons;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		finally{
			
		}
	}
	
	/**
	 * Gets all users
	 * 
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<Person> getAllUsers(Person person) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("person.getAll");
			List<Person> persons = (List<Person>)query.list();
			
			for(Person p : persons){
				p.initialize();
			}
			
			if(persons.size() > person.getNumCommentsThreadPage()){
				// A custom decorator would require you to do something other than Hibernate.intialize to get 
				// the user objects.  
				//Paginator paginator = new Paginator<Person>(persons,new PersonDecorator());
				Paginator paginator = new Paginator<Person>(persons,person.getNumCommentsThreadPage());
				persons = paginator.getPage(1);
			}
			else{
				
			}
			
			Persistence.commit();
			return persons;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Finds the paginated results 
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<Person> getPaginatedPage(int page) throws ServiceException{
		try{
			Paginator<Person> paginator = Paginator.getActivePaginator();
			if(paginator != null){
				Persistence.beginSession();//getPage needs it.
				List<Person> list = paginator.getPage(page);
				Persistence.commit();
				return list;
			}
			else
				throw new ServiceException("Paginated data couldn't be found.");
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	/**
	 * Add a user object to a user. Remember user objects are anything specific to a user
	 * site settings, widgets, facebook links, show NWS by default, muted users... everything  
	 * 
	 * @param person
	 * @param object
	 */
	public void addUserObject(Person person, UserObject object) throws ServiceException{
		
	}
	/**
	 * Create new UserObjects of any type.     
	 * 
	 * @param object
	 */
	public void createUserObject(UserObject object) throws ServiceException{
		
	}
	
	public Person createUserObjectFromTemplate(String personId, String templateId, String address, String location) throws ServiceException{
		try{
			Person p = loadPerson(personId);
			Session session = Persistence.beginSession();
			
			Query query = session.getNamedQuery("userObjectTemplate.getById").setString("guid", templateId);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			
			
			UserObject uo = new UserObject();
			if(UserObjectTemplate.TEMPLATE_WEBPAGE.equals(t.getType())){
				uo.setType(UserObject.TYPE_WEBPAGE);
				uo.setUrl(address);
				uo.setTemplate(t);
				uo.setOwner(p);
			}
			else if(UserObjectTemplate.TEMPLATE_WIDGET.equals(t.getType())){
				uo.setType(UserObject.TYPE_WIDGET);
				uo.setValue(t.getValue());
				uo.setTemplate(t);
				uo.setOwner(p);
				if(location != null && UserObject.POSITION_RIGHT.equals(location)){
					uo.setName(location);
				}
				else
					uo.setName(UserObject.POSITION_LEFT);
			}
			else{
				throw new ServiceException("Unknown template type");
			}
			session.save(uo);
			Persistence.commit();
			
			
			session = Persistence.beginSession();
			session.refresh(p);
			p.initialize();
			Persistence.commit();
			return p;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	/**
	 * Sets a special flag to denote that NWS content should be shown to this user
	 * @param showNws
	 */
	public void enableNws(Person person, boolean enabled) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			if(enabled){
				if(!person.isNwsEnabled()){
					UserObject uo = new UserObject();
					uo.setType(UserObject.TYPE_ENABLE_NWS);
					uo.setValue(nwsTagId);
					uo.setOwner(person);
					session.save(uo);
				}
			}
			else{
				if(person.isNwsEnabled()){
					UserObject uo = person.getObjectType(UserObject.TYPE_ENABLE_NWS);
					if(uo != null){
						//person.removeUserObject(uo);
						session.delete(uo);
					}
				}
			}
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(person);
			person.initialize();
			Persistence.commit();
			
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Adds a user object to a person of the specified type with the string representation of the object value.
	 * 
	 * @param person
	 * @param type
	 * @param value
	 * @throws ServiceException
	 */
	private void updateUserSetting(Person person, String type, String value) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
						
			if(!person.hasObject(type)){
					UserObject uo = new UserObject();
					uo.setType(type);
					uo.setValue(value);
					uo.setOwner(person);
					session.save(uo);
					//person.addObject(uo);
			}
			else{
				UserObject uo = person.getObjectType(type);
				
				if(!uo.getValue().equals(value)){
					uo.setValue(value);
					session.update(uo);
				}
			}
			
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(person);
			person.initialize();
			Persistence.commit();
			
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * Sets the max number of new comments to show on the users main page.
	 * 
	 * @param person
	 * @param count
	 * @throws ServiceException
	 */
	public void setNumCommentsFrontpage(Person person, int count) throws ServiceException{
		updateUserSetting(person,UserObject.TYPE_NUM_COMMENTS_FRONTPAGE,""+count);
	}
	
	/**
	 * Sets the default number of comments to show within a single thread page
	 * @param person
	 * @param count
	 * @throws ServiceException
	 */
	public void setNumCommentsThreadPage(Person person, int count) throws ServiceException{
		updateUserSetting(person,UserObject.TYPE_NUM_COMMENTS_PERTHREAD,""+count);
	}
	/**
	 * Creates a 'filter' user object for the tagId for this user
	 * @param person
	 * @param tagId
	 */
	public void addTagFilter(Person person, String tagValue) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Query query = session.getNamedQuery("tag.getByValue").setString("value", tagValue);
			Tag t = (Tag)query.uniqueResult();
			
			UserObject uo = new UserObject();
			
			if(t!=null){
				uo.setType(UserObject.TYPE_FILTER_TAG);
				uo.setValue(t.getTagId());
				uo.setOwner(person);
			}
			else{
				throw new ServiceException("Unknown template type");
			}
			session.save(uo);
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(person);
			person.initialize();
			Persistence.commit();
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Mute a thread for a user. Works similar to filtering only this uses an explicit tag id
	 */
	public void muteThread(Person person, String tagId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			UserObject uo = new UserObject();
			uo.setType(UserObject.TYPE_FILTER_TAG);
			uo.setValue(tagId);
			uo.setOwner(person);
			
			session.save(uo);
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(person);
			person.initialize();
			Persistence.commit();
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Removes a filter tag from a user. 
	 * 
	 * @param person
	 * @param tagId
	 * @throws ServiceException
	 */
	public void removeTagFilter (Person person, String tagId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			List<UserObject> objects = person.getObjects();
			
			for(UserObject uo : objects){
				if(UserObject.TYPE_FILTER_TAG.equals(uo.getType()) && uo.getValue().equals(tagId)){
					session.delete(uo);
					break;
				}
			}
			Persistence.commit();
			
			session = Persistence.beginSession();
			session.refresh(person);
			person.initialize();
			Persistence.commit();
			
			
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Loads the tags that this user has filtered 
	 * 
	 * @param person
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> readFilteredTags(Person person) throws ServiceException{
		try{
			Session session = Persistence.beginSession();	
			List<Tag> tags = new ArrayList<Tag>();
			
			List<UserObject> objects = person.getFilterUserObjects();
			List<String> tagIds = new ArrayList<String>();
			for(UserObject uo : objects){
				tagIds.add(uo.getValue());
			}
			
			if(tagIds.size() > 0){
				Query query = session.getNamedQuery("tag.getByTagIds").setParameterList("tagIds", tagIds);
				tags = query.list();
			}
			return tags;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
			
	}
	
	/**
	 * 
	 * @param objectId
	 * @return
	 */
	public UserObject readUserObject(int objectId) throws ServiceException{
		UserObject object = null;
		return object;
	}
	
	/**
	 * Reads all of the user objects for a particular user
	 * 
	 * @param userId
	 * @return
	 * @throws ServiceException
	 */
	public List<UserObject> readUserObjects(int userId) throws ServiceException{
		List<UserObject> list = new ArrayList<UserObject>();
		return list;
	}

	/**
	 * Reads all user objects of a chosen type
	 * 
	 * @return a list of user objects 
	 * @throws ServiceException
	 */
	public List<UserObject> readUserObjects(String type) throws ServiceException{
		List<UserObject> list = new ArrayList<UserObject>();
		return list;
		
	}
	/**
	 * Admin functionality.  
	 * 
	 * @throws ServiceException
	 */
	public void updateUserObject(UserObject object) throws ServiceException{
		
	}
	/**
	 * Delete a UserObject.  This will delete any user object.  Template or otherwise
	 * 
	 * @param objectId
	 * @throws ServiceException
	 */
	public void deleteUserObject(String objectId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Query query = session.getNamedQuery("object.getByGuid").setString("objectId", objectId);
			UserObject uo = (UserObject)query.uniqueResult();
			
			Person person = uo.getOwner();
			person.removeUserObject(uo);
			
			session.delete(uo);
			
			
			session.update(person);
			
			
			Persistence.commit();
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	public UserObjectTemplate readUserObjectTemplate(String guid) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("userObjectTemplate.getById").setString("guid", guid);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			Persistence.commit();
			return t;
		}
		catch(Throwable t){
			log.error(t.getMessage());
			Persistence.rollback();	
			throw new ServiceException(t);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<UserObjectTemplate> readUserObjectTemplates(String type) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("userObjectTemplate.getOfType").setString("type", type);
			List<UserObjectTemplate> list = (List<UserObjectTemplate>)query.list();
			for(UserObjectTemplate template : list){
				Hibernate.initialize(template);
				Hibernate.initialize(template.getCreator());
				Hibernate.initialize(template.getImage());
			}
			Persistence.commit();
			return list;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Creates a new user template.  All fields are required.
	 * 
	 * @param personId
	 * @param urlPrefix
	 * @param templateName
	 * @param file
	 * @param fileName
	 * @throws ServiceException
	 */
	public void createUserObjectTemplate(String personId, String value, String templateName, File file, String fileName, String type)
		throws ServiceException{
		ImageFull fullImage = null;
		try{
			Person creator = loadPerson(personId);
			fullImage = ImageService.getInstance().createImageAndSave(creator, file, fileName, false);
			
			Image image = ImageService.getInstance().readImageById(fullImage.getImageId());
			Session session = Persistence.beginSession();
			UserObjectTemplate t = new UserObjectTemplate();
			t.setCreator(creator);
			t.setImage(image);
			t.setName(templateName);
			t.setValue(value);
			t.setType(type);
			
			session.save(t);
			Persistence.commit();
		}
		catch(ServiceException e){
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			throw e;
		}
		catch(Throwable t){
			log.error(t.getMessage());
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			Persistence.rollback();	
			throw new ServiceException(t);
		}
	}
	
	
	/**
	 * update a user template.  I could create and delete them but i had no way to update them.  This is needed because
	 * once someone is using one you can't re-create it because the guid foreign key is in use.
	 * 
	 * @param personId
	 * @param templateId
	 * @param urlPrefix
	 * @param templateName
	 * @param file
	 * @param fileName
	 * @throws ServiceException
	 */
	public void updateUserObjectTemplate(String personId, String templateId, String value, String templateName, File file, String fileName, String type)
		throws ServiceException{
		ImageFull fullImage = null;
		try{
			
			Person creator = loadPerson(personId);
			
			fullImage = ImageService.getInstance().createImageAndSave(creator, file, fileName, false);
			Image image = ImageService.getInstance().readImageById(fullImage.getImageId());
			
			Session session = Persistence.beginSession();
			
			Query query = session.getNamedQuery("userObjectTemplate.getById").setString("guid", templateId);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			
			t.setImage(image);
			t.setName(templateName);
			t.setValue(value);
			t.setType(type);
			
			session.update(t);
			Persistence.commit();
		}
		catch(ServiceException e){
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			throw e;
		}
		catch(Throwable t){
			log.error(t.getMessage());
			if(fullImage != null)
				ImageService.getInstance().deleteImage(fullImage.getImageId());
			Persistence.rollback();	
			throw new ServiceException(t);
		}
	}
	
	/**
	 * 
	 * @param guid
	 */
	public void deleteUserObjectTemplate(String guid)throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("userObjectTemplate.getById").setString("guid", guid);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			session.delete(t);
			session.delete(t.getImage());
			Persistence.commit();
		}
		catch(Throwable t){
			log.error(t.getMessage());
			Persistence.rollback();	
			throw new ServiceException(t);
		}
	}
	public String getNwsTagId() {
		return nwsTagId;
	}
	
	public String getPrivateTagId() {
		return privateTagId;
	}

}

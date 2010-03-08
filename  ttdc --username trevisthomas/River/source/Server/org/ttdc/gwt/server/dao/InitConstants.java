package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class InitConstants {
	private final static Logger log = Logger.getLogger(PostSearchDaoTest.class);
	public static final int MIN_TITLE_LENGTH = 3;
//	public final static String NWS_TAG_ID;
//	public final static String PRIVATE_TAG_ID;
//	public final static String REVIEW_ID;
	
	public final static Person ANONYMOUS;
	public final static String ANON_PERSON_ID = "ANON_PERSON_ID";
	
	public static Style DEFAULT_STYLE;
	//Bad mojo here trevis.  You have a mutable public attribute. Consider a fix
	public static void refresh(){
		try{
			Session session = Persistence.beginSession();
			DEFAULT_STYLE = (Style)session.getNamedQuery("style.getDefaultStyle").uniqueResult();
			ANONYMOUS.setStyle(DEFAULT_STYLE);
			Persistence.commit();
		}
		catch(Exception e){
			Persistence.rollback();
			throw new ExceptionInInitializerError("InitConstants failed to load a default style from the db");
		}
	}
		
	static{
		try{
			ANONYMOUS = new Person();
			ANONYMOUS.setAnonymous(true);
			ANONYMOUS.setPersonId(ANON_PERSON_ID);
			ANONYMOUS.setEmail("trevisthomas@gmail.com");
			ANONYMOUS.setLastAccessDate(new Date());
			ANONYMOUS.setLogin("ANONYMOUS");
			ANONYMOUS.setPassword("ANONYMOUS");
			ANONYMOUS.setStatus(Person.STATUS_INACTIVE);
			ANONYMOUS.setName("ANONYMOUS");
			//ANONYMOUS.setStyle(ThemeService.getInstance().getDefaultStyle());
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("userObjectTemplate.getForValue").setString("value", UserObjectTemplate.WIDGET_CALENDAR);
			UserObjectTemplate t = (UserObjectTemplate)query.uniqueResult();
			
			UserObject uo = new UserObject();
			uo.setType(UserObject.TYPE_WIDGET);
			uo.setValue(t.getValue());
			uo.setTemplate(t);
			uo.setOwner(ANONYMOUS);
			uo.setName(UserObject.POSITION_RIGHT);
			//ANONYMOUS.objects.(uo);
			//ANONYMOUS.objects = new ArrayList<UserObject>();
			//ANONYMOUS.objects.add(uo);
			ANONYMOUS.getObjects().add(uo);
			Persistence.commit();
			
		}
		catch(Exception e){
			Persistence.rollback();
			log.error(e);
			throw new ExceptionInInitializerError("InitConstants failed to initialize an anonymous person. ");
		}
	}
	
	static{
		try{
			Session session = Persistence.beginSession();
			DEFAULT_STYLE = (Style)session.getNamedQuery("style.getDefaultStyle").uniqueResult();
			ANONYMOUS.setStyle(DEFAULT_STYLE);
			Persistence.commit();
		}
		catch(Exception e){
			Persistence.rollback();
			throw new ExceptionInInitializerError("InitConstants failed to load a default style from the db");
		}
	}
	
	public final static Image DEFAULT_AVATAR;
	public final static Image DEFAULT_POSTER;
	
	static{
		try{
			Persistence.beginSession();
			ImageDao dao = new ImageDao();
			dao.setName("defaultavitar.jpg");
			DEFAULT_AVATAR = dao.load();
			
			dao.setName("defaultposter.jpg");
			DEFAULT_POSTER = dao.load();
			
			
//			Query query = session().getNamedQuery("tag.getByValueAndType").setString("value", Tag.VALUE_NWS).setString("type", Tag.TYPE_DISPLAY);
//			Tag tag = (Tag)query.uniqueResult();
//			NWS_TAG_ID = tag.getTagId();
//		
//			query = session().getNamedQuery("tag.getByValueAndType").setString("value", Tag.VALUE_PRIVATE).setString("type", Tag.TYPE_DISPLAY);
//			tag = (Tag)query.uniqueResult();
//			PRIVATE_TAG_ID = tag.getTagId();
//			
//			query = session().getNamedQuery("tag.getByValueAndType").setString("value", Tag.TYPE_REVIEW).setString("type", Tag.TYPE_REVIEW);
//			tag = (Tag)query.uniqueResult();
//			REVIEW_ID = tag.getTagId();
			
			Persistence.commit();
		}
		catch(Exception e){
			throw new ExceptionInInitializerError(e);
		}
		
	}
}

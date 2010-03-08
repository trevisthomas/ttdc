package org.ttdc.persistence.migration;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.WidgetService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.util.Cryptographer;

/**
 * Run this on a new db to get an admin account, privileges and movie ratings working
 * 
 * @author Trevis
 *
 */

public class InitNewDb {
	private static Logger log = Logger.getLogger(CommentService.class);
	public static void main(String[] args) {
		
		Session session = Persistence.beginSession();
		
		Person p = new Person();
		try{
			
			Query query = session.getNamedQuery("person.getByLogin").setString("login", "admin");
			p = (Person)query.uniqueResult();
			
//			makeDisplayTag(p,Tag.VALUE_NWS);
//			makeDisplayTag(p,Tag.VALUE_PRIVATE);
			//Others are created when used.
			
			/*
			if(p == null){
				p = new Person();	
				p.setEmail("trevisthomas@gmail.com");
				p.setLastAccessDate(new Date());
				p.setLogin("admin");
				Cryptographer crypto = new Cryptographer(null);
				p.setPassword(crypto.encrypt("password"));
				p.setStatus(Person.STATUS_ACTIVE);
				p.setName("TrevisTheAdmin");
				
				session.save(p);
				Persistence.commit();
				session = Persistence.session();
			}
			
			
			
			Privilege priv = new Privilege();
			
			priv.setName("Site Administrator.");
			priv.setValue(Privilege.ADMINISTRATOR);
			session.save(priv);
			
			if(!p.isAdministrator()){
				p.addPrivilege(priv);
				session.update(p);
				
			}
			
			priv = new Privilege();
			priv.setName("Create new posts.");
			priv.setValue(Privilege.POST);
			session.save(priv);
					
			priv = new Privilege();
			priv.setName("Rate content.");
			priv.setValue(Privilege.VOTER);
			session.save(priv);
			
			priv = new Privilege();
			priv.setName("Access to private content.");
			priv.setValue(Privilege.PRIVATE);
			session.save(priv);
			
			Persistence.commit();
			session = Persistence.session();
		
			
			UserObjectTemplate uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Traffic Widget");
			uot.setValue(UserObjectTemplate.WIDGET_TRAFFIC);
			session.save(uot);
			
			uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Calendar Widget");
			uot.setValue(UserObjectTemplate.WIDGET_CALENDAR);
			session.save(uot);
			
			uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Movie Widget");
			uot.setValue(UserObjectTemplate.WIDGET_MOVIE);
			session.save(uot);
			
			uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Hot Topics");
			uot.setValue(UserObjectTemplate.WIDGET_HOT_TOPICS);
			session.save(uot);
			
			uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Latest Threads");
			uot.setValue(UserObjectTemplate.WIDGET_NEW_THREADS);
			session.save(uot);
			
			uot = new UserObjectTemplate();
			uot.setType(UserObjectTemplate.TEMPLATE_WIDGET);
			uot.setCreator(p);
			uot.setName("Most Popular Threads");
			uot.setValue(UserObjectTemplate.WIDGET_MOST_POPULAR_THREADS);
			session.save(uot);
*/
			
			Persistence.commit();
			log.info("Done");
			
			
		}
		catch(Throwable t){
			Persistence.rollback();
		}
	}
	
	
}


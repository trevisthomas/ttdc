package org.ttdc.gwt.server.nugets;

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.ThemeService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class AnonymousFactory {
	private final static Person anonymous;
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
	public static Person getAnonymous(){
		return anonymous;
	}
}

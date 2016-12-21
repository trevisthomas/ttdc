package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public class UserObjectDao {
	
	public UserObjectDao() {
		
	}
	
	public static UserObject loadUserObject(String objectId) {
		UserObject object = (UserObject)session().load(UserObject.class, objectId);
		return object;
	}

	public static void delete(String objectId){
		UserObject uo = loadUserObject(objectId);
		Person person = uo.getOwner();
		session().delete(uo);
		session().flush();
		session().refresh(person);
	}
	
	public static List<Tag> loadFilteredTags(String personId){
		List<Tag> tags = new ArrayList<Tag>();
		Person person = PersonDao.loadPerson(personId);
		List<UserObject> objects = person.getFilterUserObjects();
		List<String> tagIds = new ArrayList<String>();
		for(UserObject uo : objects){
			tagIds.add(uo.getValue());
		}
		
		if(tagIds.size() > 0){
			Query query = session().getNamedQuery("tag.getByTagIds").setParameterList("tagIds", tagIds);
			tags = query.list();
		}
		return tags;
	}
	
	public static UserObject createTagFilter(Person person, String tagId){
		UserObject uo = new UserObject();
		uo.setType(UserObject.TYPE_FILTER_TAG);
		uo.setValue(tagId);
		uo.setOwner(person);
		
		session().save(uo);
		session().flush();
		session().refresh(person);
		return uo;
	} 
	
	public static void removeTagFilter(Person person, String tagId){
		List<UserObject> objects = person.getObjects();
		
		for(UserObject uo : objects){
			if(UserObject.TYPE_FILTER_TAG.equals(uo.getType()) && uo.getValue().equals(tagId)){
				session().delete(uo);
				session().flush();
				session().refresh(person);
				break;
			}
		}
	}
	
	public static List<String> loadFilteredThreadIds(String personId){
		Person person = PersonDao.loadPerson(personId);
		List<UserObject> objects = person.getThreadFilterUserObjects();
		List<String> threadIds = new ArrayList<String>();
		for(UserObject uo : objects){
			threadIds.add(uo.getValue());
		}
		return threadIds;
	}
	
	public static UserObject createThreadFilter(Person person, String threadId){
		UserObject uo = new UserObject();
		uo.setType(UserObject.TYPE_FILTER_THREAD);
		uo.setValue(threadId);
		uo.setOwner(person);
		
		session().save(uo);
		session().flush();
		session().refresh(person);
		return uo;
	} 
	
	public static void removeThreadFilter(Person person, String threadId){
		List<UserObject> objects = person.getObjects();
		
		for(UserObject uo : objects){
			if(UserObject.TYPE_FILTER_THREAD.equals(uo.getType()) && uo.getValue().equals(threadId)){
				session().delete(uo);
				session().flush();
				session().refresh(person);
				break;
			}
		}
	}
	
	public static void removeUserObject(Person person, String type, String value){
		List<UserObject> objects = person.getObjects();
		
		for(UserObject uo : objects){
			if(uo.getType().equals(type) && uo.getValue().equals(value)){
				session().delete(uo);
				session().flush();
				session().refresh(person);
				break;
			}
		}
	}
	
	
	//This new version only does webpage links.  I'm skipping widget based templates until i decide if 
	//the new site will have them
	public static UserObject createWebLinkFromTemplate(Person person, UserObjectTemplate template, String url){
		try{
			UserObject uo = new UserObject();
			if(UserObjectTemplate.TEMPLATE_WEBPAGE.equals(template.getType())){
				uo.setType(UserObject.TYPE_WEBPAGE);
				uo.setUrl(url);
				uo.setTemplate(template);
				uo.setOwner(person);
			}
//			else if(UserObjectTemplate.TEMPLATE_WIDGET.equals(t.getType())){
//				uo.setType(UserObject.TYPE_WIDGET);
//				uo.setValue(t.getValue());
//				uo.setTemplate(t);
//				uo.setOwner(p);
//				if(location != null && UserObject.POSITION_RIGHT.equals(location)){
//					uo.setName(location);
//				}
//				else
//					uo.setName(UserObject.POSITION_LEFT);
//			}
			else{
				throw new RuntimeException("Unknown template type");
			}
			session().save(uo);
			session().flush();
			session().refresh(person);
			return uo;
		}
		catch(RuntimeException e){
			rollback();
			throw(e);
		}
	}
	
	/**
	 * Sets a special flag to denote that NWS content should be shown to this user
	 * @param showNws
	 */
	public static void enableNws(Person person, boolean enabled){
		try{
			if(enabled){
				if(!person.isNwsEnabled()){
					//NWS is nolonger a tag. The value used to be the guid but i dont think that it
					//was ever used as an id.  isNwsEnabled on person just checks to see if a tag
					//of this type exists. A better solution would probably be to let Person have a bit
					//field.
					updateUserSetting(person, UserObject.TYPE_ENABLE_NWS, UserObject.TYPE_ENABLE_NWS);
				}
			}
			else{
				if(person.isNwsEnabled()){
					UserObject uo = person.getObjectType(UserObject.TYPE_ENABLE_NWS);
					if(uo != null){
						delete(uo.getObjectId());
						session().flush();
						session().refresh(person);
					}
				}
			}
		}
		catch(RuntimeException t){
			rollback();
			throw(t);
		}
	}
	
	
	public static void updateUserSetting(Person person, String type, int value) {
		updateUserSetting(person, type, ""+value);
	}
	
	public static UserObject updateUserSetting(Person person, String type, String value) {
		try{
			UserObject uo;
			if(!person.hasObject(type)){
				uo = new UserObject();
				uo.setType(type);
				uo.setValue(value);
				uo.setOwner(person);
				session().save(uo);
			}
			else{
				uo = person.getObjectType(type);
				if(!uo.getValue().equals(value)){
					uo.setValue(value);
					session().update(uo);
				}
			}
			
			session().flush();
			session().refresh(person);
			
			return uo;
		}
		catch(RuntimeException t){
			rollback();
			throw(t);
		}
	}

	// This was created for deviceToken's for mobile push notifications. So that you could add multiple of the same type
	public static UserObject addUserObject(Person person, String type, String value) {
		try {
			UserObject uo;
			if (!person.hasObject(type)) {
				uo = new UserObject();
				uo.setType(type);
				uo.setValue(value);
				uo.setOwner(person);
				session().save(uo);
			} else {
				uo = person.getObjectType(type);
				if (!uo.getValue().equals(value)) {
					// If it's not the same, create a new one of the same type.
					uo = new UserObject();
					uo.setType(type);
					uo.setValue(value);
					uo.setOwner(person);
					session().save(uo);
				} else {
					// If a uo of type but has the same value, do nothing
				}
			}

			session().flush();
			session().refresh(person);

			return uo;
		} catch (RuntimeException t) {
			rollback();
			throw (t);
		}
	}

}

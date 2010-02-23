package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;


public class UserObjectTemplateDao {
	private final static  Logger log = Logger.getLogger(UserObjectTemplateDao.class);
	private String creatorId;
	private String imageId;
	private String value;
	private String templateName; 
	private String type;
	private String templateId;

	public static UserObjectTemplate load(String templateId){
		UserObjectTemplate uot = (UserObjectTemplate)session().load(UserObjectTemplate.class, templateId);
		return uot;
	}
	
	public static void delete(String templateId){
		UserObjectTemplate uot = load(templateId);
		session().delete(uot);
	}
	
	
	public static List<UserObjectTemplate> loadTemplatesOfType(String type){
		Query query = session().getNamedQuery("userObjectTemplate.getOfType").setString("type", type);
		@SuppressWarnings("unchecked") 
		List<UserObjectTemplate> list = (List<UserObjectTemplate>)query.list();
		return list;
	}
	
	/**
	 * Loads the list of UserObjectTemplates of a specified type that are available
	 * to be used for a specific user.
	 * 
	 * @param personId
	 * @param type - ex: UserObjectTemplate.TEMPLATE_WEBPAGE, UserObjectTemplate.TEMPLATE_WIDGET
	 * @return
	 */
	public static List<UserObjectTemplate> loadAvailableForUser(String personId, String type){
		Person person = PersonDao.loadPerson(personId);
		if(StringUtils.isEmpty(type)) 
			throw new RuntimeException("Template type is required");
		List<UserObjectTemplate> list = new ArrayList<UserObjectTemplate>();
		List<UserObjectTemplate> all = loadTemplatesOfType(type);
		
		Map<String,UserObject> map = new HashMap<String,UserObject>();
		
		for(UserObject uo : person.getObjects()){
			if(uo.getTemplate() != null && type.equals(uo.getTemplate().getType()))
				map.put(uo.getTemplate().getTemplateId(),uo);
		}
		
		for(UserObjectTemplate t : all){
			if(!map.containsKey(t.getTemplateId()))
				list.add(t);
		}
		return list;
	}

	
	public UserObjectTemplate create(){
		Person creator = PersonDao.loadPerson(creatorId);
		Image image = ImageDao.loadImage(imageId);
		
		UserObjectTemplate t = new UserObjectTemplate();
		t.setCreator(creator);
		t.setImage(image);
		t.setName(templateName);
		t.setValue(value);
		t.setType(type);
		
		session().save(t);
		
		return t;
	}
	
	public UserObjectTemplate update(){
		try{
			UserObjectTemplate t = load(templateId);
			
			if(StringUtils.isNotEmpty(imageId)){
				Image image = ImageDao.loadImage(imageId);
				t.setImage(image);
			}
			t.setName(templateName);
			t.setValue(value);
			t.setType(type);
			
			session().update(t);
			return t;
		}
		catch(RuntimeException e){
			log.error(e.toString());
			rollback();	
			throw e;
		}
	}
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}

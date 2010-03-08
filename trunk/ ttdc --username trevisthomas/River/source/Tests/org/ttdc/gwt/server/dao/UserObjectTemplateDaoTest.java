package org.ttdc.gwt.server.dao;

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Test;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObjectTemplate;

import static org.ttdc.persistence.Persistence.*;

public class UserObjectTemplateDaoTest {
	String type = UserObjectTemplate.TEMPLATE_WEBPAGE;
	String value = "http://www.google.com";
	String imageId = "EBF9FDFA-0A53-4D9F-9236-00907DE6C3E7";
	String creatorId = Helpers.personIdCSam;
	String displayName = "Google";
	String tempalateIdFlickr = Helpers.userObjectTemplateFlickr; 
	
	@Test
	public void testCreate(){
		try{
			beginSession();
			
			UserObjectTemplate template = createTemplate();
			
			assertNotNull(template.getTemplateId());
			assertEquals(type, template.getType());
			assertEquals(value, template.getValue());
			assertEquals(imageId, template.getImage().getImageId());
			assertEquals(creatorId, template.getCreator().getPersonId());
			assertEquals(displayName, template.getName());
		}
		finally{
			rollback();
		}
	}
	
	@Test
	public void testLoadForUser(){
		beginSession();
		Person person = PersonDao.loadPerson(Helpers.personIdTrevis);
		List<UserObjectTemplate> list = UserObjectTemplateDao.loadAvailableForUser(person.getPersonId(), UserObjectTemplate.TEMPLATE_WEBPAGE);
		assertTrue("Expected to find some user object templates but didnt.",list.size()>0);
//		assertEquals(6, list.size());
//		assertEquals("Blogger", list.get(0).getName());
//		assertEquals("MySpace", list.get(1).getName());
		commit();
	}
	
	@Test
	public void testLoadTemplatesOfType(){
		beginSession();
		List<UserObjectTemplate> list = UserObjectTemplateDao.loadTemplatesOfType(UserObjectTemplate.TEMPLATE_WEBPAGE);
		for(UserObjectTemplate t : list){
			assertEquals(UserObjectTemplate.TEMPLATE_WEBPAGE, t.getType());
		}
		commit();
	}
	
	@Test
	public void testDelete(){
		try{
			beginSession();
			UserObjectTemplate template = createTemplate();
			String templateId = template.getTemplateId();
			//commit(); //I want to know if it really works.
			
			//beginSession();
			UserObjectTemplateDao.delete(templateId);
			//commit(); //I want to know if it really works.
			
			//beginSession();
			
			try{
				template = UserObjectTemplateDao.load(templateId);
				template.getCreator(); // Throws ObjectNotFoundException
				fail("Failed to delete test template");
			}
			catch (Exception e) {
				//expected
			}
			commit();	 
		}
		catch (Exception e) {
			rollback();
		}
	}
	
	@Test
	public void testUpdate(){
		try{
			beginSession();
			
			UserObjectTemplate template = UserObjectTemplateDao.load(tempalateIdFlickr);
			
			String newName = template.getName() + " Mod ";
			UserObjectTemplateDao dao = new UserObjectTemplateDao();
			dao.setCreatorId(Helpers.personIdCSam);
			dao.setImageId(imageId);
			dao.setTemplateName(newName);
			dao.setTemplateId(template.getTemplateId());
			dao.setType(template.getType());
			dao.setValue(template.getValue());
			
			template = dao.update();
			
			assertEquals(newName,dao.getTemplateName());
		}
		finally {
			rollback();
		}
		
	}
	
	/**
	 * Utility function.
	 * @return
	 */
	private UserObjectTemplate createTemplate() {
		UserObjectTemplateDao dao = new UserObjectTemplateDao();
		dao.setCreatorId(creatorId);
		dao.setImageId(imageId); //Cover for love actually?
		dao.setTemplateName(displayName);
		dao.setType(type);
		dao.setValue(value);
		UserObjectTemplate template = dao.create();
		return template;
	}
}

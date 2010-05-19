package org.ttdc.gwt.server.dao;

import java.util.List;

import org.junit.Test;
import org.ttdc.gwt.client.constants.UserObjectConstants;
import org.ttdc.gwt.client.constants.UserObjectTemplateConstants;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

import static junit.framework.Assert.*;
import static org.ttdc.persistence.Persistence.*;
public class UserObjectDaoTest {
	
	boolean hasTagIdFilter(Person p, String tagId){
		for(UserObject uo : p.getFilterUserObjects()){
			if(uo.getValue().equals(tagId)) return true;
		}
		return false;
	}
	
	boolean hasThreadIdFilter(Person p, String tagId){
		for(UserObject uo : p.getThreadFilterUserObjects()){
			if(uo.getValue().equals(tagId)) return true;
		}
		return false;
	}
	
	@Test
	public void testCreateAndDelete(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertTrue(!hasTagIdFilter(person,Helpers.tagApril));
			UserObject uo = UserObjectDao.createTagFilter(person, Helpers.tagApril);
			assertTrue(hasTagIdFilter(person,Helpers.tagApril));
			UserObjectDao.delete(uo.getObjectId());
			assertTrue(!hasTagIdFilter(person,Helpers.tagApril));
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
	@Test
	public void testCreateAndDeleteThreadFilter(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertTrue(!hasThreadIdFilter(person, Helpers.tagCorporateGoodness));
			UserObject uo = UserObjectDao.createThreadFilter(person, Helpers.tagCorporateGoodness);
			assertTrue(hasThreadIdFilter(person,Helpers.tagCorporateGoodness));
			//UserObjectDao.delete(uo.getObjectId());
			UserObjectDao.removeThreadFilter(person, Helpers.tagCorporateGoodness);
			assertTrue(!hasThreadIdFilter(person,Helpers.tagCorporateGoodness));
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
	@Test
	public void testCreateWebLinkFromTemplate(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			
			List<UserObjectTemplate> templates = UserObjectTemplateDao.loadAvailableForUser(person.getPersonId(), UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
			
			UserObjectTemplate template = templates.get(0);
			
			assertNotNull("Didnt find an available template for user",template);
			UserObject uo = UserObjectDao.createWebLinkFromTemplate(person, template, "http://www.unittest.com");
			
			templates = UserObjectTemplateDao.loadAvailableForUser(person.getPersonId(), UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
			assertTrue("This template should no longer be available", !templates.contains(template));
			
			UserObjectDao.delete(uo.getObjectId());
			
			templates = UserObjectTemplateDao.loadAvailableForUser(person.getPersonId(), UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
			assertTrue("It should be available again", templates.contains(template));
			
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
	@Test
	public void testEnableNws(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			boolean nws = person.isNwsEnabled();
			UserObjectDao.enableNws(person, !nws);
			assertEquals("NWS flag didnt change",!nws, person.isNwsEnabled());
			UserObjectDao.enableNws(person, nws);
			assertEquals("NWS flag didnt change back",nws, person.isNwsEnabled());
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
	
	
	@Test
	public void testRemoveTagFilterUserObjectByTagId(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			assertTrue(!hasTagIdFilter(person,Helpers.tagApril));
			UserObjectDao.createTagFilter(person, Helpers.tagApril);
			assertTrue(hasTagIdFilter(person,Helpers.tagApril));
			UserObjectDao.removeTagFilter(person, Helpers.tagApril);
			assertTrue(!hasTagIdFilter(person,Helpers.tagApril));
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
	@Test
	public void testUserSetting(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdCSam);
			int count = person.getNumCommentsFrontpage();
			int newCount = count + 5;
			UserObjectDao.updateUserSetting(person, UserObjectConstants.TYPE_NUM_COMMENTS_FRONTPAGE, newCount);
			assertEquals(newCount, person.getNumCommentsFrontpage());
			UserObjectDao.updateUserSetting(person, UserObjectConstants.TYPE_NUM_COMMENTS_FRONTPAGE, count);
			assertEquals(count, person.getNumCommentsFrontpage());
			
		}
		catch (Exception e) {
			rollback();
			fail(e.toString());
		}
		finally{
			commit();
		}
	}
	
}

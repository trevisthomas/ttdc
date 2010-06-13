package org.ttdc.gwt.server.dao;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;
import static org.ttdc.persistence.Persistence.*;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;

import static org.ttdc.gwt.server.dao.Helpers.*;

public class TagDaoTest {
	private final static Logger log = Logger.getLogger(TagDaoTest.class);
	
	@Test
	public void loadTagTest(){
		try{
			beginSession();
			String tagId = "1FDCB845-0327-493A-AE41-5539334256E4";
			Tag tag = TagDao.loadTag(tagId);
			assertEquals("Wrong tag",tag.getValue(),"TTDC Version 6 Beta!");
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void readTagForValueAndType(){
		try{
			beginSession();
			TagDao dao = new TagDao();
			String type="TOPIC";
			String value="TTDC Version 6 Beta!";
			
			dao.setType(type);
			dao.setValue(value);
			
			Tag tag = dao.load();
			
			assertTagHasValueAndType(type, value, tag);
		commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void readTagForValueAndTypeForNonExistantTag(){
		try{
			beginSession();
			TagDao dao = new TagDao();
			String type="FAKETOPIC";
			String value="DOESNT MATTER";
			
			dao.setType(type);
			dao.setValue(value);
			
			Tag tag = dao.load();
			
			assertNull("There should not be a tag with this type and value", tag);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	@Test
	public void readTagForValueAndTypeWithLeadingAndTrailingSpaces(){
		try{
			beginSession();
			TagDao dao = new TagDao();
			String type="TOPIC";
			String value=" TTDC Version 6 Beta! ";
			
			dao.setType(type);
			dao.setValue(value);
			
			Tag tag = dao.load();
			
			assertTagHasValueAndType(type, value.trim(), tag);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void createTagRollsBackToProtectDbState(){
		try{
			beginSession();
			String type=Tag.TYPE_TOPIC;
			String value=" TTDC Version 6 Beta! New ";
			String personId = "50E7F601-71FD-40BD-9517-9699DDA611D6";
			
			TagDao dao = new TagDao();
			dao.setValue(value);
			dao.setType(type);
			
			log.debug("Creating");
			Tag tag = dao.create();
			log.debug("Done");
			
			assertTagHasValueAndType(type, value.trim(), tag);
			assertTrue("Tag has no id", StringUtils.isNotEmpty(tag.getTagId()));
			
			rollback();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
		
		
	}
	@Test
	public void createOrLoadTagLoadModeTest(){
		try{
			beginSession();
			
			TagDao dao = new TagDao();
			dao.setValue(" TTDC Version 6 Beta! ");
			dao.setType(Tag.TYPE_TOPIC);
			
			Tag tag = dao.createOrLoad();
			assertEquals("Tag ID does not match expected tag ID",tag.getTagId(),"1FDCB845-0327-493A-AE41-5539334256E4");
		
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void createOrLoadTagCreateModeTest(){
		try{
			beginSession();
			
			String value = " TTDC Version 6 Beta! One that doesnt exist ";
			String type = Tag.TYPE_TOPIC;
			
			TagDao dao = new TagDao();
			dao.setValue(value);
			dao.setType(type);
			
			Tag tag = dao.createOrLoad();
			
			assertTagHasValueAndType(type, value.trim(), tag);
			assertTrue("Tag has no id", StringUtils.isNotEmpty(tag.getTagId()));
			
			rollback();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void loadTagsOfTypeTest(){
		try{
			beginSession();
			
			TagDao dao = new TagDao();
			dao.setType(Tag.TYPE_RATING);
			List<Tag> list = dao.loadList();
			
			for(Tag t : list){
				log.info(t.getValue());
				assertEquals(t.getType(), Tag.TYPE_RATING);
			}
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
}


package org.ttdc.gwt.server.dao;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

public class InboxDaoTest {
	private final static Logger log = Logger.getLogger(InboxDaoTest.class);
	@Test
	public void testInboxForUser(){
		Persistence.beginSession();
		Person person = PersonDao.loadPerson(Helpers.personIdMatt);
		InboxDao dao = new InboxDao(person);
		PaginatedList<Post> results = dao.loadFlat();
		
		Assert.assertTrue(results.getList().size() > 0);
		log.info("Total inbox size: "+results.getTotalResults());
		for(Post post : results.getList()){
			log.info(post.getEntry());	
		}
		Persistence.commit();
		
	}
	
	@Test
	public void testInboxForUserWithoutEntry(){
		Persistence.beginSession();
		Person person = PersonDao.loadPerson(Helpers.personIdAdmin);
		InboxDao dao = new InboxDao(person);
		PaginatedList<Post> results = dao.loadFlat();
		
		Assert.assertTrue(results.getTotalResults() > 100000);
		log.info("Total inbox size: "+results.getTotalResults());
		for(Post post : results.getList()){
			log.info(post.getEntry());	
		}
		Persistence.commit();
		
	}
}

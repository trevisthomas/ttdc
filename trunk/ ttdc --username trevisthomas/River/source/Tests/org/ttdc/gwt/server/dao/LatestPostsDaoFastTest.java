package org.ttdc.gwt.server.dao;

import static junit.framework.Assert.*;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;

public class LatestPostsDaoFastTest {
	private final static Logger log = Logger.getLogger(LatestPostsDaoFastTest.class);
	
	private StopWatch stopwatch = new StopWatch();
	
	@Before
	public void startup(){
		stopwatch = new StopWatch();
		stopwatch.start();
	}
	
	@After
	public void taredown(){
		stopwatch.stop();
		log.info(stopwatch);
	}

	
	@Test
	public void flatPostsFastTest(){
		Persistence.beginSession();
//		Person person = PersonDao.loadPerson(Helpers.personIdMatt);
//		InboxDao dao = new InboxDao(person);
//		PaginatedList<GPost> results = dao.loadFlat();
		
		LatestPostsDaoFast dao = new LatestPostsDaoFast();
		
		InboxDao inboxDao = null;
		
		PaginatedList<GPost> results = dao.loadFlat(inboxDao);
		
		Persistence.commit();
		
		assertTrue(results.getList().size() > 0);
//		log.info("Total inbox size: "+results.getTotalResults());
		for(GPost post : results.getList()){
			log.info(post.getEntry());
			
			assertNotNull(post.getDate());
			assertNotNull(post.getCreator());
			assertNotNull(post.getCreator().getImage());
			assertNotNull(post.getCreator().getImage().getThumbnailName());
			
			assertTrue(StringUtil.notEmpty(post.getTitle()));
			
			if(post.getImage() != null){
				assertNotNull(post.getImage().getThumbnailName());
				assertTrue(post.getImage().getHeight() > 0);
				assertTrue(post.getImage().getWidth() > 0);
			}
		}
		
		
	}
}

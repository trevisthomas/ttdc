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
import org.ttdc.persistence.objects.Post;

public class FastTopicDaoTest {
	private final static Logger log = Logger.getLogger(FastLatestPostsDaoTest.class);
	
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
	public void loadTopicPageTest(){
		Persistence.beginSession();
		
		FastTopicDao dao = new FastTopicDao();
		
		Post p = PostDao.loadPost("9FBD8F58-DC6E-40CC-AAC7-E6716D1BF886");
		
		dao.setSourcePost(p);
		
		PaginatedList<GPost> results = dao.loadByCreateDate();
		
		Persistence.commit();
		
		assertTrue(results.getList().size() > 0);
		for(GPost post : results.getList()){
			log.info(post.getEntry());
			
			assertNotNull(post.getDate());
			assertNotNull(post.getCreator());
			assertNotNull(post.getCreator().getImage());
			assertNotNull(post.getCreator().getImage().getThumbnailName());
			
			if(post.isReview()){
				assertTrue(post.getRoot().getTagAssociations().size() > 1);
			}
			
			assertTrue(StringUtil.notEmpty(post.getTitle()));
			
			if(post.getImage() != null){
				assertNotNull(post.getImage().getThumbnailName());
				assertTrue(post.getImage().getHeight() > 0);
				assertTrue(post.getImage().getWidth() > 0);
			}
		}
		
	}

}

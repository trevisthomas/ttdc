package org.ttdc.gwt.server.dao;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

import static junit.framework.Assert.*;
import static org.ttdc.persistence.Persistence.*;

public class ThreadDaoTest {
	private final static Logger log = Logger.getLogger(PostSearchDaoTest.class);
	
	private StopWatch stopwatch = new StopWatch();
	
	@Before 
	public void setup(){
		stopwatch.start();
	}
	@After 
	public void taredown(){
		stopwatch.stop();
		log.info("Time taken: "+stopwatch);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
	
	@Test
	public void testThread(){
		ThreadDao dao = new ThreadDao();
		beginSession();
		dao.setRootId(Helpers.rootIdVersion6Live);
		dao.setPageSize(10);
		dao.setCurrentPage(1);
		
		PaginatedList<Post> results = dao.loadByReplyDate();
		
		assertNotNull(results);
		commit();
	}
	
	@Test
	public void testMoreNested(){
		beginSession();
		ThreadDao dao = new ThreadDao();
		dao.setThreadId("906C34D1-57CF-4D9D-BCA8-105184A6F2EE");//A starter with 15 replies
		dao.setCurrentPage(2);
		dao.setPageSize(5);
				
		PaginatedList<Post> results = dao.loadThreadSummmary();
		assertEquals(2, results.getCurrentPage());
		List<Post> posts = results.getList();
		
		//This test only passes if the page size in ThreadDao is set to 5
		//TREVIS you dont understand why these are in this order...sigh
		assertEquals("00075.00000.00000.00000.00000.00000.00000.00001", posts.get(4).getPath());
		assertEquals("00075.00000.00000.00000.00000.00000.00000", posts.get(0).getPath());
		commit();
	}

}

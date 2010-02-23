package org.ttdc.gwt.server.dao;

import static org.junit.Assert.fail;
import static org.ttdc.gwt.server.dao.Helpers.assertTagged;
import static org.ttdc.gwt.server.dao.Helpers.tagTrevis;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class PostSearchDaoSpeedTest {
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
	public void theTrevisTest(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			
			dao.addTagId("3325CE14-A37E-4236-875C-F1D97F006682");
			
			PaginatedList<Post> results = dao.search();
			
			//assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			
			
			
			assertTagged(results.getList(), tagTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
}

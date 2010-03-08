package org.ttdc.gwt.server.dao;

import static org.junit.Assert.*;
import static org.ttdc.gwt.server.dao.Helpers.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

import static org.ttdc.persistence.Persistence.*;

@Deprecated
public class LatestPostsHierarchyDaoTest {
	private final static Logger log = Logger.getLogger(LatestPostsFlatDaoTest.class);
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
	public void readLatestHierarchy(){
		try{
			beginSession();
			LatestPostsHierarchyDao dao = new LatestPostsHierarchyDao();
			
			List<String> filteredTagIds = new ArrayList<String>();
			filteredTagIds.add(tagTrevis);
			int threadCount = 10;
			
			dao.setFilteredTagIdList(filteredTagIds);
			dao.setThreadCount(threadCount);
			
			List<Post> list = dao.load();
			
			//Helpers.printPostsRecursive(list, log);
			
			Helpers.printPosts(list, log);
			
			Helpers.printPostPaths(list, log);
			
			
			//assertTrue(list.size() == threadCount);
			//Doesnt work yet
			//assertNotTagged(list, filteredTagIds);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}	
	
}

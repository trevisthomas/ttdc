package org.ttdc.gwt.server.dao;

import static org.junit.Assert.*;

import static org.ttdc.gwt.server.dao.Helpers.tagTrevis;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;



public class InflatinatorTest {
	
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
	public void flatTest(){
		try{
			beginSession();
			LatestPostsFlatDao dao = new LatestPostsFlatDao();
			
			List<String> filteredTagIds = new ArrayList<String>();
			filteredTagIds.add(tagTrevis);
			int pageSize = 100;
			
			dao.setFilteredTagIdList(filteredTagIds);
			dao.setPageSize(pageSize);
			
			List<Post> list = dao.load();
			
			Inflatinator inf = new Inflatinator(list);
			
			List<GPost> posts = inf.extractPosts();
			
			assertEquals("Inflatinator didnt find the right amount of stuff",list.size(),posts.size());
			
			assertEquals("More results than requested ", pageSize, list.size());
			
			commit();
			
			for(GPost p : posts){
				assertNotNull("Found a post with a null title",p.getTitle());
				log.info(p);
				log.info("");
			}
		}
		catch(Exception e){
			//rollback();
			fail(e.toString());
		}
		
	}

	@Test
	public void hierarchyTest(){
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
			
			//assertTrue(list.size() == threadCount);
			//Doesnt work yet
			//assertNotTagged(list, filteredTagIds);
			
			
			Inflatinator inf = new Inflatinator(list);
			
			List<GPost> posts = inf.extractPostHierarchy();
			
			assertTrue("Inflatinator didnt find the right amount of stuff", posts.size() == threadCount);
			
			
			commit();
			
			for(GPost p : posts){
				assertNotNull("Found a post with a null title",p.getTitle());
				log.info(p);
				log.info("");
			}
		}
		catch(Exception e){
			//rollback();
			fail(e.toString());
		}
		
	}
	
	@Test
	public void testNestedForTopic(){
		
		try{
			beginSession();
			ThreadDao dao = new ThreadDao();
			dao.setRootId(Helpers.rootIdVersion6Live);
			
//			List<String> filteredTagIds = new ArrayList<String>();
//			filteredTagIds.add(tagTrevis);
//			dao.setFilteredTagIdList(filteredTagIds);
			
			int threadCount = 10;
			dao.setPageSize(threadCount);
			dao.setCurrentPage(1);
			
			List<Post> list = dao.loadByCreateDate().getList();
			
			Inflatinator inf = new Inflatinator(list);
			
			List<GPost> posts = inf.extractPostsNested();
			
			assertTrue("Inflatinator didnt find the right amount of stuff", posts.size() == threadCount);
			
			commit();
			
			for(GPost p : posts){
				assertNotNull("Found a post with a null title",p.getTitle());
				log.info(p);
				log.info("");
			}
		}
		catch(Exception e){
			//rollback();
			fail(e.toString());
		}
	}
	
	@Test
	public void hierarchyAtRootTest(){
		try{
			
		}
		catch(Exception e){
			//rollback();
			fail(e.toString());
		}
	}
	
	@Test
	public void emptyList(){
		LatestPostsHierarchyDao dao = new LatestPostsHierarchyDao();
		
		List<Post> emptylist = new ArrayList<Post>();
		
		Inflatinator inf = new Inflatinator(emptylist);
		
		try{
			List<GPost> posts = inf.extractPostHierarchy();
			
			assertTrue(posts.isEmpty());
		}
		catch (Exception e) {
			fail("This should not cause an exception.");
		}
		
	}
	
	
	
}

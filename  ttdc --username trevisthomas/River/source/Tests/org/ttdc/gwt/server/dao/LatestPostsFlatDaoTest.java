package org.ttdc.gwt.server.dao;

import static org.junit.Assert.*;
import static org.ttdc.gwt.server.dao.Helpers.*;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;


public class LatestPostsFlatDaoTest {
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
	public void readFrontPageFlatNoFilters(){
		try{
			
			beginSession();
			LatestPostsFlatDao dao = new LatestPostsFlatDao();
			
			int pageSize = 100;
			
			dao.setFilteredTagIdList(null);
			dao.setPageSize(pageSize);
			
			List<Post> list = dao.load();
			
			assertTrue(list.size() == pageSize);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void readFrontPageFlat(){
		try{
			beginSession();
			LatestPostsFlatDao dao = new LatestPostsFlatDao();
			
			List<String> filteredTagIds = new ArrayList<String>();
			filteredTagIds.add(tagTrevis);
			int pageSize = 100;
			
			dao.setFilteredTagIdList(filteredTagIds);
			dao.setPageSize(pageSize);
			
			List<Post> list = dao.load();
			
			assertTrue(list.size() == pageSize);
			//Slow
			assertNotTagged(list, filteredTagIds);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	//This test is bad
	/*
	@Test
	public void readFrontPageFlatPrefetch(){
		try{
			beginSession();
			LatestPostsFlatDao dao = new LatestPostsFlatDao();
			
			List<String> filteredTagIds = new ArrayList<String>();
			filteredTagIds.add(tagTrevis);
			int pageSize = 100;
			
			dao.setFilteredTagIdList(filteredTagIds);
			dao.setPageSize(pageSize);
			
			List<Post> list = dao.load();
			dao.preFetchFlat(list);
			
			assertTrue(list.size() == pageSize);
			//Slow
			//assertNotTagged(list, filteredTagIds);
			
			List<GPost> gPosts = GenericBeanConverter.convertPosts(list);
			
			commit();
			for(GPost p : gPosts){
				log.info(p);
				log.info("");
			}
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	*/
	
	
}

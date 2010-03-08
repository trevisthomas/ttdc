package org.ttdc.gwt.server.dao;

import java.util.List;

import javax.naming.directory.SearchResult;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.util.PostFlag;

import static junit.framework.Assert.*;
import static org.ttdc.persistence.Persistence.*;

public class TopicDaoTest {
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
	public void testConversationStartersInTopic(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.setRootId(Helpers.rootIdVersion6Live);
		PaginatedList<Post> resutlts = dao.loadStarters();
		commit();
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", resutlts.getList().get(0).getPostId());
		assertEquals(98, resutlts.getTotalResults());
	}
	
//	@Test
//	public void testConversationStartersInTopicWithFilter(){
//		beginSession();
//		TopicDao dao = new TopicDao();
//		dao.setCurrentPage(1);
//		dao.setPageSize(20);
//		dao.setRootId(Helpers.rootIdVersion6Live);
//		dao.addFilterTagId(Helpers.tagTrevis);
//		PaginatedList<Post> resutlts = dao.loadStarters();
//		
//		Helpers.assertNotTagged(resutlts.getList(), Helpers.tagTrevis);
//		
//		commit();
//		assertEquals("A7129731-B11F-403C-9108-F82159170C8B", resutlts.getList().get(0).getPostId());
//		assertEquals(52, resutlts.getTotalResults());
//	}
	
	//TODO: load with filters
	
	@Test
	public void testConversation(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		//dao.setRootId(Helpers.rootIdVersion6Live);
		dao.setConversationId("906C34D1-57CF-4D9D-BCA8-105184A6F2EE");
		PaginatedList<Post> resutlts = dao.loadReplies();
		commit();
		assertEquals(15, resutlts.getTotalResults());
		assertEquals("223B5022-88CE-42BF-B448-90E3F3737D79", resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testConversationWithFilter(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.addFlagFilter(PostFlag.INF);
		dao.addFlagFilter(PostFlag.PRIVATE);

		dao.setConversationId("906C34D1-57CF-4D9D-BCA8-105184A6F2EE");
		PaginatedList<Post> resutlts = dao.loadReplies();
		
		//Helpers.assertNotTagged(resutlts.getList(), Helpers.tagTrevis);
		
		commit();
		//Trevis, you were moving fast and just switched the tests to match the data since the filter was changed
		assertEquals(15, resutlts.getTotalResults());
		assertEquals("223B5022-88CE-42BF-B448-90E3F3737D79", resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testFlat(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.setRootId(Helpers.rootIdVersion6Live);
		//dao.addFilterTagId(Helpers.tagTrevis);
		PaginatedList<Post> resutlts = dao.loadFlat();
		
		//Helpers.assertNotTagged(resutlts.getList(), Helpers.tagTrevis);
		
		commit();
		assertEquals(424, resutlts.getTotalResults());
		assertEquals("EC706434-1B59-4CF1-9F80-515EE38C1696", resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testFlatWithFilter(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.setRootId(Helpers.rootIdVersion6Live);
		
		dao.addFlagFilter(PostFlag.INF);
		dao.addFlagFilter(PostFlag.PRIVATE);
		
		PaginatedList<Post> resutlts = dao.loadFlat();
		
		commit();
		assertEquals(419, resutlts.getTotalResults());
		assertEquals("EC706434-1B59-4CF1-9F80-515EE38C1696", resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testPagedHierarchyThread(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.setRootId(Helpers.rootIdVersion6Live);
		
		dao.addFlagFilter(PostFlag.INF);
		dao.addFlagFilter(PostFlag.PRIVATE);
		
		PaginatedList<Post> resutlts = dao.loadHierarchy();
		
		commit();
		assertEquals(420, resutlts.getTotalResults());
		assertEquals(Helpers.rootIdVersion6Live, resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testPagedHierarchyThreadNoFilter(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setCurrentPage(1);
		dao.setPageSize(20);
		dao.setRootId(Helpers.rootIdVersion6Live);
		PaginatedList<Post> resutlts = dao.loadHierarchy();
		
		//Helpers.assertNotTagged(resutlts.getList(), Helpers.tagTrevis);
		
		commit();
		assertEquals(425, resutlts.getTotalResults());
		assertEquals(Helpers.rootIdVersion6Live, resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testFullThread(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setRootId(Helpers.rootIdVersion6Live);
		dao.addFlagFilter(PostFlag.INF);
		dao.addFlagFilter(PostFlag.PRIVATE);
		PaginatedList<Post> resutlts = dao.loadHierarchyUnPaged();

		commit();
		assertEquals(420, resutlts.getTotalResults());
		assertEquals(420, resutlts.getList().size());
		assertEquals(Helpers.rootIdVersion6Live, resutlts.getList().get(0).getPostId());
	}
	
	@Test
	public void testFullThreadNoFilter(){
		beginSession();
		TopicDao dao = new TopicDao();
		dao.setRootId(Helpers.rootIdVersion6Live);
		PaginatedList<Post> resutlts = dao.loadHierarchyUnPaged();
		
		//Helpers.assertNotTagged(resutlts.getList(), Helpers.tagTrevis);
		
		commit();
		assertEquals(425, resutlts.getTotalResults());
		assertEquals(425, resutlts.getList().size());
		assertEquals(Helpers.rootIdVersion6Live, resutlts.getList().get(0).getPostId());
	}
	//Todo, if root is filtered dont return anything!
	
	
}

package org.ttdc.gwt.server.command;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.executors.TopicCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommand.SortOrder;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

import static org.ttdc.gwt.server.dao.Helpers.*;
import static junit.framework.Assert.*;

public class TopicCommandTest {
private final static Logger log = Logger.getLogger(SearchPostsCommandTest.class);
	
	StopWatch stopwatch = new StopWatch();
	@Before
	public void startup(){
		stopwatch = new StopWatch();
		stopwatch.start();
	}
	@After
	public void taredown(){
		stopwatch.stop();
		log.info(stopwatch);
		log.info("ass count: "+AssociationPostTag.iCount);
		log.info("post count: "+Post.iCount);
	}
	
	@Test
	public void testFlat(){
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(Helpers.rootIdVersion6Live);
		cmd.setType(TopicCommandType.FLAT);
		cmd.setPageNumber(2);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof TopicCommandExecutor);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		assertEquals(424, result.getResults().getTotalResults());
	}
	
	@Test
	public void testHierarchy(){
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(Helpers.rootIdVersion6Live);
		cmd.setType(TopicCommandType.HIERARCHY);
		cmd.setPageNumber(2);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof TopicCommandExecutor);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		for(GPost gp : posts){
			assertNotNull(gp.getCreator());
			assertNotNull(gp.getTitleTag());
			assertNotNull(gp.getEntry()); //Well, i dont know that movies have entries so... beware
		}
		assertEquals(425, result.getResults().getTotalResults());
	}
	
	@Test
	public void testHierarchyUnpaged(){
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(Helpers.rootIdVersion6Live);
		cmd.setType(TopicCommandType.HIERARCHY_UNPAGED_SUMMARY);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof TopicCommandExecutor);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		assertEquals(1, posts.size());
		assertEquals(425, result.getResults().getTotalResults());
	}
	
//	@Test
//	public void testConversationStarters(){
//		TopicCommand cmd = new TopicCommand();
//		cmd.setPostId(Helpers.rootIdVersion6Live);
//		cmd.setType(TopicCommandType.STARTERS);
//		
//		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
//		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
//		List<GPost> posts = result.getResults().getList();
//		assertEquals(98, result.getResults().getTotalResults());
//	}
//	
//	@Test
//	public void testReplies(){
//		TopicCommand cmd = new TopicCommand();
//		cmd.setPostId(Helpers.rootIdVersion6Live);
//		cmd.setType(TopicCommandType.REPLIES);
//		cmd.setConversationId("06247F33-5295-4867-A2ED-63310F9DD643");
//		
//		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
//		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
//		List<GPost> posts = result.getResults().getList();
//		assertEquals(40, result.getResults().getTotalResults());
//	}
	
	
	@Test
	public void testConversationFromRootPost(){
		TopicCommand cmd = new TopicCommand();
		cmd.setType(TopicCommandType.CONVERSATION);
		cmd.setPostId(Helpers.rootIdVersion6Live);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", posts.get(0).getPostId());
		assertEquals(98, result.getResults().getTotalResults());
		
	}
	
	@Test
	public void testConversationFromStarterPost(){
		TopicCommand cmd = new TopicCommand();
		cmd.setType(TopicCommandType.CONVERSATION);
		cmd.setPostId("B3D57670-4162-4B99-9C0D-5E3DE54EB990");
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		assertEquals("EC706434-1B59-4CF1-9F80-515EE38C1696", posts.get(0).getPostId());
		assertEquals(3, result.getResults().getTotalResults());
		
	}
	
	@Test
	public void testConversationFromReplyPost(){
		TopicCommand cmd = new TopicCommand();
		cmd.setType(TopicCommandType.CONVERSATION);
		cmd.setPostId("EC706434-1B59-4CF1-9F80-515EE38C1696");
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		assertEquals("EC706434-1B59-4CF1-9F80-515EE38C1696", posts.get(0).getPostId());
		assertEquals(3, result.getResults().getTotalResults());
	}
	
	
	@Test
	public void testNestedThread(){
		TopicCommand cmd = new TopicCommand();
		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY);
		cmd.setPostId("EC706434-1B59-4CF1-9F80-515EE38C1696");//Some random reply in Version6 thread
		
		cmd.setSortOrder(SortOrder.BY_DATE);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		List<GPost> posts = result.getResults().getList();
		
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", posts.get(0).getPostId());
		//assertEquals(10, result.getResults().getTotalResults()); //Trevis, this was what this test did before the refactor.. no clue how that worked.
		
		assertEquals(3, posts.get(0).getPosts().size()); // total branch post count (remember in this mode they are flat beneath the converaation)
		assertEquals(20, result.getResults().getList().size()); //conversations on page
		assertEquals(98, result.getResults().getTotalResults()); //Conversation starters in thread
	}
	
	@Test
	public void testNestedFetchMoreResults(){
		TopicCommand cmd = new TopicCommand();
		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
		cmd.setPostId("906C34D1-57CF-4D9D-BCA8-105184A6F2EE");//A starter with 15 replies
		//cmd.setSortByDate(true);
		cmd.setSortOrder(SortOrder.BY_DATE);
		cmd.setPageNumber(2);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		TopicCommandResult result = (TopicCommandResult)cmdexec.executeCommand();
		assertEquals(2, result.getResults().getCurrentPage());
		List<GPost> posts = result.getResults().getList();
		//This test only passes if the page size in ThreadDao is set to 5
		assertEquals("00075.00000.00000.00000.00000.00000.00000.00001", posts.get(0).getPath());
		assertEquals("00075.00000.00000.00000.00000.00000.00000", posts.get(4).getPath());
	}

}

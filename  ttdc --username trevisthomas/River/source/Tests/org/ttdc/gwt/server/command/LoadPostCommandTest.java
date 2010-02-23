package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.server.command.executors.PostCrudCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class LoadPostCommandTest {
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
	public void loadPostTest(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId(Helpers.rootIdVersion6Live);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
	
	@Test
	public void loadConversationFromConversation(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId("B3D57670-4162-4B99-9C0D-5E3DE54EB990");
		cmd.setLoadThreadAncestor(true);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", result.getPost().getPostId());
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
	
	// "EC706434-1B59-4CF1-9F80-515EE38C1696" // post
	//"B3D57670-4162-4B99-9C0D-5E3DE54EB990" //thread
	@Test
	public void loadConversationFromRoot(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId(Helpers.rootIdVersion6Live);
		cmd.setLoadThreadAncestor(true);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		assertEquals(Helpers.rootIdVersion6Live, result.getPost().getPostId()); //Cant load conversation from root, so it loads itself
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
	
	@Test
	public void loadConversationFromReply(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId("EC706434-1B59-4CF1-9F80-515EE38C1696");
		cmd.setLoadThreadAncestor(true);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", result.getPost().getPostId());
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
	
	@Test
	public void loadRootFromPost(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId("B3D57670-4162-4B99-9C0D-5E3DE54EB990");
		cmd.setLoadRootAncestor(true);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		assertEquals(Helpers.rootIdVersion6Live, result.getPost().getPostId());
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
	
	@Test
	public void loadConversationFromPost(){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setPostId("EC706434-1B59-4CF1-9F80-515EE38C1696");
		cmd.setLoadThreadAncestor(true);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PostCrudCommandExecutor);
		PostCommandResult result = (PostCommandResult)cmdexec.executeCommand();
		assertEquals("B3D57670-4162-4B99-9C0D-5E3DE54EB990", result.getPost().getPostId());
		assertEquals("Version 6 Live!", result.getPost().getTitle());
	}

	
	
	
}

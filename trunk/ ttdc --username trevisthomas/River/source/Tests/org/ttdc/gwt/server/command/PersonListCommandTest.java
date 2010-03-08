package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.server.command.executors.PersonListCommandExecutor;
import org.ttdc.gwt.shared.commands.PersonListCommand;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortBy;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class PersonListCommandTest {
	private final static Logger log = Logger.getLogger(PersonListCommandTest.class);
	
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
	public void loadMovieReviewers(){
		PersonListCommand cmd = new PersonListCommand(PersonListType.MOVIE_REVIEWERS);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PersonListCommandExecutor);
		PersonListCommandResult result = (PersonListCommandResult)cmdexec.executeCommand();
		
		assertTrue(result.getPersonList().size() == 9);
		assertEquals("AntiElvis", result.getPersonList().get(0).getLogin());
	}
	
	@Test
	public void loadActive(){
		PersonListCommand cmd = new PersonListCommand(PersonListType.ACTIVE);
		cmd.setSortOrder(SortBy.BY_LOGIN);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PersonListCommandExecutor);
		PersonListCommandResult result = (PersonListCommandResult)cmdexec.executeCommand();
		
		assertTrue("Active list is smaller than i expected ",result.getResults().getTotalResults() > 20);
		//assertEquals(24,result.getResults().getTotalResults());
	}
	
	@Test
	public void loadAll(){
		PersonListCommand cmd = new PersonListCommand(PersonListType.ALL);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PersonListCommandExecutor);
		PersonListCommandResult result = (PersonListCommandResult)cmdexec.executeCommand();
		assertTrue("All user list is smaller than i expected ",result.getResults().getTotalResults() > 200);

	}
	

}

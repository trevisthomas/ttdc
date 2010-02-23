package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.server.command.executors.PrivilegeCrudCommandExecutor;
import org.ttdc.gwt.shared.commands.PrivilegeCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class PrivilegeCrudCommandTest {
private final static Logger log = Logger.getLogger(PrivilegeCrudCommandTest.class);
	
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
		PrivilegeCrudCommand cmd = new PrivilegeCrudCommand();
		cmd.setAction(ActionType.READ);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof PrivilegeCrudCommandExecutor);
		GenericListCommandResult<GPrivilege> result = (GenericListCommandResult<GPrivilege>)cmdexec.executeCommand();
		
		assertEquals(4,result.getList().size());
		
	}
	
}

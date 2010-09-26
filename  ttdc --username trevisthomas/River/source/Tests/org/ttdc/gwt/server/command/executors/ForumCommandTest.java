package org.ttdc.gwt.server.command.executors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.rollback;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.ForumCommand;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class ForumCommandTest {
	private final static Logger log = Logger.getLogger(ForumCommandTest.class);
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
	public void justExerciseTheForumList()
	{
		try{
			
			final ForumCommand cmd = new ForumCommand();
			ForumCommandExecutor cmdexec = (ForumCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			
			beginSession();
			
			CommandResult result = cmdexec.execute();
			
			assertNotNull(result);
			
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}

	}
	
}

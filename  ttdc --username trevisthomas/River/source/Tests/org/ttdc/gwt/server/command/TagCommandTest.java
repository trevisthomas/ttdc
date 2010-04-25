package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.command.executors.TagCommandExecutor;
import org.ttdc.gwt.shared.commands.TagCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.results.TagCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class TagCommandTest {
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
	
	@Ignore
	public void creatorListTest(){
//		TagCommand cmd = new TagCommand();
//		cmd.setAction(TagActionType.LOAD_CREATORS);
//		
//		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
//		assertTrue("Factory returned the wrong implementation", cmdexec instanceof TagCommandExecutor);
//		TagCommandResult result = (TagCommandResult)cmdexec.executeCommand();
//		
//		List<GTag> tags = result.getTagList();
//		
//		assertEquals("Trevis", tags.get(0).getValue());
	}
	
	@Test
	public void ratingTagListTest(){
		TagCommand cmd = new TagCommand();
		cmd.setAction(TagActionType.LOAD_RATINGS);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof TagCommandExecutor);
		GenericListCommandResult<GTag> result = (GenericListCommandResult<GTag>)cmdexec.executeCommand();
		
		List<GTag> tags = result.getList();
		
		assertEquals(10, tags.size());
		
		for(GTag t : tags){
			assertEquals("RATING", t.getType());
		}
	}
}

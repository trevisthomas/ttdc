package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.server.command.executors.ImageListCommandExecutor;
import org.ttdc.gwt.shared.commands.ImageListCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;

public class ImageListCommandTest {
	private final static Logger log = Logger.getLogger(ImageListCommandTest.class);
	
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
	public void loadImageList(){
		ImageListCommand cmd = new ImageListCommand();
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof ImageListCommandExecutor);
		PaginatedListCommandResult<GImage> result = (PaginatedListCommandResult<GImage>)cmdexec.executeCommand();
		
		assertEquals(20, result.getResults().getList().size());
	}
}

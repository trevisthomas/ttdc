package org.ttdc.gwt.server.command;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.ttdc.gwt.server.command.executors.MovieListCommandExecutor;
import org.ttdc.gwt.shared.commands.MovieListCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;

public class MovieListCommandTest {
	@Test
	public void testMovieList(){
		MovieListCommand cmd = new MovieListCommand();
		cmd.setPageNumber(1);
		cmd.setSortDirection(SortDirection.DESC);
		cmd.setSortBy(SortBy.BY_RATING);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof MovieListCommandExecutor);
		SearchPostsCommandResult result = (SearchPostsCommandResult)cmdexec.executeCommand();
		
		assertNotNull(result.getResults().getList());
		assertEquals(20,result.getResults().getList().size() );
		//assertEquals("Version 6 Live!", result.getPost().getTitle());
	}
}

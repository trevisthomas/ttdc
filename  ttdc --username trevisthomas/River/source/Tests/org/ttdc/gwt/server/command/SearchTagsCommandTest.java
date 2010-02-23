package org.ttdc.gwt.server.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.executors.SearchTagsCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class SearchTagsCommandTest {
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
	public void searchForTags(){
		SearchTagsCommand cmd = new SearchTagsCommand("morsels");
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchTagsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
		for(GTag tag : results.getList()){
			assertTrue("Cloud rank is not assigned", tag.getCloudRank() >= 0); //Check that cloud rank has been assigned.
		}
	}
	
	@Test
	public void searchForTagsPageNumber(){
		SearchTagsCommand cmd = new SearchTagsCommand("the", 2);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchTagsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		assertTrue("Asked for page two but got something else",results.getCurrentPage() == 2);
	}
	
	@Test
	public void searchForTagById(){
		//This is really just doing a simple lookup but i need this
		//to show details about a tag when the tag comes from the url
		//in the client.
		
		List<String> tagIds = new ArrayList<String>();
		tagIds.add(Helpers.tagCorporateGoodness);
		tagIds.add(Helpers.tagKimD);
		
		SearchTagsCommand cmd = new SearchTagsCommand(tagIds);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchTagsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
		assertEquals(Helpers.tagCorporateGoodness, results.getList().get(0).getTagId());
	}

	//Trevis you noticed that searching with a blank phrase just returns every tag in the system. FYI
	
}

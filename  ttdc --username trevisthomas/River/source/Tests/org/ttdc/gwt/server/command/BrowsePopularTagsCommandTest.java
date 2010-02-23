package org.ttdc.gwt.server.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ttdc.gwt.server.dao.Helpers.personIdTrevis;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.executors.BrowsePopularTagsCommandExecutor;
import org.ttdc.gwt.shared.commands.BrowsePopularTagsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchTagsCommandResult;
import org.ttdc.gwt.shared.commands.types.SortOrder;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class BrowsePopularTagsCommandTest {
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
	public void browseForMostPopularTags(){
		BrowsePopularTagsCommand cmd = new BrowsePopularTagsCommand();
		cmd.setMaxTags(100);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof BrowsePopularTagsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Didnt find the number of results that i expected",results.getPageSize() == cmd.getMaxTags());
		
		//This command should only shows topic tags
		for(GTag tag : results.getList()){
			assertEquals(tag.getType(), Tag.TYPE_TOPIC);
		}
		
		//Lame attempt to verify that the the order is not alphabetical
		assertTrue(results.getList().get(0).getValue().charAt(0) != 'A');
	}
	
	@Test
	public void browseForMostPopularTagsAlphabeticalOrder(){
		BrowsePopularTagsCommand cmd = new BrowsePopularTagsCommand();
		//cmd.setMaxTags(100);
		cmd.setSortOrder(SortOrder.ALPHABETICAL);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof BrowsePopularTagsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Didnt find the number of results that i expected",results.getPageSize() == cmd.getMaxTags());
		
		//This command should only shows topic tags
		for(GTag tag : results.getList()){
			assertEquals(tag.getType(), Tag.TYPE_TOPIC);
		}
		
		//Lame attempt to verify that the the order is alphabetical
		assertTrue(results.getList().get(0).getValue().charAt(0)== 'A');
	}

	@Test
	public void testTagCloudRanking(){
		//assignPostRelativeAges
		BrowsePopularTagsCommand cmd = new BrowsePopularTagsCommand();
		cmd.setMaxTags(100);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GTag> results = ((SearchTagsCommandResult)result).getResults();
		
		assertTrue("Didnt find the number of results that i expected",results.getPageSize() == cmd.getMaxTags());
		
		//This command should only shows topic tags
		for(GTag tag : results.getList()){
			assertEquals(tag.getType(), Tag.TYPE_TOPIC);
			assertTrue("Cloud rank is not assigned", tag.getCloudRank() >= 0); //Check that cloud rank has been assigned.
			
		}
		
		
		
		
	}

}

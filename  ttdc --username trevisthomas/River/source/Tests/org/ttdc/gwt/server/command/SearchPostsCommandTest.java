package org.ttdc.gwt.server.command;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.command.executors.SearchPostsCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.server.dao.LatestPostsDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.gwt.server.dao.PostSearchDao;

import static org.ttdc.gwt.server.dao.Helpers.*;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SortType;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;


public class SearchPostsCommandTest{
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
	public void searchPostBodyForText(){
		SearchPostsCommand cmd = new SearchPostsCommand("obama");
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchPostsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
	}
	
	@Test
	public void serachForPostsWithTags(){
		SearchPostsCommand cmd = new SearchPostsCommand("morsels");
		cmd.setTitleSearch(true);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchPostsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
		for(GPost p : results.getList()){
//			assertTrue("Non root post was found!",p.isRootPost());
			log.debug(p.getTitle());
			assertTrue("Title \""+p.getTitle()+"\" didnt contain the search arg",p.getTitle().toLowerCase().contains(cmd.getPhrase()));
		}
	}
	
//	@Test
//	public void serachForPostsWithNullTagList(){
//		SearchPostsCommand cmd = new SearchPostsCommand("morsels");
//		cmd.setTitleSearch(true);
//		cmd.setTagIdList(null);
//		
//		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
//		
//		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchPostsCommandExecutor);
//		CommandResult result = cmdexec.executeCommand();
//		
//		assertNotNull("Command execution produced a null result", result);
//		
//		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
//		
//		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
//		
//		for(GPost p : results.getList()){
//			assertTrue("Non root post was found!",p.isRootPost());
//			log.debug(p.getTitle());
//		}
//	}
	
	@Test
	public void testPagination(){
		SearchPostsCommand cmd = new SearchPostsCommand("test");
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		
		cmd.setPageNumber(2);
		
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof SearchPostsCommandExecutor);
		CommandResult result = cmdexec.executeCommand();
		
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		assertTrue("Didnt paginate to the proper page", cmd.getPageNumber() == results.getCurrentPage());
	}
	
	
	
	@Test
	public void browsePostsWithTags(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		
		List<String> unionTags = new ArrayList<String>();
		unionTags.add(tagGeneralStuff);
		
		cmd.setTagIdList(unionTags);
		
		//cmd.setSortOrder(SortType.BY_DATE);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
		assertTagged(results.getList(),tagGeneralStuff);
		
	}
	
	@Test
	public void searchWithinTagSubset(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		
		List<String> unionTags = new ArrayList<String>();
		unionTags.add(tagGeneralStuff);
		
//		List<String> excludeTags =  new ArrayList<String>();
//		excludeTags.add(tagTrevis);
		
		cmd.setTagIdList(unionTags);
//		cmd.setNotTagIdList(excludeTags);
		//cmd.setSortOrder(SortType.BY_DATE); //Ignored for full text search
		cmd.setPhrase("crap");
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		
		assertTagged(results.getList(),tagGeneralStuff);
//		assertNotTagged(results.getList(),tagTrevis);
		assertEquals("Tagged 'General Stuff'",results.getPhrase());
	}
	
	@Test
	public void searchWithinThread(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		
		//I'm also testing the tag filtered version!
//		List<String> excludeTags =  new ArrayList<String>();
//		excludeTags.add(tagTrevis);
//		cmd.setNotTagIdList(excludeTags);
		
		cmd.setPhrase("font");
		cmd.setRootId(Helpers.rootIdVersion6Live);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		assertRootId(cmd.getRootId(), results);
		assertNotTagged(results.getList(),tagTrevis);
		
	}
	
	@Test
	public void searchWithinConversation(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		
//		List<String> excludeTags =  new ArrayList<String>();
//		excludeTags.add(tagTrevis);
//		cmd.setNotTagIdList(excludeTags);
		
		cmd.setPhrase("font");
		cmd.setThreadId("06247F33-5295-4867-A2ED-63310F9DD643");//A conversation in COH
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		assertRootId(Helpers.rootIdVersion6Live, results);
		assertThreadId(cmd.getThreadId(), results);
//		assertNotTagged(results.getList(),tagTrevis);
	}
	
	@Test
	public void browseConversationsByUser(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			String creatorId = personIdTrevis;
			
			dao.setPostSearchType(PostSearchType.CONVERSATIONS);
			dao.setCreator(PersonDao.loadPerson(creatorId));
			
			PaginatedList<Post> results = dao.search();
			
			Helpers.printResults(results,log);
			
			assertTrue("Didnt find anything",results.getList().size() > 0);
						
			for(Post post : results.getList()){
				assertTrue("Found a post that isnt a conversation starter",post.isThreadPost());
			}
			for(Post post : results.getList()){
				assertEquals("Some other creator created this post. ",post.getCreator().getPersonId(),creatorId);
			}
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void searchForMovies(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setReviewsOnly(true);
		cmd.setPersonId(personIdTrevis);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something",results.getTotalResults() > 0);
		assertNotTagged(results.getList(),tagTrevis);
		
	}
	
	//This test will fail if no threads are muted by the user
	@Test
	public void searchForFilteredThreads(){
		SearchPostsCommand cmd = new SearchPostsCommand();
		cmd.setPostSearchType(PostSearchType.FILTERED_BY_USER);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(personIdTrevis,cmd);
		CommandResult result = cmdexec.executeCommand();
		assertNotNull("Command execution produced a null result", result);
		
		PaginatedList<GPost> results = ((SearchPostsCommandResult)result).getResults();
		
		assertTrue("Found nothing, expected something.  Are you sure there are muted threads for this user?",results.getTotalResults() > 0);
		//assertNotTagged(results.getList(),tagTrevis);
	}
	
	//The following are similar to the ones in helper but these use GPost inseatd
	// should probably make that class generic i guess?
	public static void assertTagged(List<GPost> posts, String tagId){
		for(GPost post : posts){
			assertTrue("Every post should contain this tag, but one doesnt!", associationListContainsTag(post.getTagAssociations(), tagId.toUpperCase()));
			//assertTagged(post.getPosts(), tagId);
		}
	}
	public static boolean associationListContainsTag(List<GAssociationPostTag> asses, String tagId){
		for(GAssociationPostTag ass : asses){
			if(ass.getTag().getTagId().equals(tagId))
				return true;
		}
		return false;
	}
	
	public static void assertNotTagged(List<GPost> posts, List<String> tagIds){
		for(String tagId : tagIds){
			assertNotTagged(posts, tagId);
		}
	}
	
	public static void assertNotTagged(List<GPost> posts, String tagId){
		for(GPost post : posts){
			assertTrue("These tags should have been fitered out!", !associationListContainsTag(post.getTagAssociations(), tagId.toUpperCase()));
		}
	}
	
	public static void assertRootId(String threadId, PaginatedList<GPost> results) {
		for(GPost post : results.getList()){
			Assert.assertEquals(threadId, post.getRoot().getPostId());
		}
	}
	public static void assertThreadId(String conversationId, PaginatedList<GPost> results) {
		for(GPost post : results.getList()){
			Assert.assertEquals(conversationId, post.getThread().getPostId());
		}
	}
	
	//cmd.setNotTagIdList(notTagIdList) // Test filtering NWS
	
}

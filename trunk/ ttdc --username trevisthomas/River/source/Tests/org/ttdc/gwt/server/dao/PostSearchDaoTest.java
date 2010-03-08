package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ttdc.gwt.shared.commands.types.PostSearchType;
import org.ttdc.gwt.shared.commands.types.SearchSortBy;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.util.PostFlag;

import static org.ttdc.gwt.server.dao.Helpers.*;


public class PostSearchDaoTest {
	private final static Logger log = Logger.getLogger(PostSearchDaoTest.class);
	
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
	public void searchForPostContaining(){
		
		try{
			beginSession();
			PostSearchDao dao = new PostSearchDao();
			String phrase = "obama";
			int currentPage = 2;
			
			dao.setCurrentPage(currentPage);
			dao.setPhrase(phrase);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(phrase, currentPage, results);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	
	}

	
	//No criteria search now returns the whole DB!
//	@Test 
//	public void searchWithNoCriteria(){
//		try{
//			beginSession();
//			PostSearchDao dao = new PostSearchDao();
//			int currentPage = 1;
//			
//			dao.setCurrentPage(currentPage);
//			
//			dao.search();
//			
//			fail("There was no search criteria given but the search didnt throw an exception as it should have");			
//			commit();
//		}
//		catch(Exception e){
//			rollback();
//			assertTrue(true);
//		}
//	
//	}
	
	@Test
	public void browseTaggedContent(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(tagGeneralStuff);
			//dao.addTagId(tagTrevis);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			
			
			
			//assertTagged(results.getList(), tagTrevis);
			assertTagged(results.getList(), tagGeneralStuff);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void theTrevisTest(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			
			dao.setPhrase("creator: Trevis");
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			
			assertCreator(results, "Trevis");
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void browseTaggedContentFilterTag(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(tagGeneralStuff);
			dao.addNotTagId(tagTrevis);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			
			assertNotTagged(results.getList(), tagTrevis);
			assertTagged(results.getList(), tagGeneralStuff);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTaggedContentSortByPopularity(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.addTagId(tagGeneralStuff);
			dao.setSortBy(SearchSortBy.POPULARITY);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			
			assertTagged(results.getList(), tagGeneralStuff);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	//TODO: add more tags an test this better. There are so few tags now...
	@Test
	public void serachWithinTaggedSubset(){
		try{
			beginSession();
			
			int currentPage = 1;
			
			PostSearchDao dao = new PostSearchDao();

			String phrase = "time";
//			dao.addNotTagId("");
//			dao.addNotTagId(tagKimD);
			dao.addTagId("1FDCB845-0327-493A-AE41-5539334256E4");
//			dao.addTagId("E6EA279D-E3E1-4168-B6EC-EE05A4DBE08D");
			
			dao.setPhrase(phrase);
			dao.setCurrentPage(currentPage);
			
			PaginatedList<Post> results = dao.search();
			assertTrue("no results",results.getList().size() > 0);
			Helpers.printResults(results, log);
//			assertNotCreator(results,"Trevis");
//			assertNotCreator(results,"KimD");
			assertTagged(results.getList(),"1FDCB845-0327-493A-AE41-5539334256E4");
//			assertTagged(results.getList(),"E6EA279D-E3E1-4168-B6EC-EE05A4DBE08D");
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void serachForPostsByUser(){
		try{
			
			beginSession();
			int currentPage = 1;
			
			PostSearchDao dao = new PostSearchDao();
			dao.setCurrentPage(currentPage);
			String phrase = "sucks +creator:trevis";
			//dao.addTagId(tagTrevis);
			dao.addTagId(tagGeneralStuff);
			dao.setPhrase(phrase);			
			
			PaginatedList<Post> results = dao.search();
			
			assertTrue("no results",results.getList().size() > 0);
			
			assertCreator(results, "Trevis");
			assertTagged(results.getList(), tagGeneralStuff);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	/* 
	 * I'm excluding a user as an example but this is also the way to exclude private and nws content.
	 * 
	 */
	@Test
	public void serachForPostsExcludeUser(){
		try{
			
			beginSession();
			int currentPage = 1;
			
			PostSearchDao dao = new PostSearchDao();
			dao.setCurrentPage(currentPage);
			String phrase = "sucks -creator:trevis";
			dao.addTagId(tagGeneralStuff);
			dao.setPhrase(phrase);
			//dao.addNotTagId(tagTrevis);
			
			PaginatedList<Post> results = dao.search();
			
			assertTrue("no results",results.getList().size() > 0);
			
			//assertCreator(results, "Trevis");
			assertTagged(results.getList(), tagGeneralStuff);
			assertNotTagged(results.getList(), tagTrevis);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void searchWithInvalidPageNumber(){
		try{
			
			beginSession();
			int currentPage = 10;
			
			PostSearchDao dao = new PostSearchDao();
			dao.setCurrentPage(currentPage);
			String phrase = "sucks";
			dao.addTagId(tagTrevis);
			dao.addTagId(tagGeneralStuff);
			
			dao.setNotTagIdList(null);
			dao.setPhrase(phrase);			
			
			PaginatedList<Post> result = dao.search();
			assertTrue("I made a valid search but choose a page number higer than the max, it should have returned an empty result", result.isEmpty());
			
			commit();
		}
		catch(Exception e){
			rollback();
			/*Expected*/
		}
	}
	
	
	@Ignore //This was was never implemented?
	public void searchUsingSetTagIdList(){
		
	}
	
	@Test
	public void searchByTitle(){
		try{
			beginSession();
			PostSearchDao dao = new PostSearchDao();
			String phrase = "political";
			int currentPage = 1;
			
			dao.setCurrentPage(currentPage);
			dao.setPhrase(phrase);
			dao.setSearchByTitle(true);
			
			PaginatedList<Post> results = dao.search();
			
//			assertTrue("Found more that one post, not what i expected",results.getList().size() == 1);
//			assertTrue("Post found was not a root, not what should have happened", results.getList().get(0).isRootPost());
//			assertEquals("Morsels of Political Goodness",results.getList().get(0).getTitle());
			
			assertTrue("found nothing, when i expected something",results.getList().size() > 0);
			
			for(Post p : results.getList()){
				assertTrue("Found a title that didnt match what i searched for.",p.getTitle().toLowerCase().indexOf(phrase) >= 0);
			}
			
			
			//Phrase doesnt look like this anymore
			//assertEquals(phrase, results.getPhrase());
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	/**
	 * 
	 * This ability isnt used but it does seem to work
	 * 
	 *... not anymore so screw it
	 */
//	@Test
//	public void serachWithinTaggedSubsetAndThread(){
//		try{
//			beginSession();
//			
//			int currentPage = 1;
//			
//			PostSearchDao dao = new PostSearchDao();
//
//			String phrase = "sucks";
//			dao.addTagId(tagTrevis);
//			//dao.addNotTagId(tagKimD);
//			//dao.addTagId(tagApril);
//			//dao.addTagId(tagOFour);
//			dao.setRootId("DA634691-21EE-40F3-9BCE-660A68AB8FE5");
//			
//			dao.setPhrase(phrase);
//			dao.setCurrentPage(currentPage);
//			
//			PaginatedList<Post> results = dao.search();
//			assertTrue("no results",results.getList().size() > 0);
//			Helpers.printResults(results, log);
//			assertCreator(results,"Trevis");
//			assertNotCreator(results,"KimD");
//			//assertTagged(results.getList(),tagApril);
//			
//			commit();
//		}
//		catch(Exception e){
//			rollback();
//			fail(e.getMessage());
//		}
//	}
	

	@Test
	public void serachWithinTopic(){
		try{
			beginSession();
			
			String rootId = "18F31393-5285-4463-9BCA-44201B59E3DB"; //Coh thread
			String phrase = "nikki knox";
			//Sample root post id's
			//String threadId = "DA634691-21EE-40F3-9BCE-660A68AB8FE5"; //Political Goodness thread
			//String threadId = "3FB109C4-1C08-4A52-A9C5-5288E985517B"; //Man Does that Suck
			//String threadId = "79B5B1FA-9E8B-4C22-B15A-FA60D19BFB8D";//
			//String threadId = "D6919F63-E1AF-4F6F-B7A8-CB4DB85214DF";//wack Lyrics
			
			
			int currentPage = 1;
			PostSearchDao dao = new PostSearchDao();
			dao.setRootId(rootId);
			dao.setPhrase(phrase);
			dao.setCurrentPage(currentPage);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getPhrase(), currentPage, results);
			assertRootId(rootId, results);
			assertTrue("no results",results.getList().size() > 0);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void searchWithinConversation(){
		try{
			beginSession();
			
			String threadId = "06247F33-5295-4867-A2ED-63310F9DD643"; //A conversation in v6 live
			String phrase = "font";
			
			
			int currentPage = 1;
			PostSearchDao dao = new PostSearchDao();
			dao.setThreadId(threadId);
			dao.setPhrase(phrase);
			dao.setCurrentPage(currentPage);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getPhrase(), currentPage, results);
			assertThreadId(threadId, results);
			assertTrue("no results",results.getList().size() > 0);
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDateRange(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			String phrase = "stuff AND date: [20081221 TO 20081225]";
			dao.setPhrase(phrase);
			//dao.addTagId("3325CE14-A37E-4236-875C-F1D97F006682");
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(phrase, currentPage, results);
			Helpers.printResults(results,log);
			
			
			
			//assertTagged(results.getList(), tagTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	//This test doesnt work with KeywordAnalyzer, but i seem to need KeywordAnalyzer for the PostSearchType
	// bridge stuff
//	@Test
//	public void testSearchByPersonUsingPhrase(){
//		try{
//			beginSession();
//			
//			PostSearchDao dao = new PostSearchDao();
//			
//			int currentPage = 1;
//			dao.setCurrentPage(currentPage);
//			String phrase = "stuff AND tagIds:3325CE14-A37E-4236-875C-F1D97F006682";
//			dao.setPhrase(phrase);
//			//dao.addTagId("3325CE14-A37E-4236-875C-F1D97F006682");
//			
//			PaginatedList<Post> results = dao.search();
//			
//			assertSearchResults(phrase, currentPage, results);
//			Helpers.printResults(results,log);
//			
//			
//			
//			//assertTagged(results.getList(), tagTrevis);
//			commit();
//		}
//		catch(Exception e){
//			rollback();
//			fail(e.getMessage());
//		}
//	}
	
	@Test
	public void browseTagedConversationStartersContentWithExclude(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			
			dao.addNotTagId(tagGeneralStuff);
			
			//dao.setConversationsOnly(true);
			dao.setPostSearchType(PostSearchType.CONVERSATIONS);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			//results should be all non review conversation starters by trevis
			
			assertNotTagged(results.getList(), tagGeneralStuff);
			
			//assertTagged(results.getList(), tagLinten);
			//assertTagged(results.getList(), tagGeneralStuff);
			
			for(Post post : results.getList()){
				assertTrue("Found a post that isnt a conversation starter",post.isThreadPost());
			}
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void browseTagedConversationStartersContent(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
//			dao.addTagId(tagReview);
//			dao.addTagId(tagTrevis);
			 
			//dao.setConversationsOnly(true);
			dao.setPostSearchType(PostSearchType.CONVERSATIONS);
			
			PaginatedList<Post> results = dao.search();
			
			assertSearchResults(dao.getTagIdList().toString(), currentPage, results);
			Helpers.printResults(results,log);
			//results should be all movie reviews (conversation starters) by trevis
			//assertTagged(results.getList(), tagTrevis);
			//assertTagged(results.getList(), tagGeneralStuff);
			
			for(Post post : results.getList()){
				assertTrue("Found a post that isnt a conversation starter",post.isThreadPost());
			}
			
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDateRangeForBlankPhrase(){
		try{
			beginSession();
			
			PostSearchDao dao = new PostSearchDao();
			
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			//dao.setSearchByTitle(true);
			dao.setPostSearchType(PostSearchType.NOT_REPLIES);
			//String phrase = "date: [20081221 TO 20081225]";
			Calendar startCal = GregorianCalendar.getInstance();
			Calendar endCal = GregorianCalendar.getInstance();
			
			startCal.set(2009, Calendar.APRIL, 1);
			endCal.set(2009, Calendar.APRIL, 30);
			
			dao.setDateRange(new DateRange(startCal.getTime(), endCal.getTime()));
			dao.setPhrase("");
			//dao.addTagId("3325CE14-A37E-4236-875C-F1D97F006682");
			
			PaginatedList<Post> results = dao.search();
			
			assertTrue("Expected results but got none",results.getList().size() > 0);
			
			for(Post post : results.getList()){
				assertTrue("Found a reply when i was looking for only roots" , post.isRootPost() || post.isThreadPost());
				assertTrue("Date is out of the requested range", startCal.getTime().before(post.getDate()) && endCal.getTime().after(post.getDate()));
			}
			
			assertSearchResults("not tested at the time of creation", currentPage, results);
			Helpers.printResults(results,log);
			
			
			
			//assertTagged(results.getList(), tagTrevis);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	}
	
	@Test 
	public void testFilter(){
		
		try{
			beginSession();
			PostSearchDao dao = new PostSearchDao();
			dao.addFlagFilter(PostFlag.MOVIE);
			PaginatedList<Post> results = dao.search();
			assertEquals("Total post count is not what i expected",115075,results.getTotalResults());
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}
	
	}
	
	@Test 
	public void testFilterInverse(){
		
		try{
			beginSession();
			PostSearchDao dao = new PostSearchDao();
			dao.addFlagFilter(PostFlag.MOVIE);
			dao.setInvertFilterFuction(true);
			PaginatedList<Post> results = dao.search();
			assertEquals("Total post count is not what i expected",1013,results.getTotalResults());
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.toString());
		}
	
	}

	private void assertSearchResults(String phrase, int currentPage, PaginatedList<Post> results) {
		assertNotNull("Results returned were null.",results);
		assertTrue("Found nothing, when should have found something",results.getTotalResults() > 0);
		assertNotNull("The results object contains a null list",results.getList());
		assertTrue("Result list is empty",results.getList().size() > 0);
		//assertEquals("Results contain incorrect search phrase. ",phrase, results.getPhrase());
		//Trevis, fix the above.  (better)
		assertEquals("Results are not showing the correct page",currentPage,results.getCurrentPage());
	}
	
}

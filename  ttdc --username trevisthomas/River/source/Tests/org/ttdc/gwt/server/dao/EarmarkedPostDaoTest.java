package org.ttdc.gwt.server.dao;


import static org.junit.Assert.*;

import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.command.executors.LatestPostCommandExecutor;
import org.ttdc.gwt.shared.commands.LatestPostsCommand;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class EarmarkedPostDaoTest {
	@Test
	public void testEarmarkedPost(){
		try{
			beginSession();
			Person person = PersonDao.loadPerson(Helpers.personIdAdmin);
			
			String postIdPolitical = "93BD3F5D-FD00-44FA-AE9A-86F0B259B91D";
			Post post = PostDao.loadPost(postIdPolitical);
			
			assertTrue("Pre test condtition not satisfied.",!post.isEarmarkedByPerson(person.getPersonId()));
			
			AssociationPostTagDao assDao = new AssociationPostTagDao();
			TagDao tagDao = new TagDao();
			tagDao.setValue(person.getPersonId());
			tagDao.setType(org.ttdc.gwt.client.constants.TagConstants.TYPE_EARMARK);
			
			Tag tag = tagDao.createOrLoad();
			
			assDao.setCreator(person);
			assDao.setPost(post);
			assDao.setTag(tag);
			AssociationPostTag ass = assDao.create();
			
			assertTrue("Post is still not earmarked by person.",post.isEarmarkedByPerson(person.getPersonId()));

			session().flush();
			
			EarmarkedPostDao dao = new EarmarkedPostDao();
			int currentPage = 1;
			dao.setCurrentPage(currentPage);
			dao.setPersonId(person.getPersonId());
			dao.setTagId(tag.getTagId());
			
			PaginatedList<Post> results = dao.loadEarmarkedPosts();
			
			assertSearchResults("", currentPage, results);
			//Helpers.printResults(results,log);
			
			//assertTagged(results.getList(), tag.getTagId());
			
			assertTrue("Expected post not found",results.getList().get(0).getPostId().equals(postIdPolitical));

			// Finished testing, remove the earmark
			AssociationPostTagDao.remove(ass.getGuid());

			assertTrue("Failed to remove earmark from the post.",!post.isEarmarkedByPerson(person.getPersonId()));
		
			commit();	
		}
		catch (Exception e) {
			fail(e.toString());
			rollback();
		}
		finally{
		}
	}
	
	@Test
	public void loadEarmarksTest(){
		LatestPostsCommand cmd = new LatestPostsCommand();
		cmd.setAction(PostListType.LATEST_EARMARKS);
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof LatestPostCommandExecutor);
		PaginatedListCommandResult<GPost> result = (PaginatedListCommandResult<GPost>)cmdexec.executeCommand();
		
		//Assert this for a user with a known number of earmarks...
		//assertEquals(20, result.getResults().getList().size());
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

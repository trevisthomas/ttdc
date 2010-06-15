package org.ttdc.gwt.server.dao;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ttdc.gwt.server.dao.Helpers.assertTagged;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;
import static org.ttdc.persistence.Persistence.session;

import org.junit.Test;
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
			
			PaginatedList<Post> results = dao.getEarmarkedPosts();
			
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

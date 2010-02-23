package org.ttdc.gwt.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.commit;
import static org.ttdc.persistence.Persistence.rollback;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Post;
import org.ttdc.test.utils.UniqueCrudPostCommandObjectMother;


/**
 * See PostCrudCommandExecutor for more PostCreation tests!
 * 
 *
 */
public class PostDaoTest {
	@Before
	public void setup(){
		
	}
	
	@Test
	public void createRootPostTestEmptyPostException(){
		try{
			beginSession();
			PostDao dao = new PostDao();
			dao.setBody(null);
			dao.create();
			fail("Post was created without a body");
			//commit();
		}
		catch(Exception e){
			
		}
		finally{
			rollback();
		}

	}
	
	@Test
	public void readPostTest(){
		try{
			final String postId = "09694597-81CE-47FE-B3A4-5F1773EBC576";
			beginSession();
			Post post = PostDao.loadPost(postId);
			assertEquals("The returned post is invalid",postId,post.getPostId());
			assertTrue(post.getReplyCount() >= 2);
			commit();
		}
		catch(Exception e){
			assertTrue(true);
		}
		
	}
	
	//Conversation starter
	@Test
	public void createReplyPostTest()
	{
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String parentId = "09694597-81CE-47FE-B3A4-5F1773EBC576";
			beginSession();
			Post parent = PostDao.loadPost(parentId);
			int replyCount = parent.getReplyCount();
			int initialMass = parent.getRoot().getMass();
			
			
			PostDao dao = new PostDao();
			dao.setParent(parent);
			dao.setBody(cmd.getBody());
			//dao.setCreator(PersonDao.loadPerson(Helpers.personIdLinten));
			Post post = dao.create();
			assertNotNull("Reply Post has no parent",post.getParent());
			assertEquals("Post parent is oh so wrong",parentId,post.getParent().getPostId());
			assertEquals("Post does not have the same root as it's parent. This is wrong.",post.getParent().getRoot().getPostId(), post.getRoot().getPostId());
			assertPostCreatedWithBody(post, cmd.getBody());
			assertPostCreatedWithProperPath(post);
			assertEquals("Path value for new conversation was set wrong ","00002", post.getPath());
			assertNotNull("New converation is not a thread, it must be",post.getThread());
			assertEquals("New conversation should have it's thread set as it's THREAD_GUID ",post.getPostId(), post.getThread().getPostId());
			
			parent = PostDao.loadPost(parentId);
			
			assertEquals("Parent reply count did not increase!",  replyCount + 1, parent.getReplyCount());
			assertEquals("Root Mass attribute didnt increment",initialMass+1, parent.getRoot().getMass());
			
			
			
		}
		catch(Exception e){
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	
	
	@Test 
	public void testPathCalculation(){
		String siblingPath = "00002.00012.00001.00511";
		String expectedPath = "00002.00012.00001.00512";
		
		String path = PostDao.calculateNextPath(siblingPath);
		assertEquals("Path calculation for next path is broken.",expectedPath,path);
	}
	@Test 
	public void testPathGenerationWithSiblings(){
		try{
			beginSession();
			String postId = "FC5B7DFE-0527-41FC-A6D7-1A83D8333EB8"; //reply two levels deep with 2 children (Path="00005.00000")
			Post post = PostDao.loadPost(postId);
			String parentPath = post.getPath();
			assertEquals("One of the base assumptions is false. The post does not have the expected path.",parentPath, "00005.00000");
			String expectedNewPath = "00005.00000.00002";
			String newPath = PostDao.generatePath(post);
			assertEquals("Path is not what i expect it to be.  Make sure that this child thread didnt get any new siblings!",expectedNewPath, newPath);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void testPathGenerationWhenNoSiblingsExist(){
		try{
			beginSession();
			String postId = "9343F57A-5797-4EF1-A861-DE54E1EBDDF5"; //This is an old post from 7/4/2001 it has no replies
			Post post = PostDao.loadPost(postId);
			String parentPath = post.getPath();
			assertEquals("One of the base assumptions is false. The old reply post by Jedi on 7/4/2001 doesnt have the expected path. DB is different.",parentPath, "00000.00002");
			String expectedNewPath = "00000.00002.00000";
			String newPath = PostDao.generatePath(post);
			assertEquals("Path is not what i expect it to be.  Make sure that this child thread didnt get any new siblings!",expectedNewPath, newPath);
			commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
	}
	@Ignore
	public void testPathGenerationForConversationStarter(){
		
	}
	/*
	@Ignore
	public void testCreateRootPostWithTag(){
		
	}
	
	
	@Ignore
	public void createReplyPostWithTag()
	{
		
	}
	@Ignore 
	public void createMoviePost(){
		
	}
	*/
	
	//Trevis, create a higher level class for this kind of thing. 
	@Ignore
	private void assertPostCreatedWithBody(Post post, String body){
		assertNotNull("You didnt even get a result. sorry",post);
		assertTrue("No entry in new post",post.getEntries().size() > 0);
		Entry entry = post.getEntry();
		assertEquals("Entry doesn't match expected",body,entry.getBody());
		assertTrue(post.getReplyCount() == 0);
	}
	@Ignore
	private void assertPostCreatedWithProperPath(Post post){
		Post parent = post.getParent();
		String path = parent.getPath();
		String childpath = post.getPath();
		assertTrue("Path wasnt populated at all ",StringUtils.isNotEmpty(childpath));
		assertTrue(childpath.length() > path.length());
		
		assertTrue("The child path is not in the proper format check it out: [" +childpath+ "] .",childpath.matches("[0-9]{5}(\\.[0-9]{5})*"));
		
	}
	
	@Test
	public void testRegex2(){
		assertTrue("00000.00000.00002".matches("[0-9]{5}(\\.[0-9]{5})*"));
		
	}
	
}

package org.ttdc.gwt.server.command.executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ttdc.persistence.Persistence.beginSession;
import static org.ttdc.persistence.Persistence.rollback;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.command.CommandExecutorFactory;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.server.dao.PostDao;

import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.types.PostActionType;
import org.ttdc.gwt.shared.util.PostFlagBitmasks;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.test.utils.UniqueCrudPostCommandObjectMother;

public class PostCrudCommandExecutorTest {
	private final static Logger log = Logger.getLogger(PostCrudCommandExecutorTest.class);
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
	public void createADeeperReplyPostTest()
	{
		try{
			
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String parentPostId = "3F400AE9-A4F7-43A8-9A60-3E4BEA768782";
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(parentPostId);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post parent = PostDao.loadPost(parentPostId);
			Post root = PostDao.loadPost(parent.getRoot().getPostId());
			Post thread = PostDao.loadPost(parent.getThread().getPostId());
			
			//Remember, all posts have a reply count but only threads and roots have mass.
			int originalParentReplyCount = parent.getReplyCount();
			int originalRootMass = root.getMass();
			int originalThreadMass = thread.getMass();
			Post post = cmdexec.create(cmd);
			
			assertNotNull("Reply Post has no parent",post.getParent());
			assertEquals("Post parent is oh so wrong",parentPostId,post.getParent().getPostId());
			assertEquals("Post does not have the same root as it's parent. This is wrong.",post.getParent().getRoot().getPostId(), post.getRoot().getPostId());
			assertNotNull("Thread_guid (conversationId) is null.  That only happens for root posts",post.getThread());
			assertEquals("Post must be in the same conversation as it's parent, i sense a bug.",parent.getThread(),post.getThread());
			Helpers.assertPostCreatedWithBody(post, cmd.getBody());
			Helpers.assertPostCreatedWithProperPath(post);
			
			assertEquals("Post is not in the same thread as it's parent",parent.getThread().getPostId(),post.getThread().getPostId());
			
			assertEquals("Path is not what i expected","00003.00000.00000.00001",post.getPath()); //parent expects to already have one child
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			
			assertEquals("Six Flags",post.getTitle());
			
			assertEquals("Parent reply count didn't increment. ",originalParentReplyCount+1,post.getParent().getReplyCount());
			assertEquals("Mass on the root post didn't increment. ",originalRootMass+1,post.getRoot().getMass());
			assertEquals("Mass on the thread didn't increment. ",originalThreadMass+1,post.getThread().getMass());

			assertEquals("Thread Reply Date isnt set properly", post.getDate(), post.getThread().getThreadReplyDate());
			
//			Helpers.assertPostDateTagsCorrect(post);
						
			
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}

	}
	
	
	@Test
	public void testCreateNewConversation(){
		try{
			
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String rootPostId = "FFDF8F77-16D5-4E9E-97BE-3612CCD47F52";
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(rootPostId);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post parent = PostDao.loadPost(rootPostId);
//			Post root = PostDao.loadPost(parent.getRoot().getPostId());
						
			//Remember, all posts have a reply count but only threads and roots have mass.
			int originalParentReplyCount = parent.getReplyCount();
			int originalRootMass = parent.getMass();
//			int originalThreadMass = thread.getMass();
			Post post = cmdexec.create(cmd);
			
			
			assertNotNull("Thread reply date on a new conversation should be set to the conversation start date!!",post.getThreadReplyDate());
			assertEquals("Thread reply date on a new conversation should be equal to the post create date.",post.getDate(), post.getThreadReplyDate());
			
			assertNotNull("Reply Post has no parent",post.getParent());
			assertEquals("Post parent is oh so wrong",rootPostId,post.getParent().getPostId());
			assertEquals("Post does not have the same root as it's parent. This is wrong.",post.getParent().getRoot().getPostId(), post.getRoot().getPostId());
			assertNotNull("Thread_guid (conversationId) is null.  That only happens for root posts",post.getThread());
			
			assertTrue("Conversation starter post is not a conversation?!?",post.isThreadPost());
			
			assertEquals("Path is not what i expected","00041",post.getPath()); //parent expects to already have one child
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			
			Tag titleTag = post.getTitleTag();
			assertEquals("Fallout 3",titleTag.getValue());
			
			assertEquals("Parent reply count didn't increment. ",originalParentReplyCount+1,post.getParent().getReplyCount());
			assertEquals("Mass on the root post didn't increment. ",originalRootMass+1,post.getRoot().getMass());
			assertTrue("Parent should be root, but it;s not",post.getParent().isRootPost());
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
		
	}
	
	@Test
	public void testReviewMustBeOnAReviewableParent(){
		try{
			/*
			 * The idea is that a reply to a legacy post should push that post up the conversation starter level 
			 * in the hierarchy.  
			 */
			final String parentId = "FFDF8F77-16D5-4E9E-97BE-3612CCD47F52"; //Some topic, not a movie
			//final String parentId = "0F1937C5-0314-4CEC-8A6A-FAA22B2391BD"; //One of hte matrix movies
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(parentId);
			cmd.setReview(true);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
	
			fail("Post was created as a review but the parent isnt reviwable!");
			
		}
		catch(HibernateException e){
			fail(e.toString());
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testOnlyPriviledgedUsersCanCreatePosts(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String rootPostId = "FFDF8F77-16D5-4E9E-97BE-3612CCD47F52";
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(rootPostId);
			String cristelBallPersonId = "B161C22D-C4E3-4C77-B435-A48F9E5EB5FF"; //cristel_ball //User has no rights to create posts
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(cristelBallPersonId,cmd);

			beginSession();
			cmdexec.create(cmd);
			
			fail("A user without user priviledges just created a post!");
		}
		catch(Exception e){
			//*expected
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testCreateNewTopicAsAdmin(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			String title = cmd.getTitle();
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertTrue("Root post has a parent! WTF!",post.getParent() == null);
			assertTrue("Thread_guid must be null for root posts",post.getThread() == null);
			assertTrue("Root post is not root!!!",post.isRootPost());
			
			assertEquals("Path should be blank for roots","",post.getPath()); 
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdAdmin,post.getCreator().getPersonId());
			
			Tag titleTag = post.getTitleTag();
			assertEquals(title,titleTag.getValue());
			assertEquals(title,titleTag.getSortValue());
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testCreateNewTopic(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			String title = cmd.getTitle();
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertTrue("Root post has a parent! WTF!",post.getParent() == null);
			assertTrue("Thread_guid must be null for root posts",post.getThread() == null);
			assertTrue("Root post is not root!!!",post.isRootPost());
			
			assertEquals("Path should be blank for roots","",post.getPath()); 
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			Tag titleTag = post.getTitleTag();
			assertEquals(title,titleTag.getValue());
			assertEquals(title,titleTag.getSortValue());
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	
	
	@Test
	public void testCreateWithEmbededContent(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			String title = cmd.getTitle();
			String embedTarget = "EmbedTarget_PLACEHOLDER";
			String body = "<a target=\"_blank\" href=\"http://www.youtube.com/watch?v=SDbQ5xvsrIU\">test</a><a href=\"javascript:tggle_video('"+embedTarget+"','http://www.youtube.com/v/SDbQ5xvsrIU&amp;hl=en_US&amp;fs=1&amp;');\">[view]</a>";
			cmd.setBody(body);
			cmd.setEmbedMarker(embedTarget);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertTrue("Testing for embeded marker replacement content failed.",post.getEntry().getBody().indexOf("tggle_video('"+post.getPostId()+"'") > -1);
			
			assertTrue("Root post has a parent! WTF!",post.getParent() == null);
			assertTrue("Thread_guid must be null for root posts",post.getThread() == null);
			assertTrue("Root post is not root!!!",post.isRootPost());
			
			assertEquals("Path should be blank for roots","",post.getPath()); 
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
//			AssociationPostTag ass = post.loadTitleTagAssociation();
//			assertNotNull("Post didnt get a title tag!",ass);
			Tag titleTag = post.getTitleTag();
			assertEquals(title,titleTag.getValue());
			assertEquals(title,titleTag.getSortValue());
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testCreatePostWithFlags(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			String title = cmd.getTitle();
			cmd.setPrivate(true);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertTrue("Root post has a parent! WTF!",post.getParent() == null);
			assertTrue("Thread_guid must be null for root posts",post.getThread() == null);
			assertTrue("Root post is not root!!!",post.isRootPost());
			
			assertEquals("Path should be blank for roots","",post.getPath()); 
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			Tag titleTag = post.getTitleTag();
			assertTrue("Post should be flagged as private.",post.isPrivate());
			assertEquals(title,titleTag.getValue());
			assertEquals(title,titleTag.getSortValue());
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testCreatePostWithTags(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			String t1 = "0667A7DB-DA69-486C-AFD7-7DA53A65EB7E"; //tori amos
			//String t2 = "0708F658-D39F-4E18-B2BB-8A79B59F3907"; // fringe
			
			GTag tag1 = new GTag();
			tag1.setTagId(t1);
			GTag tag2 = new GTag();
			//tag2.setTagId(t2);
			tag2.setValue("A Value that doesnt exist.");
			
			cmd.addTag(tag1);
			cmd.addTag(tag2);
			String title = cmd.getTitle();
			cmd.setPrivate(true);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertTrue("Root post has a parent! WTF!",post.getParent() == null);
			assertTrue("Thread_guid must be null for root posts",post.getThread() == null);
			assertTrue("Root post is not root!!!",post.isRootPost());
			
			assertEquals("Path should be blank for roots","",post.getPath()); 
			
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			Tag titleTag = post.getTitleTag();
			assertTrue("Post should be flagged as private.",post.isPrivate());
			assertEquals(title,titleTag.getValue());
			assertEquals(title,titleTag.getSortValue());
			
			assertTrue("Post has no tags, but it should.",post.getTagAssociations().size()>0);
			
			Helpers.associationListContainsTag(post.getTagAssociations(),t1);
			//Helpers.associationListContainsTag(post.getTagAssociations(),t2);
			Helpers.assertTagged(post,tag2.getValue());
			
			
//			Helpers.assertPostDateTagsCorrect(post);
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testTitleRequiredNotNullForRootTopic(){
		try{
			
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			
			cmd.setAction(PostActionType.CREATE);
			
			cmd.setTitle(null);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);

			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Post was created with a null title!!");
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testTitleRequiredNotBlankForRootTopic(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewTopic();
			cmd.setAction(PostActionType.CREATE);
			cmd.setTitle("");
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);

			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Post was created with a null title!!");
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testLegacyMarkerPostsCanNotBeRepliedTo(){
		try{
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String parentId = "248BC263-6629-4F27-A77A-D8367E79C355"; //This is a legacy post marker! Cant reply to these!
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(parentId);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			fail("Can NOT reply to a legacy post marker");
		}
		catch(HibernateException e){
			fail(e.toString());
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
		
	}
	
	@Test
	public void testReparentReplyToNewRoot(){
		final String parentId = "F2DAB356-9F04-4E9C-AD2A-001D05A47448"; //A random root post; (different root from the one below)
		final String postId = "A75E5CA3-0B90-4D2D-A652-00020A00B29C"; //An old post that is not a thread root
		final PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.REPARENT);
		cmd.setParentId(parentId);
		cmd.setPostId(postId);
		
		try{
			beginSession();
			
			Post targetPost = PostDao.loadPost(postId);
			Post newParentPost = PostDao.loadPost(parentId);
			Post newRootPost = newParentPost.getRoot();
			
			String newRootPostId = newRootPost.getPostId();
			String newParentPostId = newParentPost.getPostId();
			
			int newParentReplyCount = newParentPost.getReplyCount();
			int newRootMass = newRootPost.getMass();
			String newTitle = newRootPost.getTitle();
			
			String oldParentPostId = targetPost.getParent().getPostId();
			String oldRootPostId = targetPost.getRoot().getPostId();
			String oldThreadPostId = targetPost.getThread().getPostId();
			
			assertTrue("Roots should be different for this test becusae i'm testing the changes in mass", newRootPostId != oldRootPostId);
			
			int oldParentReplyCount = targetPost.getParent().getReplyCount();
			int oldParentRootMass = targetPost.getRoot().getMass();
			int oldThreadMass = targetPost.getThread().getMass();
			
						
			//Old counts and masses should decrement, new ones should increment
			
			Persistence.commit(); //Calm down fat boy, this test isn't updating the db 
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			
			beginSession();
			Post post = cmdexec.reparent(cmd);
			
			assertEquals("Failed to set post to new parent, guid is wrong.", newParentPostId, post.getParent().getPostId());
			assertEquals("Failed to set post to new root, guid is wrong.", newRootPostId, post.getRoot().getPostId());
			
			assertEquals(newParentReplyCount+1,post.getParent().getReplyCount());
			assertEquals(newRootMass+1,post.getRoot().getMass());
			
			assertEquals("Old parent reply count didnt get decremented",oldParentReplyCount-1,PostDao.loadPost(oldParentPostId).getReplyCount());
			assertEquals("Old root mass didnt get decremented",oldParentRootMass-1, PostDao.loadPost(oldRootPostId).getMass());
			assertEquals("Old topic mass didnt decrease", oldThreadMass-1, PostDao.loadPost(oldThreadPostId).getMass());
			
			assertEquals("New path is suspicious.", "00001" , post.getPath()); //PostDao.loadPost(newParentPostId).getPath()
		
			assertTrue("Promoted post should be a thread",post.isThreadPost());
			
			assertEquals(newTitle, post.getTitle());
			
		}
		catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	
	/*
	 * prooves that posts cant be reparent to anything but a root.  Change this test if this feature is added later
	 */
	@Test
	public void testInvalidReparent(){
		final String parentId = "A75E5CA3-0B90-4D2D-A652-00020A00B29C"; //An old post that is not a thread root
		final String postId = "416A0040-82B9-498F-BDB5-0004E486A1DD"; // a random post that is also not a root
		final PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.REPARENT);
		cmd.setParentId(parentId);
		cmd.setPostId(postId);
		
		try{
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			cmdexec.reparent(cmd);
			
			fail("Posts can only be moved to new roots.");
		}
		catch(HibernateException e){
			fail(e.toString());
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void testLegacyActivateConversation(){
		try{
			/*
			 * The idea is that a reply to a legacy post should push that post up the conversation starter level 
			 * in the hierarchy.  
			 */
			final String parentId = "CD916F4E-1A6B-48CC-8195-62E9DFCA44E4"; //This is a randomly selected old post. (This posts's parent is a legacy marker)
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(parentId);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
	
			assertTrue("FAIL. The parent legacy post wasnt promoted!",post.getParent().isThreadPost());//The parent should have been moved up to being a conversation starter
			
		}
		catch(HibernateException e){
			fail(e.toString());
		}
		catch(Exception e){
			/*expected*/
		}	
		finally{
			rollback();
		}
	}
	
	
	
	@Test
	public void testMoveConversationToNewRoot(){
		String postIdToBeMoved = "A12B5DCA-2B26-469C-8294-E7734E65EE88";//a random thread with some children
		String newRootId = "F2DAB356-9F04-4E9C-AD2A-001D05A47448";//A random root post
		
		beginSession();
		Post postToBeMoved = PostDao.loadPost(postIdToBeMoved);
		Post newRootPost = PostDao.loadPost(newRootId);
		
		
		Post oldRootPost = postToBeMoved.getRoot();
		String oldRootPostId = oldRootPost.getPostId();
		String newRootPostId = newRootPost.getPostId();
		int oldRootMass = oldRootPost.getMass();
		int newRootPostMass = newRootPost.getMass();
		Persistence.commit();
		
		final PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.REPARENT);
		cmd.setParentId(newRootId);
		cmd.setPostId(postIdToBeMoved);
		
		PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		try{
			beginSession();
			Post post = cmdexec.reparent(cmd);
			
			String newTitle = post.getTitle();
			assertEquals("Old root mass didnt reduce by the expected amount",oldRootMass - 5,PostDao.loadPost(oldRootPostId).getMass());
			assertEquals("New root mass didnt grow by the expected amount",newRootPostMass + 5,PostDao.loadPost(newRootId).getMass());
			assertEquals("Root id didnt get update properly",post.getRoot().getPostId(),newRootId);
			
			@SuppressWarnings("unchecked")
			List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId ORDER BY path")
				.setString("threadId", postIdToBeMoved)
				.list();
			
			assertEquals("Thread had more children than i expected",5, posts.size());
			
			for(Post p : posts){
				assertEquals("Root didnt change properly",newRootPostId,p.getRoot().getPostId());
				assertEquals("I dont think that path didnt get update properly",p.getPath().substring(0, 5),"00001");
				assertEquals("Title is wrong", newTitle, p.getTitle());
			}
		}
		catch(Exception e){
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
		
	}
	
	//TODO bump a random branch up to being a new conversation
	@Test
	public void testMoveNestedBrangeToNewRoot(){
		String postIdToBeMoved = "F717B2F1-4D56-4461-870F-19A857886773"; //A random post two levels deep Path:00003.00000.00000
		String newRootPostId = "768E3FA4-2730-45F4-B178-00C507FC7D9C";
		String conversationPostIdOfToBeMoved;
		beginSession();
		
		Post postToBeMoved = PostDao.loadPost(postIdToBeMoved);
		Post newRootPost = PostDao.loadPost(newRootPostId);
		
		//Tags dont have mass based on post replies anymore.  
//		Tag titleTag = postToBeMoved.getTitleTag();
//		String titleTagId = titleTag.getTagId();
//		int originalOldTitleTagMass = titleTag.getMass();
//		int originalNewTitleTagMass = newRootPost.getTitleTag().getMass();
		
		
		Post conversationPost = postToBeMoved.getThread();
		conversationPostIdOfToBeMoved = conversationPost.getPostId();
		
		Post oldRootPost = postToBeMoved.getRoot();
		String oldRootPostId = oldRootPost.getPostId();
		
		int oldConversationMass = conversationPost.getMass();
		int oldRootMass = oldRootPost.getMass();
		int newRootPostMass = newRootPost.getMass();
		
		
		Persistence.commit();
		
		final PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.REPARENT);
		cmd.setParentId(newRootPostId);
		cmd.setPostId(postIdToBeMoved);
		
		PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		try{
			beginSession();
			Post post = cmdexec.reparent(cmd);
			Tag newTitleTag = post.getTitleTag();
			String newTitle = post.getTitle();
			assertEquals("Old root mass didnt reduce by the expected amount",oldRootMass - 3,PostDao.loadPost(oldRootPostId).getMass());
			assertEquals("New root mass didnt grow by the expected amount",newRootPostMass + 3,PostDao.loadPost(newRootPostId).getMass());
			assertEquals("Old conversation mass didnt shrink by the expected amount",oldConversationMass - 3,PostDao.loadPost(conversationPostIdOfToBeMoved).getMass());
			assertEquals("Root id didnt get update properly",post.getRoot().getPostId(),newRootPostId);
			assertTrue(post.isThreadPost());//Is a topic now.
			
			@SuppressWarnings("unchecked")
			List<Post> posts = Persistence.session().createQuery("SELECT p FROM Post p WHERE p.thread.postId = :threadId ORDER BY path")
				.setString("threadId", post.getPostId())
				.list();
			
			assertEquals("more than 3 posts... um, that's not right",3, posts.size());
			
			for(Post p : posts){
				assertEquals("Root didnt change properly",newRootPostId,p.getRoot().getPostId());
				assertEquals("I dont think that path didnt get update properly",p.getPath().substring(0, 5),"00001");
				//log.info("New Family: "+p.getPostId() + " Root: " +p.getRoot().getPostId()+ " Thread:"+p.getThread().getPostId() + " Path" + p.getPath());
				assertEquals("Title is wrong", newTitle, p.getTitle()); 
			}
			
//			assertEquals("Old title mass didnt decrease!",originalOldTitleTagMass-3, TagDao.loadTag(titleTagId).getMass());
//			assertEquals("New title mass didnt increase!",originalNewTitleTagMass+3, newTitleTag.getMass());
			
		}
		catch(Exception e){
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void createAReplyToAPrivatePostTest()
	{
		try{
			
			final PostCrudCommand cmd = UniqueCrudPostCommandObjectMother.createNewReply();
			final String parentPostId = "8338F5B5-112D-4E21-81B0-A510A963F3EC"; // A private post
			cmd.setAction(PostActionType.CREATE);
			cmd.setParentId(parentPostId);
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			//cmdexec.execute();
			
			beginSession();
			Post parent = PostDao.loadPost(parentPostId);
			
			assertTrue("Precondition not satisfyied. This test requires that the parent post is private.",parent.isPrivate());
			
			
			Post root = PostDao.loadPost(parent.getRoot().getPostId());
			Post thread = PostDao.loadPost(parent.getThread().getPostId());
			
			//Remember, all posts have a reply count but only threads and roots have mass.
			int originalParentReplyCount = parent.getReplyCount();
			int originalRootMass = root.getMass();
			int originalThreadMass = thread.getMass();
			Post post = cmdexec.create(cmd);
			
			assertNotNull("Reply Post has no parent",post.getParent());
			assertEquals("Post parent is oh so wrong",parentPostId,post.getParent().getPostId());
			assertEquals("Post does not have the same root as it's parent. This is wrong.",post.getParent().getRoot().getPostId(), post.getRoot().getPostId());
			assertNotNull("Thread_guid (conversationId) is null.  That only happens for root posts",post.getThread());
			assertEquals("Post must be in the same conversation as it's parent, i sense a bug.",parent.getThread(),post.getThread());
			Helpers.assertPostCreatedWithBody(post, cmd.getBody());
			Helpers.assertPostCreatedWithProperPath(post);
			
			assertEquals("Post is not in the same thread as it's parent",parent.getThread().getPostId(),post.getThread().getPostId());
			assertNotNull("Creator is null on the post object",post.getCreator());
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			assertEquals("Parent reply count didn't increment. ",originalParentReplyCount+1,post.getParent().getReplyCount());
			assertEquals("Mass on the root post didn't increment. ",originalRootMass+1,post.getRoot().getMass());
			assertEquals("Mass on the thread didn't increment. ",originalThreadMass+1,post.getThread().getMass());

			assertEquals("Thread Reply Date isnt set properly", post.getDate(), post.getThread().getThreadReplyDate());
			
			assertTrue("Parent was private, reply should also be private",post.isPrivate());
			
			
//			Helpers.assertPostDateTagsCorrect(post);
						
			
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}

	}
	
	
	@Test
	public void editAPostTest()
	{
		try{
			
			final PostCrudCommand cmd = new PostCrudCommand();
			
			cmd.setPostId("AA173DAE-3E89-4C70-9B9C-DB8A1A4A5656");
			cmd.setAction(PostActionType.UPDATE);
			String newBody = "neeeeboooo neeboo";
			cmd.setBody(newBody);
			
			beginSession();
			Post postBefore = PostDao.loadPost(cmd.getPostId());
			int initialEditCount = postBefore.getEntries().size();
			Date initialEditDate = postBefore.getEditDate();
			Persistence.commit();
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			
			beginSession();
			cmdexec.update(cmd);
			
			Post post = PostDao.loadPost(cmd.getPostId());
			
			
			assertEquals("Body was not updated",newBody, post.getEntry().getBody());
			assertEquals("Entries list didnt increment",initialEditCount+1, post.getEntries().size());
			assertTrue("Edit date didnt change",!initialEditDate.equals(post.getEditDate()) );
			
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}

	}
	
	@Test
	public void createAMovie(){
		try{
			PostCrudCommand cmd = new PostCrudCommand();
			cmd.setAction(PostActionType.CREATE);
			
			String title = "Cool Movie";
			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
			String url = "http://www.imdb.com/title/tt0398913/";
			cmd.setTitle(title);
			cmd.setMovie(true);
			cmd.setImageUrl(poster);
			cmd.setYear("2010");
			cmd.setUrl(url);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			
			assertEquals(Helpers.personIdTrevis,post.getCreator().getPersonId());
			
			Tag titleTag = post.getTitleTag();
			assertTrue ("This shold be a movie", post.isMovie());
			assertEquals("Title is wrong", title,titleTag.getValue());
			assertEquals("URL is wrong", url, post.getUrl());
			assertNotNull("Movie has no poster", post.getImage());
			assertEquals("CoolMovie.jpg", post.getImage().getName());
			assertTrue("Year is wrong", 2010 == post.getPublishYear());
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
//	@Test
//	public void createADuplicateMovie(){
//		try{
//			PostCrudCommand cmd = new PostCrudCommand();
//			cmd.setAction(PostActionType.CREATE);
//			
//			String title = "Crazy Heart";
//			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
//			String url = "http://www.imdb.com/title/tt0398913/";
//			cmd.setTitle(title);
//			cmd.setMovie(true);
//			cmd.setImageUrl(poster);
//			cmd.setYear("2009");
//			cmd.setUrl(url);
//			
//			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
//			beginSession();
//			Post post = cmdexec.create(cmd);
//			
//			fail("Movie with same title and year should be an error");
//		}
//		catch(RuntimeException e){
//			//expected
//			assertEquals("Movie with this title and year exist.  Cannot create duplicates.", e.getMessage());
//		}
//		catch(Exception e){
//			fail(e.getMessage());
//			e.printStackTrace();
//		}	
//		finally{
//			rollback();
//		}
//	}
	
	@Test
	public void createAnInvalidMovie(){
		try{
			PostCrudCommand cmd = new PostCrudCommand();
			cmd.setAction(PostActionType.CREATE);
			
			String title = "A New Movie Called Something Cool";
			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
			String url = "http://www.imdb.com/title/tt0398913/";
			cmd.setTitle(title);
			cmd.setMovie(true);
			cmd.setImageUrl(poster);
			//cmd.setYear("2010");
			cmd.setUrl(url);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Movie post was created with date missing.");
		}
		catch(Exception e){
			//Expected
			rollback();
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void createAnInvalidMovieMissingUrl(){
		try{
			PostCrudCommand cmd = new PostCrudCommand();
			cmd.setAction(PostActionType.CREATE);
			
			String title = "A New Movie Called Something Cool";
			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
			cmd.setTitle(title);
			cmd.setMovie(true);
			cmd.setImageUrl(poster);
			cmd.setYear("2010");
			
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Movie post was created with date missing.");
		}
		catch(Exception e){
			//Expected
			rollback();
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void createAnInvalidMovieMissingTitle(){
		try{
			PostCrudCommand cmd = new PostCrudCommand();
			cmd.setAction(PostActionType.CREATE);
			
			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
			String url = "http://www.imdb.com/title/tt0398913/";
			cmd.setMovie(true);
			cmd.setImageUrl(poster);
			cmd.setYear("2010");
			cmd.setUrl(url);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Movie post was created with date missing.");
		}
		catch(Exception e){
			//Expected
			rollback();
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void createAnInvalidMovieMissingPoster(){
		try{
			PostCrudCommand cmd = new PostCrudCommand();
			cmd.setAction(PostActionType.CREATE);
			
			String title = "A New Movie Called Something Cool";
			String url = "http://www.imdb.com/title/tt0398913/";
			cmd.setTitle(title);
			cmd.setMovie(true);
			//cmd.setYear("2010");
			cmd.setUrl(url);
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
			beginSession();
			Post post = cmdexec.create(cmd);
			
			fail("Movie post was created with date missing.");
		}
		catch(Exception e){
			//Expected
			rollback();
			e.printStackTrace();
		}	
		finally{
			rollback();
		}
	}
	
	//Edit movies...
	@Test
	public void editAMovieTitle(){
		try{
			
			final PostCrudCommand cmd = new PostCrudCommand();
			
			String title = "Wack yo!";
			cmd.setPostId("874D1519-B45D-46F6-9FA9-DE7ABC050C33");
			cmd.setAction(PostActionType.UPDATE);
			cmd.setTitle(title);
			
			beginSession();
			Post postBefore = PostDao.loadPost(cmd.getPostId());
			Persistence.commit();
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			
			beginSession();
			cmdexec.update(cmd);
			
			Post post = PostDao.loadPost(cmd.getPostId());
			
		
			assertEquals("Title was not updated.",title,post.getTitle());
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	@Test
	public void editAMoviePubYear(){
		try{
			
			final PostCrudCommand cmd = new PostCrudCommand();
			
			String pubYear = "1901";
			cmd.setPostId("874D1519-B45D-46F6-9FA9-DE7ABC050C33");
			cmd.setAction(PostActionType.UPDATE);
			cmd.setYear(pubYear);
			
			beginSession();
			Post postBefore = PostDao.loadPost(cmd.getPostId());
			Persistence.commit();
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			
			beginSession();
			cmdexec.update(cmd);
			
			Post post = PostDao.loadPost(cmd.getPostId());
			
			assertTrue("Pub year not updated",Integer.parseInt(pubYear) == post.getPublishYear());
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void editAMoviePoster(){
		try{
			
			final PostCrudCommand cmd = new PostCrudCommand();
			
			String poster = "http://www.iwatchstuff.com/2008/02/27/superhero-movie-poster.jpg";
			cmd.setPostId("874D1519-B45D-46F6-9FA9-DE7ABC050C33");
			cmd.setAction(PostActionType.UPDATE);
			cmd.setImageUrl(poster);
			
			beginSession();
			Post postBefore = PostDao.loadPost(cmd.getPostId());
			Persistence.commit();
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			
			beginSession();
			cmdexec.update(cmd);
			
			Post post = PostDao.loadPost(cmd.getPostId());
			
		
			assertEquals("CrazyHeart_2.jpg",post.getImage().getName());
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.toString());
		}	
		finally{
			rollback();
		}
	}
	
	@Test
	public void editAMovieUrl(){
		try{
			
			final PostCrudCommand cmd = new PostCrudCommand();
			
			String pubYear = "1901";
			cmd.setPostId("874D1519-B45D-46F6-9FA9-DE7ABC050C33");
			cmd.setAction(PostActionType.UPDATE);
			String url = "http://www.google.com/";
			cmd.setUrl(url);
			
			beginSession();
			Post postBefore = PostDao.loadPost(cmd.getPostId());
			Persistence.commit();
			
			PostCrudCommandExecutor cmdexec = (PostCrudCommandExecutor)CommandExecutorFactory.createExecutor(Helpers.personIdAdmin,cmd);
			
			beginSession();
			cmdexec.update(cmd);
			
			Post post = PostDao.loadPost(cmd.getPostId());
			
			assertEquals("Url not properly updated",url,post.getUrl());
			//commit();
		}
		catch(Exception e){
			rollback();
			fail(e.getMessage());
		}	
		finally{
			rollback();
		}
	}
}

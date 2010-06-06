package org.ttdc.gwt.server.command;

import static org.junit.Assert.*;


import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;
import org.ttdc.test.utils.ThreadUtils;


/**
 * 
 * NOTE if these tests fail they will leave dirty data in the DB so you should be ready to clean
 * house!!!!
 * 
 * 
 * Mar 11, Tread lightly here because this is very very old
 * @author Trevis
 *
 */
public class AssociationPostTagCommandTest{
	String serverEventConnId;
	String secondServerEventConnectionId;
	
	@Before
	public void startup(){
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		serverEventConnId = broadcaster.setupConnection(Helpers.personIdTrevis);
		//You realiez that this second id could also be fore Trevis right? (two browser are no problem)
		secondServerEventConnectionId = broadcaster.setupConnection(Helpers.personIdCSam);
		
	}
	@Test
	public void testCreateAndRemoveAssociationExistingTag(){
		GTag tag = new GTag();
		tag.setTagId(Helpers.tagCorporateGoodness);
		
		String postId = "0B20985A-0123-4933-8943-80C93F49A08B";//random post
		
		AssociationPostTagCommand cmd = createTagCommand(tag, postId);
		cmd.setConnectionId(serverEventConnId);
		
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		
		//Now remove it
		cmd = createRemoveTagCommand(result.getAssociationId());
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
		
	}
	
	/**
	 * This one will create or use an existing tag
	 */
	@Test
	public void testCreateAndRemoveAssociationForStringTag(){
		
		GTag tag = new GTag();
		tag.setValue("Brand New Tag");
		tag.setType(TagConstants.TYPE_TOPIC);
		
		String postId = "0B20985A-0123-4933-8943-80C93F49A08B";//random post
		
		AssociationPostTagCommand cmd = createTagCommand(tag, postId);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		
//		List<GAssociationPostTag> asses = result.getAssociationPostTag().getPost().getTagAssociations();
//		boolean found = false;
//		for(GAssociationPostTag ass : asses){
//			if(ass.getTag().getValue().equals(tag.getValue())){
//				found = true;
//				break;
//			}
//		}
//		if(found == false){
//			fail("I dont have the tag" );
//		}
		
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		assertTagged(result.getAssociationPostTag().getPost().getPostId(), tag.getValue());
		
		
		//Now remove it
		cmd = createRemoveTagCommand(result.getAssociationId());
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
		assertNotTagged(result.getPost().getPostId(),tag.getValue());
		//String postId = result.getAssociationPostTag().getPost().getPostId();
		
//		Persistence.beginSession();
//		Post post = PostDao.loadPost(postId);
//		
//		GPost gPost = FastPostBeanConverter.convertPost(post);
//		
//		//asses = result.getAssociationPostTag().getPost().getTagAssociations();
//		asses = gPost.getTagAssociations();
//		found = false;
//		for(GAssociationPostTag ass : asses){
//			if(ass.getTag().getValue().equals(tag.getValue())){
//				found = true;
//				break;
//			}
//		}
//		if(found == true){
//			fail("The tag wasnt removed from the post" );
//		}
//		Persistence.commit();
	}
	
	
	private void assertNotTaggedWithId(String postId, String tagId){
		Persistence.beginSession();
		Post post = PostDao.loadPost(postId);
		
		GPost gPost = FastPostBeanConverter.convertPost(post,null);
		
		//asses = result.getAssociationPostTag().getPost().getTagAssociations();
		List<GAssociationPostTag> asses = gPost.getTagAssociations();
		boolean found = false;
		for(GAssociationPostTag ass : asses){
			if(ass.getTag().getTagId().equals(tagId)){
				found = true;
				break;
			}
		}
		if(found == true){
			fail("The tag is still on the post" );
		}
		Persistence.commit();
	}
	private void assertNotTagged(String postId, String tagValue){
		Persistence.beginSession();
		Post post = PostDao.loadPost(postId);
		
		GPost gPost = FastPostBeanConverter.convertPost(post, null);
		
		//asses = result.getAssociationPostTag().getPost().getTagAssociations();
		List<GAssociationPostTag> asses = gPost.getTagAssociations();
		boolean found = false;
		for(GAssociationPostTag ass : asses){
			if(ass.getTag().getValue().equals(tagValue)){
				found = true;
				break;
			}
		}
		if(found == true){
			fail("The tag is still on the post" );
		}
		Persistence.commit();
	}
	private void assertTagged(String postId, String tagValue){
		Persistence.beginSession();
		Post post = PostDao.loadPost(postId);
		
		GPost gPost = FastPostBeanConverter.convertPost(post, null);
		
		//asses = result.getAssociationPostTag().getPost().getTagAssociations();
		List<GAssociationPostTag> asses = gPost.getTagAssociations();
		boolean found = false;
		for(GAssociationPostTag ass : asses){
			if(ass.getTag().getValue().equals(tagValue)){
				found = true;
				break;
			}
		}
		
		if(asses.size() == 0){
			fail("Post has not tags" );
		}
		if(found == false){
			fail("The tag is still on the post" );
		}
		Persistence.commit();
	}
	
	private void assertTaggedWithId(String postId, String tagId){
		Persistence.beginSession();
		Post post = PostDao.loadPost(postId);
		
		GPost gPost = FastPostBeanConverter.convertPost(post, null);
		
		//asses = result.getAssociationPostTag().getPost().getTagAssociations();
		List<GAssociationPostTag> asses = gPost.getTagAssociations();
		boolean found = false;
		for(GAssociationPostTag ass : asses){
			if(ass.getTag().getTagId().equals(tagId)){
				found = true;
				break;
			}
		}
		
		if(asses.size() == 0){
			fail("Post has not tags" );
		}
		if(found == false){
			fail("The tag is still on the post" );
		}
		Persistence.commit();
	}

	@Test
	public void testServerBroadcastNotifications(){
		GTag tag = new GTag();
		tag.setTagId(Helpers.tagCorporateGoodness);
		String postId = "0B20985A-0123-4933-8943-80C93F49A08B";//random post
		
		AssociationPostTagCommand cmd = createTagCommand(tag, postId);
		
		//Create the association
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		verifyTagCreationMessageWasBroadcast();
		
		//Now remove it
		cmd = createRemoveTagCommand(result.getAssociationId());
		cmd.setConnectionId(serverEventConnId);
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
		verifyTagRemoveMessageWasBroadcast();
		
	}
	
	@Test
	public void testRateAMovie(){
		GTag tag = new GTag();
		tag.setTagId("8C86FFAB-37CC-43DC-AF3E-A8D0360E0192");
		
		String postId = "874D1519-B45D-46F6-9FA9-DE7ABC050C33"; //Wild heart or something?
		
		AssociationPostTagCommand cmd = createTagCommand(tag, postId);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		
		assertTaggedWithId(postId,tag.getTagId());
		
		cmd = createRemoveTagCommand(result.getAssociationPostTag().getGuid());
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
		assertNotTaggedWithId(result.getPost().getPostId(),tag.getTagId());
		
	}
	
	public static AssociationPostTagCommand createRemoveTagCommand(String associationId){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setAssociationId(associationId);
		cmd.setMode(AssociationPostTagCommand.Mode.REMOVE);
		return cmd;
	}
	
	public static AssociationPostTagCommand createTagCommand(GTag tag, String postId){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.setTag(tag);
		cmd.setPostId(postId);
		cmd.setMode(AssociationPostTagCommand.Mode.CREATE);
		return cmd;
	}
	
	//Modify broadcaster so that the browser that caused an event doesnt get the notification. 
	
	private void verifyTagCreationMessageWasBroadcast(){
		verifyTagCreationMessageWasBroadcast(TagEventType.NEW, secondServerEventConnectionId);	
		verifyNoEventWasBroadcast(serverEventConnId);
	}
	private void verifyTagRemoveMessageWasBroadcast(){
		verifyTagCreationMessageWasBroadcast(TagEventType.REMOVED, secondServerEventConnectionId);
		verifyNoEventWasBroadcast(serverEventConnId);
	}
	
	private void verifyTagCreationMessageWasBroadcast(TagEventType type, String connId){
		ThreadUtils.delay();
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		List<Event<?,?>> events = broadcaster.fetchMissedEvents(connId);
		
		Assert.assertTrue("Event list doesnt contain one event", events.size() == 1);
		Event<?,?> returnedEvent = events.get(0);
		Assert.assertTrue("Event not the proper object type", returnedEvent instanceof TagEvent);
		TagEvent tagEvent = (TagEvent)returnedEvent;
		assertEquals(type,tagEvent.getType());
	}
	
	private void verifyNoEventWasBroadcast(String connId){
		ThreadUtils.delay();
		ServerEventBroadcaster broadcaster = ServerEventBroadcaster.getInstance();
		List<Event<?,?>> events = broadcaster.fetchMissedEvents(connId);
		Assert.assertTrue("I shouldnt get events that i cause", events.size() == 0);
	}
	
}

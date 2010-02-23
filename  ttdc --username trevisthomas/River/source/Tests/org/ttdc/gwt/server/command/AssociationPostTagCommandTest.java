package org.ttdc.gwt.server.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;
import org.ttdc.test.utils.ThreadUtils;


/**
 * 
 * NOTE if these tests fail they will leave dirty data in the DB so you should be ready to clean
 * house!!!!
 * 
 * @author Trevis
 *
 */
public class AssociationPostTagCommandTest extends CommandExecuteTestBase{
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
		
		AssociationPostTagCommand cmd = AssociationPostTagCommand.createTagCommand(tag, postId);
		cmd.setConnectionId(serverEventConnId);
		
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		
		//Now remove it
		cmd = AssociationPostTagCommand.createRemoveTagCommand(result.getMessage());
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
		
		AssociationPostTagCommand cmd = AssociationPostTagCommand.createTagCommand(tag, postId);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		
		//Now remove it
		cmd = AssociationPostTagCommand.createRemoveTagCommand(result.getMessage());
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
	}
	

	@Test
	public void testServerBroadcastNotifications(){
		GTag tag = new GTag();
		tag.setTagId(Helpers.tagCorporateGoodness);
		String postId = "0B20985A-0123-4933-8943-80C93F49A08B";//random post
		
		AssociationPostTagCommand cmd = AssociationPostTagCommand.createTagCommand(tag, postId);
		
		//Create the association
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		cmd.setConnectionId(serverEventConnId);
		
		AssociationPostTagResult result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Create Command says that it failed. ", result.isPassed());
		
		verifyTagCreationMessageWasBroadcast();
		
		//Now remove it
		cmd = AssociationPostTagCommand.createRemoveTagCommand(result.getMessage());
		cmd.setConnectionId(serverEventConnId);
		cmdexec = CommandExecutorFactory.createExecutor(Helpers.personIdTrevis,cmd);
		result = (AssociationPostTagResult)cmdexec.executeCommand();
		assertTrue("Remove Command says that it failed. ", result.isPassed());
		
		verifyTagRemoveMessageWasBroadcast();
		
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

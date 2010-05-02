package org.ttdc.gwt.server.activity;

import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.persistence.objects.Person;

class BroadcastEventJob implements Runnable{
	private final Event<?,?> event;
	private final ServerEventBroadcaster broadcaster;
	private final String sourceConnectionId;
	public BroadcastEventJob(ServerEventBroadcaster broadcaster, Event<?,?> event) {
		this.event = event;
		this.broadcaster = broadcaster;
		this.sourceConnectionId = "";
	}
	public BroadcastEventJob(ServerEventBroadcaster broadcaster, Event<?,?> event, String sourceConnectionId) {
		this.event = event;
		this.broadcaster = broadcaster;
		this.sourceConnectionId = sourceConnectionId;
	}
	public void run() {
		for(String connectionId : broadcaster.getActiveConnectionIdSet()){
			if(!connectionId.equals(sourceConnectionId)){
				ServerEventQueue queue = broadcaster.getQueueForConnectionId(connectionId);
				
				boolean validForUser = isPostValidContentForUser(queue);
				
				if(validForUser){
					queue.addEvent(event);
				}
			}
		}	
	}
	private boolean isPostValidContentForUser(ServerEventQueue queue) {
		boolean validForUser = true;
		if(event instanceof PostEvent){
			PostEvent postEvent = (PostEvent)event;
			GPost gPost = postEvent.getSource();
			Person person = PersonDao.loadPerson(queue.getPersonId());
			List<String> filteredTagIds = person.getFrontPageFilteredTagIds();
			
			if(gPost.isNWS() && !person.isNwsEnabled()){
				validForUser = false;
			}
			if(gPost.isPrivate() && !person.isPrivateAccessAccount()){
				validForUser = false;
			}
			//TODO: Trevis. you dont have things in place to test this one yet...
			if(filteredTagIds.contains(gPost.getPostId())){
				validForUser = false;
			}
		}
		return validForUser;
	}
}

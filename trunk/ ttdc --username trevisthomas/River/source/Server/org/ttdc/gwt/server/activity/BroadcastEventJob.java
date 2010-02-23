package org.ttdc.gwt.server.activity;

import org.ttdc.gwt.client.messaging.Event;

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
		//Trevis!
		//TODO: verify that the user should have access to this content!
		//This is non trivial i think because certain types of events will require different rules.
		for(String connectionId : broadcaster.getActiveConnectionIdSet()){
			if(!connectionId.equals(sourceConnectionId)){
				ServerEventQueue queue = broadcaster.getQueueForConnectionId(connectionId);
				queue.addEvent(event);
			}
		}	
	}
}

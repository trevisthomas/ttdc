/**
 * 
 */
package org.ttdc.gwt.server.activity;

import java.util.Date;

class CleanupJob implements Runnable{
	private final static long MAX_AGE_MS = 1000 * 60 * 10;
	private final ServerEventBroadcaster target;
	private Date now;
	public CleanupJob(ServerEventBroadcaster target){
		this.target = target;
	}
	public void run() {
		now = new Date();
		for(String connectionId : target.getActiveConnectionIdSet()){
			removeQueueIfExpired(connectionId);
		}
	}
	private void removeQueueIfExpired(String connectionId) {
		ServerEventQueue queue = target.getQueueForConnectionId(connectionId);
		if(isExpired(queue)){
			target.removeUserQueue(connectionId);
		}
	}
	private boolean isExpired(ServerEventQueue queue) {
		return now.getTime() - queue.getLastAccessed().getTime() > MAX_AGE_MS;
	}
}
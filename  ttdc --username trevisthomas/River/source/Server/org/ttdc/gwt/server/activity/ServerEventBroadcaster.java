package org.ttdc.gwt.server.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.server.activity.push.PushNotificationTool;
import org.ttdc.util.ApplicationProperties;

/**
 * Singleton class which manages server events for active users.  The sever broadcasts messages
 * and clients occasionally poll this class for events that have occured since their last visit.
 * 
 * @author Trevis
 *
 */
public class ServerEventBroadcaster {
	private static final Logger log = Logger.getLogger(ServerEventBroadcaster.class);
	private final ScheduledExecutorService scheduler;
	private final ConcurrentMap<String,ServerEventQueue> userQueues;
	private volatile int nextConnectionIndex = 0;
	private static final String CONNECTION_ID_PREFIX = "CONN_ID_";
	private final PushNotificationTool pushTool = new PushNotificationTool();
	
	
	/* 
	 * WARNING!! I JUST SPENT AN HOUR Struggling to determine why this class wouldnt initialize!  Turns out it was because i didnt have a 
	 * application.properties file in my local folder! Remember, you deleted that file from github because it has authentication info
	 * in it!
	 * 
	 */
	
	private ServerEventBroadcaster(){
		scheduler = Executors.newScheduledThreadPool(1);
		userQueues = new ConcurrentHashMap<String,ServerEventQueue>();
		scheduler.scheduleAtFixedRate(new CleanupJob(this), 10, ApplicationProperties.getPropertyAsInt("SERVER_POLL_RATE_SEC"), TimeUnit.SECONDS);
	}
	private static class ServerEventBroadcasterHolder{
		private final static ServerEventBroadcaster INSTANCE = new ServerEventBroadcaster();
	}
	public static ServerEventBroadcaster getInstance(){
		return ServerEventBroadcasterHolder.INSTANCE;
	}
	
	// This wonderful method will send a push notification to all of this users devices setting their badge count to 0
	public void pushBadgeToZero(String personId) {
		pushTool.pushBadgeToZero(personId);
	}

	public String setupConnection(String personId){
		String connectionId = CONNECTION_ID_PREFIX + ++nextConnectionIndex;
		createQueueForConnectionId(connectionId, personId);
		return connectionId;
	}
	
	public List<Event<?,?>> fetchMissedEvents(String connectionId){
		try{
			ServerEventQueue queue = getQueueForConnectionId(connectionId);
			if(queue == null){
				//throw new RuntimeException("ServerEventBroadcaster not initalized for connection: " + connectionId);
				List<Event<?,?>> list = new ArrayList<Event<?,?>>();
				list.add(new MessageEvent(MessageEventType.RESET_SERVER_BROADCAST, "Connection is dead"));
				return list;
			}
			//log.debug("Loaded "+queue.getPersonId()+" at connectionId:"+connectionId);
			queue.setLastAccessed(new Date());
			return queue.popAllEvents();
		}
		catch (IllegalArgumentException e) {
			
			throw e;
		}
	}
	
	public void broadcastEvent(Event<?,?> event){
		processEventForPush(event, "");
		Callable<Object> callable = Executors.callable( new BroadcastEventJob(this, event));
		try {
			callable.call();
		} 
		
		catch (Exception e) {
			log.error(e);
		}
		
		
	}
	
	/**
	 * Only broadcasts the message to everyone else, not to self.
	 * 
	 * @param event
	 * @param sourceConnectionId
	 */
	public void broadcastEvent(Event<?, ?> event, String sourceConnectionId) {
		processEventForPush(event, sourceConnectionId);
		Callable<Object> callable = Executors.callable(new BroadcastEventJob(this, event, sourceConnectionId));
		try {
			callable.call();
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Only broadcasts the message to everyone else, not to self. And send a push notification to mobile users!
	 * 
	 * @param the
	 *            person who is causing the event
	 * @param event
	 * @param sourceConnectionId
	 */
	// Trevis: Is this method deprecated? PersonId isnt even used.
	public void broadcastEvent(String personId, Event<?, ?> event, String sourceConnectionId) {
		processEventForPush(event, sourceConnectionId);
		Callable<Object> callable = Executors.callable( new BroadcastEventJob(this, event, sourceConnectionId));
		try {
			callable.call();
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void processEventForPush(Event<?, ?> event, String sourceConnectionId) {
		if (event instanceof PostEvent) {
			pushTool.executePushEventCausedBy(sourceConnectionId, (PostEvent) event);
		} else if (event instanceof PersonEvent) {
			pushTool.executePushEventCausedBy(sourceConnectionId, (PersonEvent) event);
		}
	}
	

	Set<String> getActiveConnectionIdSet(){
		return userQueues.keySet();
	}
	
	
	ServerEventQueue getQueueForConnectionId(String connectionId){
		return userQueues.get(connectionId);
	}
	void removeUserQueue(String connectionId) {
		userQueues.remove(connectionId);
		log.debug("**** Removing abandonded user queue ("+connectionId+") ****");
	}
	private ServerEventQueue createQueueForConnectionId(String connectionId, String personId){
		ServerEventQueue queue = new ServerEventQueue(personId);
		userQueues.putIfAbsent(connectionId, queue);
		return queue;
	}
	
	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException();
	}
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException();
	}
}

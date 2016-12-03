/**
 * 
 */
package org.ttdc.gwt.server.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ttdc.gwt.client.messaging.Event;


class ServerEventQueue{
	private volatile Date lastAccessed = new Date();
	private final Queue<Event<?,?>> queue = new ConcurrentLinkedQueue<Event<?,?>>();
	private final String personId;
	
	ServerEventQueue(String personId){
		this.personId = personId;
	}
	
	boolean containsEvent(Event<?,?> e){
		return queue.contains(e);
	}
	
	public Date getLastAccessed() {
		return lastAccessed;
	}
	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	private void stampAccessTime(){
		setLastAccessed(new Date());
	}
	
	public boolean hasEvents(){
		return queue.peek() != null;
	}
	public void addOrReplace(Event<?,?> event){
		queue.remove(event);
		queue.add(event);
	}

	public List<Event<?,?>> popAllEvents(){
		List<Event<?,?>> list = new ArrayList<Event<?,?>>();
		while(!queue.isEmpty()){
			list.add(queue.poll());
		}
		stampAccessTime();
		return list;
	}

	public String getPersonId() {
		return personId;
	}
}
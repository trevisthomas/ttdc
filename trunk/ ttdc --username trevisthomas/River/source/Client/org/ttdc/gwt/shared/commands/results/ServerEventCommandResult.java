package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.services.CommandResult;

public class ServerEventCommandResult implements CommandResult{
	private String connectionId;
	private List<Event<?,?>> events;
	
	public ServerEventCommandResult(){}
	
	public ServerEventCommandResult(String connectionId){
		this.connectionId = connectionId;
	}

	public ServerEventCommandResult(List<Event<?, ?>> events){
		this.events = events;
	}
	
	public ServerEventCommandResult(String connectionId, List<Event<?, ?>> events){
		this.connectionId = connectionId;
		this.events = events;
	}
	
	public String getConnectionId() {
		return connectionId;
	}

	public List<Event<?, ?>> getEvents() {
		return events;
	}

	
}

package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.services.CommandResult;

//5-1-2010 - i'm adding person id here so that the server browser instance can know if a different
//instance caused the person in session to change.  If this happens, the browser should refresh.
//It happens if multiple instances of FF are running and you log in with one of them. 
public class ServerEventCommandResult implements CommandResult{
	private String connectionId;
	private GPerson person;
	private int serverBuildNumber;

	public ServerEventCommandResult(String connectionId){
		this.connectionId = connectionId;
	}

	public GPerson getPerson() {
		return person;
	}

	private List<Event<?,?>> events;
	
	public ServerEventCommandResult(){}
	
	
	public ServerEventCommandResult(List<Event<?, ?>> events, GPerson person){
		this.events = events;
		this.person = person;
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

	public int getServerBuildNumber() {
		return serverBuildNumber;
	}

	public void setServerBuildNumber(int serverBuildNumber) {
		this.serverBuildNumber = serverBuildNumber;
	}

	
}

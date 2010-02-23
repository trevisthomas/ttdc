package org.ttdc.gwt.server.command.executors;

import java.util.List;

import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

public class ServerEventListCommandExecutor extends CommandExecutor<ServerEventCommandResult>{
	@Override
	protected CommandResult execute() {
		List<Event<?,?>> events;
		
		ServerEventListCommand command = (ServerEventListCommand)getCommand();
		String connectionId = command.getConnectionId();
		
		events = ServerEventBroadcaster.getInstance().fetchMissedEvents(connectionId);
		
		ServerEventCommandResult result = new ServerEventCommandResult(events);
		return result;
	}
}

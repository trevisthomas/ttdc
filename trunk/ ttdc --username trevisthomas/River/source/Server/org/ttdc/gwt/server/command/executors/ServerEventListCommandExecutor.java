package org.ttdc.gwt.server.command.executors;

import java.util.List;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;
import org.ttdc.persistence.Persistence;
import org.ttdc.util.ApplicationProperties;

public class ServerEventListCommandExecutor extends CommandExecutor<ServerEventCommandResult>{
	@Override
	protected CommandResult execute() {
		List<Event<?,?>> events;
		
		ServerEventListCommand command = (ServerEventListCommand)getCommand();
		String connectionId = command.getConnectionId();
		
		events = ServerEventBroadcaster.getInstance().fetchMissedEvents(connectionId);
		Persistence.beginSession();
		GPerson person = FastPostBeanConverter.convertPerson(getPerson());
		Persistence.commit();
		ServerEventCommandResult result = new ServerEventCommandResult(events, person);
		result.setServerBuildNumber(ApplicationProperties.getBuildNumber());
		
		return result;
	}
}

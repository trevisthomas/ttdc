package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

public class ServerEventOpenConnectionCommandExecutor extends CommandExecutor<PersonCommandResult>{
	@Override
	protected CommandResult execute(){
		String connectionId = ServerEventBroadcaster.getInstance().setupConnection(getPersonId());
		return new ServerEventCommandResult(connectionId);
	}
}

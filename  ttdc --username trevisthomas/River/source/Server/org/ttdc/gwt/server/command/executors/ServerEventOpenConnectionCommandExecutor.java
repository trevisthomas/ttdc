package org.ttdc.gwt.server.command.executors;

import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.activity.ServerEventBroadcaster;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;
import org.ttdc.persistence.objects.Person;

public class ServerEventOpenConnectionCommandExecutor extends CommandExecutor<PersonCommandResult>{
	@Override
	protected CommandResult execute(){
		Person person = getPerson();
		String connectionId = ServerEventBroadcaster.getInstance().setupConnection(person.getPersonId());
		return new ServerEventCommandResult(connectionId);
	}
}

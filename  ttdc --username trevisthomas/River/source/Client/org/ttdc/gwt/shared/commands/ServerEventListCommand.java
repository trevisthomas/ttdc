package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerEventListCommand extends Command<ServerEventCommandResult> implements IsSerializable {

	public ServerEventListCommand() {}
	
	public ServerEventListCommand(String connectionId) {
		setConnectionId(connectionId);
	}
}

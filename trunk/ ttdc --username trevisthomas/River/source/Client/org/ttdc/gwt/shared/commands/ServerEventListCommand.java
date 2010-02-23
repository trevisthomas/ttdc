package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerEventListCommand extends Command<ServerEventCommandResult> implements IsSerializable{
	private String connectionId;
	public ServerEventListCommand() {}
	
	public ServerEventListCommand(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getConnectionId() {
		return connectionId;
	}
}

package org.ttdc.gwt.client.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Command<T extends CommandResult> implements IsSerializable{
	private String connectionId;

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	
}

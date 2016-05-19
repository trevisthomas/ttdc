package org.ttdc.gwt.client.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Command<T extends CommandResult> implements IsSerializable{
	private String connectionId;
	// private String securityToken;

	private String token; // Security token. Initially added for JSON calls because they don't use session.

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}

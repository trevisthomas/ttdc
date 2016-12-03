package org.ttdc.servlets.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

public class ServerEventCommandResultWrapper {
	private ServerEventCommandResult delegate;

	public ServerEventCommandResultWrapper(ServerEventCommandResult delegate) {
		this.delegate = delegate;
	}

	// private String connectionId;
	// private GPerson person;
	// private int serverBuildNumber;

	public String getConnectionId() {
		return delegate.getConnectionId();
	}

	public int getServerBuildNumber() {
		return delegate.getServerBuildNumber();
	}

	public List<GenericEventWrapper> getEvents() {
		List<GenericEventWrapper> list = new ArrayList<GenericEventWrapper>();
		for (Event<?, ?> e : delegate.getEvents()) {
			list.add(new GenericEventWrapper(e));
		}
		return list;
	}
}

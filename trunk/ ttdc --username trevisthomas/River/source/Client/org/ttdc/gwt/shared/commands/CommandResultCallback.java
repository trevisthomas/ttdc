package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.services.CommandResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

abstract public class CommandResultCallback<T extends CommandResult> implements AsyncCallback<T>{
	public void onFailure(Throwable caught) {
		EventBus.getInstance().fireEvent(new MessageEvent(MessageEventType.SYSTEM_ERROR,caught.getMessage()));
	}
}

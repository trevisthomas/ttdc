package org.ttdc.gwt.client.messaging.error;

import org.ttdc.gwt.client.messaging.Event;

public class MessageEvent extends Event<MessageEventType, String>{
	private MessageEventType type;
	private String source;
	public MessageEvent() {}
	
	public MessageEvent(MessageEventType type, String source) {
		this.type = type;
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	
	public MessageEventType getType() {
		return type;
	}
}

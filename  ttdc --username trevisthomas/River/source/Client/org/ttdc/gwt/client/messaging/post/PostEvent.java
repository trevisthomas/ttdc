package org.ttdc.gwt.client.messaging.post;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.Event;

public class PostEvent extends Event<PostEventType, GPost>{
	private GPost source;
	private PostEventType type;
	
	public PostEvent(){}
	
	public PostEvent(PostEventType type, GPost source) {
		this.source = source;
		this.type = type;
	}
	public GPost getSource() {
		return source;
	}
	public PostEventType getType() {
		return type;
	}
}

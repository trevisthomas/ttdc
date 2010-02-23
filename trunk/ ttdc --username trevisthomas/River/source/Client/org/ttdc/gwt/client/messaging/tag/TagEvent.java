package org.ttdc.gwt.client.messaging.tag;

import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.messaging.Event;

public class TagEvent extends Event<TagEventType, GAssociationPostTag>{
	private TagEventType type;
	private GAssociationPostTag source;
	
	public TagEvent(){};
	
	public TagEvent(TagEventType type, GAssociationPostTag source){
		this.type = type;
		this.source = source;
	}
	public GAssociationPostTag getSource() {
		return source;
	}

	public TagEventType getType() {
		return type;
	}
	
}

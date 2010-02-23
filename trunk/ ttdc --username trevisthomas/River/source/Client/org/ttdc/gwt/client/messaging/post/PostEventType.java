package org.ttdc.gwt.client.messaging.post;

public enum PostEventType {
	NEW,
	EDIT,
	DELETE,
	EXPAND_CONTRACT;
	
	public boolean isNew(){
		return this == NEW;
	}
	public boolean isEdit(){
		return this == EDIT;
	}
	public boolean isDelete(){
		return this == DELETE;
	}
	public boolean isExpandContract(){
		return this == EXPAND_CONTRACT;
	}
}

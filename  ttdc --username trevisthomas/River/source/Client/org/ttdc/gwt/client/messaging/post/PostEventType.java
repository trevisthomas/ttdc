package org.ttdc.gwt.client.messaging.post;

public enum PostEventType {
	NEW,
	EDIT,
	DELETE,
	NEW_FORCE_REFRESH,
	EXPAND_CONTRACT, 
	LOCAL_NEW;
	
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
	public boolean isLocalNew(){
		return this == LOCAL_NEW;
	}	
}

package org.ttdc.gwt.client.messaging.person;

public enum PersonEventType{
	USER_PROFILE_UPDATED, //This is a hack that i'm putting in place so that the user dashboard tabs can notify the tab container
	USER_CHANGED,
	TRAFFIC,
	USER_EARMKARK_COUNT_CHANGED, 
	USER_MARK_SITE_READ;
	
	public boolean isUserChanged(){
		return this == USER_CHANGED;
	}
	
	public boolean isMarkSiteRead(){
		return this == USER_MARK_SITE_READ;
	}
};


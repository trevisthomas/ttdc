package org.ttdc.gwt.client.messaging.person;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.Event;

public class PersonEvent extends Event<PersonEventType,GPerson>{
	private PersonEventType type;
	private GPerson source;
	
	public PersonEvent(){}
	
	public PersonEvent(PersonEventType type, GPerson source){
		this.type = type;
		this.source = source;
	}
	
	public GPerson getSource() {
		return source;
	}

	public PersonEventType getType() {
		return type;
	}

}

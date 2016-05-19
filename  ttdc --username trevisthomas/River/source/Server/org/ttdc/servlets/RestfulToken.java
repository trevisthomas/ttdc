package org.ttdc.servlets;

import java.io.Serializable;

public class RestfulToken implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String personId;
	private final Long randomizer; 
	
	public RestfulToken(String personId) {
		this.personId = personId;
		randomizer = System.currentTimeMillis();
	}

	public String getPersonId() {
		return personId;
	}
}

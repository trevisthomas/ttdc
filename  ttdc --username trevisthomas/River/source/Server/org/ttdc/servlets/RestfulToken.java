package org.ttdc.servlets;

import java.io.Serializable;

public class RestfulToken implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final String personId;
	private final String encryptedPassword;
	private final Long randomizer; 
	
	public RestfulToken(String personId, String encryptedPassword){
		this.personId = personId;
		this.encryptedPassword = encryptedPassword;
		randomizer = System.currentTimeMillis();
	}

	public String getPersonId() {
		return personId;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	
	
}

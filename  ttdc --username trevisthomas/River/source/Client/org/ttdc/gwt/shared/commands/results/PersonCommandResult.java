package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;

public class PersonCommandResult implements CommandResult {
	private GPerson person;
	private String token; // Security token for JSON

	public PersonCommandResult() {}
	
	public PersonCommandResult(GPerson person) {
		this.person = person;
	}
	
	public GPerson getPerson() {
		return person;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

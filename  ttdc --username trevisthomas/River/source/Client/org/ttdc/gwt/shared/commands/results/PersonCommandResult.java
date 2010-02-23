package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.CommandResult;

public class PersonCommandResult implements CommandResult {
	private GPerson person;

	public PersonCommandResult() {}
	
	public PersonCommandResult(GPerson person) {
		this.person = person;
	}
	
	public GPerson getPerson() {
		return person;
	}
}

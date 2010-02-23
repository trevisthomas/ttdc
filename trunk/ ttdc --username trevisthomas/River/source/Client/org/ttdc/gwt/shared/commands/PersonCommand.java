package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

public class PersonCommand extends Command<GenericCommandResult<GPerson>>{
	private String personId;
	private PersonStatusType type;
	private String privilegeId;
	
	public PersonCommand() {
		
	}
	
	public PersonCommand(String personId, PersonStatusType type) {
		this.personId = personId;
		this.type = type;
	}
	
	public PersonCommand(String personId, PersonStatusType type, String privilegeId) {
		this.personId = personId;
		this.privilegeId = privilegeId;
		this.type = type;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setStatus(PersonStatusType status) {
		this.type = status;
	}

	public PersonStatusType getType() {
		return type;
	}
	
	public String getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}

}

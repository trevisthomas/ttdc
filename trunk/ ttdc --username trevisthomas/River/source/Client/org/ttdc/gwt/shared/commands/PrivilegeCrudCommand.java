package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GPrivilege;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

public class PrivilegeCrudCommand extends Command<GenericListCommandResult<GPrivilege>>{
	private ActionType action;
	private String personId;
	private String privilegeId;
	
	public PrivilegeCrudCommand() {}
	
	public PrivilegeCrudCommand(ActionType action) {
		this.action = action;
	}
	
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}
	
	
}
